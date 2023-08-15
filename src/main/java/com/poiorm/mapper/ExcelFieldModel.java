package com.poiorm.mapper;

import com.poiorm.annotation.ExcelCell;
import com.poiorm.annotation.IdentifierField;
import com.poiorm.exception.PoiOrmMappingException;

import java.lang.reflect.Field;

public class ExcelFieldModel {
    private final Field field;
    private final int columnIndex;
    private final Class<?> type;
    private final boolean isIdentifierField;

    public ExcelFieldModel(Field field) {
        this.field = field;
        this.type = field.getType();

        if (!field.isAnnotationPresent(ExcelCell.class)) {
            throw new PoiOrmMappingException(
                    String.format("Field - {%s} don't have ExcelCell annotation", field.getName())
            );
        }

        ExcelCell annotation = field.getAnnotation(ExcelCell.class);
        this.columnIndex = annotation.value();
        this.isIdentifierField = field.isAnnotationPresent(IdentifierField.class);
    }

    public Field getField() {
        return field;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isIdentifierField() {
        return isIdentifierField;
    }

    @Override
    public String toString() {
        return "ExcelFieldModel{" +
                "field=" + field +
                ", columnIndex=" + columnIndex +
                ", type=" + type +
                ", isIdentifierField=" + isIdentifierField +
                '}';
    }
}
