package xyz.srclab.bytecode.provider.cglib;

import java.lang.reflect.Method;

interface CallbackFilter {

    int accept(Method method);
}
