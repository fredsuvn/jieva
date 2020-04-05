package xyz.srclab.common.reflect.invoke;

import xyz.srclab.common.exception.ExceptionWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final class ReflectedConstructorInvoker implements ConstructorInvoker {

    private final Constructor<?> constructor;

    ReflectedConstructorInvoker(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Object invoke(Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new ExceptionWrapper(e);
        }
    }
}
