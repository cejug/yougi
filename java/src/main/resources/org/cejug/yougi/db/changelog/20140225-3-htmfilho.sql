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
