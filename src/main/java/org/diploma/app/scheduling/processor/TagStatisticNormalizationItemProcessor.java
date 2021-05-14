package org.diploma.app.scheduling.processor;

import org.diploma.app.model.db.entity.TagStatistic;
import org.diploma.app.util.NormalizationAlgorithm;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

public class TagStatisticNormalizationItemProcessor implements ItemProcessor<TagStatistic, TagStatistic> {

    private float min;
    private float max;

    @BeforeStep
    public void retrieveMinMaxWeight(StepExecution stepExecution) {
        ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
        min = (float) jobContext.get("min");
        max = (float) jobContext.get("max");
    }

    @Override
    @Nullable
    public TagStatistic process(TagStatistic item) {
        float normalizedWeight = NormalizationAlgorithm.normalizeMinMax(min, max, item.getWeight());
        item.setNormalizedWeight(normalizedWeight);
        return item;
    }
}
