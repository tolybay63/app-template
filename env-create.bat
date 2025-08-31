echo off

echo stop docker

cd docker-images
docker-compose down


echo build docker images
cd kis-keycloak-postgres
call make.bat
cd ..

cd kis-keycloak
call make.bat
cd ..

cd kis-nginx
call make.bat
cd ..

cd kis-postgres
call make.bat
cd ..




docker-compose -f docker-compose.win.yml up -d


cd ..

