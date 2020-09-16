drop table if exists tags;

create table tags (
    id integer not null auto_increment,
    name varchar(255) not null,
    primary key (id)
);

alter table tags add constraint UK_tags_name unique (name);