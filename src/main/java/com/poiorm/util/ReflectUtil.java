package com.poiorm.util;

import com.poiorm.exception.PoiOrmInstantiationException;
import com.poiorm.exception.PoiOrmMappingException;
import com.poiorm.exception.PoiOrmRestrictionException;
import com.poiorm.exception.PoiOrmTypeException;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public static <T extends Annotation> T getAnnotation(Class<?> type, Class<T> annotation) {
        return type.getAnnotation(annotation);
    }

    public static Optional<Field> getUniqueFieldByAnnotation(Class<?> type, Class<? extends Annotation> annotation) {
        List<Field> fields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotation))
                .toList();
        if (fields.size() > 1) {
            throw new PoiOrmRestrictionException(
                    String.format(
                            "Can't use more than one {%s} annotation - error in class {%s}",
                            annotation.getName(),
                            type.getName()
                    )
            );
        }
        return fields.size() > 0 ? Optional.of(fields.get(0)) : Optional.empty();
    }

    public static List<Field> getFieldsByAnnotation(Class<?> type, Class<? extends Annotation> annotation) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
    }



    public static Class<?> getListGenericType(Field listField) {
        if (!List.class.isAssignableFrom(listField.getType())) {
            throw new PoiOrmTypeException(
                    String.format(
                            "Field {%s} type is not List - {%s}",
                            listField.getName(),
                            listField.getType().getName()
                    )
            );
        }
        if (listField.getGenericType() instanceof ParameterizedType genericType) {
            Type[] actualTypeArguments = genericType.getActualTypeArguments();
            if (actualTypeArguments.length != 1) {
                throw new PoiOrmRestrictionException(
                        String.format(
                                "Acceptable list can have one generic type, you have - %s",
                                actualTypeArguments.length
                        )
                );
            }
            return  (Class<?>) genericType.getActualTypeArguments()[0];
        } else {
            throw new PoiOrmTypeException(
                    String.format(
                            "Field {%s} type is not ParameterizedType - {%s}",
                            listField.getName(),
                            listField.getType().getName()
                    )
            );
        }

    }

    public static void setFieldValue(Field field, Object value, Object instance) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new PoiOrmMappingException(String.format("Unexpected cast type {%s} of field %s", value, field.getName()));
        }
    }

    public static List<?> getFieldListValue(Field field, Object instance) {
        Object fieldValue = getFieldValue(field, instance);
        if (fieldValue instanceof List<?>) {
            Class<?> listGenericType = getListGenericType(field);
            return (List<?>) fieldValue;
        } else {
            throw new PoiOrmTypeException(
                    String.format("Field {%s} is not instance of List - instance of {%s}, from Object - %s",
                            field.getName(), field.getType(), instance)
            );
        }
    }

    public static Object getFieldValue(Field field, Object instance) {
        field.setAccessible(true);
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new PoiOrmRestrictionException(
                    String.format("Can't get field {%s} value from object {%s}", field.getName(), instance)
            );
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
