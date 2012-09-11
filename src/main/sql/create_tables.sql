CREATE TABLE users (
    id                integer PRIMARY KEY,
    username          varchar(20),
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
    password          varchar(255),
    created           timestamp,
    lastModified      timestamp,
    location          varchar(255),
    version           varchar(10)
);

CREATE TABLE emails (
    id        integer PRIMARY KEY,
    value     varchar(50),
    display   varchar(50),
    primary   boolean,
    type      varchar(20),
    operation varchar(20)
);
