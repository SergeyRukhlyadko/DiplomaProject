package org.diploma.app.scheduling.processor;

import org.diploma.app.model.db.entity.GlobalStatistic;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

public class GlobalStatisticItemProcessor<T> implements ItemProcessor<T, GlobalStatistic> {

    private String name;

    public GlobalStatisticItemProcessor(String name) {
        this.name = name;
    }

    @Override
    @Nullable
    public GlobalStatistic process(T value) {
        return new GlobalStatistic(name, String.valueOf(value));
    }
}
