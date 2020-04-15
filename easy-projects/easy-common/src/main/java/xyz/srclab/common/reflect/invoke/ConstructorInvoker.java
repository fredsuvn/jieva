package xyz.srclab.common.reflect.invoke;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Constructor;

@Immutable
public interface ConstructorInvoker {

    static ConstructorInvoker of(Constructor<?> constructor) {
        return InvokerSupport.newConstructorInvoker(constructor);
    }

    Object invoke(Object... args);
}
