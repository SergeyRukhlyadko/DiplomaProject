package org.diploma.app.scheduling.job;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TagStatisticJobConfiguration {

    private JobBuilderFactory jobBuilderFactory;

    public TagStatisticJobConfiguration(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    public Job tagStatisticJob(
        Step readGlobalStatisticStep,
        Step tagStatisticCalculateWeightStep,
        Step tagStatisticFindMinMaxWeightStep,
        Step tagStatisticNormalizationStep
    ) {
        return jobBuilderFactory.get("tagStatisticJob")
            .start(readGlobalStatisticStep)
            .next(tagStatisticCalculateWeightStep)
            .next(tagStatisticFindMinMaxWeightStep)
            .next(tagStatisticNormalizationStep)
            .build();
    }

    @AfterStep
    private ExitStatus afterStep(StepExecution stepExecution) {
        ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
        if (!jobContext.containsKey("active_and_moderator_accepted_posts_count") || stepExecution.getReadCount() == 0) {
            return ExitStatus.FAILED;
        }

        return null;
    }
}
