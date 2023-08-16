package com.poiorm.util;

import com.poiorm.annotation.IdentifierMethod;
import com.poiorm.exception.PoiOrmInstantiationException;
import com.poiorm.exception.PoiOrmMappingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public final class ReflectUtil {

    private ReflectUtil() {
    }

    public static <T> T newEmptyInstance(Class<T> type) {
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
            throw new PoiOrmMappingException(String.format("Unexpected cast type {%s} of field %s", value, field.getName()));
        }
    }

    public static boolean isModifierSet(int allModifiers, ModifierType modifier) {
        return (allModifiers & modifier.getValue()) > 0;
    }

    public static Object performStaticAnnotatedMethod(Class<?> type, Object value, Class<? extends Annotation> annotationClass) {
        Method[] declaredMethods = type.getDeclaredMethods();
        List<Method> annotatedMethods = Arrays.stream(declaredMethods)
                .filter(method -> method.getAnnotation(annotationClass) != null)
                .toList();
        if (annotatedMethods.size() < 1) {
            throw new PoiOrmMappingException(
                    String.format(
                            "Class {%s} don't have annotated {%s} method",
                            type.getName(),
                            annotationClass.getName()
                    )
            );
        }
        if (annotatedMethods.size() > 1) {
            throw new PoiOrmMappingException(
                    String.format(
                            "Class {%s} have more than one annotated {%s} method",
                            type.getName(),
                            annotationClass.getName()
                    )
            );
        }
        Method annotatedMethod = annotatedMethods.get(0);
        int modifiers = annotatedMethod.getModifiers();
        if (!isModifierSet(modifiers, ModifierType.STATIC)) {
            throw new PoiOrmMappingException(
                    String.format(
                            "Annotated method - {%s} should be static - error in class {%s}", annotatedMethod.getName(), type.getName()
                    )
            );
        }
        annotatedMethod.setAccessible(true);
        try {
            return annotatedMethod.invoke(null, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PoiOrmMappingException(
                    String.format("Cant perform annotated method - {%s} from class {%s}",
                            annotatedMethod.getName(), type.getName()),
                    e
            );
        }
    }
}
