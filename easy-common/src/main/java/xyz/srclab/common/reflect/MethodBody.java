package xyz.srclab.common.reflect;

import java.lang.reflect.Method;

public interface MethodBody<T> {

    T invoke(Object object, Method method, Object[] args, MethodInvoker<T> invoker);
}
