# Модуль kis-base

Модуль, который следит за структурой БД.

## Подготовка баз данных Postgres

Перед запуском приложений в Postgres должна быть созданы:

- БД `kis_smart_catalog` и создан пользователь `kis_smart_catalog` с паролем `123456`.
- БД `kis_pages` и создан пользователь `kis_pages` с паролем `123456`.

Предполагается, что эти базы будут на разных серверах, поэтому в примере используются
разные порты (`5432` и `5433`). Выполните команду:

```shell
psql -U postgres -h localhost -p 5432 -a -f kis-base/init-db-files.sql
psql -U postgres -h localhost -p 5433 -a -f kis-base/init-db-pages.sql
```

На запрос пароля укажите `111` (такой пароль указан в образе `kis-postgres`,
см. [Dockerfile](docker-images/kis-postgres/Dockerfile)), либо свой, если у вас Postgres
не в контейнере.

База данных и пользователь должны успешно создастся:

```
DROP DATABASE kis_smart_catalog;
DROP DATABASE
DROP USER kis_smart_catalog;
DROP ROLE
CREATE USER kis_smart_catalog WITH PASSWORD '123456';
CREATE ROLE
CREATE DATABASE kis_smart_catalog WITH OWNER kis_smart_catalog;
CREATE DATABASE
```

```
DROP DATABASE kis_pages;
DROP DATABASE
DROP USER kis_pages;
DROP ROLE
CREATE USER kis_pages WITH PASSWORD '123456';
CREATE ROLE
CREATE DATABASE kis_pages WITH OWNER kis_pages;
CREATE DATABASE
```

## Получить скрипт на создание БД

```shell
./gradlew generate-sql
```

В файле `kis-base/build/generated-sql/liquibase-update.sql` будет скрипт на создание БД.
ВАЖНО: база `kis_smart_catalog` должна быть пустая, без таблиц, иначе Liquibase сгенерит
только пустую разницу (см. [README.md](../README.md) проекта,
раздел `Инициализация окружения`). 
                                                    