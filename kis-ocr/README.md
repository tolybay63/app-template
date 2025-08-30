### Настройка LibreOffice

Необходимо в `.propertie`s указать путь к данным tesseract

```
tesseract.data.path=/usr/share/tesseract-ocr/4.00/tessdata
```

**Важно!**

Для запуска переопределить не в файле `application.properties`, а в файле `application-dev.properties`.

Для запуска тестов переопределить пути под свою машину нужно не в `application-test.properties`, а в
файле `application-test-dev.properties`.

Чтобы в тестах загружался файл `application-test-dev.properties` - запускать тест с переменной
окружения `--spring.profiles.active=dev`. Сам файл `application-test-dev.properties` - не пытаться комитить (добавлен
в `.gitignore`).
