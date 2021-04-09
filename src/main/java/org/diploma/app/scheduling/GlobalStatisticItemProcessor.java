package org.diploma.app.scheduling;

import org.diploma.app.model.db.entity.GlobalStatistic;
import org.springframework.batch.item.ItemProcessor;

public class GlobalStatisticItemProcessor<T> implements ItemProcessor<T, GlobalStatistic> {

    private String name;

    public GlobalStatisticItemProcessor(String name) {
        this.name = name;
    }

    @Override
    public GlobalStatistic process(T value) {
        return new GlobalStatistic(name, String.valueOf(value));
    }
}
