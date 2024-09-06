package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.invoke.Invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This interface represents a proxy method handle to proxy method of {@link TypeProxy}.
 *
 * @author fredsuvn
 */
public interface ProxyInvoker {

    /**
     * Invokes proxy method with given proxy instance, proxied method, {@link Invoker} of proxied method and argument.
     *
     * @param inst           given proxy instance, the instance of proxied type
     * @param proxiedMethod  proxied method, usually is the super method
     * @param proxiedInvoker handle of proxied method
     * @param args           arguments
     * @return result of invocation
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invoke(
        @Nullable Object inst, Method proxiedMethod, ProxiedInvoker proxiedInvoker, Object... args) throws Throwable;
}
