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
        MappingModel<T> rootMappingModel = new MappingModel<>(rootConsumer, rootType, ReflectUtil.newEmptyInstance(rootType));

//        ListIterator<Row> iterator = (ListIterator<Row>) sheet.iterator();
        RowListIterator iterator = new RowListIterator(sheet);
        MappingModel currentMappingModel = rootMappingModel;

        while (iterator.hasNext()) {
            try {
                currentMappingModel = recursiveFromExcel(currentMappingModel, iterator);
                if (currentMappingModel.getConsumer() == null) {
                    currentMappingModel = new MappingModel<>(rootConsumer, rootType, ReflectUtil.newEmptyInstance(rootType));
                    recursiveFromExcel(currentMappingModel, iterator);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    private static MappingModel recursiveFromExcel(MappingModel mappingModel, RowListIterator iterator) throws IllegalAccessException {
        if (mappingModel.getConsumer() == null) {
            return new MappingModel<>(null, null, null);
        }

        if (iterator.hasNext()) {
            Row row = iterator.next();

            int rowNum = row.getRowNum();

            Class type = mappingModel.getType();
            boolean check = ReflectUtil.performIdentifierMethod(
                    type,
                    ExcelUtil.readCellValue(String.class, row.getCell(0))
            );
            if (check) {
                Object instance = mappingModel.getInstance();

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

//                Arrays.stream(type.getDeclaredFields())
//                        .filter(field -> field.isAnnotationPresent(ExcelCell.class))
//                        .forEach(field -> {
//                            ExcelCell annotation = field.getAnnotation(ExcelCell.class);
//                            ReflectUtil.setFieldValue(
//                                    field,
//                                    ExcelUtil.readCellValue(
//                                            field.getClass(),
//                                            row.getCell(annotation.value())
//                                    ),
//                                    instance
//                            );
//                        });

                mappingModel.getConsumer().accept(instance);

                return mappingModel;
            } else {
                Optional<Field> optionalInnerCollection = Arrays.stream(type.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(InnerRowObject.class))
                        .findFirst();

                if (optionalInnerCollection.isPresent()) {
                    Field innerCollection = optionalInnerCollection.get();
                    ParameterizedType genericType = (ParameterizedType) innerCollection.getGenericType();
                    Class innerClass = (Class) genericType.getActualTypeArguments()[0];

                    List innerClassList = (List) innerCollection.get(mappingModel.getInstance());

                    System.out.println("Create innerInstance - " + innerClass.getName());
                    Object innerInstance = ReflectUtil.newEmptyInstance(innerClass);
//                    innerClassList.add(innerInstance);

                    MappingModel newMappingModel = new MappingModel<>(
                            innerClassList::add,
                            innerClass,
                            innerInstance
                    );

                    iterator.previous();
                    return recursiveFromExcel(newMappingModel, iterator);
                } else {
                    Class rootClass = ReflectUtil.getRecursiveParentRoot(type);
                    iterator.previous();
                    return new MappingModel(null, null, null);
                }
            }
        }
        return new MappingModel<>(null, null, null);
    }
}
