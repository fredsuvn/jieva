package xyz.srclab.common.reflect.invoke;

import xyz.srclab.annotations.Immutable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Immutable
public interface InvokerProvider {

    MethodInvoker newMethodInvoker(Method method);

    ConstructorInvoker newConstructorInvoker(Constructor<?> constructor);
}
