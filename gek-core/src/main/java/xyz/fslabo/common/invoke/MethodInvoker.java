package xyz.fslabo.common.invoke;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.Method;

final class MethodInvoker implements Invoker {

    private final Method method;

    MethodInvoker(Method method) {
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
