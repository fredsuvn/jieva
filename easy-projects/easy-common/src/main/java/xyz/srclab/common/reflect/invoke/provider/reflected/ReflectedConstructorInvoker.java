package xyz.srclab.common.reflect.invoke.provider.reflected;

import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.invoke.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final class ReflectedConstructorInvoker<T> implements ConstructorInvoker<T> {

    private final Constructor<T> constructor;

    ReflectedConstructorInvoker(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    @Override
    public T invoke(Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            throw new ExceptionWrapper(e);
        }
    }
}