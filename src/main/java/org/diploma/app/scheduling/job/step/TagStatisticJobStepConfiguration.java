package org.diploma.app.scheduling.job.step;

import org.diploma.app.model.db.entity.TagStatistic;
import org.diploma.app.model.db.entity.projection.TagIdAndPostsCount;
import org.diploma.app.scheduling.processor.TagStatisticItemProcessor;
import org.diploma.app.scheduling.processor.TagStatisticNormalizationItemProcessor;
import org.diploma.app.scheduling.tasklet.GlobalStatisticRepositoryReaderTasklet;
import org.diploma.app.scheduling.tasklet.TagStatisticRepositoryReaderTasklet;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TagStatisticJobStepConfiguration {

    private StepBuilderFactory stepBuilderFactory;

    public TagStatisticJobStepConfiguration(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step readGlobalStatisticStep(GlobalStatisticRepositoryReaderTasklet globalStatisticRepositoryReaderTasklet) {
        globalStatisticRepositoryReaderTasklet.setGlobalStatisticName("active_and_moderator_accepted_posts_count");
        return stepBuilderFactory.get("readGlobalStatisticStep")
            .tasklet(globalStatisticRepositoryReaderTasklet)
            .build();
    }

    @Bean
    public Step tagStatisticCalculateWeightStep(
        RepositoryItemReader<TagIdAndPostsCount> tagIdAndPostsCountRepositoryItemReader,
        RepositoryItemWriter<TagStatistic> tagStatisticRepositoryItemWriter
    ) {
        return stepBuilderFactory.get("tagStatisticCalculateWeightStep")
            .<TagIdAndPostsCount, TagStatistic>chunk(10)
            .reader(tagIdAndPostsCountRepositoryItemReader)
            .processor(new TagStatisticItemProcessor("active_and_moderator_accepted_posts_count"))
            .writer(tagStatisticRepositoryItemWriter)
            .build();
    }

    @Bean
    public Step tagStatisticFindMinMaxWeightStep(
        TagStatisticRepositoryReaderTasklet tagStatisticRepositoryReaderTasklet
    ) {
        return stepBuilderFactory.get("tagStatisticFindMinMaxWeightStep")
            .tasklet(tagStatisticRepositoryReaderTasklet)
            .build();
    }

    @Bean
    public Step tagStatisticNormalizationStep(
        RepositoryItemReader<TagStatistic> tagStatisticRepositoryItemReader,
        RepositoryItemWriter<TagStatistic> tagStatisticRepositoryItemWriter
    ) {
        return stepBuilderFactory.get("tagStatisticNormalizationStep")
            .<TagStatistic, TagStatistic>chunk(10)
            .reader(tagStatisticRepositoryItemReader)
            .processor(new TagStatisticNormalizationItemProcessor())
            .writer(tagStatisticRepositoryItemWriter)
            .build();
    }
}
