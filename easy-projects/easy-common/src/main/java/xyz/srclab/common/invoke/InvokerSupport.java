package xyz.srclab.common.invoke;

import xyz.srclab.common.KitvaBoot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class InvokerSupport {

    private static final InvokerProvider invokerProvider = KitvaBoot.getProvider(InvokerProvider.class);

    static <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor) {
        return invokerProvider.getConstructorInvoker(constructor);
    }

    static MethodInvoker getMethodInvoker(Method method) {
        return invokerProvider.getMethodInvoker(method);
    }

    static FunctionInvoker getFunctionInvoker(Method method) {
        return invokerProvider.getFunctionInvoker(method);
    }
}
