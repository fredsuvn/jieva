package xyz.srclab.common.bean;

import java.lang.reflect.Type;

public interface BeanPropertyDescriptor {

    String getName();

    Class<?> getType();

    Type getGenericType();

    boolean isReadable();

    Object getValue(Object bean) throws UnsupportedOperationException;

    boolean isWriteable();

    void setValue(Object bean, Object value) throws UnsupportedOperationException;
}
