import uuid, math, json
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy.orm import Session
from app.database import get_db
from app.deps import get_current_user
from app import models

router = APIRouter(tags=["quote"])


# ── Schemas ────────────────────────────────────────────────────────────────

class QuoteRequest(BaseModel):
    shopping_list_id: str
    user_lat: float | None = None
    user_lng: float | None = None
    strategy: str = "best_balance"
    max_results: int = 5

class QuoteItemOut(BaseModel):
    list_item_id: str
    raw_name: str
    matched: bool
    match_confidence: float
    unit_price: float | None = None
    quantity: float
    subtotal: float | None = None
    substitution_for: str | None = None
    product_id: str | None = None

class QuoteResultOut(BaseModel):
    id: str
    chain_id: str
    chain_name: str
    store_id: str | None = None
    store_name: str | None = None
    store_lat: float | None = None
    store_lng: float | None = None
    store_address: str | None = None
    total: float
    items_found: int
    items_missing: int
    distance_km: float | None = None
    eta_minutes: float | None = None
    score: float
    rank: int
    savings_vs_max: float | None = None
    items: list[QuoteItemOut] = []

class QuoteResponse(BaseModel):
    quote_id: str
    strategy: str
    results: list[QuoteResultOut]
    best_cheapest_id: str | None = None
    best_nearest_id: str | None = None
    best_balance_id: str | None = None


# ── Helpers ───────────────────────────────────────────────────────────────

def haversine(lat1, lon1, lat2, lon2) -> float:
    R = 6371.0
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = (math.sin(dlat/2)**2
         + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon/2)**2)
    return R * 2 * math.asin(math.sqrt(max(0, a)))


def match_product(raw_name: str, db: Session) -> tuple[models.Product | None, float]:
    """
    Fuzzy match: busca el producto cuyo nombre contenga más tokens del raw_name.
    Devuelve (producto, confianza 0-1).
    """
    tokens = [t.lower() for t in raw_name.split() if len(t) > 2]
    if not tokens:
        return None, 0.0

    all_products = db.query(models.Product).all()
    best_product = None
    best_score = 0.0

    for product in all_products:
        name_lower = product.name.lower()
        brand_lower = (product.brand or "").lower()
        hits = sum(1 for t in tokens if t in name_lower or t in brand_lower)
        score = hits / len(tokens) if tokens else 0.0
        if score > best_score:
            best_score = score
            best_product = product

    if best_score < 0.3:
        return None, 0.0
    return best_product, min(1.0, best_score)


# ── Endpoints ─────────────────────────────────────────────────────────────

