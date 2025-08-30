




echo
echo Остановка сервисов

cd docker-images

docker-compose -f docker-compose.win.yml down

cd ..



echo
echo Очистка томов

cd clear

call env-clear.bat

cd ..



echo
echo Запуск сервисов

call env-start.bat



echo 
echo Инициализация сервисов

cd init

call env-init.bat

cd ..

