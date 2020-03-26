package xyz.srclab.common.bytecode.impl.cglib;

import java.lang.reflect.Method;

public interface MethodInterceptor extends Callback {

    Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable;
}
