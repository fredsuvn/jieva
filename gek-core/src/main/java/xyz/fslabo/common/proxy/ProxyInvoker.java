package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Invoker to invoke proxied method (super) on given proxy or proxied instance.
 * <p>
 * Instance of {@link ProxyInvoker} should be generated from {@link ProxyProvider}, and each instance associates a
 * proxied method.
 *
 * @author fredsuvn
 */
public interface ProxyInvoker {

    /**
     * Invokes proxied method associated to this invoker with given non-proxy instance.
     * <p>
     * Note do not use a proxy instance (such as first parameter of
     * {@link MethodProxyHandler#invoke(Object, Method, Object[], ProxyInvoker)}), it will cause a recursion error.
     *
     * @param inst given instance
     * @param args arguments
     * @return result of invocation
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invoke(Object inst, Object[] args) throws Throwable;

    /**
     * Invokes proxied method associated to this invoker, this method is equivalent to call {@code super} method.
     *
     * @param args arguments
     * @return result of invocation
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invokeSuper(Object[] args) throws Throwable;
}
