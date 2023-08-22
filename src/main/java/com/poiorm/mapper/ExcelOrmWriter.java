package com.poiorm.mapper;

import com.poiorm.annotation.WritableCells;
import com.poiorm.annotation.WritableHeader;
import com.poiorm.annotation.WritableObject;
import com.poiorm.exception.PoiOrmMappingException;
import com.poiorm.util.ExcelUtil;
import com.poiorm.util.ReflectUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class ExcelOrmWriter {

    private static final Class<WritableObject> WRITABLE_OBJECT_CLASS = WritableObject.class;
    private static final Class<WritableHeader> WRITABLE_HEADER_CLASS = WritableHeader.class;
    private static final Class<WritableCells> WRITABLE_CELLS_CLASS = WritableCells.class;

    /**
     *
     * @param sheet - sheet to write
     * @param data - data to write
     * @return lastCell - bottom right cell
     * @param <T> - data type
     */
    public static <T> Cell toExcel(final Sheet sheet, List<T> data) {
        return toExcel(sheet, data, 0, 0);
    }

    public static <T> Cell toExcel(final Sheet sheet, List<T> data, int skipTopRows, int skipLeftColumns) {
        if (data == null || data.size() == 0) {
            throw new PoiOrmMappingException("Cant write empty list");
        }
        T firstDataInstance = data.get(0);
        Class<T> dataType = (Class<T>) firstDataInstance.getClass();

        Cell lastCell = null;

        WritableObject writableObjectAnnotation = ReflectUtil.getAnnotation(dataType, WRITABLE_OBJECT_CLASS);
        switch (writableObjectAnnotation.direction()) {
            case ROW -> lastCell = writeRows(sheet, data, skipTopRows, skipLeftColumns);
            case COLUMN -> lastCell = writeColumns(sheet, data, skipTopRows, skipLeftColumns);
        }

        return lastCell;
    }

    private static <T> Cell writeColumns(Sheet sheet, List<T> data, final int startRowIndex, final int startColumnIndex) {
        Cell lastCell = null;
        int columnIndex = startColumnIndex;

        for (T item: data) {
            lastCell = writeColumn(sheet, item, startRowIndex, columnIndex);
            columnIndex++;
        }

        return lastCell;
    }

    private static <T> Cell writeColumn(Sheet sheet, T data, final int startRowIndex, final int columnIndex) {
        int currentRowIndex = startRowIndex;
        Cell currentCell = null;

        Class<?> type = data.getClass();

        // HEADER
        Optional<Field> headerFieldOptional = ReflectUtil.getUniqueFieldByAnnotation(type, WRITABLE_HEADER_CLASS);
        if (headerFieldOptional.isPresent()) {
            Field headerField = headerFieldOptional.get();
            WritableHeader headerAnnotation = headerField.getAnnotation(WRITABLE_HEADER_CLASS);
            CellType cellType = headerAnnotation.cellType();
            Object value = ReflectUtil.getFieldValue(headerField, data);
            currentCell = ExcelUtil.getOrCreateCell(sheet, currentRowIndex, columnIndex, cellType);
            ExcelUtil.writeCellValue(value, currentCell);
            currentRowIndex++;
        }
        // DATA CELLS
        Optional<Field> dataFieldOptional = ReflectUtil.getUniqueFieldByAnnotation(type, WRITABLE_CELLS_CLASS);
        if (dataFieldOptional.isPresent()) {
            Field dataField = dataFieldOptional.get();
            WritableCells cellsAnnotation = dataField.getAnnotation(WRITABLE_CELLS_CLASS);
            CellType cellType = cellsAnnotation.cellType();
            List<?> value = ReflectUtil.getFieldListValue(dataField, data);
            for(Object cellValue : value) {
                currentCell = ExcelUtil.getOrCreateCell(sheet, currentRowIndex, columnIndex, cellType);
                ExcelUtil.writeCellValue(cellValue, currentCell);
                currentRowIndex++;
            }
        }

        return currentCell;
    }

    private static <T> Cell writeRows(Sheet sheet, List<T> data, int startRowIndex, final int startColumnIndex) {
        Cell lastCell = null;
        int rowIndex = startRowIndex;

        for (T item: data) {
            lastCell = writeColumn(sheet, item, rowIndex, startColumnIndex);
            rowIndex++;
        }

        return lastCell;
    }

    private static <T> Cell writeRow(Sheet sheet, List<T> data, final int startRowIndex, int startColumnIndex) {
        int currentColumnIndex = startColumnIndex;
        Cell currentCell = null;

        Class<?> type = data.getClass();

        // HEADER
        Optional<Field> headerFieldOptional = ReflectUtil.getUniqueFieldByAnnotation(type, WRITABLE_HEADER_CLASS);
        if (headerFieldOptional.isPresent()) {
            Field headerField = headerFieldOptional.get();
            WritableHeader headerAnnotation = headerField.getAnnotation(WRITABLE_HEADER_CLASS);
            CellType cellType = headerAnnotation.cellType();
            Object value = ReflectUtil.getFieldValue(headerField, data);
            currentCell = ExcelUtil.getOrCreateCell(sheet, startRowIndex, currentColumnIndex, cellType);
            ExcelUtil.writeCellValue(value, currentCell);
            currentColumnIndex++;
        }
        // DATA CELLS
        Optional<Field> dataFieldOptional = ReflectUtil.getUniqueFieldByAnnotation(type, WRITABLE_CELLS_CLASS);
        if (dataFieldOptional.isPresent()) {
            Field dataField = dataFieldOptional.get();
            WritableCells cellsAnnotation = dataField.getAnnotation(WRITABLE_CELLS_CLASS);
            CellType cellType = cellsAnnotation.cellType();
            List<?> value = ReflectUtil.getFieldListValue(dataField, data);
            for(Object cellValue : value) {
                currentCell = ExcelUtil.getOrCreateCell(sheet, startRowIndex, currentColumnIndex, cellType);
                ExcelUtil.writeCellValue(cellValue, currentCell);
                currentColumnIndex++;
            }
        }

        return currentCell;
    }
}
