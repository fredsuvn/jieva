package xyz.srclab.common.bytecode.provider.invoke.jdkasm;

import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.InvokerProvider;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public class JdkAsmInvokerProvider implements InvokerProvider {

    @Override
    public <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor) {
        return null;
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method) {
        return null;
    }

    @Override
    public FunctionInvoker getFunctionInvoker(Method method) {
        return null;
    }
}
