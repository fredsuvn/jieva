package xyz.srclab.common.reflect.method;

import xyz.srclab.common.reflect.invoke.MethodInvoker;

import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public interface ProxyMethod {

    Object invoke(Object object, Object[] args, Method method, MethodInvoker superInvoker);
}
