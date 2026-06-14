"""
Datos semilla: cadenas, tiendas en Querétaro y catálogo de productos con precios en MXN.
Se ejecuta automáticamente al arrancar si la DB está vacía.
"""
import uuid
from sqlalchemy.orm import Session
from app import models


CHAINS = [
    {"slug": "walmart",        "name": "Walmart",         "color": "#0071CE"},
    {"slug": "soriana",        "name": "Soriana",         "color": "#E31837"},
    {"slug": "chedraui",       "name": "Chedraui",        "color": "#00853E"},
    {"slug": "lacomer",        "name": "La Comer",        "color": "#F15A22"},
    {"slug": "bodega-aurrera", "name": "Bodega Aurrerá",  "color": "#003087"},
    {"slug": "heb",            "name": "HEB",             "color": "#CC0000"},
]

# Tiendas en Querétaro con coordenadas reales aproximadas
STORES = [
    # Walmart
    {"chain": "walmart", "name": "Walmart Antea",       "address": "Blvd. Bernardo Quintana 9000, Juriquilla",    "lat": 20.6357, "lng": -100.4072},
    {"chain": "walmart", "name": "Walmart Soriana",     "address": "Av. 5 de Febrero 1703, Querétaro",            "lat": 20.5997, "lng": -100.4026},
    # Soriana
    {"chain": "soriana", "name": "Soriana Hiper Centro", "address": "Av. Universidad s/n, Querétaro",             "lat": 20.5928, "lng": -100.3901},
    {"chain": "soriana", "name": "Soriana Peñuelas",    "address": "Av. Constituyentes 1, Peñuelas",              "lat": 20.5703, "lng": -100.4181},
    {"chain": "soriana", "name": "Soriana Juriquilla",  "address": "Blvd. Juriquilla 3400",                       "lat": 20.7015, "lng": -100.4388},
    # Chedraui
    {"chain": "chedraui", "name": "Chedraui Querétaro", "address": "Av. Constituyentes 20, Querétaro",            "lat": 20.5893, "lng": -100.3959},
    {"chain": "chedraui", "name": "Chedraui Zibatá",   "address": "Blvd. Zibatá 1200",                           "lat": 20.6652, "lng": -100.4523},
    # La Comer
    {"chain": "lacomer",  "name": "La Comer Pedregal",  "address": "Blvd. Bernardo Quintana 1902",                "lat": 20.6186, "lng": -100.4215},
    # Bodega Aurrerá
    {"chain": "bodega-aurrera", "name": "Bodega Aurrerá Corregidora", "address": "Av. Corregidora Sur 600",       "lat": 20.5378, "lng": -100.4315},
    {"chain": "bodega-aurrera", "name": "Bodega Aurrerá Arboledas",   "address": "Calz. de los Arcos 100",        "lat": 20.6012, "lng": -100.4450},
    # HEB
    {"chain": "heb", "name": "HEB Querétaro",           "address": "Av. Prolongación División del Norte 3300",   "lat": 20.6126, "lng": -100.4129},
]

