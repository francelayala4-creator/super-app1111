param(
    [Parameter(Mandatory=$true)]
    [string]$GitHubUser,
    [Parameter(Mandatory=$true)]
    [string]$RepoName
)

$ErrorActionPreference = "Stop"
$MonorepoPath = "C:\dev\superapp"

Write-Host ""
Write-Host "  ==========================================" -ForegroundColor Cyan
Write-Host "    SuperApp - Creando monorepo en GitHub" -ForegroundColor Cyan
Write-Host "  ==========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/6] Verificando git..." -ForegroundColor Yellow
git --version | Out-Null
Write-Host "      git OK" -ForegroundColor Green

Write-Host "[2/6] Creando estructura C:\dev\superapp..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path "$MonorepoPath\backend" | Out-Null
New-Item -ItemType Directory -Force -Path "$MonorepoPath\android" | Out-Null

Write-Host "[3/6] Copiando backend..." -ForegroundColor Yellow
robocopy "C:\dev\superapp-backend" "$MonorepoPath\backend" /E /NFL /NDL /NJH /NJS /XD __pycache__ .git venv env .venv /XF "*.db" "*.db-journal" "*.db-wal" "*.db-shm" "*.pyc" "*.log" "setup-github.ps1" | Out-Null
Write-Host "      Backend copiado OK" -ForegroundColor Green

Write-Host "[4/6] Copiando Android..." -ForegroundColor Yellow
robocopy "C:\dev\superapp-android" "$MonorepoPath\android" /E /NFL /NDL /NJH /NJS /XD .gradle build .git .idea /XF "*.apk" "*.aab" "*.log" "local.properties" | Out-Null
Write-Host "      Android copiado OK" -ForegroundColor Green

Write-Host "[5/6] Creando .gitignore y README..." -ForegroundColor Yellow

$gitignore = @'
# Python
__pycache__/
*.py[cod]
*.db
*.db-journal
*.db-wal
.env
venv/
.venv/

# Android
*.apk
*.aab
.gradle/
build/
local.properties
*.keystore
*.jks

# IDEs
.idea/
.vscode/
*.iml

# OS
.DS_Store
Thumbs.db
'@
$gitignore | Set-Content "$MonorepoPath\.gitignore" -Encoding UTF8

$readme = @'
# SuperApp

Comparador de precios de supermercados para Queretaro, Mexico.

## Estructura

- android/  - App Android (Jetpack Compose)
- backend/  - API REST (FastAPI + SQLAlchemy)

## Iniciar el backend

cd backend
python main.py

## Tunnel Cloudflare

cloudflared tunnel run superapp-backend
'@
$readme | Set-Content "$MonorepoPath\README.md" -Encoding UTF8

Write-Host "      Archivos creados OK" -ForegroundColor Green

Write-Host "[6/6] Inicializando git y haciendo primer commit..." -ForegroundColor Yellow
Set-Location $MonorepoPath
git init
git add .
git commit -m "feat: initial commit - SuperApp Android + Backend"

$RemoteUrl = "https://github.com/$GitHubUser/$RepoName.git"
Write-Host "  Remote: $RemoteUrl" -ForegroundColor White
git remote add origin $RemoteUrl
git branch -M main
git push -u origin main

Write-Host ""
Write-Host "  Listo! https://github.com/$GitHubUser/$RepoName" -ForegroundColor Green
Write-Host ""
