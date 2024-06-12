package xyz.fsgek.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Invoker interface, to invoke an executable or runnable object.
 *
 * @author fredsuvn
 */
public interface GekInvoker {

    /**
     * Returns GekInvoker instance of given method by reflecting.
     *
     * @param method given method
     * @return GekInvoker instance by reflecting
     */
    static GekInvoker reflectMethod(Method method) {
        return new Impls.OfMethod(method);
    }

    /**
     * Returns GekInvoker instance of given constructor by reflecting.
     *
     * @param constructor given constructor
     * @return GekInvoker instance by reflecting
     */
    static GekInvoker reflectConstructor(Constructor<?> constructor) {
        return new Impls.OfConstructor(constructor);
    }

    /**
     * Returns GekInvoker instance of given method by {@link MethodHandles}.
     *
     * @param method given method
     * @return GekInvoker instance by {@link MethodHandles}
     */
    static GekInvoker unreflectMethod(Method method) {
        return new Impls.OfMethodHandle(method);
    }

    /**
     * Returns GekInvoker instance of given constructor by {@link MethodHandles}.
     *
     * @param constructor given constructor
     * @return GekInvoker instance by {@link MethodHandles}
     */
    static GekInvoker unreflectConstructor(Constructor<?> constructor) {
        return new Impls.OfMethodHandle(constructor);
    }

    /**
     * Invokes with instance object and given arguments.
     * If this invoker represents a member method of object instance, the instance object must not null.
     * If this invoker represents a constructor or a static method, the instance object may be null.
     *
     * @param inst instance object
     * @param args given arguments
     * @return result of invoking
     */
    @Nullable
    Object invoke(@Nullable Object inst, Object... args);
}


