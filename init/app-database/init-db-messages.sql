DROP DATABASE IF EXISTS app_messages;

DROP USER IF EXISTS app_messages;


CREATE USER app_messages WITH PASSWORD '123456';

CREATE DATABASE app_messages WITH OWNER app_messages;