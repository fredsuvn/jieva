package xyz.srclab.common.util.proxy;

import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public interface ProxyMethod {

    Object invoke(Object object, Object[] args, Method method, SuperInvoker superInvoker);
}
