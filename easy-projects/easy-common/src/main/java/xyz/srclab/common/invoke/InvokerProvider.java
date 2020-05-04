package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.instance.InstanceHelper;
import xyz.srclab.common.reflect.method.MethodHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Immutable
public interface InvokerProvider {

    <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor);

    @Nullable
    default <T> ConstructorInvoker<T> getConstructorInvoker(Class<T> type, Class<?>... parameterTypes) {
        @Nullable Constructor<T> constructor = InstanceHelper.getConstructor(type, parameterTypes);
        if (constructor == null) {
            return null;
        }
        return getConstructorInvoker(constructor);
    }

    MethodInvoker getMethodInvoker(Method method);

    @Nullable
    default MethodInvoker getMethodInvoker(Class<?> type, String methodName, Class<?>... parameterTypes) {
        @Nullable Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
        if (method == null) {
            return null;
        }
        return getMethodInvoker(method);
    }

    FunctionInvoker getFunctionInvoker(Method method);

    @Nullable
    default FunctionInvoker getFunctionInvoker(Class<?> type, String methodName, Class<?>... parameterTypes) {
        @Nullable Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
        if (method == null) {
            return null;
        }
        return getFunctionInvoker(method);
    }
}