# Catálogo de productos con precios por cadena (MXN)
# Formato: {nombre, marca, categoría, presentación, tamaño, unidad, precios: {slug: precio}}
PRODUCTS = [
    # ── Lácteos ──
    {"name": "Leche entera",      "brand": "Lala",    "cat": "Lácteos",    "pres": "Cartón",  "size": 1.0,  "unit": "L",
     "prices": {"walmart": 24.50, "soriana": 24.90, "chedraui": 23.80, "lacomer": 25.50, "bodega-aurrera": 22.90, "heb": 24.00}},
    {"name": "Leche semidescremada", "brand": "Lala", "cat": "Lácteos",   "pres": "Cartón",  "size": 1.0,  "unit": "L",
     "prices": {"walmart": 25.00, "soriana": 25.50, "chedraui": 24.50, "lacomer": 26.00, "bodega-aurrera": 23.50, "heb": 25.00}},
    {"name": "Yogurt natural",    "brand": "Danone",  "cat": "Lácteos",    "pres": "Frasco",  "size": 900.0,"unit": "g",
     "prices": {"walmart": 42.00, "soriana": 43.50, "chedraui": 41.00, "lacomer": 44.00, "bodega-aurrera": 39.90, "heb": 42.50}},
    {"name": "Queso fresco",      "brand": "Chilchota","cat": "Lácteos",   "pres": "Pieza",   "size": 400.0,"unit": "g",
     "prices": {"walmart": 38.00, "soriana": 39.50, "chedraui": 37.50, "lacomer": 40.00, "bodega-aurrera": 36.00, "heb": 38.50}},
    {"name": "Crema ácida",       "brand": "Lala",    "cat": "Lácteos",    "pres": "Bolsa",   "size": 500.0,"unit": "g",
     "prices": {"walmart": 28.00, "soriana": 29.00, "chedraui": 27.50, "lacomer": 29.50, "bodega-aurrera": 26.50, "heb": 28.50}},
    {"name": "Mantequilla",       "brand": "Lala",    "cat": "Lácteos",    "pres": "Barra",   "size": 90.0, "unit": "g",
     "prices": {"walmart": 22.00, "soriana": 22.50, "chedraui": 21.50, "lacomer": 23.00, "bodega-aurrera": 20.90, "heb": 22.00}},

    # ── Huevo ──
    {"name": "Huevo blanco",      "brand": "Bachoco", "cat": "Huevo",      "pres": "Caja",    "size": 30.0, "unit": "pz",
     "prices": {"walmart": 89.00, "soriana": 91.00, "chedraui": 87.50, "lacomer": 93.00, "bodega-aurrera": 85.00, "heb": 90.00}},
    {"name": "Huevo rojo",        "brand": "San Juan", "cat": "Huevo",     "pres": "Caja",    "size": 30.0, "unit": "pz",
     "prices": {"walmart": 92.00, "soriana": 93.50, "chedraui": 90.00, "lacomer": 95.00, "bodega-aurrera": 88.00, "heb": 92.50}},

    # ── Pan y tortillas ──
    {"name": "Pan blanco de caja", "brand": "Bimbo",  "cat": "Panadería",  "pres": "Bolsa",   "size": 680.0,"unit": "g",
     "prices": {"walmart": 49.00, "soriana": 50.00, "chedraui": 48.50, "lacomer": 51.00, "bodega-aurrera": 46.50, "heb": 49.50}},
    {"name": "Tortillas de maíz", "brand": "Maseca",  "cat": "Panadería",  "pres": "Paquete", "size": 1000.0,"unit":"g",
     "prices": {"walmart": 22.00, "soriana": 22.00, "chedraui": 21.50, "lacomer": 22.50, "bodega-aurrera": 20.50, "heb": 22.00}},
    {"name": "Tortillas de harina","brand": "Mission", "cat": "Panadería", "pres": "Paquete", "size": 560.0,"unit": "g",
     "prices": {"walmart": 34.00, "soriana": 35.00, "chedraui": 33.50, "lacomer": 36.00, "bodega-aurrera": 32.00, "heb": 34.50}},

    # ── Carnes ──
    {"name": "Pechuga de pollo",  "brand": None,      "cat": "Carnes",     "pres": "Kg",      "size": 1.0,  "unit": "kg",
     "prices": {"walmart": 89.00, "soriana": 92.00, "chedraui": 87.00, "lacomer": 95.00, "bodega-aurrera": 84.00, "heb": 90.00}},
    {"name": "Milanesa de res",   "brand": None,      "cat": "Carnes",     "pres": "Kg",      "size": 1.0,  "unit": "kg",
     "prices": {"walmart": 149.00,"soriana": 152.00,"chedraui": 146.00,"lacomer": 158.00,"bodega-aurrera": 142.00,"heb": 150.00}},
    {"name": "Jamón de pavo",     "brand": "FUD",     "cat": "Carnes",     "pres": "Paquete", "size": 500.0,"unit": "g",
     "prices": {"walmart": 68.00, "soriana": 70.00, "chedraui": 67.00, "lacomer": 72.00, "bodega-aurrera": 65.00, "heb": 69.00}},
    {"name": "Salchicha de pavo", "brand": "FUD",     "cat": "Carnes",     "pres": "Paquete", "size": 500.0,"unit": "g",
     "prices": {"walmart": 58.00, "soriana": 60.00, "chedraui": 57.00, "lacomer": 62.00, "bodega-aurrera": 55.00, "heb": 59.00}},

    # ── Frutas y verduras ──
    {"name": "Jitomate",          "brand": None,      "cat": "Verduras",   "pres": "Kg",      "size": 1.0,  "unit": "kg",
     "prices": {"walmart": 28.00, "soriana": 29.50, "chedraui": 27.00, "lacomer": 31.00, "bodega-aurrera": 25.00, "heb": 28.50}},
    {"name": "Cebolla blanca",    "brand": None,      "cat": "Verduras",   "pres": "Kg",      "size": 1.0,  "unit": "kg",
     "prices": {"walmart": 22.00, "soriana": 23.00, "chedraui": 21.00, "lacomer": 24.00, "bodega-aurrera": 19.00, "heb": 22.50}},
    {"name": "Chile serrano",     "brand": None,      "cat": "Verduras",   "pres": "Kg",      "size": 1.0,  "unit": "kg",
     "prices": {"walmart": 45.00, "soriana": 48.00, "chedraui": 43.00, "lacomer": 50.00, "bodega-aurrera": 40.00, "heb": 46.00}},
    {"name": "Limón",             "brand": None,      "cat": "Frutas",     "pres": "Kg",      "size": 1.0,  "unit": "kg",
     "prices": {"walmart": 35.00, "soriana": 37.00, "chedraui": 33.00, "lacomer": 39.00, "bodega-aurrera": 30.00, "heb": 36.00}},
    {"name": "Manzana roja",      "brand": None,      "cat": "Frutas",     "pres": "Kg",      "size": 1.0,  "unit": "kg",
     "prices": {"walmart": 48.00, "soriana": 50.00, "chedraui": 46.00, "lacomer": 52.00, "bodega-aurrera": 44.00, "heb": 49.00}},
    {"name": "Plátano",           "brand": None,      "cat": "Frutas",     "pres": "Kg",      "size": 1.0,  "unit": "kg",
     "prices": {"walmart": 20.00, "soriana": 21.00, "chedraui": 19.00, "lacomer": 22.00, "bodega-aurrera": 18.00, "heb": 20.50}},

    # ── Abarrotes ──
    {"name": "Arroz extra largo", "brand": "La Mejor","cat": "Abarrotes",  "pres": "Bolsa",   "size": 1000.0,"unit":"g",
     "prices": {"walmart": 34.00, "soriana": 35.00, "chedraui": 33.00, "lacomer": 36.00, "bodega-aurrera": 31.50, "heb": 34.50}},
    {"name": "Frijol negro",      "brand": "La Joya", "cat": "Abarrotes",  "pres": "Bolsa",   "size": 1000.0,"unit":"g",
     "prices": {"walmart": 38.00, "soriana": 39.00, "chedraui": 37.00, "lacomer": 40.00, "bodega-aurrera": 35.50, "heb": 38.50}},
    {"name": "Aceite vegetal",    "brand": "Olitalia","cat": "Abarrotes",  "pres": "Botella", "size": 1000.0,"unit":"mL",
     "prices": {"walmart": 52.00, "soriana": 54.00, "chedraui": 50.00, "lacomer": 56.00, "bodega-aurrera": 48.00, "heb": 53.00}},
    {"name": "Sal de mesa",       "brand": "La Fina", "cat": "Abarrotes",  "pres": "Caja",    "size": 1000.0,"unit":"g",
     "prices": {"walmart": 14.00, "soriana": 14.50, "chedraui": 13.50, "lacomer": 15.00, "bodega-aurrera": 13.00, "heb": 14.00}},
    {"name": "Azúcar morena",     "brand": "Zulka",   "cat": "Abarrotes",  "pres": "Bolsa",   "size": 1000.0,"unit":"g",
     "prices": {"walmart": 28.00, "soriana": 29.00, "chedraui": 27.50, "lacomer": 30.00, "bodega-aurrera": 26.00, "heb": 28.50}},
    {"name": "Pasta espagueti",   "brand": "Barilla", "cat": "Abarrotes",  "pres": "Bolsa",   "size": 500.0, "unit":"g",
     "prices": {"walmart": 22.00, "soriana": 23.00, "chedraui": 21.50, "lacomer": 24.00, "bodega-aurrera": 20.50, "heb": 22.50}},
    {"name": "Atún en agua",      "brand": "Dolores", "cat": "Abarrotes",  "pres": "Lata",    "size": 140.0, "unit":"g",
     "prices": {"walmart": 18.50, "soriana": 19.00, "chedraui": 18.00, "lacomer": 20.00, "bodega-aurrera": 17.00, "heb": 18.50}},

    # ── Bebidas ──
    {"name": "Agua purificada",   "brand": "Ciel",    "cat": "Bebidas",    "pres": "Botella", "size": 1500.0,"unit":"mL",
     "prices": {"walmart": 14.00, "soriana": 14.50, "chedraui": 13.50, "lacomer": 15.00, "bodega-aurrera": 12.50, "heb": 14.00}},
    {"name": "Refresco Coca-Cola","brand": "Coca-Cola","cat": "Bebidas",   "pres": "Botella", "size": 2000.0,"unit":"mL",
     "prices": {"walmart": 34.00, "soriana": 35.00, "chedraui": 33.00, "lacomer": 36.00, "bodega-aurrera": 32.00, "heb": 34.50}},
    {"name": "Jugo de naranja",   "brand": "Del Valle","cat": "Bebidas",   "pres": "Caja",    "size": 1000.0,"unit":"mL",
     "prices": {"walmart": 38.00, "soriana": 39.50, "chedraui": 37.00, "lacomer": 41.00, "bodega-aurrera": 35.50, "heb": 38.50}},

    # ── Café y desayuno ──
    {"name": "Café soluble",      "brand": "Nescafé", "cat": "Café",       "pres": "Frasco",  "size": 200.0, "unit":"g",
     "prices": {"walmart": 115.00,"soriana": 118.00,"chedraui": 112.00,"lacomer": 120.00,"bodega-aurrera": 109.00,"heb": 115.00}},
    {"name": "Cereal Zucaritas",  "brand": "Kellogg's","cat": "Cereales",  "pres": "Caja",    "size": 500.0, "unit":"g",
     "prices": {"walmart": 62.00, "soriana": 64.00, "chedraui": 60.00, "lacomer": 66.00, "bodega-aurrera": 58.00, "heb": 63.00}},
    {"name": "Avena tradicional", "brand": "Quaker",  "cat": "Cereales",   "pres": "Caja",    "size": 560.0, "unit":"g",
     "prices": {"walmart": 48.00, "soriana": 49.50, "chedraui": 47.00, "lacomer": 51.00, "bodega-aurrera": 45.00, "heb": 48.50}},

    # ── Limpieza ──
    {"name": "Detergente líquido","brand": "Ariel",   "cat": "Limpieza",   "pres": "Botella", "size": 1000.0,"unit":"mL",
     "prices": {"walmart": 85.00, "soriana": 87.00, "chedraui": 83.00, "lacomer": 89.00, "bodega-aurrera": 80.00, "heb": 85.00}},
    {"name": "Jabón de barra",    "brand": "Zote",    "cat": "Limpieza",   "pres": "Barra",   "size": 400.0, "unit":"g",
     "prices": {"walmart": 18.00, "soriana": 18.50, "chedraui": 17.50, "lacomer": 19.00, "bodega-aurrera": 16.50, "heb": 18.00}},
    {"name": "Suavizante de ropa","brand": "Downy",   "cat": "Limpieza",   "pres": "Botella", "size": 800.0, "unit":"mL",
     "prices": {"walmart": 58.00, "soriana": 60.00, "chedraui": 56.00, "lacomer": 62.00, "bodega-aurrera": 54.00, "heb": 59.00}},

    # ── Higiene personal ──
    {"name": "Shampoo",           "brand": "Pantene", "cat": "Higiene",    "pres": "Botella", "size": 400.0, "unit":"mL",
     "prices": {"walmart": 72.00, "soriana": 74.00, "chedraui": 70.00, "lacomer": 76.00, "bodega-aurrera": 68.00, "heb": 72.00}},
    {"name": "Pasta dental",      "brand": "Colgate", "cat": "Higiene",    "pres": "Tubo",    "size": 150.0, "unit":"mL",
     "prices": {"walmart": 38.00, "soriana": 39.00, "chedraui": 37.00, "lacomer": 40.00, "bodega-aurrera": 35.00, "heb": 38.00}},
    {"name": "Jabón de baño",     "brand": "Dove",    "cat": "Higiene",    "pres": "Barra",   "size": 90.0,  "unit":"g",
     "prices": {"walmart": 22.00, "soriana": 23.00, "chedraui": 21.00, "lacomer": 24.00, "bodega-aurrera": 20.00, "heb": 22.00}},
    {"name": "Papel higiénico",   "brand": "Kleenex", "cat": "Higiene",    "pres": "Paquete", "size": 4.0,   "unit":"rollos",
     "prices": {"walmart": 48.00, "soriana": 50.00, "chedraui": 46.00, "lacomer": 52.00, "bodega-aurrera": 44.00, "heb": 48.00}},
]


