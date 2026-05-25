@echo off
setlocal

set "ROOT=%~dp0.."
pushd "%ROOT%\frontend" >nul

if not exist "package.json" (
    echo [ERROR] No se encontro frontend\package.json
    popd >nul
    exit /b 1
)

if not exist "node_modules" (
    echo Instalando dependencias Angular...
    call npm.cmd install
    if errorlevel 1 (
        echo [ERROR] npm install fallo.
        popd >nul
        exit /b 1
    )
)

echo Iniciando GAIA frontend en http://localhost:4200
echo.

call npm.cmd start
set "EXIT_CODE=%ERRORLEVEL%"

popd >nul
exit /b %EXIT_CODE%
