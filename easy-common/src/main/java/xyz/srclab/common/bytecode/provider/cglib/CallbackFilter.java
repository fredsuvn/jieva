package xyz.srclab.common.bytecode.provider.cglib;

import java.lang.reflect.Method;

public interface CallbackFilter {

    int accept(Method method);
}
