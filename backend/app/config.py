import os

SECRET_KEY = os.getenv("SECRET_KEY", "superapp-secret-key-2026-change-in-prod")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 * 24 * 7   # 7 días
REFRESH_TOKEN_EXPIRE_DAYS = 30
DATABASE_URL = os.getenv("DATABASE_URL", "sqlite:///./superapp.db")
