package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Describer;
import xyz.srclab.common.reflect.MethodHelper;

import java.lang.reflect.Method;

@Immutable
public interface MethodInvoker {

    static MethodInvoker of(Method method) {
        return InvokerSupport.getMethodInvoker(method);
    }

    static MethodInvoker of(Class<?> type, String methodName, Class<?>... parameterTypes) {
        @Nullable Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
        Checker.checkState(method != null, () ->
                "Method not found: " + Describer.methodToString(type, methodName, parameterTypes));
        return of(method);
    }

    Method getMethod();

    @Nullable
    Object invoke(@Nullable Object object, Object... args);
}
