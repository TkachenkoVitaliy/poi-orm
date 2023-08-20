package com.poiorm.util;

import com.poiorm.exception.PoiOrmMappingException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelUtil {
    public static Object readCellValue(Class<?> fieldType, Cell cell) {
        if (cell == null) {
            throw new PoiOrmMappingException("Ячейка null");
        }
        if (fieldType == String.class) {
            if (cell.getCellType() == CellType.NUMERIC) {
                double numericCellValue = cell.getNumericCellValue();
                return String.valueOf((int) numericCellValue);
            }
            return cell.getStringCellValue();
        }
        if (fieldType == int.class || fieldType == Integer.class
                || fieldType == double.class || fieldType == Double.class) {
            CellType cellType = cell.getCellType();
            if (cellType == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }
            if (cellType == CellType._NONE || cellType == CellType.BLANK || cellType == CellType.ERROR) {
                return 0.0;
            }
            if (cellType == CellType.STRING) {
                String value = cell.getStringCellValue();
                if (fieldType == int.class || fieldType == Integer.class) return Integer.valueOf(value);
                if (fieldType == double.class || fieldType == Double.class) return Double.valueOf(value);
            }
            throw new PoiOrmMappingException(
                    String.format(
                            "Не верный тип ячейки. CellType - %s, row - %s, column - %s, FieldType - %s",
                            cellType, cell.getRowIndex(), cell.getColumnIndex(), fieldType
                    )
            );
        } else {
            //TODO create normal exception
            throw new RuntimeException();
        }
    }
}
