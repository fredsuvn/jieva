package xyz.srclab.common.reflect.invoke.provider.reflected;

import xyz.srclab.common.reflect.invoke.ConstructorInvoker;
import xyz.srclab.common.reflect.invoke.FunctionInvoker;
import xyz.srclab.common.reflect.invoke.InvokerProvider;
import xyz.srclab.common.reflect.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ReflectedInvokerProvider implements InvokerProvider {

    public static final ReflectedInvokerProvider INSTANCE = new ReflectedInvokerProvider();

    @Override
    public <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor) {
        return new ReflectedConstructorInvoker<>(constructor);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method) {
        return new ReflectedMethodInvoker(method);
    }

    @Override
    public FunctionInvoker getFunctionInvoker(Method method) {
        return new ReflectedFunctionInvoker(method);
    }
}
