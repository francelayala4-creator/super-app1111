from fastapi import APIRouter, Depends
from pydantic import BaseModel
from app.deps import get_current_user
from app import models

router = APIRouter(prefix="/users", tags=["users"])

class UserOut(BaseModel):
    id: str
    email: str
    full_name: str | None = None

@router.get("/me", response_model=UserOut)
def me(user: models.User = Depends(get_current_user)):
    return UserOut(id=user.id, email=user.email, full_name=user.full_name)
