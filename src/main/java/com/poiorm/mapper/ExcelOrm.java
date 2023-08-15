package com.poiorm.mapper;

import com.poiorm.annotation.ExcelCell;
import com.poiorm.annotation.InnerRowObject;
import com.poiorm.util.ExcelUtil;
import com.poiorm.util.ReflectUtil;
import com.poiorm.util.RowListIterator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Consumer;

public class ExcelOrm {
    public static <T> List<T> fromExcel(final Sheet sheet, Class<T> rootType) {
        List<T> result = new ArrayList<>();
        Consumer<T> rootConsumer = result::add;
        MappingContext<T> rootMappingContext = new MappingContext<>(rootConsumer, rootType, ReflectUtil.newEmptyInstance(rootType));

//        ListIterator<Row> iterator = (ListIterator<Row>) sheet.iterator();
        RowListIterator iterator = new RowListIterator(sheet);
        MappingContext currentMappingContext = rootMappingContext;

        while (iterator.hasNext()) {
            try {
                currentMappingContext = recursiveFromExcel(currentMappingContext, iterator);
                if (currentMappingContext.consumer() == null) {
                    currentMappingContext = new MappingContext<>(rootConsumer, rootType, ReflectUtil.newEmptyInstance(rootType));
                    recursiveFromExcel(currentMappingContext, iterator);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private static MappingContext recursiveFromExcel(MappingContext mappingContext, RowListIterator iterator) throws IllegalAccessException {
        if (mappingContext.consumer() == null) {
            return new MappingContext<>(null, null, null);
        }

        if (iterator.hasNext()) {
            Row row = iterator.next();

            int rowNum = row.getRowNum();

            Class type = mappingContext.type();
            boolean check = ReflectUtil.performIdentifierMethod(
                    type,
                    ExcelUtil.readCellValue(String.class, row.getCell(0))
            );
            if (check) {
                Object instance = mappingContext.instance();

                for (Field field : type.getDeclaredFields()) {
                    if (field.isAnnotationPresent(ExcelCell.class)) {
                        ExcelCell annotation = field.getAnnotation(ExcelCell.class);
                        ReflectUtil.setFieldValue(
                                field,
                                ExcelUtil.readCellValue(
                                        field.getType(),
                                        row.getCell(annotation.value())
                                ),
                                instance
                        );
                    }
                }

                mappingContext.consumer().accept(instance);

                return mappingContext;
            } else {
                Optional<Field> optionalInnerCollection = Arrays.stream(type.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(InnerRowObject.class))
                        .findFirst();

                if (optionalInnerCollection.isPresent()) {
                    Field innerCollection = optionalInnerCollection.get();
                    ParameterizedType genericType = (ParameterizedType) innerCollection.getGenericType();
                    Class innerClass = (Class) genericType.getActualTypeArguments()[0];

                    List innerClassList = (List) innerCollection.get(mappingContext.instance());

                    System.out.println("Create innerInstance - " + innerClass.getName());
                    Object innerInstance = ReflectUtil.newEmptyInstance(innerClass);
//                    innerClassList.add(innerInstance);

                    MappingContext newMappingContext = new MappingContext<>(
                            innerClassList::add,
                            innerClass,
                            innerInstance
                    );

                    iterator.previous();
                    return recursiveFromExcel(newMappingContext, iterator);
                } else {
                    Class rootClass = ReflectUtil.getRecursiveParentRoot(type);
                    iterator.previous();
                    return new MappingContext(null, null, null);
                }
            }
        }
        return new MappingContext<>(null, null, null);
    }
}
