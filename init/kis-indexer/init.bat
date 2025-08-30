@echo off
setlocal enabledelayedexpansion

echo Удаляем старый индекс
curl -X DELETE "localhost:9200/file-index"
echo.

echo Создаем новый индекс
curl -X PUT "localhost:9200/file-index" -H "Content-Type: application/json" --data-binary "@./init-file-index.json"
echo.

echo Проверяем индекс
curl -X GET "http://localhost:9200"
echo.
