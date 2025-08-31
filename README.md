# KIS Smart Catalog

Программный продукт KIS Smart Catalog — это комплексное решение для хранения, обработки, анализа и поиска документов.

Проект kis-smartcatalog — это общий проект, состоит из нескольких микросервисов, каждый из которых выполняет свою роль.
Например: индексация и поиск данных через Elasticsearch.

## Модули

Некоторые модули являются отдельными микросервисами, некоторые - общие модули или библиотеки. Проект включает следующие
модули:

- `kis-core` — общая базовая функциональность.
- `kis-dbtools` — библиотека для работы с базой данных. Подробнее см. [kis-dbtools/README.md](kis-dbtools/README.md).
- `kis-base` — предоставляет управление структурой базы данных. Подробнее см. [kis-base/README.md](kis-base/README.md)
- `kis-auth` — отвечает за аутентификацию и авторизацию пользователей.
- `kis-messagebroker` — управляет сообщениями в Kafka.
- `kis-storage` — модуль для работы с MinIO, который хранит файлы.
- `kis-web` — подпроект Quasar, web-фронт проекта.

## Требования к среде разработчика

### Для сборки и запуска

Для запуска всех модулей вам потребуются:

- **Java 21** или выше
- **Gradle 8.10** (для сборки и запуска проектов)
- **Docker** и **Docker Compose** (для запуска сторонних решений в контейнерах)

