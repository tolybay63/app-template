@echo off
setlocal enabledelayedexpansion

:: Get the list of Kafka topics
for /f "delims=" %%t in ('docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list') do (
    set TOPIC=%%t
    :: Skip empty topics
    if "!TOPIC!"=="" (
        echo Skipping empty topic.
        goto :continue
    )

    echo Deleting topic: !TOPIC!

    :: Check if the topic exists and delete it
    docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list | findstr /b /l "!TOPIC!" >nul
    if !errorlevel! equ 0 (
        docker exec kafka kafka-topics --bootstrap-server localhost:9092 --delete --topic "!TOPIC!"
    ) else (
        echo Topic !TOPIC! does not exist, skipping delete.
    )

    :continue
)

echo All topics processed.
endlocal
