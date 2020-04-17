package xyz.srclab.common.invoke.provider.reflected;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class ReflectedMethodInvoker implements MethodInvoker {

    private final Method method;

    ReflectedMethodInvoker(Method method) {
        this.method = method;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object object, Object... args) {
        try {
            return method.invoke(object, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new ExceptionWrapper(e);
        }
    }
}
