package xyz.srclab.common.reflect.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Method;

@Immutable
public interface MethodInvoker {

    static MethodInvoker of(Method method) {
        return InvokerSupport.newMethodInvoker(method);
    }

    @Nullable
    Object invoke(Object object, Object... args);
}
