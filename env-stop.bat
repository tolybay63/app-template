cd docker-images

call env.bat

docker-compose -f docker-compose.win.yml down

cd ..
