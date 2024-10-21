package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class OfMethodHandle implements Invoker {

    private final MethodHandle methodHandle;
    private final boolean isStatic;

    OfMethodHandle(Method method) {
        try {
            this.methodHandle = MethodHandles.lookup().unreflect(method);
            this.isStatic = Modifier.isStatic(method.getModifiers());
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    OfMethodHandle(Constructor<?> constructor) {
        try {
            this.methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
            this.isStatic = true;
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    OfMethodHandle(MethodHandle methodHandle, boolean isStatic) {
        this.methodHandle = methodHandle;
        this.isStatic = isStatic;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
        try {
            return isStatic ? JieInvoke.invokeStatic(methodHandle, args)
                : JieInvoke.invokeInstance(methodHandle, inst, args);
        } catch (Throwable e) {
            throw new InvocationException(e);
        }
    }
}
