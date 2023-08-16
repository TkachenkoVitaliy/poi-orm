package com.poiorm.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelUtil {
    public static Object readCellValue(Class<?> fieldType, Cell cell) {
        if (fieldType == String.class) {
            if (cell.getCellType() == CellType.NUMERIC) {
                double numericCellValue = cell.getNumericCellValue();
                return String.valueOf((int) numericCellValue);
            }
            return cell.getStringCellValue();
        }
        if (fieldType == int.class || fieldType == Integer.class
                || fieldType == double.class || fieldType == Double.class) {
            return cell.getNumericCellValue();
        } else {
            //TODO create normal exception
            throw new RuntimeException();
        }
    }
}
