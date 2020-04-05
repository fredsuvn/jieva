package xyz.srclab.common.reflect.invoke;

import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class InvokerHelper {

    private static final ThreadLocalCache<Method, MethodInvoker> METHOD_INVOKER_CACHE = new ThreadLocalCache<>();

    private static final ThreadLocalCache<Constructor<?>, ConstructorInvoker> CONSTRUCTOR_INVOKER_CACHE =
            new ThreadLocalCache<>();

    public static MethodInvoker getMethodInvoker(Method method) {
        return METHOD_INVOKER_CACHE.getNonNull(method, MethodInvoker::of);
    }

    public static ConstructorInvoker getConstructorInvoker(Constructor<?> constructor) {
        return CONSTRUCTOR_INVOKER_CACHE.getNonNull(constructor, ConstructorInvoker::of);
    }
}
