package xyz.srclab.common.bytecode.provider.invoke.jdkasm;

import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class AsmInvokerGenerator {

    public <T> ConstructorInvoker<T> newConstructorInvoker(Constructor<T> constructor) {
        return null;
    }

    public MethodInvoker newMethodInvoker(Method method) {
        return null;
    }

    public FunctionInvoker newFunctionInvoker(Method method) {
        return null;
    }
}
