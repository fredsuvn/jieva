package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class InvokerImpls {

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

        @Override
        public @Nullable Object invoke(@Nullable Object inst, Object... args) {
            try {
                return isStatic ? invokeStatic(methodHandle, args) : invokeVirtual(methodHandle, inst, args);
            } catch (Throwable e) {
                throw new InvokingException(e);
            }
        }
    }

    private static @Nullable Object invokeVirtual(MethodHandle methodHandle, @Nullable Object inst, Object... args) throws Throwable {
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

    private static @Nullable Object invokeStatic(MethodHandle methodHandle, Object... args) throws Throwable {
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
