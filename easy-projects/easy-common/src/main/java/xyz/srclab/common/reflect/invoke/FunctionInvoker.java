package xyz.srclab.common.reflect.invoke;

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

    default MethodInvoker asMethodInvoker() {
        return new MethodInvoker() {
            @Override
            public @Nullable Object invoke(@Nullable Object object, Object... args) {
                return FunctionInvoker.this.invoke(args);
            }
        };
    }
}
