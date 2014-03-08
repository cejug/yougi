--liquibase formatted sql

--changeset htmfilho:3
alter table event add parent char(32) null;
create index idx_parent_event on event (parent);
alter table event add constraint fk_parent_event foreign key (parent) references event(id) on delete set null;

create table venue (
    id        char(32)     not null,
    name      varchar(100) not null,
    address   varchar(255)     null,
    country   char(3)          null,
    province  char(32)         null,
    city      char(32)         null,
    latitude  varchar(15)      null,
    longitude varchar(15)      null,
    website   varchar(255)     null
) engine = innodb;

alter table venue add constraint pk_venue primary key (id);
create index idx_country_venue on venue (country);
create index idx_province_venue on venue (province);
create index idx_city_venue on venue (city);
alter table venue add constraint fk_country_venue foreign key (country) references country(acronym) on delete set null;
alter table venue add constraint fk_province_venue foreign key (province) references province(id) on delete set null;
alter table venue add constraint fk_city_venue foreign key (city) references city(id) on delete set null;

alter table event drop foreign key fk_event_venue;
alter table event drop foreign key fk_country_event;
alter table event drop foreign key fk_province_event;
alter table event drop foreign key fk_city_event;
alter table event drop index fk_city_event;
alter table event drop index fk_country_event;
alter table event drop index fk_event_venue;
alter table event drop index fk_province_event;

create table event_venue (
    id    char(32) not null,
    event char(32) not null,
    venue char(32) not null
) engine = innodb;

alter table event_venue add constraint pk_event_venue primary key (id);
create index idx_event_venue on event_venue (event);
create index idx_venue_event on event_venue (venue);
alter table event_venue add constraint fk_event_venue foreign key (event) references event(id) on delete cascade;
alter table event_venue add constraint fk_venue_event foreign key (venue) references venue(id) on delete cascade;

insert into venue (id, name, address, country, province, city, latitude, longitude, website) select e.id, p.name, e.address, e.country, e.province, e.city, e.latitude, e.longitude, p.url from event e left join partner p on p.id = e.venue;
insert into event_venue (id, event, venue) select id, id, venue from event;

alter table event drop column external;
alter table event drop column address;
alter table event drop column country;
alter table event drop column province;
alter table event drop column city;
alter table event drop column latitude;
alter table event drop column longitude;
alter table event drop column venue;

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

--changeset htmfilho:7
insert into language values ('fr', 'Français');

--changeset htmfilho:8
insert into access_group (id, name, description, user_default) values ('SDHFGSIFUSLEOSJFNMDKELSOEJDKNWJE', 'admins', 'Admins', 0);
update user_group set group_id = 'SDHFGSIFUSLEOSJFNMDKELSOEJDKNWJE', group_name = 'admins' where group_name = 'leaders';

--changeset htmfilho:9
alter table user_account drop column birth_date;
alter table user_account drop column postal_code;

--changeset htmfilho:10
create table timezone (
    id            varchar(50) not null,
    raw_offset    int         not null,
    label         varchar(50)     null,
    default_tz    tinyint(1)      null
) engine = innodb;

alter table timezone add constraint pk_timezone primary key (id);

