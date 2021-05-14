drop table if exists tag_statistic;

create table tag_statistic (
    tag_id int primary key,
    active_and_moderator_accepted_posts_count int,
    weight float,
    normalized_weight float,
    constraint FK_tag_statistic_tag_id foreign key (tag_id) references tags (id)
);