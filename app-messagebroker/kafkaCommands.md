## Полезные команды Kafka 

### для Windows

```
docker ps --найти кафку (взять айдишку или имя)
docker exec -it <айдишка или имя> /bin/bash
cd /bin --здесь скрипты
```

#### Список топиков

```shell
kafka-topics --bootstrap-server localhost:9092 --list
```

#### Создание/удаления топиков

```shell
kafka-topics --bootstrap-server localhost:9092 --create --topic file-create --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --create --topic parsed-results --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --create --topic rast-results --partitions 1 --replication-factor 1
kafka-topics --bootstrap-server localhost:9092 --delete --topic parsed-results
kafka-topics --bootstrap-server localhost:9092 --delete --topic file-create
kafka-topics --bootstrap-server localhost:9092 --delete --topic rast-results
```
               
Полезно для очистки сообщений

#### Мониторинг сообщений

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic parsed-results --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic file-create --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic rast-results --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic ocr-retry --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic test-topic-out --from-beginning
```


