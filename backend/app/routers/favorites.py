import uuid
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy.orm import Session
from app.database import get_db
from app.deps import get_current_user
from app import models

router = APIRouter(prefix="/favorites", tags=["favorites"])

class FavIn(BaseModel):
    product_id: str

class FavOut(BaseModel):
    id: str
    product_id: str


@router.get("", response_model=list[FavOut])
def list_favorites(db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    favs = db.query(models.Favorite).filter(models.Favorite.user_id == user.id).all()
    return [FavOut(id=f.id, product_id=f.product_id) for f in favs]


@router.post("", response_model=FavOut, status_code=201)
def add_favorite(body: FavIn, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    existing = db.query(models.Favorite).filter(
        models.Favorite.user_id == user.id,
        models.Favorite.product_id == body.product_id,
    ).first()
    if existing:
        return FavOut(id=existing.id, product_id=existing.product_id)
    fav = models.Favorite(id=str(uuid.uuid4()), user_id=user.id, product_id=body.product_id)
    db.add(fav)
    db.commit()
    db.refresh(fav)
    return FavOut(id=fav.id, product_id=fav.product_id)


@router.delete("/{product_id}", status_code=204)
def remove_favorite(product_id: str, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    fav = db.query(models.Favorite).filter(
        models.Favorite.user_id == user.id,
        models.Favorite.product_id == product_id,
    ).first()
    if not fav:
        raise HTTPException(404, "Favorito no encontrado")
    db.delete(fav)
    db.commit()
