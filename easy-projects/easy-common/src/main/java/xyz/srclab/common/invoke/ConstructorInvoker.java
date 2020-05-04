package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.instance.InstanceHelper;

import java.lang.reflect.Constructor;

@Immutable
public interface ConstructorInvoker<T> {

    static <T> ConstructorInvoker<T> of(Constructor<T> constructor) {
        return InvokerSupport.getConstructorInvoker(constructor);
    }

    @Nullable
    static <T> ConstructorInvoker<T> of(Class<T> type, Class<?>... parameterTypes) {
        @Nullable Constructor<T> constructor = InstanceHelper.getConstructor(type, parameterTypes);
        if (constructor == null) {
            return null;
        }
        return of(constructor);
    }

    Constructor<T> getConstructor();

    T invoke(Object... args);
}
