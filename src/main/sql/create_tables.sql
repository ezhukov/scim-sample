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
    id                bigint PRIMARY KEY,
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
    created           timestamp with time zone,
    lastModified      timestamp with time zone,
    location          varchar(255),
    version           varchar(10),
    gender            varchar(6)
);

CREATE TABLE emails (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) unique not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    bigint not null
);

CREATE TABLE phoneNumbers (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    bigint not null
);

CREATE TABLE ims (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    bigint not null
);

CREATE TABLE photos (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    bigint not null
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
    userId        bigint not null
);

CREATE TABLE groups (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    bigint not null
);

CREATE TABLE entitlements (
    id        SERIAL PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    bigint not null
);

CREATE TABLE roles (
    id        integer PRIMARY KEY,
    value     varchar(50) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    bigint not null
);

CREATE TABLE x509Certificates (
    id        SERIAL PRIMARY KEY,
    value     varchar(250) not null,
    display   varchar(50),
    isPrimary boolean,
    type      varchar(20),
    operation varchar(20),
    userId    bigint not null
);

