package xyz.srclab.bytecode.proxy;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.reflect.method.ProxyMethod;

@Immutable
public interface ProxyClass<T> {

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return ProxyClassBuilderHelper.newBuilder(superClass);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    interface Builder<T> {

        Builder<T> addInterfaces(Iterable<Class<?>> interfaces);

        Builder<T> overrideMethod(String name, Class<?>[] parameterTypes, ProxyMethod proxyMethod);

        ProxyClass<T> build();
    }
}
