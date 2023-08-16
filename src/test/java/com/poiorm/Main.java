package com.poiorm;

import com.poiorm.mapper.ExcelOrm;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Workbook workbook = WorkbookFactory.create(new File("/test.xlsx"));
        ExcelOrm.fromExcel(workbook.getSheetAt(0), Person.class);
    }


}

class Person {

    String name = "test";
    double age = 10;
}
