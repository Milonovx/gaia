@echo off
setlocal

set "ROOT=%~dp0.."

if not exist "%ROOT%\backend\pom.xml" (
    echo [ERROR] No se encontro backend\pom.xml
    exit /b 1
)

if not exist "%ROOT%\frontend\package.json" (
    echo [ERROR] No se encontro frontend\package.json
    exit /b 1
)

echo Iniciando GAIA backend y frontend...
echo Backend:  https://localhost:8443
echo Frontend: http://localhost:4200
echo.

start "GAIA Backend" cmd /k ""%ROOT%\scripts\run-backend.cmd""
start "GAIA Frontend" cmd /k ""%ROOT%\scripts\run-frontend.cmd""

exit /b 0
