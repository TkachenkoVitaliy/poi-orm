package com.poiorm.util;

import com.poiorm.exception.PoiOrmMappingException;
import com.poiorm.exception.PoiOrmRestrictionException;
import com.poiorm.mapper.DataFormatter;
import com.poiorm.type.WriteCellFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class ExcelUtil {
    private ExcelUtil() {}
    public static Row getOrCreateRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    public static Cell getOrCreateCell(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }

    public static Cell getOrCreateCell(Row row, int columnIndex, WriteCellFormat writeCellFormat) {
        CellType cellType = null;

        switch (writeCellFormat) {
            case PERCENTAGE, FLOAT_NUMBER, NUMBER -> cellType = CellType.NUMERIC;
            case STRING -> cellType = CellType.STRING;
        }

        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex, cellType);
        }
        return cell;
    }

    public static Cell getOrCreateCell(Sheet sheet, int rowIndex, int columnIndex) {
        Row row = getOrCreateRow(sheet, rowIndex);
        return getOrCreateCell(row, columnIndex);
    }

    public static Cell getOrCreateCell(Sheet sheet, int rowIndex, int columnIndex, WriteCellFormat writeCellFormat) {
        Row row = getOrCreateRow(sheet, rowIndex);
        return getOrCreateCell(row, columnIndex, writeCellFormat);
    }

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

    public static Cell writeCellValue(Object value, Cell cell, WriteCellFormat writeCellFormat, DataFormatter formatter) {
        switch (writeCellFormat) {
            case PERCENTAGE -> {
                if (value == null) return cell;
                Double doubleValue = (Double) value;
                Double percentage = doubleValue / 100;
                cell.setCellValue(percentage);
                cell.setCellStyle(formatter.getPercentageCellStyle());
            }
            case FLOAT_NUMBER -> {
                if (value == null) return cell;
                Double doubleValue = (Double) value;
                cell.setCellValue(doubleValue);
                cell.setCellStyle(formatter.getDefaultCellStyle());
            }
            case NUMBER -> {
                if (value == null) return cell;
                Double doubleValue = (Double) value;
                cell.setCellValue(doubleValue);
                cell.setCellStyle(formatter.getNumberCellStyle());
            }
            case STRING -> {
                if (value == null) return cell;
                String stringValue = (String) value;
                cell.setCellValue(stringValue);
                cell.setCellStyle(formatter.getDefaultCellStyle());
            }
        }

        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC -> {
                if (value == null) return cell;
                Double doubleValue = (Double) value;
                cell.setCellValue(doubleValue);
            }
            case STRING -> {
                if (value == null) return cell;
                String stringValue = (String) value;
                cell.setCellValue(stringValue);
            }
            case _NONE, FORMULA, BOOLEAN, ERROR, BLANK -> {
                throw new PoiOrmRestrictionException(
                        String.format("Not supported CellType {%s}", cellType)
                );
            }
        }
        return cell;
    }
}
