package org.diploma.app.scheduling;

import org.diploma.app.repository.CaptchaCodesRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class ScheduledTasks {

    private CaptchaCodesRepository captchaCodesRepository;
    private JobLauncher jobLauncher;
    private Job job;

    public ScheduledTasks(CaptchaCodesRepository captchaCodesRepository, JobLauncher jobLauncher, Job job) {
        this.captchaCodesRepository = captchaCodesRepository;
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    /*
        All captcha created over one hour are deleted
     */
    @Scheduled(cron = "@hourly")
    @Transactional
    public void clearCaptcha() {
        captchaCodesRepository.deleteByTimeLessThen(LocalDateTime.now().minusHours(1));
    }

    @Scheduled(cron = "@hourly")
    public void countActiveAndModeratorAcceptedPosts() throws JobExecutionException {
        jobLauncher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }
}
