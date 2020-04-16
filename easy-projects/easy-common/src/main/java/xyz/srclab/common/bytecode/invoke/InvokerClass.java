package xyz.srclab.common.bytecode.invoke;

import xyz.srclab.common.reflect.invoke.ConstructorInvoker;
import xyz.srclab.common.reflect.invoke.MethodInvoker;

/**
 * @author sunqian
 */
public interface InvokerClass<T> {

    static <T> InvokerClass<T> ofType(Class<T> type) {
        return InvokerClassSupport.getInvokerClass(type);
    }

    ConstructorInvoker<T> getConstructorInvoker(Class<?>... parameterTypes);

    MethodInvoker getMethodInvoker(String methodName, Class<?>... parameterTypes);
}
