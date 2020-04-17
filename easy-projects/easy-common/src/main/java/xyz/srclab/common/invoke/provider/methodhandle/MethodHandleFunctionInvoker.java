package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.invoke.FunctionInvoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class MethodHandleFunctionInvoker implements FunctionInvoker {

    private final FunctionInvoker invokeByMethodHandle;

    MethodHandleFunctionInvoker(Method method) {
        this.invokeByMethodHandle = buildMethodHandleInvoker(method);
    }

    private FunctionInvoker buildMethodHandleInvoker(Method method) {
        MethodHandle methodHandle = buildMethodHandle(method);
        return new InvokeByMethodHandle.StaticMethodInvoker(methodHandle);
    }

    private MethodHandle buildMethodHandle(Method method) {
        MethodType methodType;
        switch (method.getParameterCount()) {
            case 0:
                methodType = MethodType.methodType(method.getReturnType());
                break;
            case 1:
                methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes()[0]);
                break;
            default:
                methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
        }
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return Modifier.isStatic(method.getModifiers()) ?
                    lookup.findStatic(method.getDeclaringClass(), method.getName(), methodType)
                    :
                    lookup.findVirtual(method.getDeclaringClass(), method.getName(), methodType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public @Nullable Object invoke(Object... args) {
        return invokeByMethodHandle.invoke(args);
    }
}
