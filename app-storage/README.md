# Модуль app-storage

## Первичная инициализация MinIO (вручную через командную строку)

Выполняется при запущенном контейнере MinIO.

### Установка MinIO-клиента

Выполняем команды:

```shell
wget -O minioc https://dl.min.io/client/mc/release/linux-amd64/mc
chmod +x minioc
sudo mv minioc /usr/local/bin/
```

### Настройка соединения

Выполняем:

```shell
minioc alias set app-files http://localhost:9000 minioadmin minioadmin
minioc alias set app-pages http://localhost:8000 minioadmin minioadmin
```

Видим:

```
...
Added `app-files` successfully.
```

### Создание bucket-ов `files`, `pages` и `pages-preview`

Выполняем:

```shell
minioc mb app-files/files
minioc mb app-pages/pages
minioc mb app-pages/pages-preview
```

Видим:

```
...
Bucket created successfully `app-files/files`. 
```

Проверяем, что bucket появился в списке bucket'ов:

```shell
minioc ls app-files
minioc ls app-pages
```

Видим:

```
[2024-10-21 18:48:03 +05]     0B files/  
```

## Первичная инициализация MinIO (вручную через веб-интерфейс)

Создайте необходимые бакеты:

- `files` — для хранения загруженных файлов.
- `pages` — для хранения изображений для предварительного просмотра.
- `pages-preview` — для хранения уменьшенных изображений.

1. Откройте интерфейс MinioAdmin в браузере по
   адресу [http://localhost:9001](http://localhost:9001).

2. Войдите в систему, используя учетные данные:
    - **Логин**: `minioadmin`
    - **Пароль**: `minioadmin`

3. Создайте бакет `files`.

4. Откройте интерфейс MinioAdmin в браузере по
   адресу [http://localhost:8001](http://localhost:8001).

5. Войдите в систему, используя учетные данные:
    - **Логин**: `minioadmin`
    - **Пароль**: `minioadmin`

6. Создайте бакет `preview` и `pages-preview`