def seed(db: Session) -> None:
    if db.query(models.Chain).count() > 0:
        return  # Ya sembrado

    # Crear cadenas
    chain_map: dict[str, models.Chain] = {}
    for c in CHAINS:
        chain = models.Chain(id=str(uuid.uuid4()), slug=c["slug"], name=c["name"], color=c["color"])
        db.add(chain)
        chain_map[c["slug"]] = chain

    db.flush()

    # Crear tiendas
    for s in STORES:
        db.add(models.Store(
            id=str(uuid.uuid4()),
            chain_id=chain_map[s["chain"]].id,
            name=s["name"],
            address=s["address"],
            latitude=s["lat"],
            longitude=s["lng"],
        ))

    db.flush()

    # Crear productos y precios
    for p in PRODUCTS:
        product = models.Product(
            id=str(uuid.uuid4()),
            name=p["name"],
            brand=p.get("brand"),
            category=p.get("cat"),
            presentation=p.get("pres"),
            size=p.get("size"),
            unit=p.get("unit"),
        )
        db.add(product)
        db.flush()

        for slug, price_val in p["prices"].items():
            if slug in chain_map:
                db.add(models.Price(
                    id=str(uuid.uuid4()),
                    product_id=product.id,
                    chain_id=chain_map[slug].id,
                    price=price_val,
                ))

    db.commit()
    print("✅ Seed completado: cadenas, tiendas y productos cargados.")
