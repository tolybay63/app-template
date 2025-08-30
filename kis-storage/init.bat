@echo off

rem Проверяем наличие MinIO-клиента
mc.exe -v

rem Загрузка MinIO-клиента
if %ERRORLEVEL% neq 0 (
  echo "Загрузка MinIO-клиента"
  curl -o mc.exe https://dl.min.io/client/mc/release/windows-amd64/mc.exe
  echo MinIO-клиент установлен
) else (
  echo MinIO-клиент уже установлен
)


echo Настройка соединения

mc.exe alias set kis-files http://localhost:9000 minioadmin minioadmin
mc.exe alias set kis-pages http://localhost:8000 minioadmin minioadmin



echo ""
echo Удаление существующих bucket-ов

rem Удаление всех объектов внутри bucket
mc.exe rm --recursive --force kis-files/files
mc.exe rm --recursive --force kis-pages/pages
mc.exe rm --recursive --force kis-pages/pages-preview
rem  Удаление самого bucket
mc.exe rb kis-files/files
mc.exe rb kis-pages/pages
mc.exe rb kis-pages/pages-preview



echo ""
echo Создание bucket-ов

mc.exe mb kis-files/files
mc.exe mb kis-pages/pages
mc.exe mb kis-pages/pages-preview



echo ""
echo Список bucket-ов

mc.exe ls kis-files
mc.exe ls kis-pages

