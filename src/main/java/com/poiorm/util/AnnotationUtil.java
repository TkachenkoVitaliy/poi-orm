package com.poiorm.util;

import com.poiorm.annotation.IdentifierMethod;
import com.poiorm.exception.PoiOrmMethodTypeException;

import java.lang.annotation.Annotation;

public final class AnnotationUtil {
    private AnnotationUtil() {
    }

    public static boolean performIdentifierMethod(Class<?> type, Object value) {
        final Class<? extends Annotation> IDENTIFIER_METHOD_CLASS = IdentifierMethod.class;

        Object result = ReflectUtil.performStaticAnnotatedMethod(type, value, IDENTIFIER_METHOD_CLASS);

        if (result instanceof Boolean) {
            return (boolean) result;
        } else {
            throw new PoiOrmMethodTypeException(
                    String.format(
                            "IdentifierMethod in class {%s} should be return boolean but returns {%s}",
                            type.getName(),
                            result.getClass().getName()
                    )
            );
        }
    }
}
