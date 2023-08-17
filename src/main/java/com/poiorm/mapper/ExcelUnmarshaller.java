package com.poiorm.mapper;

import com.poiorm.annotation.ExcelCell;
import com.poiorm.util.ExcelUtil;
import com.poiorm.util.ReflectUtil;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Field;

public class ExcelUnmarshaller {

    private ExcelUnmarshaller(){
    }

    public static <T> void unmarshalToInstance(T instance, Row row) {
        Class<?> type = instance.getClass();
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExcelCell.class)) {
                ExcelCell annotation = field.getAnnotation(ExcelCell.class);
                ReflectUtil.setFieldValue(
                        field,
                        ExcelUtil.readCellValue(
                                field.getType(),
                                row.getCell(annotation.value())
                        ),
                        instance
                );
            }
        }
    }
}
