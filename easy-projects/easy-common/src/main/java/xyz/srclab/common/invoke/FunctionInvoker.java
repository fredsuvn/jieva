package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

@Immutable
public interface FunctionInvoker {

    //static FunctionInvoker of(Method method) {
    //    return InvokerSupport.getMethodInvoker(method);
    //}
    //
    //static FunctionInvoker of(Class<?> type, String methodName, Class<?>... parameterTypes) {
    //    return InvokerSupport.getMethodInvoker(type, methodName, parameterTypes);
    //}

    @Nullable
    Object invoke(Object... args);
}
