package xyz.srclab.bytecode.proxy;

import xyz.srclab.common.reflect.method.MethodBody;
import xyz.srclab.common.reflect.method.MethodDefinition;

import java.util.Arrays;

public interface ProxyClass<T> {

    static Builder<Object> newBuilder() {
        return Builder.newBuilder(Object.class);
    }

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return Builder.newBuilder(superClass);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    interface Builder<T> {

        static <T> Builder<T> newBuilder(Class<T> superClass) {
            return ProxyClassBuilderProvider.getInstance().newBuilder(superClass);
        }

        default Builder<T> addInterfaces(Class<?>... interfaces) {
            return addInterfaces(Arrays.asList(interfaces));
        }

        Builder<T> addInterfaces(Iterable<Class<?>> interfaces);

        <R> Builder<T> overrideMethod(String name, Class<?>[] parameterTypes, MethodBody<R> methodBody);

        <R> Builder<T> overrideMethod(MethodDefinition<R> methodDefinition);

        ProxyClass<T> build();
    }
}
