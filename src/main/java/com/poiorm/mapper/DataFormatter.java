package com.poiorm.mapper;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public class DataFormatter {
    private CellStyle defaultCellStyle;
    private CellStyle percentageCellStyle;
    private CellStyle numberCellStyle;

    public DataFormatter(Workbook wb) {
        this.defaultCellStyle = wb.createCellStyle();

        short percentageFormat = wb.createDataFormat().getFormat("0.0%");
        this.percentageCellStyle = wb.createCellStyle();
        percentageCellStyle.setDataFormat(percentageFormat);

        short numberFormat = wb.createDataFormat().getFormat("0");
        this.numberCellStyle = wb.createCellStyle();
        numberCellStyle.setDataFormat(numberFormat);
    }

    public CellStyle getDefaultCellStyle() {
        return defaultCellStyle;
    }

    public void setDefaultCellStyle(CellStyle defaultCellStyle) {
        this.defaultCellStyle = defaultCellStyle;
    }

    public CellStyle getPercentageCellStyle() {
        return percentageCellStyle;
    }

    public void setPercentageCellStyle(CellStyle percentageCellStyle) {
        this.percentageCellStyle = percentageCellStyle;
    }

    public CellStyle getNumberCellStyle() {
        return numberCellStyle;
    }

    public void setNumberCellStyle(CellStyle numberCellStyle) {
        this.numberCellStyle = numberCellStyle;
    }
}
