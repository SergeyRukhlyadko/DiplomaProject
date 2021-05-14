package org.diploma.app.scheduling.job;

import org.diploma.app.scheduling.tasklet.CleaningCaptchaTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CleaningCaptchaJobConfiguration {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    public CleaningCaptchaJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job cleaningCaptchaJob(CleaningCaptchaTasklet cleaningCaptchaTasklet) {
        return jobBuilderFactory.get("clearCaptchaJob")
            .flow(stepBuilderFactory.get("cleaningCaptchaStep").tasklet(cleaningCaptchaTasklet).build())
            .end()
            .build();
    }
}
