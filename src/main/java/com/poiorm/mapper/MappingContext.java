package com.poiorm.mapper;

import java.util.function.Consumer;

public record MappingContext<T>(Consumer<T> consumer, Class<T> type, T instance) {
}
