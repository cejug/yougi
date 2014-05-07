--liquibase formatted sql

alter table mailing_list drop column subscription;
alter table mailing_list drop column unsubscription;
alter table mailing_list_message drop column message_type;
alter table mailing_list_message drop foreign key fk_message_reply_to;
alter table mailing_list_message drop column reply_to;
alter table mailing_list_message drop column published;