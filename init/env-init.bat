@echo off
setlocal enabledelayedexpansion



rem Инициализация сторонних сервисов



echo -------
echo kis-keycloak

cd kis-keycloak

call init.bat

cd ..



echo -------
echo kis-base

cd kis-base

call init.bat

cd ..



echo -------
echo kis-storage

cd kis-storage

call init.bat

cd ..



echo -------
echo kis-indexer

cd kis-indexer

call init.bat

cd ..



echo -------
echo kis-messagebroker

cd kis-messagebroker

call init.bat

cd ..



rem Инициализация бизнес-логики



echo -------
echo kis-base

cd kis-base

rem Создаёт структуру БД
call gradlew bootRun

cd ..



echo -------
echo kis-events

cd kis-events

call init.bat

cd ..



echo -------
echo kis-redis

cd kis-redis

call init.bat

cd ..




