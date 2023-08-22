package com.poiorm;

import com.poiorm.mapper.ExcelOrmReader;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        Workbook workbook = WorkbookFactory.create(new File("/test.xlsx"));
        ExcelOrmReader.fromExcel(workbook.getSheetAt(0), Person.class);
    }


}

class Person {

    String name = "test";
    double age = 10;
}
