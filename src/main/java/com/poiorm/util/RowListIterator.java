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

    public RowListIterator(Sheet sheet) {
        this.sheet = sheet;
        this.minIndex = sheet.getFirstRowNum();
        this.maxIndex = 127; //sheet.getLastRowNum();
        this.index = minIndex;
    }
}
