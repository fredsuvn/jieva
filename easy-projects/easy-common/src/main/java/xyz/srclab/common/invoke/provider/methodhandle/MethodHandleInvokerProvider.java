package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.InvokerProvider;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MethodHandleInvokerProvider implements InvokerProvider {

    public static final MethodHandleInvokerProvider INSTANCE = new MethodHandleInvokerProvider();

    @Override
    public <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor) {
        return new MethodHandleConstructorInvoker<>(constructor);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method) {
        return new MethodHandleMethodInvoker(method);
    }

    @Override
    public FunctionInvoker getFunctionInvoker(Method method) {
        return new MethodHandleFunctionInvoker(method);
    }
}
