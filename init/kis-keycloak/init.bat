@echo off
setlocal enabledelayedexpansion

set MAX_ATTEMPTS=10
set attempt=1

:retry
echo Попытка %attempt%...

:: Получение токена доступа для администратора
rem for /f "delims=" %%i in ('curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token ^| jq -r .access_token') do set ACCESS_TOKEN=%%i
curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token
curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token
curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token
curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token
curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token

:: Проверка, что токен получен
rem if "!ACCESS_TOKEN!"=="" (
rem     echo ACCESS_TOKEN is empty 1>&2
rem     set /a attempt+=1
rem     if !attempt! LEQ %MAX_ATTEMPTS% (
rem         timeout /t 15 >nul
rem         goto retry
rem     )
rem     exit /b 1
rem )

:: Проверка, что токен получен
if "%ACCESS_TOKEN%"=="" (
    echo ACCESS_TOKEN is empty
    rem exit /b 1
)

:: Создание scope - ов
curl -X POST "http://localhost:8282/admin/realms/master/client-scopes" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"id\": \"4bb9a4be-942a-4a92-ae0d-86367e014ea6\", \"name\": \"smart_catalog-scope\", \"description\": \"Custom scope with user attribute\", \"protocol\": \"openid-connect\", \"attributes\": {\"include.in.token.scope\": \"true\", \"display.on.consent.screen\": \"false\"}}"

curl -X POST "http://localhost:8282/admin/realms/master/client-scopes" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"id\": \"6e903e6c-e272-46fc-a74c-bbad5e40fbd7\", \"name\": \"agent_smart_catalog-scope\", \"description\": \"Custom scope with user attribute\", \"protocol\": \"openid-connect\", \"attributes\": {\"include.in.token.scope\": \"true\", \"display.on.consent.screen\": \"false\"}}"

curl -X POST "http://localhost:8282/admin/realms/master/client-scopes" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"id\": \"507ab215-ac0b-4860-8c85-7731957e773b\", \"name\": \"filesync_smart_catalog-scope\", \"description\": \"Custom scope with user attribute\", \"protocol\": \"openid-connect\", \"attributes\": {\"include.in.token.scope\": \"true\", \"display.on.consent.screen\": \"false\"}}"

:: Создание мапперов
curl -X POST "http://localhost:8282/admin/realms/master/client-scopes/4bb9a4be-942a-4a92-ae0d-86367e014ea6/protocol-mappers/models" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"name\": \"user_id\", \"protocol\": \"openid-connect\", \"protocolMapper\": \"oidc-usermodel-attribute-mapper\", \"consentRequired\": false, \"config\": {\"introspection.token.claim\": \"true\", \"userinfo.token.claim\": \"true\", \"user.attribute\": \"user_id\", \"id.token.claim\": \"true\", \"access.token.claim\": \"true\", \"claim.name\": \"user_id\", \"jsonType.label\": \"String\"}}"

curl -X POST "http://localhost:8282/admin/realms/master/client-scopes/6e903e6c-e272-46fc-a74c-bbad5e40fbd7/protocol-mappers/models" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"name\": \"user_id\", \"protocol\": \"openid-connect\", \"protocolMapper\": \"oidc-usermodel-attribute-mapper\", \"consentRequired\": false, \"config\": {\"introspection.token.claim\": \"true\", \"userinfo.token.claim\": \"true\", \"user.attribute\": \"user_id\", \"id.token.claim\": \"true\", \"access.token.claim\": \"true\", \"claim.name\": \"user_id\", \"jsonType.label\": \"String\"}}"

curl -X POST "http://localhost:8282/admin/realms/master/client-scopes/507ab215-ac0b-4860-8c85-7731957e773b/protocol-mappers/models" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"name\": \"user_id\", \"protocol\": \"openid-connect\", \"protocolMapper\": \"oidc-usermodel-attribute-mapper\", \"consentRequired\": false, \"config\": {\"introspection.token.claim\": \"true\", \"userinfo.token.claim\": \"true\", \"user.attribute\": \"user_id\", \"id.token.claim\": \"true\", \"access.token.claim\": \"true\", \"claim.name\": \"user_id\", \"jsonType.label\": \"String\"}}"

