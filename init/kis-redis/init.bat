@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

:: Check if Docker is installed
docker --version >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo Docker is not installed. Please install Docker and try again.
    exit /b 1
)

:: Check if Redis container is running
docker ps --filter "name=redis" --format "{{.Names}}" | findstr /C:"redis" >nul
IF %ERRORLEVEL% NEQ 0 (
    echo Starting Redis container...
    docker run -d --name redis -p 6379:6379 redis:7.2.0
) ELSE (
    echo Redis container is already running.
)

:: Flush Redis data
echo Flushing all Redis databases...
docker exec redis redis-cli FLUSHALL
IF %ERRORLEVEL% NEQ 0 (
    echo Failed to flush Redis.
    exit /b 1
)
echo Redis flushed successfully.

:: Verify Redis is empty
FOR /F "tokens=*" %%i IN ('docker exec redis redis-cli DBSIZE') DO SET KEYS_COUNT=%%i
IF "!KEYS_COUNT!"=="0" (
    echo Redis is now empty.
) ELSE (
    echo Redis still contains data. Something went wrong!
    exit /b 1
)

exit /b 0
