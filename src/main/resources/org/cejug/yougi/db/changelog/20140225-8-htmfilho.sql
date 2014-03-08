--liquibase formatted sql

--changeset htmfilho:8
insert into access_group (id, name, description, user_default) values ('SDHFGSIFUSLEOSJFNMDKELSOEJDKNWJE', 'admins', 'Admins', 0);
update user_group set group_id = 'SDHFGSIFUSLEOSJFNMDKELSOEJDKNWJE', group_name = 'admins' where group_name = 'leaders';