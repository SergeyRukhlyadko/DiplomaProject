package org.diploma.app.scheduling.tasklet;

import org.diploma.app.repository.TagStatisticRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class TagStatisticRepositoryReaderTasklet implements Tasklet {

    private TagStatisticRepository tagStatisticRepository;

    public TagStatisticRepositoryReaderTasklet(TagStatisticRepository tagStatisticRepository) {
        this.tagStatisticRepository = tagStatisticRepository;
    }

    @Override
    @Nullable
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        tagStatisticRepository.findMinMaxWeight().ifPresent(
            (minMaxWeight) -> {
                ExecutionContext jobContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
                jobContext.put("min", minMaxWeight.getMin());
                jobContext.put("max", minMaxWeight.getMax());
            }
        );

        return RepeatStatus.FINISHED;
    }
}
