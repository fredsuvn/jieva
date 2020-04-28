package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class MethodHandleMethodInvoker implements MethodInvoker {

    private final Method method;
    private final MethodInvoker methodHandle;

    MethodHandleMethodInvoker(Method method) {
        this.method = method;
        this.methodHandle = buildMethodHandleInvoker(method);
    }

    private MethodInvoker buildMethodHandleInvoker(Method method) {
        MethodHandle methodHandle = MethodHandleSupport.getMethodHandle(method);
        return Modifier.isStatic(method.getModifiers()) ?
                new StaticMethodHandle(methodHandle)
                :
                new VirtualMethodHandle(methodHandle);
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object object, Object... args) {
        return methodHandle.invoke(object, args);
    }
}
