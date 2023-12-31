package com.poiorm.annotation;

import com.poiorm.type.WriteCellFormat;
import org.apache.poi.ss.usermodel.CellType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({FIELD})
public @interface WritableCells {
    WriteCellFormat cellFormat();
}
