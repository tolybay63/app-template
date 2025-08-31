echo off

echo stop docker

cd docker-images
docker-compose down


echo build docker images
cd app-keycloak-postgres
call make.bat
cd ..

cd app-keycloak
call make.bat
cd ..

cd app-nginx
call make.bat
cd ..

cd app-postgres
call make.bat
cd ..




docker-compose -f docker-compose.win.yml up -d


cd ..

