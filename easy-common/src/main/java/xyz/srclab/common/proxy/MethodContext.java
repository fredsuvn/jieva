package xyz.srclab.common.proxy;

import java.lang.reflect.Method;

public interface MethodContext {

    Object getObject();

    Method getMethod();

    Object[] getArguments();

    MethodProxy getMethodProxy();
}
