package com.poiorm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark Object as Excel row
 */
@Retention(RUNTIME)
@Target({TYPE})
@Documented
public @interface RowObject {
    Class parent() default void.class;
}
