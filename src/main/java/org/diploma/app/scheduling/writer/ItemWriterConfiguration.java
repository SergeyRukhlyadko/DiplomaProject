package org.diploma.app.scheduling.writer;

import org.diploma.app.model.db.entity.GlobalStatistic;
import org.diploma.app.model.db.entity.TagStatistic;
import org.diploma.app.repository.GlobalStatisticRepository;
import org.diploma.app.repository.TagStatisticRepository;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemWriterConfiguration {

    @Bean
    public RepositoryItemWriter<GlobalStatistic> globalStatisticRepositoryItemWriter(
        GlobalStatisticRepository globalStatisticRepository
    ) {
        return new RepositoryItemWriterBuilder<GlobalStatistic>().repository(globalStatisticRepository).build();
    }

    @Bean
    public RepositoryItemWriter<TagStatistic> tagStatisticRepositoryItemWriter(
        TagStatisticRepository tagStatisticRepository
    ) {
        return new RepositoryItemWriterBuilder<TagStatistic>().repository(tagStatisticRepository).build();
    }
}
