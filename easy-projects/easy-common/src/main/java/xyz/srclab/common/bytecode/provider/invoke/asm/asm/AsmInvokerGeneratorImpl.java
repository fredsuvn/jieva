package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerGenerator;
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
        return null;
    }

    @Override
    public FunctionInvoker newFunctionInvoker(Method method) {
        return null;
    }

    public static void main(String[] args) throws Exception {
        AsmInvokerGeneratorImpl impl = new AsmInvokerGeneratorImpl();
        Constructor<String> constructor = String.class.getConstructor();
        ConstructorInvoker<String> constructorInvoker = impl.newConstructorInvoker(constructor);
        System.out.println(constructorInvoker.invoke());
    }
}
