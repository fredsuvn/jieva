package xyz.srclab.bytecode.provider.cglib;

import java.lang.reflect.Method;

public interface CallbackFilter {

    int accept(Method method);
}
