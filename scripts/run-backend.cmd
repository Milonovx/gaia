@echo off
setlocal

set "ROOT=%~dp0.."
pushd "%ROOT%\backend" >nul

if not exist "pom.xml" (
    echo [ERROR] No se encontro backend\pom.xml
    popd >nul
    exit /b 1
)

if not exist "uploads" (
    mkdir "uploads"
)

echo Iniciando GAIA backend en https://localhost:8443
echo Swagger: https://localhost:8443/swagger-ui/index.html
echo.

call mvnw.cmd spring-boot:run
set "EXIT_CODE=%ERRORLEVEL%"

popd >nul
exit /b %EXIT_CODE%
