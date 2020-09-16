drop table if exists posts;

create table posts (
    id integer not null auto_increment,
    is_active bit not null,
    moderation_status varchar(255) not null,
    text TEXT not null,
    time datetime(6) not null,
    title varchar(255) not null,
    view_count integer not null,
    moderator_id integer,
    user_id integer not null,
    primary key (id)
);