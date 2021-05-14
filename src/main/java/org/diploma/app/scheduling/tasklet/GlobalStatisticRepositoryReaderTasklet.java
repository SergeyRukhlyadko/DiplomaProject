package org.diploma.app.scheduling.tasklet;

import org.diploma.app.repository.GlobalStatisticRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class GlobalStatisticRepositoryReaderTasklet implements Tasklet {

    private GlobalStatisticRepository globalStatisticRepository;

    private String name;

    public GlobalStatisticRepositoryReaderTasklet(GlobalStatisticRepository globalStatisticRepository) {
        this.globalStatisticRepository = globalStatisticRepository;
    }

    @Override
    @Nullable
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        globalStatisticRepository.findValueByName(name).ifPresent(
            (value) ->
                contribution.getStepExecution().getJobExecution().getExecutionContext()
                    .put(name, value)
        );

        return RepeatStatus.FINISHED;
    }

    public void setGlobalStatisticName(String name) {
        this.name = name;
    }
}
