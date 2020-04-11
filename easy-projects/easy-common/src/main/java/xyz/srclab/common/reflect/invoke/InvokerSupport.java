package xyz.srclab.common.reflect.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class InvokerSupport {

    private static final InvokerProvider invokerProvider =
            InvokerProviderManager.INSTANCE.getProvider();

    static MethodInvoker newMethodInvoker(Method method) {
        return invokerProvider.newMethodInvoker(method);
    }

    static ConstructorInvoker newConstructorInvoker(Constructor<?> constructor) {
        return invokerProvider.newConstructorInvoker(constructor);
    }
}
