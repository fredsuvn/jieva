package xyz.srclab.common.bean;

import xyz.srclab.annotations.Immutable;
import xyz.srclab.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Immutable
public interface BeanProperty {

    String getName();

    Class<?> getType();

    Type getGenericType();

    boolean isReadable();

    @Nullable
    Object getValue(Object bean) throws UnsupportedOperationException;

    @Nullable
    Method getReadMethod();

    boolean isWriteable();

    void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException;

    @Nullable
    Method getWriteMethod();
}
