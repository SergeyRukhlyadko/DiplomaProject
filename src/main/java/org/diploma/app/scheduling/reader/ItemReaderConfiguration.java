package org.diploma.app.scheduling.reader;

import org.diploma.app.model.db.entity.TagStatistic;
import org.diploma.app.model.db.entity.enumeration.ModerationStatus;
import org.diploma.app.model.db.entity.projection.TagIdAndPostsCount;
import org.diploma.app.repository.PostsRepository;
import org.diploma.app.repository.TagStatisticRepository;
import org.diploma.app.repository.TagsRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Map;

@Configuration
public class ItemReaderConfiguration {

    @Bean
    public RepositorySingleQueryItemReader<Integer> postsCountByIsActiveAndModerationStatusRepositorySingleQueryItemReader(
        PostsRepository postsRepository
    ) {
        return new RepositorySingleQueryItemReader<>(
            postsRepository, "countByIsActiveAndModerationStatus", true, ModerationStatus.ACCEPTED);
    }

    @Bean
    public RepositoryItemReader<TagStatistic> tagStatisticRepositoryItemReader(
        TagStatisticRepository tagStatisticRepository
    ) {
        return new RepositoryItemReaderBuilder<TagStatistic>()
            .name("tagStatisticRepositoryItemReader")
            .repository(tagStatisticRepository)
            .methodName("findAll")
            .sorts(Map.of("tagId", Sort.DEFAULT_DIRECTION))
            .build();
    }

    @Bean
    public RepositoryItemReader<TagIdAndPostsCount> tagIdAndPostsCountRepositoryItemReader(
        TagsRepository tagsRepository
    ) {
        return new RepositoryItemReaderBuilder<TagIdAndPostsCount>()
            .name("tagIdAndPostsCountRepositoryItemReader")
            .repository(tagsRepository)
            .methodName("findByIsActiveAndModerationStatusGroupById")
            .arguments(true, ModerationStatus.ACCEPTED, TagIdAndPostsCount.class)
            .sorts(Map.of("id", Sort.DEFAULT_DIRECTION))
            .build();
    }
}
