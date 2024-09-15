package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

final class ConstructorInvoker implements Invoker {

    private final Constructor<?> constructor;

    ConstructorInvoker(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                throw new InvokingException(e.getCause());
            }
            throw new InvokingException(e);
        }
    }
}
