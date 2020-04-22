package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Method;

@Immutable
public interface FunctionInvoker {

    static FunctionInvoker of(Method method) {
        return InvokerSupport.getFunctionInvoker(method);
    }

    static FunctionInvoker of(Class<?> type, String methodName, Class<?>... parameterTypes) {
        return InvokerSupport.getFunctionInvoker(type, methodName, parameterTypes);
    }

    @Nullable
    Object invoke(Object... args);
}
