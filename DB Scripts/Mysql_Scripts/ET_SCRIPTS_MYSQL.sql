USE EVERYTING_DB;

DROP TABLE IF EXISTS ET_DS;
CREATE TABLE ET_DS(
ID INT PRIMARY KEY AUTO_INCREMENT,
ALT_NAME VARCHAR(50) UNIQUE,
TABLE_NAME VARCHAR(50),
PK_NAME VARCHAR(50),
SEQ_NAME VARCHAR(50),
SCHEMA_NAME VARCHAR(200),
DB_VENDOR VARCHAR(100),
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT1 INT,
N_EXT2 INT,
D_EXT1 TIMESTAMP,
D_EXT2 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);

DROP TABLE IF EXISTS ET_DS_ATTRS;
CREATE TABLE ET_DS_ATTRS(
ID INT PRIMARY KEY AUTO_INCREMENT,
TABLE_NAME VARCHAR(50) NOT NULL,
COLUMN_NAME VARCHAR(50),
DATA_TYPE VARCHAR(50),
KEY_TYPE VARCHAR(20),
IS_NULL VARCHAR(1),
IS_AI VARCHAR(1),
SEQUENCE VARCHAR(100),
DEFAULT_VALUE VARCHAR(500),
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT1 INT,
N_EXT2 INT,
D_EXT1 TIMESTAMP,
D_EXT2 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);

DROP TABLE IF EXISTS ET_PAGES;
CREATE TABLE ET_PAGES(
ID INT PRIMARY KEY AUTO_INCREMENT,
PAGE_NAME VARCHAR(200) NOT NULL UNIQUE,
STATE VARCHAR(200) NOT NULL,
URL VARCHAR(200) NOT NULL,
ICON VARCHAR(100),
SHOW_IN_MENU VARCHAR(1),
HTML_BLOB_ID INT,
JS_BLOB_ID INT,
LAZY_LOAD LONGBLOB,
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT1 INT,
N_EXT2 INT,
D_EXT1 TIMESTAMP,
D_EXT2 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);


DROP TABLE IF EXISTS ET_INTERPRETERS;
CREATE TABLE ET_INTERPRETERS(
ID INT PRIMARY KEY AUTO_INCREMENT,
NAME VARCHAR(200) NOT NULL UNIQUE,
LANG VARCHAR(50),
ICON VARCHAR(50),
IS_ACTIVE VARCHAR(1),
CONTENT LONGBLOB,
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT1 INT,
N_EXT2 INT,
D_EXT1 TIMESTAMP,
D_EXT2 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);


DROP TABLE IF EXISTS ET_FILES;
CREATE TABLE ET_FILES(
ID INT PRIMARY KEY AUTO_INCREMENT,
FILE_NAME VARCHAR(200) NOT NULL,
CONTENT_TYPE VARCHAR(100),
FILE_SIZE INT,
FILE_PATH VARCHAR(200),
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT3 INT,
N_EXT4 INT,
D_EXT5 TIMESTAMP,
D_EXT6 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);

DROP TABLE IF EXISTS ET_BLOBS;
CREATE TABLE ET_BLOBS(
ID INT PRIMARY KEY AUTO_INCREMENT,
FILE_NAME VARCHAR(200) NOT NULL,
CONTENT_TYPE VARCHAR(100),
CONTENT BLOB,
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT3 INT,
N_EXT4 INT,
D_EXT5 TIMESTAMP,
D_EXT6 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);

DROP TABLE IF EXISTS ET_APPS;
CREATE TABLE ET_APPS(
ID INT AUTO_INCREMENT PRIMARY KEY,
TITLE VARCHAR(250) NOT NULL,
THEME VARCHAR(100) NOT NULL,
TEMPLATE_ID INT NOT NULL,
ICON VARCHAR(100),
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT3 INT,
N_EXT4 INT,
D_EXT5 TIMESTAMP,
D_EXT6 TIMESTAMP,
IS_PUBLISHED VARCHAR(1),
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);

DROP TABLE IF EXISTS ET_APP_STRUCTURE;
CREATE TABLE ET_APP_STRUCTURE(
ID INT AUTO_INCREMENT PRIMARY KEY,
TYPE VARCHAR(50) NOT NULL,
NAME VARCHAR(250) NOT NULL,
PARENT_ID INT NOT NULL,
APP_ID INT NOT NULL,
FILE_CONTENT LONGBLOB,
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT3 INT,
N_EXT4 INT,
D_EXT5 TIMESTAMP,
D_EXT6 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);

DROP TABLE IF EXISTS ET_TEMPLATES; 
CREATE TABLE ET_TEMPLATES(
ID INT AUTO_INCREMENT PRIMARY KEY,
TEMPLATE_NAME VARCHAR(250) NOT NULL,
THUMBNAIL_URL VARCHAR(250),
THEME VARCHAR(150),
VERSION VARCHAR(50),
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT3 INT,
N_EXT4 INT,
D_EXT5 TIMESTAMP,
D_EXT6 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);


DROP TABLE IF EXISTS ET_TEMPLATE_STRUCTURE; 
CREATE TABLE ET_TEMPLATE_STRUCTURE(
ID INT AUTO_INCREMENT PRIMARY KEY,
UID VARCHAR(250) UNIQUE,
TYPE VARCHAR(20) NOT NULL,
TITLE VARCHAR(250) NOT NULL,
PARENT_UID INT NOT NULL, 
TEMPLATE_ID INT NOT NULL,
FILE_CONTENT LONGBLOB,
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT3 INT,
N_EXT4 INT,
D_EXT5 TIMESTAMP,
D_EXT6 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP,
UNIQUE KEY ET_TEMPLATE_STRUCTURE_U1 (UID,PARENT_UID)
);

DROP TABLE IF EXISTS ET_WIDGETS; 
CREATE TABLE ET_WIDGETS(
ID INT AUTO_INCREMENT PRIMARY KEY,
WIDGET_ID VARCHAR(150) NOT NULL UNIQUE,
WIDGET_NAME VARCHAR(100) NOT NULL,
WIDGET_GROUP VARCHAR(100),
SYNTAX TEXT NOT NULL,
THUMBNAIL TEXT,
TAGS VARCHAR(2000),
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT3 INT,
N_EXT4 INT,
D_EXT5 TIMESTAMP,
D_EXT6 TIMESTAMP,
CREATED_BY INT,
CREATION_DATE TIMESTAMP,
LAST_UPDATED_BY INT,
LAST_UPDATE_DATE TIMESTAMP
);

DROP TABLE IF EXISTS ET_USERS; 
CREATE TABLE ET_USERS(
USER_ID INT AUTO_INCREMENT PRIMARY KEY,
USER_NAME VARCHAR(200) UNIQUE NOT NULL,
DISPLAY_NAME VARCHAR(200),
EMAIL_ADDRESS VARCHAR(250) UNIQUE NOT NULL,
ENCRYPT_PASSWORD VARCHAR(1000) NOT NULL,
AVATAR_URL VARCHAR(250),
CREATION_DATE TIMESTAMP,
LAST_LOGIN_DATE TIMESTAMP,
LAST_LOGIN_IP VARCHAR(250),
EXT1 VARCHAR(550),
EXT2 VARCHAR(550),
N_EXT3 INT,
N_EXT4 INT,
D_EXT5 TIMESTAMP,
D_EXT6 TIMESTAMP
);






