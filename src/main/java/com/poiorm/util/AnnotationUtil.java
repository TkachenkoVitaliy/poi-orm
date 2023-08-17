package com.poiorm.util;

import com.poiorm.annotation.ExcelCell;
import com.poiorm.annotation.IdentifierField;
import com.poiorm.annotation.IdentifierMethod;
import com.poiorm.annotation.InnerRowObject;
import com.poiorm.exception.PoiOrmTypeException;
import com.poiorm.exception.PoiOrmRestrictionException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class AnnotationUtil {
    private AnnotationUtil() {
    }

    public static boolean checkIdentity(Class<?> type, Row row) {
        final Class<? extends Annotation> IDENTIFIER_METHOD_CLASS = IdentifierMethod.class;

        int identifierColumnIndex = getIdentifierFieldColumnIndex(type);

        if (identifierColumnIndex < 0) {
            return true;
        }

        Cell cell = row.getCell(getIdentifierFieldColumnIndex(type));

        if (cell == null) {
            return false;
        }

        Object value = ExcelUtil.readCellValue(String.class, cell);

        Object result = ReflectUtil.performStaticAnnotatedMethod(type, value, IDENTIFIER_METHOD_CLASS);

        if (result instanceof Boolean) {
            return (boolean) result;
        } else {
            throw new PoiOrmTypeException(
                    String.format(
                            "IdentifierMethod in class {%s} should be return boolean but returns {%s}",
                            type.getName(),
                            result.getClass().getName()
                    )
            );
        }
    }

    public static Optional<Field> getInnerCollectionField(Class<?> type) {
        final Class<? extends Annotation> INNER_ROW_OBJECT_CLASS = InnerRowObject.class;

        List<Field> fields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(INNER_ROW_OBJECT_CLASS))
                .toList();
        if (fields.size() > 1) {
            throw new PoiOrmRestrictionException(
                    String.format(
                            "Can't use more than one {%s} annotation - error in class {%s}",
                            INNER_ROW_OBJECT_CLASS.getName(),
                            type.getName()
                    )
            );
        }
        return fields.size() > 0 ? Optional.of(fields.get(0)) : Optional.empty();
    }

    public static int getTreeDepth(Class<?> rootType) {
        Class<?> currentType = rootType;

        int depthCount = 1;
        while (true) {
            Optional<Field> innerCollectionField = getInnerCollectionField(currentType);
            if (innerCollectionField.isPresent()) {
                depthCount++;
                currentType = ReflectUtil.getListGenericType(innerCollectionField.get());
            } else {
                return depthCount;
            }
        }
    }

    public static int getIdentifierFieldColumnIndex(Class<?> type) {
        final Class<? extends Annotation> IDENTIFIER_FIELD_CLASS = IdentifierField.class;
        final Class<? extends Annotation> EXCEL_CELL_CLASS = ExcelCell.class;

        List<Field> identifierFields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(IDENTIFIER_FIELD_CLASS))
                .toList();
        if (identifierFields.size() > 1) {
            throw new PoiOrmRestrictionException(
                    String.format("Class {%s} can have only one identifier field", type.getName())
            );
        }
        if (identifierFields.size() == 0 ) {
            if (getInnerCollectionField(type).isEmpty()) {
                return -1;
            } else {
                throw new PoiOrmRestrictionException(
                        String.format(
                                "If you have InnerRowObject you need to use IdentifierField annotation. Class - {%s}",
                                type.getName()
                        )
                );
            }
        }
        Field identifierField = identifierFields.get(0);
        if (identifierField.isAnnotationPresent(EXCEL_CELL_CLASS)) {
            ExcelCell excelCellAnnotation = (ExcelCell) identifierField.getAnnotation(EXCEL_CELL_CLASS);
            return excelCellAnnotation.value();
        } else {
            throw new PoiOrmRestrictionException(
                    String.format(
                            "Identifier field {%s} in class {%s} should be have annotation ExcelCell",
                            identifierField.getName(),
                            type.getName()
                    )
            );
        }
    }
}
