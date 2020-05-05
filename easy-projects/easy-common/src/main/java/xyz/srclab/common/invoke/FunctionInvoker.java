package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.reflect.MethodHelper;

import java.lang.reflect.Method;

@Immutable
public interface FunctionInvoker {

    static FunctionInvoker of(Method method) {
        return InvokerSupport.getFunctionInvoker(method);
    }

    static FunctionInvoker of(Class<?> type, String methodName, Class<?>... parameterTypes) {
        @Nullable Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
        Checker.checkState(method != null, () ->
                "Method not found: " + MethodHelper.methodToString(type, methodName, parameterTypes));
        return of(method);
    }

    Method getMethod();

    @Nullable
    Object invoke(Object... args);
}
