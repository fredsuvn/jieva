package xyz.srclab.common.bytecode;

public interface ClassConstructor<T> {

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);
}
