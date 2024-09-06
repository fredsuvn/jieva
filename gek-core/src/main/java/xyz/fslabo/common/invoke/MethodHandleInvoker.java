package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodHandleInvoker implements Invoker {

    private final MethodHandle methodHandle;
    private final boolean isStatic;

    MethodHandleInvoker(Method method) {
        try {
            this.methodHandle = MethodHandles.lookup().unreflect(method);
            this.isStatic = Modifier.isStatic(method.getModifiers());
        } catch (IllegalAccessException e) {
            throw new InvokingException(e);
        }
    }

    MethodHandleInvoker(Constructor<?> constructor) {
        try {
            this.methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
            this.isStatic = true;
        } catch (IllegalAccessException e) {
            throw new InvokingException(e);
        }
    }

    MethodHandleInvoker(MethodHandle methodHandle, boolean isStatic) {
        this.methodHandle = methodHandle;
        this.isStatic = isStatic;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
        try {
            return isStatic ? JieInvoke.invokeStatic(methodHandle, args)
                : JieInvoke.invokeVirtual(methodHandle, inst, args);
        } catch (Throwable e) {
            throw new InvokingException(e);
        }
    }
}
