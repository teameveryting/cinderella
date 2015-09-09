/*SELECT CONCAT('DROP TABLE IF EXISTS ',table_name,';') FROM
information_schema.tables
WHERE table_schema = 'EVERYTHING_DB';*/

DROP TABLE IF EXISTS et_app_structure;
DROP TABLE IF EXISTS et_apps;
DROP TABLE IF EXISTS et_data_sources;
DROP TABLE IF EXISTS et_files;
DROP TABLE IF EXISTS et_interpreters;
DROP TABLE IF EXISTS et_template_structure;
DROP TABLE IF EXISTS et_templates;
DROP TABLE IF EXISTS et_users;
DROP TABLE IF EXISTS et_widgets;