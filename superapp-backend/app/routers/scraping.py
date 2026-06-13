from fastapi import APIRouter, Depends
from pydantic import BaseModel
from app.deps import get_current_user

router = APIRouter(prefix="/scraping", tags=["scraping"])

class RunScrapingIn(BaseModel):
    chains: list[str] = []

class ScrapingJobOut(BaseModel):
    id: str
    chain: str
    status: str
    started_at: str | None = None
    finished_at: str | None = None
    products_updated: int = 0


@router.post("/run")
def run_scraping(body: RunScrapingIn = RunScrapingIn(), _=Depends(get_current_user)):
    return {"status": "queued", "message": "Scraping programado. Los precios se actualizarán en los próximos minutos."}


@router.get("/jobs", response_model=list[ScrapingJobOut])
def scraping_jobs(_=Depends(get_current_user)):
    return []
