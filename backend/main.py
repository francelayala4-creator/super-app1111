import traceback
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.database import engine, SessionLocal
from app import models
from app.seed import seed
from app.routers import auth, users, chains, lists, search, quote, favorites, alerts, history, scraping

# Crear tablas
try:
    models.Base.metadata.create_all(bind=engine)
    print("✅ Tablas creadas/verificadas")
except Exception as e:
    print(f"❌ Error creando tablas: {e}")
    traceback.print_exc()

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup
    try:
        db = SessionLocal()
        seed(db)
        db.close()
    except Exception as e:
        print(f"❌ Error en seed: {e}")
        traceback.print_exc()
    yield
    # Shutdown (nada que hacer)

app = FastAPI(
    title="SuperApp API",
    version="1.0.0",
    description="Backend de SUPERAPP — comparador de precios de supermercados",
    lifespan=lifespan,
    debug=True,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def root():
    return {"ok": True, "message": "SuperApp API v1.0 corriendo"}

@app.get("/health")
def health():
    return {"status": "healthy"}

PREFIX = "/api/v1"
app.include_router(auth.router,      prefix=PREFIX)
app.include_router(users.router,     prefix=PREFIX)
app.include_router(chains.router,    prefix=PREFIX)
app.include_router(lists.router,     prefix=PREFIX)
app.include_router(search.router,    prefix=PREFIX)
app.include_router(quote.router,     prefix=PREFIX)
app.include_router(favorites.router, prefix=PREFIX)
app.include_router(alerts.router,    prefix=PREFIX)
app.include_router(history.router,   prefix=PREFIX)
app.include_router(scraping.router,  prefix=PREFIX)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
