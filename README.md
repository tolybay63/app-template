# KIS Smart Catalog

Программный продукт KIS Smart Catalog — это комплексное решение для хранения, обработки, анализа и поиска документов.

Проект app-smartcatalog — это общий проект, состоит из нескольких микросервисов, каждый из которых выполняет свою роль.
Например: индексация и поиск данных через Elasticsearch.

## Модули

Некоторые модули являются отдельными микросервисами, некоторые - общие модули или библиотеки. Проект включает следующие
модули:

- `app-core` — общая базовая функциональность.
- `app-dbtools` — библиотека для работы с базой данных. Подробнее см. [app-dbtools/README.md](app-dbtools/README.md).
- `app-database` — предоставляет управление структурой базы данных. Подробнее см. [app-database/README.md](app-database/README.md)
- `app-auth` — отвечает за аутентификацию и авторизацию пользователей.
- `app-messagebroker` — управляет сообщениями в Kafka.
- `app-storage` — модуль для работы с MinIO, который хранит файлы.
- `app-web` — подпроект Quasar, web-фронт проекта.

## Требования к среде разработчика

### Для сборки и запуска

Для запуска всех модулей вам потребуются:

- **Java 21** или выше
- **Gradle 8.10** (для сборки и запуска проектов)
- **Docker** и **Docker Compose** (для запуска сторонних решений в контейнерах)

Дополнительные утилиты:

- **jq** утилита работы с json (под Windows скачиваем с https://jqlang.org, делаем jq доступной в путях)

##### Замечания и подсказки

Под Windows для установки Docker нужно инициализировать для подсистемы linux

```shell

wsl --install

wsl -l -v
```

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
git clone https://github.com/your-repo/app-smartcatalog.git
cd app-smartcatalog
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

- таблицы в базах Postgres создает модуль `app-database`
- топики в Kafka создает модуль `app-messagebroker`

#### Вариант Б. Первичная инициализация вручную.

Не рекомендуется для разработчиков.

Если по каким-то причинам вы не используете некоторые из сторонних сервисов в контейнерах, то проведите инициализацию
сервисов (создайте необходимые базы, топики и т.п.)
самостоятельно.

- Настройка БД Postgres, см. [app-database/README.md](app-database/README.md)
- Настройка MinIO, см. [app-storage/README.md](app-storage/README.md)
- Настройка Kafka, см. [app-messagebroker/README.md](app-messagebroker/README.md)
- Настройка Elasticsearch, см. [app-indexer/README.md](app-indexer/README.md)

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
(модуль `app-web` [app-web](app-web/README.md)), нужно _дополнительно_ запустить контейнер `app-nginx`:

```shell
cd docker-images 
docker-compose -f ./docker-compose.nginx.yml up -d
```

Контейнер nginx будет прокси-сервером, обеспечивает единый сетевой адрес для web-приложения и для сервисов:

Если модуль `app-filestorage` запущен на локальном адресе `http://localhost:19090`, то он будет доступен по
адресу `http://localhost/api/`, а по адресу `http://localhost/` будет доступно веб-приложение.

### 6. Запуск проекта при разработке

Для запуска проекта необходимо запустить следующие модули (каждый в одельном командом интерпретаторе):

- `app-filestorage`
- `app-parser`
- `app-indexer`
- `app-rast`
- `app-ocr`

Например, для `app-filestorage`:

```
cd app-filestorage
 
./gradlew bootRun
```

## Сборка проекта для развертывания

Соберите все модули проекта с помощью Gradle:

```bash
./gradlew clean bootJar
```

Каждый модуль соберется в свою папку, см. папки `*/build/libs/*.jar`

Подробная инструкция по разворачиванию на тестовом сервере [README.test-srv.md](README.test-srv.md).
