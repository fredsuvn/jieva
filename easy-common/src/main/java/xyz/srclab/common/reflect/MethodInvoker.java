package xyz.srclab.common.reflect;

public interface MethodInvoker<T> {

    T invoke(Object object);

    T invoke(Object object, Object[] args);
}
