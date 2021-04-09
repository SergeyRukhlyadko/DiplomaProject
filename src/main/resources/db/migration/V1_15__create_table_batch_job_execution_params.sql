drop table if exists batch_job_execution_params;

create table batch_job_execution_params (
    job_execution_id bigint not null,
    type_cd varchar(6) not null,
    key_name varchar(100) not null,
    string_val varchar(250),
    date_val datetime default null,
    long_val bigint,
    double_val double precision,
    identifying char(1) not null
);