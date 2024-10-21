package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class OfMethod implements Invoker {

    private final Method method;

    OfMethod(Method method) {
        this.method = method;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
        try {
            return method.invoke(inst, args);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                throw new InvocationException(e.getCause());
            }
            throw new InvocationException(e);
        }
    }
}
