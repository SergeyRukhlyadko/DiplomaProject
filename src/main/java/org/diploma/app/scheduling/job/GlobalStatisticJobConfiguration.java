package org.diploma.app.scheduling.job;

import org.diploma.app.model.db.entity.GlobalStatistic;
import org.diploma.app.scheduling.processor.GlobalStatisticItemProcessor;
import org.diploma.app.scheduling.reader.RepositorySingleQueryItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalStatisticJobConfiguration {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    public GlobalStatisticJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job globalStatisticJob(
        RepositorySingleQueryItemReader<Integer> postsCountByIsActiveAndModerationStatusRepositorySingleQueryItemReader,
        RepositoryItemWriter<GlobalStatistic> globalStatisticRepositoryItemWriter
    ) {
        Step step = stepBuilderFactory.get("globalStatisticPostsCountStep")
            .<Integer, GlobalStatistic> chunk(1)
            .reader(postsCountByIsActiveAndModerationStatusRepositorySingleQueryItemReader)
            .processor(new GlobalStatisticItemProcessor<>("active_and_moderator_accepted_posts_count"))
            .writer(globalStatisticRepositoryItemWriter)
            .build();

        return jobBuilderFactory.get("globalStatisticJob").flow(step).end().build();
    }
}
