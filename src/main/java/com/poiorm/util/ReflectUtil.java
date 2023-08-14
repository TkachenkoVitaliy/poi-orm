package com.poiorm.util;

import com.poiorm.exception.PoiOrmInstantiationException;
import com.poiorm.exception.MappingException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class ReflectUtil {

    private ReflectUtil() {
    }

    public static <T> T newInstance(Class<T> type) {
        T instance;
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            instance = constructor.newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new PoiOrmInstantiationException(String.format("Cannot create a new instance of %s", type.getName()), e);
        }
        return instance;
    }

    public static void setFieldValue(Field field, Object value, Object instance) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new MappingException(String.format("Unexpected cast type {%s} of field %s", value, field.getName()));
        }
    }
}
