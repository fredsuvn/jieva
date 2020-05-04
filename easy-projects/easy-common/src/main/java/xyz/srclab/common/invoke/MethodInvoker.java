package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.method.MethodHelper;

import java.lang.reflect.Method;

@Immutable
public interface MethodInvoker {

    static MethodInvoker of(Method method) {
        return InvokerSupport.getMethodInvoker(method);
    }

    @Nullable
    static MethodInvoker of(Class<?> type, String methodName, Class<?>... parameterTypes) {
        @Nullable Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
        if (method == null) {
            return null;
        }
        return of(method);
    }

    Method getMethod();

    @Nullable
    Object invoke(@Nullable Object object, Object... args);
}
