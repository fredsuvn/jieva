package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.exception.RuntimeExceptionWrapper;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
final class StaticMethodHandle implements MethodInvoker {

    private final MethodHandle methodHandle;

    StaticMethodHandle(MethodHandle methodHandle) {
        this.methodHandle = methodHandle;
    }

    @Override
    public Method getMethod() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Object invoke(@Nullable Object object, Object... args) {
        return invoke(args);
    }

    private @Nullable Object invoke(Object... args) {
        try {
            return invoke0(args);
        } catch (Throwable throwable) {
            throw new RuntimeExceptionWrapper(throwable);
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
}
