package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.invoke.Invoker;

import java.lang.reflect.Method;

/**
 * This interface represents method of proxied type from {@link TypeProxy}.
 *
 * @author fredsuvn
 */
public interface TypeProxyMethod {

    /**
     * Invokes proxy method of proxy type.
     *
     * @param inst         instance of proxied type which method to be invoked
     * @param superMethod  super method, which is original proxied method
     * @param superInvoker the invoker to invoke {@code superMethod}, first param is instance of proxied type
     * @param args         arguments of invocation
     * @return result of invocation
     * @throws TypeProxyException if any exception occurs
     */
    @Nullable
    Object invokeProxy(
        @Nullable Object inst, Method superMethod, Invoker superInvoker, Object... args) throws TypeProxyException;
}
