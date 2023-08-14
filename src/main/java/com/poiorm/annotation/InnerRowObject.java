package com.poiorm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark inner Object as Excel row
 */
@Retention(RUNTIME)
@Target({FIELD})
@Documented
public @interface InnerRowObject {
}
