package com.poiorm.util;

import com.poiorm.annotation.IdentifierMethod;
import com.poiorm.annotation.RowObject;
import com.poiorm.exception.PoiOrmInstantiationException;
import com.poiorm.exception.PoiOrmMappingException;

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

    public static Function<String, Boolean> getIdentifierMethod(Class<?> type) {
        Method[] declaredMethods = type.getDeclaredMethods();
        List<Method> identifierMethods = Arrays.stream(declaredMethods)
                .filter(method -> method.getAnnotation(IdentifierMethod.class) != null)
                .toList();
        if (identifierMethods.size() < 1) {
            throw new PoiOrmMappingException(String.format("Object %s don't have IdentifierMethod", type.getName()));
        }
        if (identifierMethods.size() > 1) {
            throw new PoiOrmMappingException(String.format("Object %s have more than one IdentifierMethod", type.getName()));
        }
        Method identifierMethod = identifierMethods.get(0);
        int modifiers = identifierMethod.getModifiers();
        if (isModifierSet(modifiers, ModifierType.STATIC)) {
            throw new PoiOrmMappingException(
                    String.format("IdentifierMethod should be static - error in class %s", type.getName())
            );
        }
        return value -> {
            try {
                return (Boolean) identifierMethod.invoke(null, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                //TODO сделать нормальный экспешн
                throw new RuntimeException(e);
            }
        };
    }

    //TODO переместить, в Utils должны быть методы которые ничего не знают о внешнем окружении
    public static boolean invokerIdentifierMethod(Function<String, Boolean> function, String value) {
        return function.apply(value);
    }
    public static boolean performIdentifierMethod(Class<?> type, Object value) {
        Method[] declaredMethods = type.getDeclaredMethods();
        List<Method> identifierMethods = Arrays.stream(declaredMethods)
                .filter(method -> method.getAnnotation(IdentifierMethod.class) != null)
                .toList();
        if (identifierMethods.size() < 1) {
            throw new PoiOrmMappingException(String.format("Object %s don't have IdentifierMethod", type.getName()));
        }
        if (identifierMethods.size() > 1) {
            throw new PoiOrmMappingException(String.format("Object %s have more than one IdentifierMethod", type.getName()));
        }
        Method identifierMethod = identifierMethods.get(0);
        int modifiers = identifierMethod.getModifiers();
        if (!isModifierSet(modifiers, ModifierType.STATIC)) {
            throw new PoiOrmMappingException(
                    String.format("IdentifierMethod should be static - error in class %s", type.getName())
            );
        }
        identifierMethod.setAccessible(true);
        try {
            Object result = identifierMethod.invoke(null, value);
            if (result.getClass() != boolean.class && result.getClass() != Boolean.class) {
                throw new PoiOrmMappingException(
                        String.format(
                                "Cant cast result identifierMethod - {%s} from object {%s}",
                                identifierMethod.getName(), type.getName()
                        )
                );
            }
            return (boolean) result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PoiOrmMappingException(
                    String.format("Cant perform identifierMethod - {%s} from object {%s}",
                            identifierMethod.getName(), type.getName()),
                    e
            );
        }
    }

    public static Class getRecursiveParentRoot(Class type) {
        RowObject annotation = (RowObject) type.getAnnotation(RowObject.class);
        return annotation.parent() != void.class ? getRecursiveParentRoot(annotation.parent()) : type;
    }
}
