package xyz.fsgek.common.proxy;

import java.lang.reflect.Method;

/**
 * Proxy for method.
 *
 * @author fredsuvn
 */
public interface FsProxyMethod {

    /**
     * Invokes proxy method with invocation arguments, source method and source method invocation function.
     *
     * @param args             invocation arguments
     * @param sourceMethod     source method
     * @param sourceInvocation source method invocation
     * @return result of invoking
     */
    Object invokeProxy(Object[] args, Method sourceMethod, Invoke sourceInvocation);

    /**
     * Invocation of method.
     */
    interface Invoke {

        /**
         * Invocation body.
         *
         * @param args arguments
         * @return result of invoking
         * @throws Throwable error during invoking
         */
        Object invoke(Object[] args) throws Throwable;
    }
}
