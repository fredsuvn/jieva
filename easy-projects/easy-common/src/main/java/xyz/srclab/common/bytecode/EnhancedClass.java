package xyz.srclab.common.bytecode;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.util.proxy.ProxyMethod;

import java.util.Arrays;

@Immutable
public interface EnhancedClass<T> {

    static <T> Builder<T> newBuilder(Class<T> superClass) {
        return ByteCodeSupport.newEnhancedClassBuilder(superClass);
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
