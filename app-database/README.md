# Модуль app-database

Модуль, который следит за структурой БД.

## Подготовка баз данных Postgres

Перед запуском приложений в Postgres должна быть созданы:

- БД `app_files` и создан пользователь `app_files` с паролем `123456`.
- БД `app_pages` и создан пользователь `app_pages` с паролем `123456`.

Предполагается, что эти базы будут на разных серверах, поэтому в примере используются
разные порты (`5432` и `5433`). Выполните команду:

```shell
psql -U postgres -h localhost -p 5432 -a -f app-database/init-db-files.sql
psql -U postgres -h localhost -p 5433 -a -f app-database/init-db-pages.sql
```

На запрос пароля укажите `111` (такой пароль указан в образе `app-postgres`,
см. [Dockerfile](docker-images/app-postgres/Dockerfile)), либо свой, если у вас Postgres
не в контейнере.

База данных и пользователь должны успешно создастся:

```
DROP DATABASE app_files;
DROP DATABASE
DROP USER app_files;
DROP ROLE
CREATE USER app_files WITH PASSWORD '123456';
CREATE ROLE
CREATE DATABASE app_files WITH OWNER app_files;
CREATE DATABASE
```

```
DROP DATABASE app_pages;
DROP DATABASE
DROP USER app_pages;
DROP ROLE
CREATE USER app_pages WITH PASSWORD '123456';
CREATE ROLE
CREATE DATABASE app_pages WITH OWNER app_pages;
CREATE DATABASE
```

## Получить скрипт на создание БД

```shell
./gradlew generate-sql
```

В файле `app-database/build/generated-sql/liquibase-update.sql` будет скрипт на создание БД.
ВАЖНО: база `app_files` должна быть пустая, без таблиц, иначе Liquibase сгенерит
только пустую разницу (см. [README.md](../README.md) проекта,
раздел `Инициализация окружения`). 
                                                    