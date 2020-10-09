package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.exception.RuntimeExceptionWrapper;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
final class VirtualMethodHandle implements MethodInvoker {

    private final MethodHandle methodHandle;

    VirtualMethodHandle(MethodHandle methodHandle) {
        this.methodHandle = methodHandle;
    }

    @Override
    public Method getMethod() {
        throw new UnsupportedOperationException();
    }

    public @Nullable Object invoke(@Nullable Object object, Object... args) {
        try {
            return invoke0(object, args);
        } catch (Throwable throwable) {
            throw new RuntimeExceptionWrapper(throwable);
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
