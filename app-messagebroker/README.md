# Модуль app-messagebroker

Фасад для брокера сообщений

## Первичная инициализация сервиса Kafka

1. Убедитесь, что Kafka и Zookeeper запущены через Docker Compose.

```shell
docker ps
```

В списке запущенных контейнеров должны присутствовать имена `Kafka` `Zookeper`.

```
xxxxxxxxxxxxxxxxxxxxxxxxxxxx
xxxxxxxxxxxxxxxxxxxxxxxxxxxx
xxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

2. Создайте необходимые топики:
    - **file-create** — для передачи информации о загруженных файлах.
    - **parsed-results** — для получения результатов парсинга файлов.

Вы можете создать топики с помощью Kafka CLI или используя скрипты.

Пример создания топиков через Kafka CLI:

##### для Windows

Попасть в командную строку контейнера Kafka (имя контейнера по умолчанию `kafka`)

```
docker exec -it kafka /bin/bash
```

Если у вас другое имя контейнера, то сначала найти айди или имя контейнера Kafka:

```
docker ps 
```

Попасть в командную строку контейнера Kafka

```
docker exec -it <айдишка или имя контейнера> /bin/bash
```

Выполнение команд

```
cd /bin --здесь скрипты ????????????????????????????????
```

```
kafka-topics --bootstrap-server localhost:9092 --create --topic file-upload --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --create --topic file-upload --partitions 1 --replication-factor 1
```

##### для Linux

В Linux можно запустить команды в контейнере напрямую

```bash
docker exec -it kafka-container kafka-topics.sh --create --topic file-upload --bootstrap-server localhost:9092
docker exec -it kafka-container kafka-topics.sh --create --topic parsed-results --bootstrap-server localhost:9092
```
