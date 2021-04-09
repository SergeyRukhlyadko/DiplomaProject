drop table if exists batch_job_execution_context;

create table batch_job_execution_context (
    job_execution_id bigint primary key,
    short_context varchar(2500) not null,
    serialized_context longtext
);