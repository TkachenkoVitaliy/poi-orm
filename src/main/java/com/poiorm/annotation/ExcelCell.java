package com.poiorm.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Mark field as Excel cell
 */
@Retention(RUNTIME)
@Target({FIELD})
@Documented
public @interface ExcelCell {
    /**
     * Specifies the column index where the corresponding value is mapped from the Excel data
     * @return column index
     */
    int value();
}
