package org.diploma.app.scheduling;

import org.diploma.app.model.db.entity.GlobalStatistic;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.repository.GlobalStatisticRepository;
import org.diploma.app.repository.PostsRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private PostsRepository postsRepository;
    private GlobalStatisticRepository globalStatisticRepository;

    public BatchConfig(
        JobBuilderFactory jobBuilderFactory,
        StepBuilderFactory stepBuilderFactory,
        PostsRepository postsRepository,
        GlobalStatisticRepository globalStatisticRepository
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.postsRepository = postsRepository;
        this.globalStatisticRepository = globalStatisticRepository;
    }

    @Bean
    public Job globalStatisticJob() {
        return jobBuilderFactory.get("globalStatisticJob").flow(step()).end().build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step1")
            .<Integer, GlobalStatistic> chunk(1)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build();
    }

    public RepositorySingleQueryItemReader<Integer> reader() {
        return new RepositorySingleQueryItemReader<>(
            postsRepository, "countByIsActiveAndModerationStatus", true, ModerationStatus.ACCEPTED);
    }

    public GlobalStatisticItemProcessor<Integer> processor() {
        return new GlobalStatisticItemProcessor<>("active_and_moderator_accepted_posts_count");
    }

    public RepositoryItemWriter<GlobalStatistic> writer() {
        return new RepositoryItemWriterBuilder<GlobalStatistic>().repository(globalStatisticRepository).build();
    }
}
