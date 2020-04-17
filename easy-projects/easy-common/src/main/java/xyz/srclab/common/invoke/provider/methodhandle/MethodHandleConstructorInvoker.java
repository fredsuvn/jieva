package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.common.invoke.ConstructorInvoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

final class MethodHandleConstructorInvoker<T> implements ConstructorInvoker<T> {

    private final ConstructorInvoker<T> invokeByMethodHandle;

    MethodHandleConstructorInvoker(Constructor<T> constructor) {
        this.invokeByMethodHandle = buildMethodHandleInvoker(constructor);
    }

    private ConstructorInvoker<T> buildMethodHandleInvoker(Constructor<T> constructor) {
        MethodHandle methodHandle = buildMethodHandle(constructor);
        return new InvokeByMethodHandle.DefaultConstructorInvoker(methodHandle);
    }

    private MethodHandle buildMethodHandle(Constructor<?> constructor) {
        MethodType methodType;
        switch (constructor.getParameterCount()) {
            case 0:
                methodType = MethodType.methodType(void.class);
                break;
            case 1:
                methodType = MethodType.methodType(void.class, constructor.getParameterTypes()[0]);
                break;
            default:
                methodType = MethodType.methodType(void.class, constructor.getParameterTypes());
        }
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return lookup.findConstructor(constructor.getDeclaringClass(), methodType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public T invoke(Object... args) {
        return invokeByMethodHandle.invoke(args);
    }
}
