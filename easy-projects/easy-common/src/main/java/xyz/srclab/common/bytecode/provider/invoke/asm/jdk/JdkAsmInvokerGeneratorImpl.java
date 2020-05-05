package xyz.srclab.common.bytecode.provider.invoke.asm.jdk;

import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerGenerator;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
final class JdkAsmInvokerGeneratorImpl implements AsmInvokerGenerator {

    @Override
    public <T> ConstructorInvoker<T> newConstructorInvoker(Constructor<T> constructor) {
        return null;
    }

    @Override
    public MethodInvoker newMethodInvoker(Method method) {
        return null;
    }

    @Override
    public FunctionInvoker newFunctionInvoker(Method method) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println("123");
    }
}
