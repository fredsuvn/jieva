package xyz.srclab.common.bytecode.provider.invoke.asm;

import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class AsmInvokerSupport {

    private static final Cache<Constructor<?>, ConstructorInvoker<?>> constructorInvokerCache =
            Cache.newGcThreadLocalL2(Cache.newPermanent());

    private static final Cache<Method, MethodInvoker> methodInvokerCache =
            Cache.newGcThreadLocalL2(Cache.newPermanent());

    private static final Cache<Method, FunctionInvoker> functionInvokerCache =
            Cache.newGcThreadLocalL2(Cache.newPermanent());

    public static <T> ConstructorInvoker<T> getConstructorInvoker(
            Constructor<T> constructor, AsmInvokerGenerator generator) {
        ConstructorInvoker<?> result =
                constructorInvokerCache.getNonNull(constructor, generator::newConstructorInvoker);
        return (ConstructorInvoker<T>) result;
    }

    public static MethodInvoker getMethodInvoker(Method method, AsmInvokerGenerator generator) {
        return methodInvokerCache.getNonNull(method, generator::newMethodInvoker);
    }

    public static FunctionInvoker getFunctionInvoker(Method method, AsmInvokerGenerator generator) {
        return functionInvokerCache.getNonNull(method, generator::newFunctionInvoker);
    }
}
