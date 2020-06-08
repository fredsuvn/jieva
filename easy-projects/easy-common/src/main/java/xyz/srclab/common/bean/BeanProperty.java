package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

@Immutable
public interface BeanProperty extends BeanMember {

    Class<?> type();

    Type genericType();

    Class<?> ownerType();

    boolean readable();

    boolean writeable();

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
        Object convertedValue = converter.convert(value, genericType());
        setValue(bean, convertedValue);
    }

    @Nullable
    Field tryField();

    @Immutable
    List<Annotation> tryFieldAnnotations();
}
