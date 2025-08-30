cd docker-images

call env.bat

docker-compose -f docker-compose.win.yml up -d

cd ..
