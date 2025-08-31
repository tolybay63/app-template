@echo on
setlocal enabledelayedexpansion

:: Получаем список тем Kafka
for /f "delims=" %%T in ('docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list') do (
    set "TOPIC=%%T"
    set "TOPIC=!TOPIC: =!"

    :: Пропускаем пустые строки
    if "!TOPIC!"=="" (
        echo Пропускаем пустую тему.
        goto :continue
    )

    echo Проверяем и удаляем тему: !TOPIC!

    :: Проверяем существование темы
    docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list | findstr /r /c:"^!TOPIC!$" > nul
    if !errorlevel! equ 0 (
        echo Тема найдена. Удаляем...
        docker exec kafka kafka-topics --bootstrap-server localhost:9092 --delete --topic !TOPIC!
    ) else (
        echo Тема !TOPIC! не существует. Пропускаем удаление.
    )

    :continue
)
echo Все темы обработаны.
