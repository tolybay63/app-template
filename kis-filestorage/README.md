# Импорт каталога и всего что в нём

Делается командой по типу

```
./gradlew bootRun --args="src/test/java/kz/kis/kisfilestorage/testFiles/testA 1000 admin 111"
```

В кавычках после args передается 4 параметра:

- первый - локальный каталог, содержимое которого будет перенесено в хранилище;
- второй - id директории, в которую будет всё скопировано;
- третий - логин пользователя, от имени которого будет выполняться загрузка;
- четвёртый - пароль

 
Для собранного запускается так

```
java -jar /home/sc/kis-smart-catalog/kis-filestorage-0.0.1.jar "/mnt/shared/Общая папка АО ММГ" 1005 admin 111 --spring.config.additional-location=/home/sc/kis-smart-catalog/kis-filestorage.application.properties --server.port=60999
```
