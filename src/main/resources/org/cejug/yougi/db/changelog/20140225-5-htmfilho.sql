--liquibase formatted sql

--changeset htmfilho:5
alter table user_account drop organization;

alter table session add detailed_description text null;
alter table session add experience_level varchar(20) null;
alter table session add approved tinyint(1) null;

alter table speaker add experience text null;
alter table speaker add organization varchar(100) null;

create table speaker_session (
    id      char(32) not null,
    speaker char(32) not null,
    session char(32) not null
) engine = innodb;

alter table speaker_session add constraint pk_speaker_session primary key (id);
create index idx_speaker_session on speaker_session (speaker);
create index idx_session_speaker on speaker_session (session);
alter table speaker_session add constraint fk_speaker_session foreign key (speaker) references speaker(id) on delete cascade;
alter table speaker_session add constraint fk_session_speaker foreign key (session) references session(id) on delete cascade;

alter table track drop topics;

alter table event_sponsor rename to sponsorship_event;
alter table sponsorship_event add sponsorship_level varchar(20) null;

create table attendee_session (
    id         char(32)    not null,
    attendee   char(32)    not null,
    session    char(32)    not null,
    bookmark   tinyint(1)      null,
    evaluation varchar(15)     null
) engine = innodb;

alter table attendee_session add constraint pk_attendee_session primary key (id);
create index idx_attendee_session on attendee_session (attendee);
create index idx_session_attendee on attendee_session (session);
alter table attendee_session add constraint fk_attendee_session foreign key (attendee) references attendee(id) on delete cascade;
alter table attendee_session add constraint fk_session_attendee foreign key (session) references session(id) on delete cascade;
