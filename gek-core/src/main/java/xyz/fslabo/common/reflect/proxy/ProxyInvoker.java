package xyz.fslabo.common.reflect.proxy;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This interfaces represents an invoker to help invoking proxied method directly. each instance associates a proxied
 * method generated by {@link ProxyBuilder}.
 *
 * @author fredsuvn
 */
public interface ProxyInvoker {

    /**
     * Invokes method associated to this invoker with specified instance and arguments.
     * <p>
     * This method expects a non-proxy instance to be passed in, so that it invokes the actual method implementation of
     * that instance. If a proxy instance is passed in (such as first argument of
     * {@link MethodProxyHandler#invoke(Object, Method, Object[], ProxyInvoker)}), this method may cause a stack
     * overflow because it always invokes this method recursively.
     *
     * @param inst given instance
     * @param args the arguments
     * @return result of invocation
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invoke(Object inst, Object[] args) throws Throwable;

    /**
     * Invokes proxied ({@code super}) method associated to this invoker with specified instance and arguments.
     *
     * @param inst given instance
     * @param args the arguments
     * @return result of invocation
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invokeSuper(Object inst, Object[] args) throws Throwable;
}
