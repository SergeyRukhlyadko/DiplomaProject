drop table if exists users;

create table users (
    id integer not null auto_increment,
    code varchar(255),
    email varchar(255) not null,
    is_moderator bit not null,
    name varchar(255) not null,
    password varchar(255) not null,
    photo TEXT,
    reg_time datetime(6) not null,
    primary key (id)
);