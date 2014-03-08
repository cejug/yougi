--liquibase formatted sql

--changeset htmfilho:12
insert into access_group (id, name, description, user_default) values
    ('PQOWKSIFUSLEOSJFNMDKKIJGEJDKNWJE', 'speakers', 'Speakers', 0);