--liquibase formatted sql

--changeset htmfilho:4
alter table user_account add organization varchar(100) null;

alter table speaker drop foreign key fk_event_speaker;
alter table speaker drop foreign key fk_session_speaker;
alter table speaker drop index fk_event_speaker;
alter table speaker drop index fk_session_speaker;
alter table speaker drop column session;
alter table speaker drop column event;

create table room (
    id          char(32)    not null,
    name        varchar(50) not null,
    venue       char(32)    not null,
    description text            null,
    capacity    numeric(4)      null
) engine = innodb;

alter table room add constraint pk_room primary key (id);
create index idx_room_venue on room (venue);
alter table room add constraint fk_room_venue foreign key (venue) references venue(id) on delete cascade;

create table track (
    id          char(32)     not null,
    name        varchar(50)  not null,
    event       char(32)     not null,
    color       char(6)          null,
    description text             null,
    topics      varchar(255)     null
) engine = innodb;

alter table track add constraint pk_track primary key (id);
create index idx_track_event on track (event);
alter table track add constraint fk_track_event foreign key (event) references event(id) on delete cascade;

alter table event_session rename to session;
alter table session change title name varchar(255) not null;
alter table session change abstract description text null;
alter table session change session_date start_date date null;
alter table session add column end_date date null;
alter table session drop column room;
alter table session add room char(32) null;
create index idx_room_session on session (room);
alter table session add constraint fk_room_session foreign key (room) references room (id) on delete set null;
alter table session add track char(32) null;
create index idx_track_session on session (track);
alter table session add constraint fk_track_session foreign key (track) references track (id) on delete set null;
