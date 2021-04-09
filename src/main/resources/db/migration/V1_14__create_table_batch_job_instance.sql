drop table if exists batch_job_instance;

create table batch_job_instance (
    job_instance_id bigint primary key,
    version bigint,
    job_name varchar(100) not null,
    job_key varchar(2500)
);