package com.poiorm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark method as identifier
 */
@Retention(RUNTIME)
@Target({METHOD})
@Documented
public @interface IdentifierMethod {
}
