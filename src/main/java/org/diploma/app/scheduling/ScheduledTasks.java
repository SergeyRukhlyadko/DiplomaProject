package org.diploma.app.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class ScheduledTasks {

    private JobLauncher jobLauncher;
    private Job cleaningCaptchaJob;
    private Job globalStatisticJob;
    private Job tagStatisticJob;

    public ScheduledTasks(
        JobLauncher jobLauncher,
        @Qualifier("cleaningCaptchaJob") Job cleaningCaptchaJob,
        @Qualifier("globalStatisticJob") Job globalStatisticJob,
        @Qualifier("tagStatisticJob") Job tagStatisticJob
    ) {
        this.jobLauncher = jobLauncher;
        this.cleaningCaptchaJob = cleaningCaptchaJob;
        this.globalStatisticJob = globalStatisticJob;
        this.tagStatisticJob = tagStatisticJob;
    }

    @Scheduled(cron = "@hourly")
    void cleaningCaptcha() {
        runJob(cleaningCaptchaJob);
    }

    @Scheduled(cron = "@hourly")
    void collectGlobalStatistic() {
        runJob(globalStatisticJob);
    }

    @Scheduled(cron = "@hourly")
    void collectTagStatistic() {
        runJob(tagStatisticJob);
    }

    private void runJob(Job job) {
        try {
            jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        } catch (JobExecutionException e) {
            log.error(e.getMessage(), e);
        }
    }
}
