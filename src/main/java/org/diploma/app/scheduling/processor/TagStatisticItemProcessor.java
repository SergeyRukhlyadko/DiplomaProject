package org.diploma.app.scheduling.processor;

import org.diploma.app.model.db.entity.TagStatistic;
import org.diploma.app.model.db.entity.projection.TagIdAndPostsCount;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

public class TagStatisticItemProcessor implements ItemProcessor<TagIdAndPostsCount, TagStatistic> {

    private String globalStatisticName;
    private String globalStatisticValue;

    public TagStatisticItemProcessor(String globalStatisticName) {
        this.globalStatisticName = globalStatisticName;
    }

    @BeforeStep
    public void retrieveGlobalStatisticValue(StepExecution stepExecution) {
        ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
        globalStatisticValue = String.valueOf(jobContext.get(globalStatisticName));
    }

    @Override
    @Nullable
    public TagStatistic process(TagIdAndPostsCount item) {
        float weight = (float) item.getPostsCount() / Integer.parseInt(globalStatisticValue);
        return new TagStatistic(item.getId(), item.getPostsCount(), weight);
    }
}
