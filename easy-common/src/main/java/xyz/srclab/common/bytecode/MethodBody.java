package xyz.srclab.common.bytecode;

import java.lang.reflect.Method;

public interface MethodBody<T> {

    T invoke(Object object, Method method, Object[] args, MethodInvoker invoker);
}
