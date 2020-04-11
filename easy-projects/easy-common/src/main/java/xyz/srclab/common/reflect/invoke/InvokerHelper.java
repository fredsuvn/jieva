package xyz.srclab.common.reflect.invoke;

import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.reflect.instance.InstanceHelper;
import xyz.srclab.common.reflect.method.MethodHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class InvokerHelper {

    private static final Cache<Object, MethodInvoker> methodInvokerCache = new ThreadLocalCache<>();

    private static final Cache<Object, ConstructorInvoker> constructorInvokerCache =
            new ThreadLocalCache<>();

    public static MethodInvoker getMethodInvoker(Method method) {
        return methodInvokerCache.getNonNull(method, k -> MethodInvoker.of(method));
    }

    public static MethodInvoker getMethodInvoker(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Method method = MethodHelper.getMethod(cls, methodName, parameterTypes);
        return getMethodInvoker(method);
    }

    public static ConstructorInvoker getConstructorInvoker(Constructor<?> constructor) {
        return constructorInvokerCache.getNonNull(constructor, k -> ConstructorInvoker.of(constructor));
    }

    public static ConstructorInvoker getConstructorInvoker(Class<?> cls, Class<?>... parameterTypes) {
        Constructor<?> constructor = InstanceHelper.getConstructor(cls, parameterTypes);
        return getConstructorInvoker(constructor);
    }
}
