drop table if exists batch_step_execution_context;

create table batch_step_execution_context (
    step_execution_id bigint primary key,
    short_context varchar(2500) not null,
    serialized_context longtext
);