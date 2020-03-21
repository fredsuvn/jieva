package xyz.srclab.common.bytecode.impl.cglib;

import java.lang.reflect.Method;

public interface CallbackFilter {

    int accept(Method method);
}
