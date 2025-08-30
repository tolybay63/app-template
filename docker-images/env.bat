@echo off

cd ..

if not exist "_office_shared" (
    mkdir "_office_shared"
)

set OFFICE_SHARED=%cd%/_office_shared

cd docker-images