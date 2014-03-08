--liquibase formatted sql

--changeset joewong:11
alter table user_account change gender gender tinyint(1) null;