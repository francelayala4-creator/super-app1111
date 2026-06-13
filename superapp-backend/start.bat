@echo off
cd /d "%~dp0"
echo.
echo ========================================
echo   SuperApp Backend v1.0
echo ========================================
echo.

REM La DB se conserva entre reinicios para no perder usuarios y datos

REM Instalar dependencias si no existen
python -c "import fastapi" 2>nul || (
    echo Instalando dependencias...
    pip install -r requirements.txt
)

REM Verificar integridad de tablas en la DB
echo Verificando tablas de la base de datos...
python -c "
from app.database import engine
from app import models
import sqlite3, os

db_path = 'superapp.db'
tablas_esperadas = [
    'users', 'chains', 'stores', 'products',
    'shopping_lists', 'shopping_list_items',
    'favorites', 'price_alerts', 'quote_history'
]

if not os.path.exists(db_path):
    print('  DB no existe, se creara nueva...')
else:
    conn = sqlite3.connect(db_path)
    cur = conn.cursor()
    cur.execute(\"SELECT name FROM sqlite_master WHERE type='table'\")
    tablas_existentes = [r[0] for r in cur.fetchall()]
    conn.close()
    faltantes = [t for t in tablas_esperadas if t not in tablas_existentes]
    if faltantes:
        print(f'  ADVERTENCIA: tablas faltantes: {faltantes}')
    else:
        print(f'  OK: {len(tablas_existentes)} tablas verificadas')

models.Base.metadata.create_all(bind=engine)
print('  Tablas sincronizadas con modelos')
"
if errorlevel 1 (
    echo ERROR: Fallo al verificar la base de datos
    pause
    exit /b 1
)

echo Iniciando servidor en http://localhost:8000
echo Docs en http://localhost:8000/docs
echo.
echo Presiona Ctrl+C para detener el servidor.
echo.
python main.py
