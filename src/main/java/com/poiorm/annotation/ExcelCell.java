package com.poiorm.annotation;

import java.lang.annotation.*;


/**
 * Mark field as Excel cell
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExcelCell {
    /**
     * Specifies the column index where the corresponding value is mapped from the Excel data
     * @return column index
     */
    int value();
}
