drop table if exists global_statistic;

create table global_statistic (
    name varchar(128) not null,
    value varchar(128),
    primary key (name)
);

insert into global_statistic (name) values('active_and_moderator_accepted_posts_count');
