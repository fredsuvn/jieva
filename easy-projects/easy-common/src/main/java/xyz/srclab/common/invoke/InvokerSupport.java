package xyz.srclab.common.invoke;

import xyz.srclab.common.EasyBoot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class InvokerSupport {

    private static final InvokerProvider invokerProvider = EasyBoot.getProvider(InvokerProvider.class);

    static <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor) {
        return invokerProvider.getConstructorInvoker(constructor);
    }

    static <T> ConstructorInvoker<T> getConstructorInvoker(Class<T> type, Class<?>... parameterTypes) {
        return invokerProvider.getConstructorInvoker(type, parameterTypes);
    }

    static MethodInvoker getMethodInvoker(Method method) {
        return invokerProvider.getMethodInvoker(method);
    }

    static MethodInvoker getMethodInvoker(Class<?> type, String methodName, Class<?>... parameterTypes) {
        return invokerProvider.getMethodInvoker(type, methodName, parameterTypes);
    }

    static FunctionInvoker getFunctionInvoker(Method method) {
        return invokerProvider.getFunctionInvoker(method);
    }

    static FunctionInvoker getFunctionInvoker(Class<?> type, String methodName, Class<?>... parameterTypes) {
        return invokerProvider.getFunctionInvoker(type, methodName, parameterTypes);
    }
}
