package xyz.srclab.common.reflect;

import xyz.srclab.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Invoker interface, to invoke an executable or runnable object.
 *
 * @author sunq62
 */
public interface FsInvoker {

    /**
     * Returns FsInvoker instance of given method by reflecting.
     *
     * @param method given method
     */
    static FsInvoker reflectMethod(Method method) {
        return new OfMethod(method);
    }

    /**
     * Returns FsInvoker instance of given constructor by reflecting.
     *
     * @param constructor given constructor
     */
    static FsInvoker reflectConstructor(Constructor<?> constructor) {
        return new OfConstructor(constructor);
    }

    /**
     * Returns FsInvoker instance of given method by {@link MethodHandles}.
     *
     * @param method given method
     */
    static FsInvoker unreflectMethod(Method method) {
        return new OfMethodHandle(method);
    }

    /**
     * Returns FsInvoker instance of given constructor by {@link MethodHandles}.
     *
     * @param constructor given constructor
     */
    static FsInvoker unreflectConstructor(Constructor<?> constructor) {
        return new OfMethodHandle(constructor);
    }

    /**
     * Invokes with instance object and given arguments.
     * If this invoker represents a member method of object instance, the instance object must not null.
     * If this invoker represents a constructor or a static method, the instance object may be null.
     *
     * @param inst instance object
     * @param args given arguments
     */
    @Nullable
    Object invoke(@Nullable Object inst, Object... args);
}

final class OfMethod implements FsInvoker {

    private final Method method;

    OfMethod(Method method) {
        this.method = method;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
        try {
            return method.invoke(inst, args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

final class OfConstructor implements FsInvoker {

    private final Constructor<?> constructor;

    OfConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

final class OfMethodHandle implements FsInvoker {

    private final MethodHandle methodHandle;
    private final boolean isStatic;

    OfMethodHandle(Method method) {
        try {
            this.methodHandle = MethodHandles.lookup().unreflect(method);
            this.isStatic = Modifier.isStatic(method.getModifiers());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    OfMethodHandle(Constructor<?> constructor) {
        try {
            this.methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
            this.isStatic = true;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
        try {
            return isStatic ? invokeStatic(args) : invokeVirtual(inst, args);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private @Nullable Object invokeVirtual(@Nullable Object inst, Object... args) throws Throwable {
        switch (args.length) {
            case 0:
                return methodHandle.invoke(inst);
            case 1:
                return methodHandle.invoke(inst, args[0]);
            case 2:
                return methodHandle.invoke(inst, args[0], args[1]);
            case 3:
                return methodHandle.invoke(inst, args[0], args[1], args[2]);
            case 4:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3]);
            case 5:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return methodHandle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                Object[] actualArgs = new Object[args.length + 1];
                actualArgs[0] = inst;
                System.arraycopy(args, 0, actualArgs, 1, args.length);
                return methodHandle.invokeWithArguments(actualArgs);
        }
    }

    private @Nullable Object invokeStatic(Object... args) throws Throwable {
        switch (args.length) {
            case 0:
                return methodHandle.invoke();
            case 1:
                return methodHandle.invoke(args[0]);
            case 2:
                return methodHandle.invoke(args[0], args[1]);
            case 3:
                return methodHandle.invoke(args[0], args[1], args[2]);
            case 4:
                return methodHandle.invoke(args[0], args[1], args[2], args[3]);
            case 5:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return methodHandle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                return methodHandle.invokeWithArguments(args);
        }
    }
}