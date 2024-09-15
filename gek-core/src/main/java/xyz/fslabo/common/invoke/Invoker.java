package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * This interface represents an invocation handle to executable content, which can be a method, a constructor, a block
 * of code, or any other executable object.
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
    static Invoker reflect(Method method) {
        return new MethodInvoker(method);
    }

    /**
     * Returns {@link Invoker} instance of given constructor by reflecting.
     *
     * @param constructor given constructor
     * @return {@link Invoker} instance
     */
    static Invoker reflect(Constructor<?> constructor) {
        return new ConstructorInvoker(constructor);
    }

    /**
     * Returns {@link Invoker} instance of given method by {@link MethodHandles.Lookup#unreflect(Method)}.
     *
     * @param method given method
     * @return {@link Invoker} instance
     */
    static Invoker handle(Method method) {
        return new MethodHandleInvoker(method);
    }

    /**
     * Returns {@link Invoker} instance of given constructor by
     * {@link MethodHandles.Lookup#unreflectConstructor(Constructor)}.
     *
     * @param constructor given constructor
     * @return {@link Invoker} instance
     */
    static Invoker handle(Constructor<?> constructor) {
        return new MethodHandleInvoker(constructor);
    }

    /**
     * Returns {@link Invoker} instance with specified {@link MethodHandle}.
     *
     * @param handle   specified {@link MethodHandle}
     * @param isStatic whether treats the handle as static when invoking
     * @return {@link Invoker} instance
     */
    static Invoker handle(MethodHandle handle, boolean isStatic) {
        return new MethodHandleInvoker(handle, isStatic);
    }

    /**
     * Invokes with specified object and arguments.
     * <p>
     * If this invoker references a {@link Method}, {@link Constructor} or {@link MethodHandle}, the usage of its
     * parameters are equivalent to {@link Method#invoke(Object, Object...)}, {@link Constructor#newInstance(Object...)}
     * or {@link MethodHandle#invokeWithArguments(Object...)}. Otherwise, usage of the parameters is up to the
     * implementation.
     * <p>
     * This method only throws {@link InvokingException} if any problem occurs, use {@link InvokingException#getCause()}
     * to get underlying cause.
     *
     * @param obj  specified object
     * @param args specified arguments
     * @return result of invoking
     * @throws InvokingException wraps if any problem occurs
     */
    @Nullable
    Object invoke(@Nullable Object obj, Object... args) throws InvokingException;
}


