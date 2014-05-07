--liquibase formatted sql

--changeset htmfilho:9
alter table user_account drop column birth_date;
alter table user_account drop column postal_code;