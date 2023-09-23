drop table if exists users cascade;
drop table if exists requests cascade;
drop table if exists items cascade;
drop table if exists bookings cascade;
drop table if exists comments cascade;
create table if not exists users (
  id bigint not null,
  name varchar(255) not null,
  email varchar(512) not null,
  constraint pk_user
    primary key (id),
  constraint UQ_USER_EMAIL
    unique (email)
);
create table if not exists requests (
  id bigint not null,
  description clob,
  requestor_id bigint,
  created timestamp,
  foreign key (requestor_id)
  references users (id) on delete cascade,
  constraint pk_request
    primary key (id)
);
create table if not exists items (
  id bigint not null,
  name varchar(255) not null,
  description clob,
  is_avaliable boolean not null,
  owner_id bigint,
  request_id bigint,
  foreign key (owner_id)
  references users (id) on delete cascade,
  foreign key (request_id)
  references requests (id),
  constraint pk_item
    primary key (id)
);
create table if not exists bookings (
  id bigint not null,
  start_date timestamp,
  end_date timestamp,
  item_id bigint,
  booker_id bigint,
  status varchar(25),
  foreign key (item_id)
  references items (id) on delete cascade,
  foreign key (booker_id)
  references users (id) on delete cascade,
  constraint pk_booking
    primary key (id)
);
create table if not exists comments (
  id bigint not null,
  text clob,
  item_id bigint,
  author_id bigint,
  created timestamp,
  foreign key (item_id)
  references items (id) on delete cascade,
  foreign key (author_id)
  references users (id) on delete cascade,
  constraint pk_comment
    primary key (id)
);
