@echo off


docker cp init-db-files.sql db_files:/home/init-db-files.sql
docker cp init-db-pages.sql db_pages:/home/init-db-pages.sql


echo ""
echo "db files"
docker exec db_files psql -U postgres -h localhost -p 5432 -a --set ON_ERROR_STOP=on -f /home/init-db-files.sql


echo ""
echo "db pages"
docker exec db_pages psql -U postgres -h localhost -p 5433 -a --set ON_ERROR_STOP=on -f /home/init-db-pages.sql


echo ""
echo "db messages"
docker exec db_messages psql -U postgres -h localhost -p 5433 -a --set ON_ERROR_STOP=on -f /home/init-db-messages.sql