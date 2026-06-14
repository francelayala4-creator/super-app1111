from fastapi import APIRouter, Depends, Query
from pydantic import BaseModel
from sqlalchemy.orm import Session
from app.database import get_db
from app.deps import get_current_user
from app import models
import math

router = APIRouter(tags=["chains"])

class ChainOut(BaseModel):
    id: str
    slug: str
    name: str
    color: str | None = None
    logo_url: str | None = None

class StoreOut(BaseModel):
    id: str
    chain_id: str
    name: str
    address: str | None = None
    latitude: float
    longitude: float
    distance_km: float | None = None


def haversine(lat1, lon1, lat2, lon2) -> float:
    R = 6371.0
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat/2)**2 + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon/2)**2
    return R * 2 * math.asin(math.sqrt(a))


@router.get("/chains", response_model=list[ChainOut])
def list_chains(db: Session = Depends(get_db), _=Depends(get_current_user)):
    return [ChainOut(id=c.id, slug=c.slug, name=c.name, color=c.color, logo_url=c.logo_url)
            for c in db.query(models.Chain).all()]


@router.get("/stores/nearby", response_model=list[StoreOut])
def nearby_stores(
    lat: float = Query(...),
    lng: float = Query(...),
    limit: int = Query(20),
    db: Session = Depends(get_db),
    _=Depends(get_current_user),
):
    stores = db.query(models.Store).all()
    result = []
    for s in stores:
        dist = haversine(lat, lng, s.latitude, s.longitude)
        result.append(StoreOut(
            id=s.id, chain_id=s.chain_id, name=s.name,
            address=s.address, latitude=s.latitude, longitude=s.longitude,
            distance_km=round(dist, 2),
        ))
    result.sort(key=lambda x: x.distance_km or 999)
    return result[:limit]
