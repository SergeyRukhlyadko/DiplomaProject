drop table if exists post_votes;

create table post_votes (
    id integer not null auto_increment,
    time datetime(6) not null,
    value tinyint not null,
    post_id integer not null,
    user_id integer not null,
    primary key (id)
);
