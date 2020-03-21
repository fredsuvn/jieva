package xyz.srclab.common.bytecode.proxy;

public interface ProxyClass<T> {

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);
}
