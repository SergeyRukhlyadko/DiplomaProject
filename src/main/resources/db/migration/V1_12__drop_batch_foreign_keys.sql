call proc_drop_foreign_key('batch_job_execution_params', 'job_exec_params_fk');
call proc_drop_foreign_key('batch_job_execution', 'job_instance_execution_fk');
call proc_drop_foreign_key('batch_step_execution', 'job_execution_step_fk');
call proc_drop_foreign_key('batch_job_execution_context', 'job_exec_ctx_fk');
call proc_drop_foreign_key('batch_step_execution_context', 'step_exec_ctx_fk');