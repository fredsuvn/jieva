package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerGenerator;
import xyz.srclab.common.bytecode.provider.invoke.asm.asm.ConstructorInvokerClassGenerator;
import xyz.srclab.common.bytecode.provider.invoke.asm.asm.MethodInvokerClassGenerator;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
final class AsmInvokerGeneratorImpl implements AsmInvokerGenerator {

    @Override
    public <T> ConstructorInvoker<T> newConstructorInvoker(Constructor<T> constructor) {
        try {
            Class<?> generatedClass = ConstructorInvokerClassGenerator.generateClass(constructor);
            Constructor<?> generatedConstructor = generatedClass.getConstructor(Constructor.class);
            return (ConstructorInvoker<T>) generatedConstructor.newInstance(constructor);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public MethodInvoker newMethodInvoker(Method method) {
        try {
            Class<?> generatedClass = MethodInvokerClassGenerator.generateClass(method);
            Constructor<?> generatedConstructor = generatedClass.getConstructor(Method.class);
            return (MethodInvoker) generatedConstructor.newInstance(method);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public FunctionInvoker newFunctionInvoker(Method method) {
        return (FunctionInvoker) newMethodInvoker(method);
    }
}
