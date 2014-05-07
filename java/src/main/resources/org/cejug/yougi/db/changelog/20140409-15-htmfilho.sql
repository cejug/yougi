--liquibase formatted sql

create table job_scheduler (
  id             char(32)     not null,
  name           varchar(50)  not null,
  default_owner  char(32)     not null,
  start_date     date         not null,
  start_time     time             null,
  frequency_type varchar(20)      null,
  frequency      numeric(2,0)     null,
  day_year       numeric(2,0)     null,
  day_month      numeric(2,0)     null,
  day_week       varchar(20)      null,
  working_day    tinyint(1)       null,
  end_date       date             null,
  description    text             null,
  active         tinyint(1)       null
) engine = innodb;

alter table job_scheduler add constraint pk_job_scheduler primary key (id);
create index idx_default_owner_job on job_scheduler (default_owner);
alter table job_scheduler add constraint fk_default_owner_job foreign key (default_owner) references user_account(id) on delete cascade;

create table job_execution (
  id               char(32)    not null,
  job_scheduler    char(32)    not null,
  owner            char(32)    not null,
  instance_id      integer         null,
  status           varchar(20)     null,
  start_time       timestamp       null,
  end_time         timestamp       null
) engine = innodb;

alter table job_execution add constraint pk_job_execution primary key (id);
create index idx_scheduler_job on job_execution (job_scheduler);
create index idx_owner_job on job_execution (owner);
alter table job_execution add constraint fk_scheduler_job foreign key (job_scheduler) references job_scheduler (id) on delete cascade;
alter table job_execution add constraint fk_owner_job foreign key (owner) references user_account(id) on delete cascade;