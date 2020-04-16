package xyz.srclab.common.reflect.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class InvokerSupport {

    private static final InvokerProvider invokerProvider =
            InvokerProviderManager.INSTANCE.getProvider();

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
}
