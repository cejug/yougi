--liquibase formatted sql

alter table job_scheduler drop column day_year;
alter table job_scheduler drop column day_month;
alter table job_scheduler drop column day_week;