package com.poiorm.annotation;

import com.poiorm.type.Direction;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE})
public @interface WritableObject {
    Direction direction();
    int marginTopRows() default 0;
    int marginLeftColumns() default 0;

    int marginBotRows() default 0;
    int marginRightColumns() default 0;

}
