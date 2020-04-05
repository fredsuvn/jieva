package xyz.srclab.common.reflect.invoke;

import java.lang.reflect.Constructor;

public interface ConstructorInvoker {

    static ConstructorInvoker of(Constructor<?> constructor) {
        return InvokerSupport.newConstructorInvoker(constructor);
    }

    Object invoke(Object... args);
}
