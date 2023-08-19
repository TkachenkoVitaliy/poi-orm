package com.poiorm.mapper;

import com.poiorm.annotation.RowObject;
import com.poiorm.util.AnnotationUtil;
import com.poiorm.util.ReflectUtil;
import com.poiorm.util.RowListIterator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
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
        int loops = 0;
        int treeDepth = AnnotationUtil.getTreeDepth(rootType);

        RowListIterator iterator = new RowListIterator(sheet, startRowIndex);
        MappingContext<T> currentMappingContext = rootMappingContext;

        while (iterator.hasNext()) {
            loops = currentRowIndex == iterator.getCurrentIndex() ? loops + 1 : 0;

            if (loops > treeDepth) break;
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
            Object instance = mappingContext.instance();
            Consumer consumer = mappingContext.consumer();

            if (AnnotationUtil.checkIdentity(type, row)) {
                ExcelUnmarshaller.unmarshalToInstance(instance, row);
                consumer.accept(instance);

                Optional<Field> optionalInnerCollection = AnnotationUtil.getInnerCollectionField(type);

                if (optionalInnerCollection.isPresent()) {
                    return mappingContext;
                } else {
                    return new MappingContext(consumer, type, ReflectUtil.newEmptyInstance(type));
                }
            } else {
                Optional<Field> optionalInnerCollection = AnnotationUtil.getInnerCollectionField(type);

                if (optionalInnerCollection.isPresent()) {
                    Field innerCollection = optionalInnerCollection.get();
                    innerCollection.setAccessible(true);
                    List children = (List) innerCollection.get(mappingContext.instance());
                    Class innerClass = ReflectUtil.getListGenericType(innerCollection);

                    Object innerInstance = ReflectUtil.newEmptyInstance(innerClass);

                    iterator.previous();
                    return recursiveFromExcel(
                            new MappingContext(children::add, innerClass, innerInstance),
                            iterator
                    );
                } else {
                    iterator.previous();
                    return new MappingContext<>(null, null, null);
                }
            }
        }
        return new MappingContext<>(null, null, null);
    }
}
