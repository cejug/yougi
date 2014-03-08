--liquibase formatted sql

--changeset htmfilho:13
insert into access_group (id, name, description, user_default) values
    ('PQOWKSIQMSLDKFJDUMDKKIJGEJDKNWJE', 'leaders', 'Leaders', 0);