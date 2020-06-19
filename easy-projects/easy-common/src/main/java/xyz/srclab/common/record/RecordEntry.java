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

    static RecordEntry newEntryOnField(Field field) {
        return RecordResolverSupport.newEntryOnField(field);
    }

    static RecordEntry newEntryOnMethods(
            String name, @Nullable Method readMethod, @Nullable Method writeMethod, Class<?> owner) {
        return RecordResolverSupport.newEntryOnMethods(name, readMethod, writeMethod, owner);
    }

    String getKey();

    Class<?> getType();

    Type getGenericType();

    boolean isReadable();

    boolean isWriteable();

    @Nullable
    Object getValue(Object bean) throws UnsupportedOperationException;

    @Nullable
    default <T> T getValue(Object bean, Class<T> type, Converter converter) throws UnsupportedOperationException {
        @Nullable Object value = getValue(bean);
        if (value == null) {
            return null;
        }
        return converter.convert(value, type);
    }

    @Nullable
    default <T> T getValue(Object bean, Type type, Converter converter) throws UnsupportedOperationException {
        @Nullable Object value = getValue(bean);
        if (value == null) {
            return null;
        }
        return converter.convert(value, type);
    }

    @Nullable
    default <T> T getValue(Object bean, TypeRef<T> type, Converter converter) throws UnsupportedOperationException {
        @Nullable Object value = getValue(bean);
        if (value == null) {
            return null;
        }
        return converter.convert(value, type);
    }

    void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException;

    default void setValue(Object bean, @Nullable Object value, Converter converter) throws UnsupportedOperationException {
        if (value == null) {
            setValue(bean, null);
            return;
        }
        Object convertedValue = converter.convert(value, getGenericType());
        setValue(bean, convertedValue);
    }

    default boolean hasField() {
        return getField() != null;
    }

    @Nullable
    Field getField();

    @Immutable
    List<Annotation> getFieldAnnotations();

    Class<?> getOwner();

    @Override
    int hashCode();

    @Override
    boolean equals(@Nullable Object that);

    @Override
    String toString();
}
