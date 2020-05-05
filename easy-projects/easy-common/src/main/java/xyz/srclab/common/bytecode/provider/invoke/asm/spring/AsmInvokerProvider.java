package xyz.srclab.common.bytecode.provider.invoke.asm.spring;

import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerGenerator;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerSupport;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.InvokerProvider;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public final class AsmInvokerProvider implements InvokerProvider {

    private final AsmInvokerGenerator invokerGenerator = new SpringAsmInvokerGeneratorImpl();

    @Override
    public <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor) {
        return AsmInvokerSupport.getConstructorInvoker(constructor, invokerGenerator);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method) {
        return AsmInvokerSupport.getMethodInvoker(method, invokerGenerator);
    }

    @Override
    public FunctionInvoker getFunctionInvoker(Method method) {
        return AsmInvokerSupport.getFunctionInvoker(method, invokerGenerator);
    }
}
