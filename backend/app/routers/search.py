from fastapi import APIRouter, Depends, Query
from pydantic import BaseModel
from sqlalchemy.orm import Session
from sqlalchemy import or_
from app.database import get_db
from app.deps import get_current_user
from app import models

router = APIRouter(tags=["search"])

class ProductOut(BaseModel):
    id: str
    name: str
    brand: str | None = None
    category: str | None = None
    presentation: str | None = None
    size: float | None = None
    unit: str | None = None
    image_url: str | None = None

class SearchResponse(BaseModel):
    products: list[ProductOut]
    total: int


@router.get("/search", response_model=SearchResponse)
def search(
    q: str = Query(..., min_length=1),
    limit: int = Query(25, le=100),
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    terms = q.lower().split()
    query = db.query(models.Product)
    for term in terms:
        query = query.filter(
            or_(
                models.Product.name.ilike(f"%{term}%"),
                models.Product.brand.ilike(f"%{term}%"),
                models.Product.category.ilike(f"%{term}%"),
            )
        )
    products = query.limit(limit).all()
    out = [ProductOut(
        id=p.id, name=p.name, brand=p.brand, category=p.category,
        presentation=p.presentation, size=p.size, unit=p.unit, image_url=p.image_url,
    ) for p in products]
    return SearchResponse(products=out, total=len(out))
