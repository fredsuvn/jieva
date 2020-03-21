package xyz.srclab.common.bytecode.proxy;

import xyz.srclab.common.reflect.MethodBody;
import xyz.srclab.common.reflect.MethodDefinition;

import java.util.Arrays;

public interface ProxyBuilder<T> {

    static ProxyBuilder<Object> newBuilder() {
        return ProxyOperator.getInstance().newBuilder();
    }

    static <T> ProxyBuilder<T> newBuilder(Class<T> superClass) {
        return ProxyOperator.getInstance().newBuilder(superClass);
    }

    default ProxyBuilder<T> addInterfaces(Class<?>... interfaces) {
        return addInterfaces(Arrays.asList(interfaces));
    }

    ProxyBuilder<T> addInterfaces(Iterable<Class<?>> interfaces);

    <R> ProxyBuilder<T> overrideMethod(String name, Class<?>[] parameterTypes, MethodBody<R> methodBody);

    <R> ProxyBuilder<T> overrideMethod(MethodDefinition<R> methodDefinition);

    ProxyClass<T> build();
}
