package com.poiorm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark field as identifier parameter
 */
@Retention(RUNTIME)
@Target({FIELD})
@Documented
public @interface IdentifierField {
}
