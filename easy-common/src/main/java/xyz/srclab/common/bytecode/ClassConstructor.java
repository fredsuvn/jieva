package xyz.srclab.common.bytecode;

public interface ClassConstructor<T> {

    T create();

    T create(Class<?>[] parameterTypes, Object[] args);
}