insert into timezone (id, raw_offset, label, default_tz)
    values ('Etc/GMT+12',           -43200000, 'ETC/GMT-12',                   false),
           ('Etc/GMT+11',           -39600000, 'International Date Line West', false),
           ('Pacific/Midway',       -39600000, 'Midway Island',                false),
           ('Pacific/Samoa',        -39600000, 'Samoa',                        false),
           ('Pacific/Honolulu',     -36000000, 'Honolulu',                     false),
           ('US/Alaska',            -32400000, 'Alaska',                       false),
           ('PST',                  -28800000, 'Pacific Time (US & Canada)',   false),
           ('America/Tijuana',      -28800000, 'Tijuana',                      false),
           ('US/Arizona',           -25200000, 'Arizona',                      false),
           ('MST',                  -25200000, 'Mountain Time (US & Canada)',  false),
           ('America/Chihuahua',    -25200000, 'Chihuahua',                    false),
           ('America/Mazatlan',     -25200000, 'Mazatlan',                     false),
           ('CST',                  -21600000, 'Central Time (US & Canada)',   false),
           ('Canada/Central',       -21600000, 'Central America',              false),
           ('America/Guatemala',    -21600000, 'Guatemala',                    false),
           ('America/Mexico_City',  -21600000, 'Mexico City',                  false),
           ('America/Monterrey',    -21600000, 'Monterrey',                    false),
           ('Canada/Saskatchewan',  -21600000, 'Saskatchewan',                 false),
           ('EST',                  -18000000, 'Eastern Time (US & Canada)',   false),
           ('America/Indianapolis', -18000000, 'Indianapolis',                 false),
           ('America/Bogota',       -18000000, 'Bogota',                       false),
           ('America/Lima',         -18000000, 'Lima',                         false),
           ('America/Caracas',      -16200000, 'Caracas',                      false),
           ('PRT',                  -14400000, 'Atlantic Time (Canada)',       false),
           ('America/La_Paz',       -14400000, 'La Paz',                       false),
           ('America/Santiago',     -14400000, 'Santiago',                     false),
           ('Canada/Newfoundland',  -12600000, 'Newfoundland',                 false),
           ('BET',                  -10800000, 'Brasilia',                     false),
           ('America/Buenos_Aires', -10800000, 'Buenos Aires',                 false),
           ('America/Fortaleza',    -10800000, 'Fortaleza',                    false),
           ('America/Godthab',      -10800000, 'Greenland',                    false),
           ('America/Noronha',       -7200000, 'Mid-Atlantic',                 false),
           ('Atlantic/Azores',       -3600000, 'Azores',                       false),
           ('Atlantic/Cape_Verde',   -3600000, 'Cape Verde Is.',               false),
           ('Etc/Greenwich',               +0, 'Greenwich Mean Time',          true),
           ('Africa/Casablanca',           +0, 'Casablanca',                   false),
           ('Europe/Dublin',               +0, 'Dublin',                       false),
           ('Europe/Guernsey',             +0, 'Edinburgh',                    false),
           ('Europe/Lisbon',               +0, 'Lisbon',                       false),
           ('Europe/London',               +0, 'London',                       false),
           ('Africa/Monrovia',             +0, 'Monrovia',                     false),
           ('Europe/Amsterdam',      +3600000, 'Amsterdam',                    false),
           ('Europe/Belgrade',       +3600000, 'Belgrade',                     false),
           ('Europe/Berlin',         +3600000, 'Berlin',                       false),
           ('Europe/Bratislava',     +3600000, 'Bratislava',                   false),
           ('Europe/Brussels',       +3600000, 'Brussels',                     false),
           ('Europe/Budapest',       +3600000, 'Budapest',                     false),
           ('Europe/Copenhagen',     +3600000, 'Copenhagen',                   false),
           ('Europe/Ljubljana',      +3600000, 'Ljubljana',                    false),
           ('Europe/Madrid',         +3600000, 'Madrid',                       false),
           ('Europe/Paris',          +3600000, 'Paris',                        false),
           ('Europe/Prague',         +3600000, 'Prague',                       false),
           ('Europe/Rome',           +3600000, 'Rome',                         false),
           ('Europe/Sarajevo',       +3600000, 'Sarajevo',                     false),
           ('Europe/Skopje',         +3600000, 'Skopje',                       false),
           ('Europe/Stockholm',      +3600000, 'Stockholm',                    false),
           ('Europe/Vienna',         +3600000, 'Vienna',                       false),
           ('Europe/Warsaw',         +3600000, 'Warsaw',                       false),
           ('Europe/Zagreb',         +3600000, 'Zagreb',                       false),
           ('Europe/Athens',         +7200000, 'Athens',                       false),
           ('Europe/Bucharest',      +7200000, 'Bucharest',                    false),
           ('Africa/Cairo',          +7200000, 'Cairo',                        false),
           ('Africa/Harare',         +7200000, 'Harare',                       false),
           ('Europe/Helsinki',       +7200000, 'Helsinki',                     false),
           ('Europe/Istanbul',       +7200000, 'Istanbul',                     false),
           ('Asia/Jerusalem',        +7200000, 'Jerusalem',                    false),
           ('Europe/Kiev',           +7200000, 'Kiev',                         false),
           ('Europe/Minsk',          +7200000, 'Minsk',                        false),
           ('Europe/Riga',           +7200000, 'Riga',                         false),
           ('Europe/Sofia',          +7200000, 'Sofia',                        false),
           ('Europe/Tallinn',        +7200000, 'Tallinn',                      false),
           ('Europe/Vilnius',        +7200000, 'Vilnius',                      false),
           ('Asia/Baghdad',         +10800000, 'Baghdad',                      false),
           ('Asia/Kuwait',          +10800000, 'Kuwait',                       false),
           ('Europe/Moscow',        +10800000, 'Moscow',                       false),
           ('Africa/Nairobi',       +10800000, 'Nairobi',                      false),
           ('Asia/Riyadh',          +10800000, 'Riyadh',                       false),
           ('Europe/Volgograd',     +10800000, 'Volgograd',                    false),
           ('Asia/Tehran',          +12600000, 'Tehran',                       false),
           ('Asia/Dubai',           +14400000, 'Dubai',                        false),
           ('Asia/Baku',            +14400000, 'Baku',                         false),
           ('Asia/Muscat',          +14400000, 'Muscat',                       false),
           ('Asia/Tbilisi',         +14400000, 'Tbilisi',                      false),
           ('Asia/Yerevan',         +14400000, 'Yerevan',                      false),
           ('Asia/Kabul',           +16200000, 'Kabul',                        false),
           ('Asia/Karachi',         +18000000, 'Karachi',                      false),
           ('Asia/Tashkent',        +18000000, 'Tashkent',                     false),
           ('Asia/Kolkata',         +19800000, 'Kolkata',                      false),
           ('IST',                  +19800000, 'New Delhi',                    false),
           ('Asia/Kathmandu',       +20700000, 'Kathmandu',                    false),
           ('Asia/Almaty',          +21600000, 'Almaty',                       false),
           ('Asia/Dhaka',           +21600000, 'Dhaka',                        false),
           ('Asia/Novosibirsk',     +21600000, 'Novosibirsk',                  false),
           ('Asia/Rangoon',         +23400000, 'Rangoon',                      false),
           ('Asia/Bangkok',         +25200000, 'Bangkok',                      false),
           ('Asia/Jakarta',         +25200000, 'Jakarta',                      false),
           ('Asia/Krasnoyarsk',     +25200000, 'Krasnoyarsk',                  false),
           ('CTT',                  +28800000, 'Beijing',                      false),
           ('Asia/Chongqing',       +28800000, 'Chongqing',                    false),
           ('Asia/Hong_Kong',       +28800000, 'Hong Kong',                    false),
           ('Asia/Irkutsk',         +28800000, 'Irkutsk',                      false),
           ('Asia/Kuala_Lumpur',    +28800000, 'Kuala Lumpur',                 false),
           ('Australia/Perth',      +28800000, 'Perth',                        false),
           ('Asia/Singapore',       +28800000, 'Singapore',                    false),
           ('Asia/Taipei',          +28800000, 'Taipei',                       false),
           ('Asia/Ulan_Bator',      +28800000, 'Ulaan Bataar',                 false),
           ('Asia/Urumqi',          +28800000, 'Urumqi',                       false),
           ('Australia/Eucla',      +31500000, 'Eucla',                        false),
           ('Asia/Seoul',           +32400000, 'Seoul',                        false),
           ('Asia/Tokyo',           +32400000, 'Tokyo',                        false),
           ('Australia/Adelaide',   +34200000, 'Adelaide',                     false),
           ('Australia/Darwin',     +34200000, 'Darwin',                       false),
           ('Australia/Brisbane',   +36000000, 'Brisbane',                     false),
           ('Australia/Canberra',   +36000000, 'Canberra',                     false),
           ('Pacific/Guam',         +36000000, 'Guam',                         false),
           ('Australia/Hobart',     +36000000, 'Hobart',                       false),
           ('Australia/Melbourne',  +36000000, 'Melbourne',                    false),
           ('Pacific/Port_Moresby', +36000000, 'Port Moresby',                 false),
           ('Australia/Sydney',     +36000000, 'Sydney',                       false),
           ('Asia/Vladivostok',     +36000000, 'Vladivostok',                  false),
           ('Asia/Magadan',         +39600000, 'Magadan',                      false),
           ('SST',                  +39600000, 'Solomon Is.',                  false),
           ('Pacific/Norfolk',      +41400000, 'Norfolk',                      false),
           ('Pacific/Auckland',     +43200000, 'Auckland',                     false),
           ('Pacific/Fiji',         +43200000, 'Fiji',                         false),
           ('Asia/Kamchatka',       +43200000, 'Kamchatka',                    false),
           ('Kwajalein',            +43200000, 'Marshall Is.',                 false),
           ('Pacific/Apia',         +46800000, 'Samoa',                        false),
           ('Pacific/Kiritimati',   +50400000, 'Kiritimati',                   false);

