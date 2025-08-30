rem для очистки базы данных удаляем volume образа
docker volume rm docker-images_db_data_auth

rem Собираем образ Docker
docker build -t kis-keycloak-postgres .
