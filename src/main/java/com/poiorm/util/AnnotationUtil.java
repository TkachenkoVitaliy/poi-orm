package com.poiorm.util;

import com.poiorm.annotation.IdentifierMethod;
import com.poiorm.annotation.InnerRowObject;
import com.poiorm.exception.PoiOrmTypeException;
import com.poiorm.exception.PoiOrmRestrictionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class AnnotationUtil {
    private AnnotationUtil() {
    }

    public static boolean performIdentifierMethod(Class<?> type, Object value) {
        final Class<? extends Annotation> IDENTIFIER_METHOD_CLASS = IdentifierMethod.class;

        Object result = ReflectUtil.performStaticAnnotatedMethod(type, value, IDENTIFIER_METHOD_CLASS);

        if (result instanceof Boolean) {
            return (boolean) result;
        } else {
            throw new PoiOrmTypeException(
                    String.format(
                            "IdentifierMethod in class {%s} should be return boolean but returns {%s}",
                            type.getName(),
                            result.getClass().getName()
                    )
            );
        }
    }

    public static Optional<Field> getInnerCollectionField(Class<?> type) {
        final Class<? extends Annotation> INNER_ROW_OBJECT_CLASS = InnerRowObject.class;

        List<Field> fields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(INNER_ROW_OBJECT_CLASS))
                .toList();
        if (fields.size() > 1) {
            throw new PoiOrmRestrictionException(
                    String.format(
                            "Can't use more than one {%s} annotation - error in class {%s}",
                            INNER_ROW_OBJECT_CLASS.getName(),
                            type.getName()
                    )
            );
        }
        return fields.size() > 0 ? Optional.of(fields.get(0)) : Optional.empty();
    }
}
