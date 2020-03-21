package xyz.srclab.common.reflect;

public interface PropertyAccessor {

    boolean isReadable();

    boolean isWriteable();

    Object getValue(Object object) throws UnsupportedOperationException;

    void setValue(Object object, Object value) throws UnsupportedOperationException;
}
