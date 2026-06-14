#!/bin/bash
# SuperApp — Launcher para macOS
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CF_TOKEN="eyJhIjoiZmIxNzkyYjdjMmJkZGYyMTZlZmUyZDQ3N2M3ZTUzZTYiLCJzIjoiTkRObU9ERXlPV0V0TWpnMk1DMDBNemt4TFRoa01qRXRNR000TVRrd00yWTNPVEkzIiwidCI6ImUyMzUyYWNlLWNhNWEtNDZiYS05MzNiLTZkM2U4NDAwMTdmNyJ9"

GREEN="\033[0;32m"
CYAN="\033[0;36m"
YELLOW="\033[1;33m"
RED="\033[0;31m"
NC="\033[0m"

echo ""
echo "  =========================================="
echo "    SUPERAPP — Iniciando todos los servicios"
echo "  =========================================="
echo ""

# ── 1. Matar procesos viejos en puerto 8000 ────────────────────────────────
echo -e "${CYAN}[1/5] Limpiando procesos anteriores en puerto 8000...${NC}"
PIDS=$(lsof -ti tcp:8000 2>/dev/null || true)
if [ -n "$PIDS" ]; then
    echo "      Terminando PID(s): $PIDS"
    echo "$PIDS" | xargs kill -9 2>/dev/null || true
else
    echo "      Sin procesos previos"
fi

# ── 2. Verificar Python ────────────────────────────────────────────────────
echo -e "${CYAN}[2/5] Verificando Python...${NC}"
if ! command -v python3 &>/dev/null; then
    echo -e "${RED}  ERROR: python3 no encontrado.${NC}"
    echo "  Instálalo desde https://python.org o con: brew install python"
    exit 1
fi
echo "      $(python3 --version) OK"

# ── 3. Verificar / instalar dependencias ──────────────────────────────────
echo -e "${CYAN}[3/5] Verificando dependencias...${NC}"
cd "$SCRIPT_DIR"
if ! python3 -c "import fastapi, sqlalchemy, jose" &>/dev/null; then
    echo "      Instalando dependencias..."
    pip3 install -r requirements.txt --quiet
    echo "      Dependencias instaladas OK"
else
    echo "      Dependencias OK"
fi

# ── 4. Verificar cloudflared ───────────────────────────────────────────────
echo -e "${CYAN}[4/5] Verificando cloudflared...${NC}"
if ! command -v cloudflared &>/dev/null; then
    echo -e "${YELLOW}  cloudflared no encontrado. Intentando instalar con Homebrew...${NC}"
    if command -v brew &>/dev/null; then
        brew install cloudflared
    else
        echo -e "${RED}  ERROR: Instala cloudflared manualmente:${NC}"
        echo "  https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/downloads/"
        exit 1
    fi
fi
echo "      $(cloudflared --version 2>&1 | head -1) OK"

# ── 5. Limpiar pycache ─────────────────────────────────────────────────────
echo -e "${CYAN}[5/5] Limpiando cache de Python...${NC}"
find "$SCRIPT_DIR" -type d -name "__pycache__" -exec rm -rf {} + 2>/dev/null || true
echo "      Cache limpio"

echo ""
echo "  ──────────────────────────────────────────"
echo "   Levantando servicios..."
echo "  ──────────────────────────────────────────"
echo ""

# ── Backend (nueva ventana de Terminal) ───────────────────────────────────
echo -e "${GREEN}  Iniciando backend en http://localhost:8000 ...${NC}"
osascript -e "tell application \"Terminal\"
    do script \"echo -e '\\\\033[0;36m=== SuperApp Backend ===\\\\033[0m' && cd '$SCRIPT_DIR' && python3 main.py\"
    set custom title of front window to \"SuperApp Backend\"
end tell"

# Esperar a que el backend responda (máx 15 seg)
echo -n "      Esperando backend"
for i in $(seq 1 15); do
    sleep 1
    if curl -s http://localhost:8000/health &>/dev/null; then
        echo -e " ✓ (${i}s)"
        break
    fi
    echo -n "."
    if [ "$i" -eq 15 ]; then
        echo -e "\n${YELLOW}  ADVERTENCIA: Backend tardó más de lo esperado — verifica la ventana Terminal${NC}"
    fi
done

# ── Cloudflare Tunnel (nueva ventana de Terminal) ─────────────────────────
echo -e "${GREEN}  Iniciando túnel → https://api.skyguardiansmexico.com ...${NC}"
osascript -e "tell application \"Terminal\"
    do script \"echo -e '\\\\033[0;35m=== SuperApp Tunnel ===\\\\033[0m' && cloudflared tunnel run --token $CF_TOKEN\"
    set custom title of front window to \"SuperApp Tunnel\"
end tell"

echo ""
echo -e "${GREEN}  =========================================="
echo "   Todo corriendo."
echo "   Cierra las ventanas Terminal para detener."
echo -e "  ==========================================${NC}"
echo ""
echo "   Backend:  http://localhost:8000/docs"
echo "   API pub:  https://api.skyguardiansmexico.com"
echo ""
