echo on
echo ��⠭���� �ࢨᮢ


cd docker-images
docker-compose down


echo
echo �������� Docker-��ࠧ�� ��� �ࢨᮢ
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
call make
cd ..

cd libreoffice
REM call make
cd ..

cd libreoffice-server
call make
cd ..

cd tesseract
REM call make
cd ..

cd kis-redis
REM call make
cd ..


echo
echo ����� �ࢨᮢ


docker-compose -f docker-compose.win.yml up -d


cd ..

