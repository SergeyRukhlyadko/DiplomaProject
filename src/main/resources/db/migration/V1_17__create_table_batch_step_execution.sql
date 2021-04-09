drop table if exists batch_step_execution;

create table batch_step_execution (
    step_execution_id bigint primary key,
    version bigint not null,
    step_name varchar(100) not null,
    job_execution_id bigint not null,
    start_time timestamp not null,
    end_time timestamp default null,
    status varchar(10),
    commit_count bigint,
    read_count bigint,
    filter_count bigint,
    write_count bigint,
    read_skip_count bigint,
    write_skip_count bigint,
    process_skip_count bigint,
    rollback_count bigint,
    exit_code varchar(20),
    exit_message varchar(2500),
    last_updated timestamp
);