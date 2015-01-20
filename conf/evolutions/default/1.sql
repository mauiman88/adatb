# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table address (
  id                        bigint not null,
  country_id                bigint,
  city_id                   bigint,
  street                    varchar(255),
  street_number             varchar(255),
  constraint pk_address primary key (id))
;

create table city (
  id                        bigint not null,
  name                      varchar(255),
  zip_code                  varchar(255),
  country_id                bigint,
  constraint pk_city primary key (id))
;

create table client (
  id                        bigint not null,
  name                      varchar(255),
  address_id                bigint,
  email                     varchar(255),
  phone_number              varchar(255),
  tax_number                varchar(255),
  constraint pk_client primary key (id))
;

create table country (
  id                        bigint not null,
  name                      varchar(255),
  country_code              varchar(255),
  constraint uq_country_country_code unique (country_code),
  constraint pk_country primary key (id))
;

create table driver (
  id                        bigint not null,
  name                      varchar(255),
  address_id                bigint,
  phone_number              varchar(255),
  licence                   varchar(6),
  countries                 varchar(255),
  total_kilometers          decimal(15,4),
  since                     timestamp,
  constraint ck_driver_licence check (licence in ('B_TYPE','C_TYPE','E_TYPE')),
  constraint pk_driver primary key (id))
;

create table item (
  id                        bigint not null,
  product_name              varchar(255),
  weight                    decimal(15,4),
  from_address_id           bigint,
  product_type              varchar(8),
  constraint ck_item_product_type check (product_type in ('FRAGILE','FLEXIBLE')),
  constraint pk_item primary key (id))
;

create table orders (
  id                        bigint not null,
  created                   timestamp,
  address_id                bigint,
  client_id                 bigint,
  order_item_id             bigint,
  order_state               varchar(20),
  selected_date             timestamp,
  amount                    decimal(15,4),
  description               varchar(255),
  feedback_msg              varchar(255),
  delivered                 boolean,
  constraint ck_orders_order_state check (order_state in ('NEW','VERIFIED','WAITING_FOR_SHIPPING','SHIPPED')),
  constraint pk_orders primary key (id))
;

create table transport (
  id                        bigint not null,
  date                      timestamp,
  driver_id                 bigint,
  truck_id                  bigint,
  distance                  decimal(15,4),
  constraint pk_transport primary key (id))
;

create table truck (
  id                        bigint not null,
  lp_number                 varchar(255),
  truck_type                varchar(6),
  kg_limit                  bigint,
  mo_ttest_date             timestamp,
  available                 boolean,
  constraint ck_truck_truck_type check (truck_type in ('SMALL','MEDIUM','BIG')),
  constraint pk_truck primary key (id))
;

create table users (
  id                        bigint not null,
  name                      varchar(255),
  email                     varchar,
  password                  varchar(255),
  role                      varchar(6),
  client_id                 bigint,
  constraint ck_users_role check (role in ('ADMIN','CLIENT')),
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id))
;


create table transport_item (
  transport_id                   bigint not null,
  item_id                        bigint not null,
  constraint pk_transport_item primary key (transport_id, item_id))
;
create sequence address_seq;

create sequence city_seq;

create sequence client_seq;

create sequence country_seq;

create sequence driver_seq;

create sequence item_seq;

create sequence orders_seq;

create sequence transport_seq;

create sequence truck_seq;

create sequence users_seq;

alter table address add constraint fk_address_country_1 foreign key (country_id) references country (id);
create index ix_address_country_1 on address (country_id);
alter table address add constraint fk_address_city_2 foreign key (city_id) references city (id);
create index ix_address_city_2 on address (city_id);
alter table city add constraint fk_city_country_3 foreign key (country_id) references country (id);
create index ix_city_country_3 on city (country_id);
alter table client add constraint fk_client_address_4 foreign key (address_id) references address (id);
create index ix_client_address_4 on client (address_id);
alter table driver add constraint fk_driver_address_5 foreign key (address_id) references address (id);
create index ix_driver_address_5 on driver (address_id);
alter table item add constraint fk_item_fromAddress_6 foreign key (from_address_id) references address (id);
create index ix_item_fromAddress_6 on item (from_address_id);
alter table orders add constraint fk_orders_address_7 foreign key (address_id) references address (id);
create index ix_orders_address_7 on orders (address_id);
alter table orders add constraint fk_orders_client_8 foreign key (client_id) references client (id);
create index ix_orders_client_8 on orders (client_id);
alter table orders add constraint fk_orders_orderItem_9 foreign key (order_item_id) references item (id);
create index ix_orders_orderItem_9 on orders (order_item_id);
alter table transport add constraint fk_transport_driver_10 foreign key (driver_id) references driver (id);
create index ix_transport_driver_10 on transport (driver_id);
alter table transport add constraint fk_transport_truck_11 foreign key (truck_id) references truck (id);
create index ix_transport_truck_11 on transport (truck_id);
alter table users add constraint fk_users_client_12 foreign key (client_id) references client (id);
create index ix_users_client_12 on users (client_id);



alter table transport_item add constraint fk_transport_item_transport_01 foreign key (transport_id) references transport (id);

alter table transport_item add constraint fk_transport_item_item_02 foreign key (item_id) references item (id);

# --- !Downs

drop table if exists address cascade;

drop table if exists city cascade;

drop table if exists client cascade;

drop table if exists country cascade;

drop table if exists driver cascade;

drop table if exists item cascade;

drop table if exists orders cascade;

drop table if exists transport cascade;

drop table if exists transport_item cascade;

drop table if exists truck cascade;

drop table if exists users cascade;

drop sequence if exists address_seq;

drop sequence if exists city_seq;

drop sequence if exists client_seq;

drop sequence if exists country_seq;

drop sequence if exists driver_seq;

drop sequence if exists item_seq;

drop sequence if exists orders_seq;

drop sequence if exists transport_seq;

drop sequence if exists truck_seq;

drop sequence if exists users_seq;

