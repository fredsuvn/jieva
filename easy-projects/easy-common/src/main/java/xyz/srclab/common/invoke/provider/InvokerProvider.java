package xyz.srclab.common.invoke.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.instance.InstanceHelper;
import xyz.srclab.common.reflect.method.MethodHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Immutable
public interface InvokerProvider {

    <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor);

    default <T> ConstructorInvoker<T> getConstructorInvoker(Class<T> type, Class<?>... parameterTypes) {
        Constructor<T> constructor = InstanceHelper.getConstructor(type, parameterTypes);
        return getConstructorInvoker(constructor);
    }

    MethodInvoker getMethodInvoker(Method method);

    default MethodInvoker getMethodInvoker(Class<?> type, String methodName, Class<?>... parameterTypes) {
        Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
        return getMethodInvoker(method);
    }

    FunctionInvoker getFunctionInvoker(Method method);

    default FunctionInvoker getFunctionInvoker(Class<?> type, String methodName, Class<?>... parameterTypes) {
        Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
        return getFunctionInvoker(method);
    }
}
