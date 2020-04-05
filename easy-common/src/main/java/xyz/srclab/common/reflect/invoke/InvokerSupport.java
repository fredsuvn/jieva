package xyz.srclab.common.reflect.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

class InvokerSupport {

    private static final InvokerProvider INVOKER_PROVIDER =
            InvokerProviderManager.getInstance().getProvider();

    static MethodInvoker newMethodInvoker(Method method) {
        return INVOKER_PROVIDER.newMethodInvoker(method);
    }

    static ConstructorInvoker newConstructorInvoker(Constructor<?> constructor) {
        return INVOKER_PROVIDER.newConstructorInvoker(constructor);
    }
}
