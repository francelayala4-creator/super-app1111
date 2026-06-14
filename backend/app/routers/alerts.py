import uuid
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy.orm import Session
from app.database import get_db
from app.deps import get_current_user
from app import models

router = APIRouter(prefix="/alerts", tags=["alerts"])

class AlertIn(BaseModel):
    product_id: str
    target_price: float
    type: str = "price_drop"

class AlertOut(BaseModel):
    id: str
    product_id: str
    target_price: float
    type: str
    is_active: bool


@router.get("", response_model=list[AlertOut])
def list_alerts(db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    alerts = db.query(models.Alert).filter(models.Alert.user_id == user.id).all()
    return [AlertOut(id=a.id, product_id=a.product_id, target_price=a.target_price,
                     type=a.type, is_active=a.is_active) for a in alerts]


@router.post("", response_model=AlertOut, status_code=201)
def create_alert(body: AlertIn, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    alert = models.Alert(
        id=str(uuid.uuid4()),
        user_id=user.id,
        product_id=body.product_id,
        target_price=body.target_price,
        type=body.type,
    )
    db.add(alert)
    db.commit()
    db.refresh(alert)
    return AlertOut(id=alert.id, product_id=alert.product_id, target_price=alert.target_price,
                    type=alert.type, is_active=alert.is_active)
