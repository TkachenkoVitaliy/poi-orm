package com.poiorm.mapper;

import com.poiorm.annotation.ExcelCell;
import com.poiorm.annotation.InnerRowObject;
import com.poiorm.annotation.RowObject;
import com.poiorm.util.AnnotationUtil;
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
    public static <T> List<T> fromExcel(final Sheet sheet, Class<T> rootType) throws IllegalAccessException {
        List<T> result = new ArrayList<>();
        Consumer<T> rootConsumer = result::add;
        MappingContext<T> rootMappingContext = new MappingContext<>(rootConsumer, rootType, ReflectUtil.newEmptyInstance(rootType));
        RowObject rowObjectAnnotation = rootType.getAnnotation(RowObject.class);
        int startRowIndex = rowObjectAnnotation.startRowIndex();
        int currentRowIndex = startRowIndex;
        int cicles = 0;
        int treeDepth = AnnotationUtil.getTreeDepth(rootType);

        RowListIterator iterator = new RowListIterator(sheet, startRowIndex);
        MappingContext<T> currentMappingContext = rootMappingContext;

        while (iterator.hasNext()) {
            cicles = currentRowIndex == iterator.getCurrentIndex() ? cicles + 1 : 0;

            if (cicles > treeDepth) break;
            if (currentMappingContext == null) break;

            if (currentMappingContext.consumer() == null) {
                currentMappingContext = new MappingContext<>(rootConsumer, rootType, ReflectUtil.newEmptyInstance(rootType));
                recursiveFromExcel(currentMappingContext, iterator);
            } else {
                currentRowIndex = iterator.getCurrentIndex();
            }

            currentMappingContext = recursiveFromExcel(currentMappingContext, iterator);
        }

        return result;
    }

    private static MappingContext recursiveFromExcel(MappingContext mappingContext, RowListIterator iterator) throws IllegalAccessException {
        if (mappingContext.consumer() == null) {
            return mappingContext;
        }

        if (iterator.hasNext()) {
            Row row = iterator.next();

            if (row == null) {
                return null;
            }

            Class type = mappingContext.type();
            int identifierColumnIndex = AnnotationUtil.getIdentifierFieldColumnIndex(type);

            boolean check = identifierColumnIndex < 0 || AnnotationUtil.performIdentifierMethod(type, row);

            if (check) {
                Object instance = mappingContext.instance();
                Consumer consumer = mappingContext.consumer();

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

                consumer.accept(instance);

                Optional<Field> optionalInnerCollection = AnnotationUtil.getInnerCollectionField(type);

                if (optionalInnerCollection.isPresent()) {
                    return mappingContext;
                } else {
                    return new MappingContext(
                            consumer,
                            type,
                            ReflectUtil.newEmptyInstance(type)
                    );
                }
            } else {
                Optional<Field> optionalInnerCollection = AnnotationUtil.getInnerCollectionField(type);

                if (optionalInnerCollection.isPresent()) {
                    Field innerCollection = optionalInnerCollection.get();
                    Class innerClass = ReflectUtil.getListGenericType(innerCollection);

                    // TODO скрыть от глаз ВАЖНО!!!
                    List children = (List) innerCollection.get(mappingContext.instance());

                    Object innerInstance = ReflectUtil.newEmptyInstance(innerClass);

                    MappingContext newMappingContext = new MappingContext<>(
                            children::add,
                            innerClass,
                            innerInstance
                    );

                    iterator.previous();
                    return recursiveFromExcel(newMappingContext, iterator);
                } else {
                    iterator.previous();
                    return new MappingContext(null, null, null);
                }
            }
        }
        return new MappingContext<>(null, null, null);
    }
}
