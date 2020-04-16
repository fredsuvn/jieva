package xyz.srclab.common.reflect.invoke.provider.methodhandle;

import xyz.srclab.common.reflect.invoke.ConstructorInvoker;
import xyz.srclab.common.reflect.invoke.FunctionInvoker;
import xyz.srclab.common.reflect.invoke.InvokerProvider;
import xyz.srclab.common.reflect.invoke.MethodInvoker;

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
