DROP DATABASE IF EXISTS kis_smart_catalog;

DROP USER IF EXISTS kis_smart_catalog;


CREATE USER kis_smart_catalog WITH PASSWORD '123456';

CREATE DATABASE kis_smart_catalog WITH OWNER kis_smart_catalog;