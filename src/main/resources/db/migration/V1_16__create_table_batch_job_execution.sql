drop table if exists batch_job_execution;

create table batch_job_execution (
    job_execution_id bigint primary key,
    version bigint,
    job_instance_id bigint not null,
    create_time timestamp not null,
    start_time timestamp default null,
    end_time timestamp default null,
    status varchar(10),
    exit_code varchar(20),
    exit_message varchar(2500),
    last_updated timestamp,
    job_configuration_location varchar(2500) null
);