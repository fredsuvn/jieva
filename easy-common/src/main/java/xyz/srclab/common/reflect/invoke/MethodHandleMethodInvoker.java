package xyz.srclab.common.reflect.invoke;

import xyz.srclab.annotation.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class MethodHandleMethodInvoker implements MethodInvoker {

    private final MethodInvoker invokeByMethodHandle;

    MethodHandleMethodInvoker(Method method) {
        this.invokeByMethodHandle = buildMethodHandleInvoker(method);
    }

    private MethodInvoker buildMethodHandleInvoker(Method method) {
        MethodHandle methodHandle = buildMethodHandle(method);
        return Modifier.isStatic(method.getModifiers()) ?
                new InvokeByMethodHandle.StaticMethodInvoker(methodHandle)
                :
                new InvokeByMethodHandle.VirtualMethodInvoker(methodHandle);
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
    public @Nullable Object invoke(Object object, Object... args) {
        return invokeByMethodHandle.invoke(object, args);
    }
}
