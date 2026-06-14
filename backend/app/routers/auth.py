import traceback
from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel
from sqlalchemy.orm import Session
from app.database import get_db
from app import models
from app.auth import hash_password, verify_password, create_access_token, create_refresh_token
import uuid

router = APIRouter(prefix="/auth", tags=["auth"])

class RegisterIn(BaseModel):
    email: str
    password: str
    full_name: str | None = None

class LoginIn(BaseModel):
    email: str
    password: str

class TokenPair(BaseModel):
    access_token: str
    refresh_token: str
    token_type: str = "bearer"


@router.post("/register", response_model=TokenPair, status_code=status.HTTP_201_CREATED)
def register(body: RegisterIn, db: Session = Depends(get_db)):
    try:
        existing = db.query(models.User).filter(models.User.email == body.email).first()
        if existing:
            raise HTTPException(status_code=400, detail="Email ya registrado")
        print(f"[register] Hashing password...")
        pw_hash = hash_password(body.password)
        print(f"[register] Hash OK, creando usuario...")
        user = models.User(
            id=str(uuid.uuid4()),
            email=body.email,
            password_hash=pw_hash,
            full_name=body.full_name,
        )
        db.add(user)
        db.commit()
        db.refresh(user)
        print(f"[register] Usuario creado: {user.id}")
        return TokenPair(
            access_token=create_access_token(user.id),
            refresh_token=create_refresh_token(user.id),
        )
    except HTTPException:
        raise
    except Exception as e:
        print(f"[register] ERROR: {e}")
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/login", response_model=TokenPair)
def login(body: LoginIn, db: Session = Depends(get_db)):
    user = db.query(models.User).filter(models.User.email == body.email).first()
    if not user or not verify_password(body.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Credenciales incorrectas")
    return TokenPair(
        access_token=create_access_token(user.id),
        refresh_token=create_refresh_token(user.id),
    )
