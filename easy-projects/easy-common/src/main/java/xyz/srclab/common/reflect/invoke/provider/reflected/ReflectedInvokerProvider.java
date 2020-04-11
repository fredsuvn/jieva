package xyz.srclab.common.reflect.invoke.provider.reflected;

import xyz.srclab.common.reflect.invoke.ConstructorInvoker;
import xyz.srclab.common.reflect.invoke.InvokerProvider;
import xyz.srclab.common.reflect.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ReflectedInvokerProvider implements InvokerProvider {

    public static final ReflectedInvokerProvider INSTANCE = new ReflectedInvokerProvider();

    @Override
    public MethodInvoker newMethodInvoker(Method method) {
        return new ReflectedMethodInvoker(method);
    }

    @Override
    public ConstructorInvoker newConstructorInvoker(Constructor<?> constructor) {
        return new ReflectedConstructorInvoker(constructor);
    }
}
