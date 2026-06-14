@echo off
cd /d "%~dp0"
echo.
echo ========================================
echo   SuperApp Backend v1.0
echo ========================================
echo.

REM Limpiar DB corrupta si existe (generada en sandbox)
if exist superapp.db (
    echo Limpiando base de datos anterior...
    del /f /q superapp.db 2>nul
    del /f /q superapp.db-journal 2>nul
    del /f /q superapp.db-wal 2>nul
)

REM Instalar dependencias si no existen
python -c "import fastapi" 2>nul || (
    echo Instalando dependencias...
    pip install -r requirements.txt
)

echo Iniciando servidor en http://localhost:8000
echo Docs en http://localhost:8000/docs
echo.
echo Presiona Ctrl+C para detener el servidor.
echo.
python main.py