:: Создание клиентов
curl -X POST "http://localhost:8282/admin/realms/master/clients" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"id\": \"51516cc0-a026-47dd-bbab-1fe359ad111a\", \"clientId\": \"smart_catalog\", \"secret\": \"jmG7PLDd5O5jJGnUXfK28YcgPiIu3KOl\", \"enabled\": true, \"protocol\": \"openid-connect\", \"publicClient\": false, \"bearerOnly\": false, \"defaultClientScopes\": [\"web-origins\", \"acr\", \"profile\", \"roles\", \"smart_catalog-scope\", \"basic\", \"email\"]}"

curl -X POST "http://localhost:8282/admin/realms/master/clients" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"id\": \"45178321-52bb-4d4b-961f-8821ca29336b\", \"clientId\": \"agent_smart_catalog\", \"secret\": \"U9gJv4NoAgUhuOmmFFrTsz1JUpFWwOkB\", \"enabled\": true, \"protocol\": \"openid-connect\", \"publicClient\": false, \"bearerOnly\": false, \"defaultClientScopes\": [\"web-origins\", \"acr\", \"profile\", \"roles\", \"agent_smart_catalog-scope\", \"basic\", \"email\"]}"

curl -X POST "http://localhost:8282/admin/realms/master/clients" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"id\": \"64ff8c11-979f-43fc-bc9f-f637af3eff33\", \"clientId\": \"filesync_smart_catalog\", \"secret\": \"x9GJn4NoPgjhdOMMFyrTsz0JUpfWpOKQ\", \"enabled\": true, \"protocol\": \"openid-connect\", \"publicClient\": false, \"bearerOnly\": false, \"attributes\":{\"access.token.lifespan\":1315360000, \"use.refresh.tokens\":false}, \"defaultClientScopes\": [\"web-origins\", \"acr\", \"profile\", \"roles\", \"filesync_smart_catalog-scope\", \"basic\", \"email\"]}"

:: Задание аттрибутов в user profile
curl -X PUT "http://localhost:8282/admin/realms/master/users/profile" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"attributes\": [{\"name\": \"username\", \"displayName\": \"username\"}, {\"name\": \"email\", \"displayName\": \"email\"}, {\"name\": \"firstName\", \"displayName\": \"firstName\"}, {\"name\": \"lastName\", \"displayName\": \"lastName\"}, {\"name\": \"user_id\", \"displayName\": \"user_id\", \"permissions\": {\"edit\": [\"admin\"]}}]}"

:: Создание пользователя admin
curl -X POST "http://localhost:8282/admin/realms/master/users" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"username\": \"admin\", \"enabled\": true, \"firstName\": \"admin\", \"lastName\": \"admin\", \"email\": \"admin@mail.com\", \"attributes\": {\"user_id\": [\"1\"]}, \"credentials\": [{\"type\":\"password\", \"value\":\"111\", \"temporary\":false}]}"

:: Создание пользователя user
curl -X POST "http://localhost:8282/admin/realms/master/users" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "{\"username\": \"user\", \"enabled\": true, \"firstName\": \"user\", \"lastName\": \"user\", \"email\": \"user@mail.com\", \"attributes\": {\"user_id\": [\"2\"]}, \"credentials\": [{\"type\":\"password\", \"value\":\"user\", \"temporary\":false}]}"

:: Получение userId пользователя admin
for /f "delims=" %%a in ('curl -s -X GET "http://localhost:8282/admin/realms/master/users?username=admin" -H "Authorization: Bearer %ACCESS_TOKEN%" ^| jq -r ".[0].id"') do set USER_ID=%%a

:: Проверка, что userId получен
if "%USER_ID%"=="" (
    echo User ID for admin not found
    exit /b 1
)

:: Назначение роли admin пользователю admin
curl -X POST "http://localhost:8282/admin/realms/master/users/%USER_ID%/role-mappings/realm" -H "Authorization: Bearer %ACCESS_TOKEN%" -H "Content-Type: application/json" -d "[{\"name\": \"admin\"}]"

echo.
echo Client and users created successfully

call ldap-init.bat