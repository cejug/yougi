--liquibase formatted sql

delete from mailing_list_message;
alter table mailing_list_message drop foreign key fk_mailing_list_sender;
alter table mailing_list_message change sender sender varchar(100) null;
drop table mailing_list_subscription;