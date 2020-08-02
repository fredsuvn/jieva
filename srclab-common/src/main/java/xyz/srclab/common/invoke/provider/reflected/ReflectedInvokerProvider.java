package xyz.srclab.common.invoke.provider.reflected;

import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.invoke.InvokerProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ReflectedInvokerProvider implements InvokerProvider {

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