Дополнительно под Windows 
- **jq** утилита (скачиваем с https://jqlang.org, делаем jq доступной в путях)             

### Для работы приложения

В контейнерах (для их запуска используется Docker Compose) запускаются сторонние сервисы:

- **Kafka**
- **Zookeeper**
- **MinIO**
- **Elasticsearch**
- **Postgres**

Для удобства администрирования (рекомендуется пользоваться) также запускаются:

- **Kibana** (для Elasticsearch)
- **MinioAdmin** (для MinIO)

## Разворачивание проекта у разработчика

### 1. Репозиторий

Склонируйте проект на ваш локальный компьютер:

```bash
git clone https://github.com/your-repo/kis-smartcatalog.git
cd kis-smartcatalog
```

### 2. Установка инструментов

#### Java

Установите 21 и выше.

#### Gradle

Linux:

Скачиваем дистрибутив с https://gradle.org/releases, распаковываем в `/opt/gradle`:

```
mkdir /opt/gradle
unzip -d /opt/gradle gradle-8.10.2-bin.zip
```

Прописываем PATH:

```
export PATH=$PATH:/opt/gradle/gradle-8.10.2/bin
```

Проверяем версию Gradle:

```shell
gradle -v
```

Должны увидеть:

```
------------------------------------------------------------
Gradle 8.10.2
------------------------------------------------------------
...
```

#### Docker

Linux:

```
sudo apt-get install docker
sudo apt-get install docker-compose
```

### 3. Сторонние сервисы

Сторонние сервисы запускаются через docker-compose и содержат контейнеры для следующих сервисов:

Базы данных (Postgres):

- Postgres для БД файлов: `localhost:5432` (логин/пароль: postgres/111)
- Postgres для БД страниц: `localhost:5433` (логин/пароль: postgres/111)

Служба очереди сообщений (Kafka):

- Kafka: `localhost:9092`
- Zookeeper: `localhost:2181`

Служба s3-хранилища для хранения файлов и других объектов (MinIO):

- MinIO для файлов: `localhost:9000` (админка доступна через браузер: http://localhost:9001, логин/пароль:
  minioadmin/minioadmin)
- MinIO для страниц: `localhost:8000` (админка доступна через браузер: http://localhost:8001, логин/пароль:
  minioadmin/minioadmin)

Служба движка поиска (Licene через Elasticsearch):

- Elasticsearch: `localhost:9200`
- Kibana (доступ через браузер: http://localhost:5601).

#### Возможные проблемы с Postgres

Возможно, у вас на машине уже будет установлен и запущен Postgres. Если это так, то Postgres не сможет стартовать в
контейнере. Это не фатально, можете использовать свой локальный Postgres и тогда указывайте свой логин/пароль, в
противном случае остановите свой Postgres перед запуском контейнеров.

```shell
sudo systemctl stop postgresql
```

### 4. Первичная инициализация сторонних сервисов

#### Вариант А. Автоматическая первичная инициализация.

Для первичной инициализации сторонних сервисов нужно их запустить с пересозданием баз данных. При таком способе запуска
базы будут уничтожены и созданы заново (полезно для тестирования):

Linux:

```shell
sudo ./env-create
sudo ./env-init
```

Windows:

```bat
env-create.bat
env-init.bat
```

После первичной инициализации сторонних сервисов некоторые модули дополнительно инициализируют свои сервисы сами, при
своём первом запуске:

- таблицы в базах Postgres создает модуль `kis-base`
- топики в Kafka создает модуль `kis-messagebroker`

#### Вариант Б. Первичная инициализация вручную.

Не рекомендуется для разработчиков.

Если по каким-то причинам вы не используете некоторые из сторонних сервисов в контейнерах, то проведите инициализацию
сервисов (создайте необходимые базы, топики и т.п.)
самостоятельно.

- Настройка БД Postgres, см. [kis-base/README.md](kis-base/README.md)
- Настройка MinIO, см. [kis-storage/README.md](kis-storage/README.md)
- Настройка Kafka, см. [kis-messagebroker/README.md](kis-messagebroker/README.md)
- Настройка Elasticsearch, см. [kis-indexer/README.md](kis-indexer/README.md)

### 5. Запуск и остановка сторонних сервисов

Linux:

```bash
sudo ./env-start
```

Windows:

```bat
env-start.bat
```

На консоли должны увидеть:

```
...
Creating network "docker-images_default" with the default driver
Creating network "docker-images_app_network" with driver "bridge"
Creating db_files                  ... done
Creating minio_pages               ... done
Creating elasticsearch             ... done
Creating minio_files               ... done
Creating db_pages                  ... done
Creating docker-images_zookeeper_1 ... done
Creating kibana                    ... done
Creating docker-images_kafka_1     ... done
```

Остановить сторонние сервисы:

Linux:

```shell
cd docker-images 
docker-compose down
```

#### Nginx и web-приложение

Для тестирования только отдельных модулей nginx не требуется.

Для тестирования сервисов вместе с запуском web-приложения
(модуль `kis-web` [kis-web](kis-web/README.md)), нужно _дополнительно_ запустить контейнер `kis-nginx`:

```shell
cd docker-images 
docker-compose -f ./docker-compose.nginx.yml up -d
```

Контейнер nginx будет прокси-сервером, обеспечивает единый сетевой адрес для web-приложения и для сервисов:

Если модуль `kis-filestorage` запущен на локальном адресе `http://localhost:19090`, то он будет доступен по
адресу `http://localhost/api/`, а по адресу `http://localhost/` будет доступно веб-приложение.

### 6. Запуск проекта при разработке

Для запуска проекта необходимо запустить следующие модули (каждый в одельном командом интерпретаторе):

- `kis-filestorage`
- `kis-parser`
- `kis-indexer`
- `kis-rast`
- `kis-ocr`

Например, для `kis-filestorage`:

```
cd kis-filestorage
 
./gradlew bootRun
```

Для `kis-rast`

Чтобы эксель файлы растеризовались в dzi надо запустить сервер Libreoffice.
В докере он запускается сам, локально можно запустить через команду

`soffice --headless --accept="socket,host=localhost,port=10002;urp;"`

При удачном запуске на экране увидим что-то типа:

```
xxxxxxxxxxxxxxxxxxxxxxxxxxxx
xxxxxxxxxxxxxxxxxxxxxxxxxxxx
xxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

### 7. Тестирование

Чтобы запустить тесты в каждом из модулей, выполните команду:

```bash
./gradlew test
```

Эта команда запустит все тесты, определенные в модуле и выведет результаты.

### 8. Использование API

#### 8.1 Загрузка файла

Для загрузки файла в систему используйте следующий эндпоинт микросервиса `kis-filestorage`: `dir/uploadFile`

Через Postman:

```bash
POST http://localhost:8082/dir/uploadFile
```

Через команду `curl`:

```bash
curl -F "file=@testfile.txt" -F "directoryId=1" -F "author=test-author" http://localhost:8082/dir/uploadFile
```

#### 8.2 Поиск данных

Для поиска по тексту через Elasticsearch используйте следующий эндпоинт микросервиса
`/search/find`

Через Postman:

```bash
POST http://localhost:8084/search/find
```

Через команду `curl`:

```bash
curl -X POST -H "Content-Type: application/json" -d '{"searchField": "testfile"}' http://localhost:8084/search/find
```

## Сборка проекта для развертывания

Соберите все модули проекта с помощью Gradle:

```bash
./gradlew clean bootJar
```

Каждый модуль соберется в свою папку, см. папки `*/build/libs/*.jar`

Подробная инструкция по разворачиванию на тестовом сервере [README.test-srv.md](README.test-srv.md).

### Сборка проекта c тестами:

```bash
./gradlew clean build
```

