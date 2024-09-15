package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Utilities for invoking operation.
 *
 * @author fredsuvn
 */
public class JieInvoke {

    /**
     * Invokes given {@link MethodHandle} with specified instance and arguments. The handle must reference to an
     * instance method, and specified instance must not {@code null}.
     *
     * @param handle handle must reference to an instance method
     * @param inst   specified instance, not {@code null}
     * @param args   specified arguments
     * @return result of invocation
     * @throws Throwable anything thrown by the target method invocation
     */
    public static @Nullable Object invoke(MethodHandle handle, Object inst, Object... args) throws Throwable {
        switch (args.length) {
            case 0:
                return handle.invoke(inst);
            case 1:
                return handle.invoke(inst, args[0]);
            case 2:
                return handle.invoke(inst, args[0], args[1]);
            case 3:
                return handle.invoke(inst, args[0], args[1], args[2]);
            case 4:
                return handle.invoke(inst, args[0], args[1], args[2], args[3]);
            case 5:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                Object[] actualArgs = new Object[args.length + 1];
                actualArgs[0] = inst;
                System.arraycopy(args, 0, actualArgs, 1, args.length);
                return handle.invokeWithArguments(actualArgs);
        }
    }

    /**
     * Invokes given {@link MethodHandle} with specified arguments. The handle must reference to a non-instance method
     * (static method or constructor).
     *
     * @param handle handle must reference to a non-instance method (static method or constructor)
     * @param args   specified arguments
     * @return result of invocation
     * @throws Throwable anything thrown by the target method invocation
     */
    public static @Nullable Object invokeStatic(MethodHandle handle, Object... args) throws Throwable {
        switch (args.length) {
            case 0:
                return handle.invoke();
            case 1:
                return handle.invoke(args[0]);
            case 2:
                return handle.invoke(args[0], args[1]);
            case 3:
                return handle.invoke(args[0], args[1], args[2]);
            case 4:
                return handle.invoke(args[0], args[1], args[2], args[3]);
            case 5:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4]);
            case 6:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5]);
            case 7:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
            case 8:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
            case 9:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
            case 10:
                return handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            default:
                return handle.invokeWithArguments(args);
        }
    }

    static final class OfMethod implements Invoker {

        private final Method method;

        OfMethod(Method method) {
            this.method = method;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, Object... args) {
            try {
                return method.invoke(inst, args);
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    throw new InvokingException(e.getCause());
                }
                throw new InvokingException(e);
            }
        }
    }

    static final class OfConstructor implements Invoker {

        private final Constructor<?> constructor;

        OfConstructor(Constructor<?> constructor) {
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

    static final class OfMethodHandle implements Invoker {

        private final MethodHandle methodHandle;
        private final boolean isStatic;

        OfMethodHandle(Method method) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflect(method);
                this.isStatic = Modifier.isStatic(method.getModifiers());
            } catch (IllegalAccessException e) {
                throw new InvokingException(e);
            }
        }

        OfMethodHandle(Constructor<?> constructor) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
                this.isStatic = true;
            } catch (IllegalAccessException e) {
                throw new InvokingException(e);
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
                    : JieInvoke.invoke(methodHandle, inst, args);
            } catch (Throwable e) {
                throw new InvokingException(e);
            }
        }
    }
}
