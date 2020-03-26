package xyz.srclab.common.bytecode.provider.cglib;

import java.lang.reflect.Method;

public interface MethodInterceptor extends Callback {

    Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable;
}
