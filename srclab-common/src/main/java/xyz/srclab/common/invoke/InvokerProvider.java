package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Immutable
public interface InvokerProvider {

    <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor);

    MethodInvoker getMethodInvoker(Method method);

    FunctionInvoker getFunctionInvoker(Method method);
}
