package xyz.srclab.bytecode.proxy;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.reflect.method.MethodBody;
import xyz.srclab.common.reflect.method.MethodDefinition;

@ThreadSafe
public interface ProxyClass<T> {

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return ProxyClassBuilderHelper.newBuilder(superClass);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    interface Builder<T> {

        Builder<T> addInterfaces(Iterable<Class<?>> interfaces);

        <R> Builder<T> overrideMethod(String name, Class<?>[] parameterTypes, MethodBody<R> methodBody);

        <R> Builder<T> overrideMethod(MethodDefinition<R> methodDefinition);

        ProxyClass<T> build();
    }
}
