package xyz.srclab.common.reflect.invoke;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Constructor;

@Immutable
public interface ConstructorInvoker<T> {

    static <T> ConstructorInvoker<T> of(Constructor<T> constructor) {
        return InvokerSupport.getConstructorInvoker(constructor);
    }

    static <T> ConstructorInvoker<T> of(Class<T> type, Class<?>... parameterTypes) {
        return InvokerSupport.getConstructorInvoker(type, parameterTypes);
    }

    T invoke(Object... args);
}
