package xyz.srclab.common.bytecode.enhance;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.reflect.method.ProxyMethod;

import java.util.Arrays;

@Immutable
public interface EnhancedClass<T> {

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return EnhancedClassBuilderSupport.newBuilder(superClass);
    }

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    interface Builder<T> {

        default Builder<T> addInterfaces(Class<?>... interfaces) {
            return addInterfaces(Arrays.asList(interfaces));
        }

        Builder<T> addInterfaces(Iterable<Class<?>> interfaces);

        Builder<T> overrideMethod(String methodName, Class<?>[] parameterTypes, ProxyMethod proxyMethod);

        EnhancedClass<T> build();
    }
}
