package com.poiorm.mapper;

import com.poiorm.annotation.ExcelCell;
import com.poiorm.annotation.InnerRowObject;
import com.poiorm.util.ExcelUtil;
import com.poiorm.util.ReflectUtil;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ExcelUnmarshaller {
    public static <T> T unmarshall(Class<T> type, Row row) {
        return null;
    }
}