alter table city modify timezone varchar(50) null;

update city set timezone = 'Etc/Greenwich'       where timezone = 'UTC';
update city set timezone = 'Etc/GMT+12'          where timezone = 'UTC -12:00';
update city set timezone = 'Etc/GMT+11'          where timezone = 'UTC -11:00';
update city set timezone = 'Pacific/Honolulu'    where timezone = 'UTC -10:00';
update city set timezone = 'US/Alaska'           where timezone = 'UTC -9:00';
update city set timezone = 'America/Tijuana'     where timezone = 'UTC -8:00';
update city set timezone = 'America/Chihuahua'   where timezone = 'UTC -7:00';
update city set timezone = 'America/Mexico_City' where timezone = 'UTC -6:00';
update city set timezone = 'America/Bogota'      where timezone = 'UTC -5:00';
update city set timezone = 'America/Santiago'    where timezone = 'UTC -4:00';
update city set timezone = 'America/Fortaleza'   where timezone = 'UTC -3:00';
update city set timezone = 'America/Noronha'     where timezone = 'UTC -2:00';
update city set timezone = 'Atlantic/Azores'     where timezone = 'UTC -1:00';
update city set timezone = 'Etc/Greenwich'       where timezone = 'UTC +0:00';
update city set timezone = 'Europe/Brussels'     where timezone = 'UTC +1:00';
update city set timezone = 'Europe/Athens'       where timezone = 'UTC +2:00';
update city set timezone = 'Asia/Baghdad'        where timezone = 'UTC +3:00';
update city set timezone = 'Asia/Dubai'          where timezone = 'UTC +4:00';
update city set timezone = 'Asia/Karachi'        where timezone = 'UTC +5:00';
update city set timezone = 'Asia/Dhaka'          where timezone = 'UTC +6:00';
update city set timezone = 'Asia/Jakarta'        where timezone = 'UTC +7:00';
update city set timezone = 'Asia/Hong_Kong'      where timezone = 'UTC +8:00';
update city set timezone = 'Asia/Tokyo'          where timezone = 'UTC +9:00';
update city set timezone = 'Australia/Melbourne' where timezone = 'UTC +10:00';
update city set timezone = 'Asia/Magadan'        where timezone = 'UTC +11:00';
update city set timezone = 'Asia/Kamchatka'      where timezone = 'UTC +12:00';
update city set timezone = 'Pacific/Apia'        where timezone = 'UTC +13:00';
update city set timezone = 'Pacific/Kiritimati'  where timezone = 'UTC +14:00';

--changeset joewong:11
alter table user_account change gender gender tinyint(1) null;

--changeset htmfilho:12
insert into access_group (id, name, description, user_default) values
    ('PQOWKSIFUSLEOSJFNMDKKIJGEJDKNWJE', 'speakers', 'Speakers', 0);

--changeset htmfilho:13
insert into access_group (id, name, description, user_default) values
    ('PQOWKSIQMSLDKFJDUMDKKIJGEJDKNWJE', 'leaders', 'Leaders', 0);
    
--changeset kenia:14
create table project (
	id           char(32)     not null,
	name         varchar(100) not null,
	description  text  null,
	website      varchar(100) null,
	source       varchar(100) null
) engine = innodb;

alter table project add constraint pk_project primary key (id);
