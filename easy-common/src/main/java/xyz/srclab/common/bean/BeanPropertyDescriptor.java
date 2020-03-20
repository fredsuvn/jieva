package xyz.srclab.common.bean;

public interface BeanPropertyDescriptor {

    String getName();

    Class<?> getType();

    boolean isReadable();

    Object getValue(Object bean) throws UnsupportedOperationException;

    boolean isWriteable();

    void setValue(Object bean, Object value) throws UnsupportedOperationException;
}
