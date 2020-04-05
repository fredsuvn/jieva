package xyz.srclab.common.reflect.method;

public interface MethodInvoker2<T> {

    T invoke(Object object);

    T invoke(Object object, Object[] args);
}
