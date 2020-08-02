package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author sunqian
 */
public interface RecordEntry {

    static RecordEntry newEntry(
            Type owner, String name, @Nullable Method readMethod, @Nullable Method writeMethod) {
        return RecordResolverSupport.newEntry(owner, name, readMethod, writeMethod);
    }

    Class<?> ownerType();

    Type genericOwnerType();

    String key();

    Class<?> type();

    Type genericType();

    boolean readable();

    boolean writeable();

    @Nullable
    Object getValue(Object record) throws UnsupportedOperationException;

    @Nullable
    default <T> T getValue(Object record, Class<T> type, Converter converter) throws UnsupportedOperationException {
        @Nullable Object value = getValue(record);
        if (value == null) {
            return null;
        }
        return converter.convert(value, type);
    }

    @Nullable
    default <T> T getValue(Object record, Type type, Converter converter) throws UnsupportedOperationException {
        @Nullable Object value = getValue(record);
        if (value == null) {
            return null;
        }
        return converter.convert(value, type);
    }

    @Nullable
    default <T> T getValue(Object record, TypeRef<T> type, Converter converter) throws UnsupportedOperationException {
        @Nullable Object value = getValue(record);
        if (value == null) {
            return null;
        }
        return converter.convert(value, type);
    }

    void setValue(Object record, @Nullable Object value) throws UnsupportedOperationException;

    default void setValue(Object record, @Nullable Object value, Converter converter)
            throws UnsupportedOperationException {
        if (value == null) {
            setValue(record, null);
            return;
        }
        Object convertedValue = converter.convert(value, genericType());
        setValue(record, convertedValue);
    }

    default boolean hasField() {
        return getField() != null;
    }

    @Nullable
    Field getField();

    @Immutable
    List<Annotation> getFieldAnnotations();

    @Override
    int hashCode();

    @Override
    boolean equals(@Nullable Object that);

    @Override
    String toString();
}
