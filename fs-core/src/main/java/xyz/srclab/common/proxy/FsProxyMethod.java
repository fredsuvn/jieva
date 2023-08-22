package xyz.srclab.common.proxy;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * Proxy for method.
 *
 * @author fredsuvn
 */
public interface FsProxyMethod {

    /**
     * Invokes proxy method with invocation arguments, source method invocation function and source method.
     *
     * @param args             invocation arguments
     * @param sourceMethod     source method
     * @param sourceInvocation source method invocation
     */
    Object invoke(Object[] args, Method sourceMethod, Function<Object[], Object> sourceInvocation);
}
