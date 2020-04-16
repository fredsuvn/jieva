package xyz.srclab.common.reflect.invoke.provider.methodhandle;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.invoke.ConstructorInvoker;
import xyz.srclab.common.reflect.invoke.FunctionInvoker;
import xyz.srclab.common.reflect.invoke.MethodInvoker;

import java.lang.invoke.MethodHandle;

final class InvokeByMethodHandle {

    static final class DefaultConstructorInvoker<T> implements ConstructorInvoker<T> {

        private final StaticMethodInvoker staticMethodInvoker;

        DefaultConstructorInvoker(MethodHandle methodHandle) {
            this.staticMethodInvoker = new StaticMethodInvoker(methodHandle);
        }

        @Override
        public T invoke(Object... args) {
            return (T) staticMethodInvoker.invoke(null, args);
        }
    }

    static final class VirtualMethodInvoker implements MethodInvoker {

        private final MethodHandle methodHandle;

        VirtualMethodInvoker(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object object, Object... args) {
            try {
                return invoke0(object, args);
            } catch (Throwable throwable) {
                throw new ExceptionWrapper(throwable);
            }
        }

        private @Nullable Object invoke0(@Nullable Object object, Object... args) throws Throwable {
            switch (args.length) {
                case 0:
                    return methodHandle.invoke(object);
                case 1:
                    return methodHandle.invoke(object, args[0]);
                case 2:
                    return methodHandle.invoke(object, args[0], args[1]);
                case 3:
                    return methodHandle.invoke(object, args[0], args[1], args[2]);
                case 4:
                    return methodHandle.invoke(object, args[0], args[1], args[2], args[3]);
                case 5:
                    return methodHandle.invoke(object, args[0], args[1], args[2], args[3], args[4]);
                default:
                    Object[] arguments = new Object[args.length + 1];
                    arguments[0] = object;
                    System.arraycopy(args, 0, arguments, 1, args.length);
                    return methodHandle.invokeWithArguments(arguments);
            }
        }
    }

    static final class StaticMethodInvoker implements MethodInvoker, FunctionInvoker {

        private final MethodHandle methodHandle;

        StaticMethodInvoker(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object object, Object... args) {
            try {
                return invoke0(args);
            } catch (Throwable throwable) {
                throw new ExceptionWrapper(throwable);
            }
        }

        @Override
        public @Nullable Object invoke(Object... args) {
            try {
                return invoke0(args);
            } catch (Throwable throwable) {
                throw new ExceptionWrapper(throwable);
            }
        }

        private @Nullable Object invoke0(Object... args) throws Throwable {
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
                default:
                    return methodHandle.invokeWithArguments(args);
            }
        }

        @Override
        public MethodInvoker asMethodInvoker() {
            return this;
        }
    }
}
