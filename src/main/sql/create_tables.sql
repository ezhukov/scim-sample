--After postgresql installation:
--sudo -u postgres psql

--create database scim_db;

--CREATE ROLE eugene;
--alter role eugene with LOGIN;
--alter role eugene with superuser;
--alter role eugene with unencrypted password 'scim_db';

--Only then:
--psql scim_db

DROP TABLE users;
DROP TABLE emails;
DROP TABLE phoneNumbers;
DROP TABLE ims;
DROP TABLE photos;
DROP TABLE addresses;
DROP TABLE groups;
DROP TABLE entitlements;
DROP TABLE roles;
DROP TABLE x509Certificates;

CREATE TABLE users (
    id                uuid PRIMARY KEY,
    username          varchar(20) unique not null,
    formattedName     varchar(255),
    familyName        varchar(70),
    givenName         varchar(70),
    middleName        varchar(70),
    honorificPrefix   varchar(5),
    honorificSuffix   varchar(5),
	displayName       varchar(30),
    nickname          varchar(30),
    profileURL        varchar(255),
    title             varchar(50),
    userType          varchar(255),
    preferredLanguage char(5),
    locale            char(5),
    timezone          varchar(100),
    active            boolean,
    password          varchar(30),
    created           timestamp with time zone not null,
    lastModified      timestamp with time zone not null,
    location          varchar(255) not null,
    version           varchar(100) not null,
    gender            varchar(6)
);

CREATE TABLE emails (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) unique not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    uuid not null
);

CREATE TABLE phoneNumbers (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    uuid not null
);

CREATE TABLE ims (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    uuid not null
);

CREATE TABLE photos (
    id        SERIAL PRIMARY KEY,
    value     varchar(2000) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    uuid not null
);

CREATE TABLE addresses (
    id            SERIAL PRIMARY KEY,
    value         varchar(250),
    display       varchar(50),
    isPrimary     boolean,
    type          varchar(20),
    operation     varchar(20),
    formatted     varchar(200),
    streetAddress varchar(100),
    locality      varchar(50),
    region        varchar(50),
    postalCode    varchar(10),
    country       char(2),
    userId        uuid not null
);

CREATE TABLE groups (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    uuid not null
);

CREATE TABLE entitlements (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    uuid not null
);

CREATE TABLE roles (
    id        integer PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    uuid not null
);

CREATE TABLE x509Certificates (
    id        SERIAL PRIMARY KEY,
    value     varchar(250) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    uuid not null
);

