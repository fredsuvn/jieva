package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

final class MethodHandleFunctionInvoker implements FunctionInvoker {

    private final Method method;
    private final MethodInvoker methodHandle;

    MethodHandleFunctionInvoker(Method method) {
        this.method = method;
        this.methodHandle = buildMethodHandleInvoker(method);
    }

    private MethodInvoker buildMethodHandleInvoker(Method method) {
        MethodHandle methodHandle = MethodHandleSupport.getMethodHandle(method);
        return new StaticMethodHandle(methodHandle);
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public @Nullable Object invoke(Object... args) {
        return methodHandle.invoke(null, args);
    }
}
