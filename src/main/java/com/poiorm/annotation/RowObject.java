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
    /**
     * Specifies parent Object class
     * @return parent Object class
     */
    Class parent() default void.class;
    /**
     * Specifies the start row index
     * @return row index
     */
    int startRowIndex() default 0;
}
