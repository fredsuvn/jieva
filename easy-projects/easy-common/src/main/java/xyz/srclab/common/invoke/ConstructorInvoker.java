package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.reflect.ConstructorHelper;

import java.lang.reflect.Constructor;

@Immutable
public interface ConstructorInvoker<T> {

    static <T> ConstructorInvoker<T> of(Constructor<T> constructor) {
        return InvokerSupport.getConstructorInvoker(constructor);
    }

    static <T> ConstructorInvoker<T> of(Class<T> type, Class<?>... parameterTypes) {
        @Nullable Constructor<T> constructor = ConstructorHelper.getConstructor(type, parameterTypes);
        Checker.checkState(constructor != null, () ->
                "Constructor not found: " + ConstructorHelper.constructorToString(type, parameterTypes));
        return of(constructor);
    }

    Constructor<T> getConstructor();

    T invoke(Object... args);
}
