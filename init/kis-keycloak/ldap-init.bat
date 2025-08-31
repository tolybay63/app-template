@echo off
setlocal enabledelayedexpansion

set "PROVIDER_NAME=LDAP_AD"
set "GROUP_NAME=LDAP_GROUPS"
set "URL=ldap://192.168.0.6"
set "USERS_DN=DC=kazinfosystems,DC=kz"
set "GROUP_DN=DC=kazinfosystems,DC=kz"
set "LOGIN=batman@kazinfosystems.kz"
set "PASSWORD=Axario09S"

:: Получение access token
rem for /f "delims=" %%i in ('curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token ^| jq -r ".access_token"') do set "ACCESS_TOKEN=%%i"
curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token ^
curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token ^
curl -s --data "client_id=admin-cli" --data "username=smart_catalog" --data "password=Grpn404tfgNbp09we21" --data "grant_type=password" http://localhost:8282/realms/master/protocol/openid-connect/token ^

:: Проверяем результат
rem if "%ACCESS_TOKEN%"=="" (
rem    echo ACCESS_TOKEN is empty
rem    exit /b 1
rem )

:: Удаление компонента
curl -X DELETE "http://localhost:8282/admin/realms/master/components/Ld6R3o3pT_64Kr9Nk3m8yQ" ^
     -H "Content-Type: application/json" ^
     -H "Authorization: Bearer %ACCESS_TOKEN%"

:: Получение ID Realm
for /f "delims=" %%i in ('curl -s -X GET "http://localhost:8282/admin/realms" ^
     -H "Content-Type: application/json" ^
     -H "Authorization: Bearer %ACCESS_TOKEN%" ^| jq -r ".[] | select(.realm == \"master\") | .id"') do set "REALM_ID=%%i"

echo %REALM_ID%

:: Создание UserStorageProvider
curl -X POST "http://localhost:8282/admin/realms/master/components" ^
     -H "Content-Type: application/json" ^
     -H "Authorization: Bearer %ACCESS_TOKEN%" ^
     -d "{\"id\": \"Ld6R3o3pT_64Kr9Nk3m8yQ\", \"name\": \"%PROVIDER_NAME%\", \"parentId\": \"%REALM_ID%\", \"providerId\": \"ldap\", \"providerType\": \"org.keycloak.storage.UserStorageProvider\", \"config\": {\"enabled\": [\"true\"], \"priority\": [\"0\"], \"importEnabled\": [\"true\"], \"editMode\": [\"UNSYNCED\"], \"syncRegistrations\": [\"false\"], \"vendor\": [\"Active Directory\"], \"usernameLDAPAttribute\": [\"sAMAccountName\"], \"rdnLDAPAttribute\": [\"cn\"], \"uuidLDAPAttribute\": [\"objectGUID\"], \"userObjectClasses\": [\"person, organizationalPerson, user\"], \"connectionUrl\": [\"%URL%\"], \"usersDn\": [\"%USERS_DN%\"], \"bindDn\": [\"%LOGIN%\"], \"bindCredential\": [\"%PASSWORD%\"], \"useTruststoreSpi\": [\"ldapsOnly\"], \"connectionPooling\": [\"true\"], \"pagination\": [\"true\"], \"allowKerberosAuthentication\": [\"false\"], \"batchSizeForSync\": [\"1000\"], \"fullSyncPeriod\": [\"3600\"], \"changedSyncPeriod\": [\"600\"], \"searchScope\": [\"2\"], \"customUserSearchFilter\": [\"(cn=*)\"]}}"

:: Создание GroupMapper
curl -X POST "http://localhost:8282/admin/realms/master/components" ^
     -H "Content-Type: application/json" ^
     -H "Authorization: Bearer %ACCESS_TOKEN%" ^
     -d "{\"id\": \"6ed33a0f-bab1-4c15-9762-483f701a677c\", \"name\": \"%GROUP_NAME%\", \"providerId\": \"group-ldap-mapper\", \"providerType\": \"org.keycloak.storage.ldap.mappers.LDAPStorageMapper\", \"parentId\": \"Ld6R3o3pT_64Kr9Nk3m8yQ\", \"config\": {\"membership.attribute.type\": [\"DN\"], \"group.name.ldap.attribute\": [\"cn\"], \"preserve.group.inheritance\": [\"false\"], \"membership.user.ldap.attribute\": [\"cn\"], \"groups.dn\": [\"%GROUP_DN%\"], \"mapped.group.attributes\": [\"MemberOf\"], \"mode\": [\"READ_ONLY\"], \"user.roles.retrieve.strategy\": [\"GET_GROUPS_FROM_USER_MEMBEROF_ATTRIBUTE\"], \"membership.ldap.attribute\": [\"MemberOf\"], \"ignore.missing.groups\": [\"false\"], \"memberof.ldap.attribute\": [\"MemberOf\"], \"group.object.classes\": [\"group\"], \"groups.path\": [\"/\"], \"drop.non.existing.groups.during.sync\": [\"false\"]}}"

endlocal