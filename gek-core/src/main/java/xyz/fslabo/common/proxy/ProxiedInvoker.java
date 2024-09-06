package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

/**
 * This interface represents a proxied method handle of {@link TypeProxy}, usually be provided by
 * {@link ProxyBuilder}.
 *
 * @author fredsuvn
 */
public interface ProxiedInvoker {

    /**
     * Invokes with given instance and argument.
     *
     * @param inst given instance
     * @param args arguments
     * @return result of invocation
     * @throws Throwable the bare exceptions thrown by the proxied method, without any wrapping such as
     *                   {@link InvocationTargetException}
     */
    @Nullable
    Object invoke(@Nullable Object inst, Object... args) throws Throwable;
}
