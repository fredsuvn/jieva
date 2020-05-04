package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.method.MethodHelper;

import java.lang.reflect.Method;

@Immutable
public interface FunctionInvoker {

    static FunctionInvoker of(Method method) {
        return InvokerSupport.getFunctionInvoker(method);
    }

    @Nullable
    static FunctionInvoker of(Class<?> type, String methodName, Class<?>... parameterTypes) {
        @Nullable Method method = MethodHelper.getMethod(type, methodName, parameterTypes);
        if (method == null) {
            return null;
        }
        return of(method);
    }

    Method getMethod();

    @Nullable
    Object invoke(Object... args);
}
