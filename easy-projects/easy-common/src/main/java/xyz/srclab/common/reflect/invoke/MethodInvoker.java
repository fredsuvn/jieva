package xyz.srclab.common.reflect.invoke;

import xyz.srclab.annotations.Immutable;
import xyz.srclab.annotations.Nullable;

import java.lang.reflect.Method;

@Immutable
public interface MethodInvoker {

    static MethodInvoker of(Method method) {
        return InvokerSupport.newMethodInvoker(method);
    }

    @Nullable
    Object invoke(@Nullable Object object, Object... args);
}
