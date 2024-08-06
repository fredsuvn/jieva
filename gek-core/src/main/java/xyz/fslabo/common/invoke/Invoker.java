package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * This interface represents a reference to executable content, which can be a method, a constructor, a block of code,
 * or any other executable object.
 *
 * @author fredsuvn
 */
@FunctionalInterface
public interface Invoker {

    /**
     * Returns {@link Invoker} instance of given method by reflecting.
     *
     * @param method given method
     * @return {@link Invoker} instance
     */
    static Invoker reflectMethod(Method method) {
        return new InvokerImpls.OfMethod(method);
    }

    /**
     * Returns {@link Invoker} instance of given constructor by reflecting.
     *
     * @param constructor given constructor
     * @return {@link Invoker} instance
     */
    static Invoker reflectConstructor(Constructor<?> constructor) {
        return new InvokerImpls.OfConstructor(constructor);
    }

    /**
     * Returns {@link Invoker} instance of given method by {@link MethodHandles.Lookup#unreflect(Method)}.
     *
     * @param method given method
     * @return {@link Invoker} instance
     */
    static Invoker unreflectMethod(Method method) {
        return new InvokerImpls.OfMethodHandle(method);
    }

    /**
     * Returns {@link Invoker} instance of given constructor by
     * {@link MethodHandles.Lookup#unreflectConstructor(Constructor)}.
     *
     * @param constructor given constructor
     * @return {@link Invoker} instance
     */
    static Invoker unreflectConstructor(Constructor<?> constructor) {
        return new InvokerImpls.OfMethodHandle(constructor);
    }

    /**
     * Invokes with specified object and arguments.
     * <p>
     * If this invoker references a {@link Method} or {@link Constructor}, this method and usage of its parameters are
     * equivalent to {@link Method#invoke(Object, Object...)} and {@link Constructor#newInstance(Object...)}.
     * Otherwise, usage of the parameters is up to the implementation.
     *
     * @param obj  specified object
     * @param args specified arguments
     * @return result of invoking
     * @throws InvokerException if any exception occurs
     */
    @Nullable
    Object invoke(@Nullable Object obj, Object... args) throws InvokerException;
}


