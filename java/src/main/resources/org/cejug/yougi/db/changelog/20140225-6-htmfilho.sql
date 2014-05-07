--liquibase formatted sql

--changeset htmfilho:6
create table slot (
    id          char(32)     not null,
    event       char(32)     not null,
    date_slot   date             null,
    start_time  time             null,
    end_time    time             null
) engine = innodb;

alter table slot add constraint pk_slot primary key (id);
create index idx_slot_event on slot (event);
alter table slot add constraint fk_slot_event foreign key (event) references event(id) on delete cascade;