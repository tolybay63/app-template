@echo off




if not exist bot-info.txt (
  echo "bot-info.txt not found"
  exit /b
)


echo ""
echo "adding bot info"


docker cp add-bot-info.sql db_files:/home/add-bot-info.sql

for /f "tokens=2" %%i in ('findstr token: bot-info.txt') do set token=%%i
for /f "tokens=2" %%i in ('findstr username: bot-info.txt') do set username=%%i

docker exec db_files psql -U kis_smart_catalog -h localhost -p 5432 -a --set ON_ERROR_STOP=on --set token=%token% --set username=%username% -f /home/add-bot-info.sql
