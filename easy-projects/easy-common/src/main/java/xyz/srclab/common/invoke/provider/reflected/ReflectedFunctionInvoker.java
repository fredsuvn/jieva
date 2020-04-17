package xyz.srclab.common.invoke.provider.reflected;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.invoke.FunctionInvoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
final class ReflectedFunctionInvoker implements FunctionInvoker {

    private final Method method;

    ReflectedFunctionInvoker(Method method) {
        this.method = method;
    }

    @Override
    public @Nullable Object invoke(Object... args) {
        try {
            return method.invoke(null, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new ExceptionWrapper(e);
        }
    }
}
