package xyz.fs404.common.proxy;

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
         */
        Object invoke(Object[] args) throws Throwable;
    }
}
