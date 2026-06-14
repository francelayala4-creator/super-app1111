from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy.orm import Session
from app.database import get_db
from app.deps import get_current_user
from app import models
import uuid

router = APIRouter(prefix="/shopping-lists", tags=["lists"])


class ItemIn(BaseModel):
    raw_name: str
    product_id: str | None = None
    quantity: float = 1.0
    unit: str | None = None

class ItemOut(BaseModel):
    id: str
    raw_name: str
    product_id: str | None = None
    quantity: float = 1.0
    unit: str | None = None

class ListIn(BaseModel):
    name: str
    notes: str | None = None
    items: list[ItemIn] = []

class ListOut(BaseModel):
    id: str
    name: str
    notes: str | None = None
    items: list[ItemOut] = []


def _item_out(item: models.ShoppingListItem) -> ItemOut:
    return ItemOut(id=item.id, raw_name=item.raw_name, product_id=item.product_id,
                   quantity=item.quantity, unit=item.unit)

def _list_out(lst: models.ShoppingList) -> ListOut:
    return ListOut(id=lst.id, name=lst.name, notes=lst.notes,
                   items=[_item_out(i) for i in lst.items])


@router.get("", response_model=list[ListOut])
def my_lists(db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    lists = db.query(models.ShoppingList).filter(models.ShoppingList.user_id == user.id).all()
    return [_list_out(l) for l in lists]


@router.post("", response_model=ListOut, status_code=201)
def create_list(body: ListIn, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    lst = models.ShoppingList(id=str(uuid.uuid4()), user_id=user.id, name=body.name, notes=body.notes)
    db.add(lst)
    db.flush()
    for item in body.items:
        db.add(models.ShoppingListItem(
            id=str(uuid.uuid4()), list_id=lst.id,
            raw_name=item.raw_name, product_id=item.product_id,
            quantity=item.quantity, unit=item.unit,
        ))
    db.commit()
    db.refresh(lst)
    return _list_out(lst)


@router.get("/{list_id}", response_model=ListOut)
def get_list(list_id: str, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    lst = db.query(models.ShoppingList).filter(
        models.ShoppingList.id == list_id,
        models.ShoppingList.user_id == user.id,
    ).first()
    if not lst:
        raise HTTPException(status_code=404, detail="Lista no encontrada")
    return _list_out(lst)


@router.post("/{list_id}/items", response_model=ItemOut, status_code=201)
def add_item(list_id: str, body: ItemIn, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    lst = db.query(models.ShoppingList).filter(
        models.ShoppingList.id == list_id,
        models.ShoppingList.user_id == user.id,
    ).first()
    if not lst:
        raise HTTPException(status_code=404, detail="Lista no encontrada")
    item = models.ShoppingListItem(
        id=str(uuid.uuid4()), list_id=list_id,
        raw_name=body.raw_name, product_id=body.product_id,
        quantity=body.quantity, unit=body.unit,
    )
    db.add(item)
    db.commit()
    db.refresh(item)
    return _item_out(item)


@router.delete("/{list_id}/items/{item_id}", status_code=204)
def delete_item(list_id: str, item_id: str, db: Session = Depends(get_db), user: models.User = Depends(get_current_user)):
    lst = db.query(models.ShoppingList).filter(
        models.ShoppingList.id == list_id,
        models.ShoppingList.user_id == user.id,
    ).first()
    if not lst:
        raise HTTPException(status_code=404, detail="Lista no encontrada")
    item = db.query(models.ShoppingListItem).filter(
        models.ShoppingListItem.id == item_id,
        models.ShoppingListItem.list_id == list_id,
    ).first()
    if not item:
        raise HTTPException(status_code=404, detail="Item no encontrado")
    db.delete(item)
    db.commit()