@router.post("/quote", response_model=QuoteResponse, status_code=201)
def create_quote(body: QuoteRequest, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    # Verificar lista
    lst = db.query(models.ShoppingList).filter(
        models.ShoppingList.id == body.shopping_list_id,
        models.ShoppingList.user_id == user.id,
    ).first()
    if not lst:
        raise HTTPException(404, "Lista no encontrada")

    items = lst.items
    if not items:
        raise HTTPException(400, "La lista está vacía")

    chains = db.query(models.Chain).all()
    stores = db.query(models.Store).all()

    # Agrupar tiendas por cadena (tomar la más cercana si hay lat/lng)
    store_by_chain: dict[str, models.Store] = {}
    for chain in chains:
        chain_stores = [s for s in stores if s.chain_id == chain.id]
        if not chain_stores:
            continue
        if body.user_lat is not None and body.user_lng is not None:
            chain_stores.sort(key=lambda s: haversine(body.user_lat, body.user_lng, s.latitude, s.longitude))
        store_by_chain[chain.id] = chain_stores[0]

    # Hacer match de cada item con productos y buscar precios por cadena
    # chain_id -> {total, items_found, items_missing, item_details}
    chain_totals: dict[str, dict] = {c.id: {"total": 0.0, "found": 0, "missing": 0, "items": []} for c in chains}

    for item in items:
        product, confidence = match_product(item.raw_name, db)

        for chain in chains:
            detail: dict = {
                "list_item_id": item.id,
                "raw_name": item.raw_name,
                "matched": False,
                "match_confidence": 0.0,
                "unit_price": None,
                "quantity": item.quantity,
                "subtotal": None,
                "product_id": None,
            }

            if product and confidence >= 0.3:
                price_row = db.query(models.Price).filter(
                    models.Price.product_id == product.id,
                    models.Price.chain_id == chain.id,
                ).first()
                if price_row:
                    subtotal = price_row.price * item.quantity
                    chain_totals[chain.id]["total"] += subtotal
                    chain_totals[chain.id]["found"] += 1
                    detail.update({
                        "matched": True,
                        "match_confidence": round(confidence, 2),
                        "unit_price": price_row.price,
                        "subtotal": round(subtotal, 2),
                        "product_id": product.id,
                    })
                else:
                    chain_totals[chain.id]["missing"] += 1
            else:
                chain_totals[chain.id]["missing"] += 1

            chain_totals[chain.id]["items"].append(detail)

    # Calcular distancias
    totals_list = []
    for chain in chains:
        if chain.id not in store_by_chain:
            continue
        store = store_by_chain[chain.id]
        dist = None
        eta = None
        if body.user_lat is not None and body.user_lng is not None:
            dist = round(haversine(body.user_lat, body.user_lng, store.latitude, store.longitude), 2)
            eta = round(dist / 40 * 60, 1)  # ~40 km/h en ciudad

        totals_list.append({
            "chain": chain,
            "store": store,
            "total": round(chain_totals[chain.id]["total"], 2),
            "found": chain_totals[chain.id]["found"],
            "missing": chain_totals[chain.id]["missing"],
            "dist": dist,
            "eta": eta,
            "items": chain_totals[chain.id]["items"],
        })

    # Quitar cadenas sin ningún producto encontrado
    totals_list = [t for t in totals_list if t["found"] > 0]
    if not totals_list:
        # Devolver todos aunque sea con 0
        totals_list = [t for t in [
            {
                "chain": chain,
                "store": store_by_chain.get(chain.id),
                "total": 0.0,
                "found": 0,
                "missing": len(items),
                "dist": None,
                "eta": None,
                "items": chain_totals[chain.id]["items"],
            }
            for chain in chains if chain.id in store_by_chain
        ]]

    # Scoring compuesto
    max_total = max(t["total"] for t in totals_list) or 1
    min_total = min(t["total"] for t in totals_list)
    total_range = max_total - min_total or 1

    has_dist = any(t["dist"] is not None for t in totals_list)
    max_dist = max((t["dist"] or 0) for t in totals_list) or 1
    min_dist = min((t["dist"] or 0) for t in totals_list)
    dist_range = max_dist - min_dist or 1

    for t in totals_list:
        price_score = (max_total - t["total"]) / total_range
        if has_dist and t["dist"] is not None:
            dist_score = (max_dist - t["dist"]) / dist_range
        else:
            dist_score = 0.5
        t["score"] = round(0.6 * price_score + 0.4 * dist_score, 4)

    # Ordenar según estrategia
    if body.strategy == "cheapest":
        totals_list.sort(key=lambda t: t["total"])
    elif body.strategy == "nearest":
        totals_list.sort(key=lambda t: (t["dist"] or 999))
    else:  # best_balance
        totals_list.sort(key=lambda t: t["score"], reverse=True)

    totals_list = totals_list[:body.max_results]

    # Persisten los resultados
    quote = models.Quote(
        id=str(uuid.uuid4()),
        user_id=user.id,
        list_id=body.shopping_list_id,
        strategy=body.strategy,
    )
    db.add(quote)
    db.flush()

    result_rows = []
    for rank, t in enumerate(totals_list, start=1):
        savings = round(max_total - t["total"], 2) if max_total > 0 else None
        row = models.QuoteResult(
            id=str(uuid.uuid4()),
            quote_id=quote.id,
            chain_id=t["chain"].id,
            store_id=t["store"].id if t["store"] else None,
            total=t["total"],
            items_found=t["found"],
            items_missing=t["missing"],
            distance_km=t["dist"],
            eta_minutes=t["eta"],
            score=t["score"],
            rank=rank,
            savings_vs_max=savings,
            items_json=json.dumps(t["items"]),
        )
        db.add(row)
        result_rows.append((row, t))

    db.commit()

    # Identificar mejores
    cheapest_row = min(result_rows, key=lambda x: x[0].total)
    nearest_row = min(result_rows, key=lambda x: (x[0].distance_km or 999))
    balance_row = max(result_rows, key=lambda x: x[0].score)

    # Construir respuesta
    results_out = []
    for row, t in result_rows:
        item_list = [QuoteItemOut(**i) for i in json.loads(row.items_json or "[]")]
        store = t["store"]
        results_out.append(QuoteResultOut(
            id=row.id,
            chain_id=row.chain_id,
            chain_name=t["chain"].name,
            store_id=row.store_id,
            store_name=store.name if store else None,
            store_lat=store.latitude if store else None,
            store_lng=store.longitude if store else None,
            store_address=store.address if store else None,
            total=row.total,
            items_found=row.items_found,
            items_missing=row.items_missing,
            distance_km=row.distance_km,
            eta_minutes=row.eta_minutes,
            score=row.score,
            rank=row.rank,
            savings_vs_max=row.savings_vs_max,
            items=item_list,
        ))

    return QuoteResponse(
        quote_id=quote.id,
        strategy=body.strategy,
        results=results_out,
        best_cheapest_id=cheapest_row[0].id,
        best_nearest_id=nearest_row[0].id,
        best_balance_id=balance_row[0].id,
    )


@router.get("/quote/{quote_id}", response_model=QuoteResponse)
def get_quote(quote_id: str, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    quote = db.query(models.Quote).filter(
        models.Quote.id == quote_id,
        models.Quote.user_id == user.id,
    ).first()
    if not quote:
        raise HTTPException(404, "Cotización no encontrada")

    results_out = []
    for row in sorted(quote.results, key=lambda r: r.rank):
        chain = db.get(models.Chain, row.chain_id)
        store = db.get(models.Store, row.store_id) if row.store_id else None
        item_list = [QuoteItemOut(**i) for i in json.loads(row.items_json or "[]")]
        results_out.append(QuoteResultOut(
            id=row.id,
            chain_id=row.chain_id,
            chain_name=chain.name if chain else "",
            store_id=row.store_id,
            store_name=store.name if store else None,
            store_lat=store.latitude if store else None,
            store_lng=store.longitude if store else None,
            store_address=store.address if store else None,
            total=row.total,
            items_found=row.items_found,
            items_missing=row.items_missing,
            distance_km=row.distance_km,
            eta_minutes=row.eta_minutes,
            score=row.score,
            rank=row.rank,
            savings_vs_max=row.savings_vs_max,
            items=item_list,
        ))

    cheapest = min(results_out, key=lambda r: r.total, default=None)
    nearest = min(results_out, key=lambda r: (r.distance_km or 999), default=None)
    balance = max(results_out, key=lambda r: r.score, default=None)

    return QuoteResponse(
        quote_id=quote.id,
        strategy=quote.strategy,
        results=results_out,
        best_cheapest_id=cheapest.id if cheapest else None,
        best_nearest_id=nearest.id if nearest else None,
        best_balance_id=balance.id if balance else None,
    )
