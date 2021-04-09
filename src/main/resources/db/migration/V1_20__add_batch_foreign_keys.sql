alter table batch_job_execution_params add constraint job_exec_params_fk
    foreign key (job_execution_id) references batch_job_execution (job_execution_id);

alter table batch_job_execution add constraint job_instance_execution_fk
    foreign key (job_instance_id) references batch_job_instance (job_instance_id);

alter table batch_step_execution add constraint job_execution_step_fk
    foreign key (job_execution_id) references batch_job_execution (job_execution_id);

alter table batch_job_execution_context add constraint job_exec_ctx_fk
    foreign key (job_execution_id) references batch_job_execution (job_execution_id);

alter table batch_step_execution_context add constraint step_exec_ctx_fk
    foreign key (step_execution_id) references batch_step_execution (step_execution_id);