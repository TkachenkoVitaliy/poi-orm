package com.poiorm.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class RowListIterator {

    private final Sheet sheet;

    private final int maxIndex;

    private final int minIndex;

    private int index;

    public Row next() {
        index++;
        return sheet.getRow(index - 1);
    }

    public boolean hasNext() {
        return index + 1 <= maxIndex;
    }

    public Row previous() {
        index--;
        return sheet.getRow(index + 1);
    }

    public boolean hasPrevious() {
        return index - 1 >= minIndex;
    }

    public int getCurrentIndex() {
        return index;
    }

    public RowListIterator(Sheet sheet) {
        this.sheet = sheet;
        this.minIndex = sheet.getFirstRowNum();
        this.maxIndex = Math.min(sheet.getLastRowNum(), sheet.getPhysicalNumberOfRows() + sheet.getFirstRowNum());
        this.index = minIndex;
    }

    public RowListIterator(Sheet sheet, int startIndex) {
        this.sheet = sheet;
        this.minIndex = Math.max(startIndex, sheet.getFirstRowNum());
        this.maxIndex = Math.min(sheet.getLastRowNum(), Math.max(startIndex, sheet.getFirstRowNum()) + sheet.getPhysicalNumberOfRows());
        this.index = Math.max(startIndex, sheet.getFirstRowNum());
    }
}
