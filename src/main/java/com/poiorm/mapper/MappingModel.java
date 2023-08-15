package com.poiorm.mapper;

import java.util.function.Consumer;

public class MappingModel<T> {
    private final Consumer<T> consumer;
    private final Class<T> type;

    private final T instance;

    public MappingModel(Consumer<T> consumer, Class<T> type, T instance) {
        this.consumer = consumer;
        this.type = type;
        this.instance = instance;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    public Class<T> getType() {
        return type;
    }

    public T getInstance() {
        return instance;
    }
}
