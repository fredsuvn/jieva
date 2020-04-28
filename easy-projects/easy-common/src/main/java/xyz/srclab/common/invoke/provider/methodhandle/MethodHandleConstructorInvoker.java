package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;

final class MethodHandleConstructorInvoker<T> implements ConstructorInvoker<T> {

    private final Constructor<T> constructor;
    private final MethodInvoker methodHandle;

    MethodHandleConstructorInvoker(Constructor<T> constructor) {
        this.constructor = constructor;
        this.methodHandle = buildMethodHandleInvoker(constructor);
    }

    private MethodInvoker buildMethodHandleInvoker(Constructor<T> constructor) {
        MethodHandle methodHandle = MethodHandleSupport.getMethodHandle(constructor);
        return new StaticMethodHandle(methodHandle);
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public T invoke(Object... args) {
        return (T) methodHandle.invoke(null, args);
    }
}
