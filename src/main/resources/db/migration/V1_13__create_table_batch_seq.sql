drop table if exists batch_step_execution_seq;
create table batch_step_execution_seq (id bigint not null);
insert into batch_step_execution_seq values(0);

drop table if exists batch_job_execution_seq;
create table batch_job_execution_seq (id bigint not null);
insert into batch_job_execution_seq values(0);

drop table if exists batch_job_seq;
create table batch_job_seq (id bigint not null);
insert into batch_job_seq values(0);