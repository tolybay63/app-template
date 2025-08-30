DROP DATABASE IF EXISTS kis_messages;

DROP USER IF EXISTS kis_messages;


CREATE USER kis_messages WITH PASSWORD '123456';

CREATE DATABASE kis_messages WITH OWNER kis_messages;