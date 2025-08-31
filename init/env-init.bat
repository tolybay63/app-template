@echo off
setlocal enabledelayedexpansion



rem Инициализация сторонних сервисов



echo -------
echo app-keycloak

cd app-keycloak

call init.bat

cd ..



echo -------
echo app-database

cd app-database

call init.bat

cd ..



echo -------
echo app-storage

cd app-storage

call init.bat

cd ..



echo -------
echo app-messagebroker

cd app-messagebroker

call init.bat

cd ..



rem Инициализация бизнес-логики



echo -------
echo app-database

cd app-database

rem Создаёт структуру БД
call gradlew bootRun

cd ..


