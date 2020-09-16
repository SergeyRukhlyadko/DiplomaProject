drop table if exists post_comments;

create table post_comments (
    id integer not null auto_increment,
    text TEXT not null,
    time datetime(6) not null,
    parent_id integer,
    post_id integer not null,
    user_id integer not null,
    primary key (id)
);