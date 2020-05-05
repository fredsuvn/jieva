package xyz.srclab.common.bytecode.provider.invoke.asm.jdk;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastConstructor;
import xyz.srclab.common.base.Context;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerGenerator;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.ReflectConstants;

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

    public static void main(String[] args) throws Exception {
        Constructor<A> constructor = A.class.getConstructor();
        ConstructorInvoker<A<String>> constructorInvoker = cast(new AConstructorInvoker());
        FastClass fastClass = FastClass.create(A.class);
        FastConstructor fastConstructor = fastClass.getConstructor(ReflectConstants.EMPTY_PARAMETER_TYPES);
        long times = 10000000000L;
        showTime("Reflect invoker", times, constructor::newInstance);
        showTime("Static invoker", times, constructorInvoker::invoke);
        showTime("Fast constructor invoker", times, fastConstructor::newInstance);
    }

    private static void showTime(String title, long times, RunThrow runnable) {
        try {
            long startTime = Context.millis();
            for (long i = 0; i < times; i++) {
                runnable.run();
            }
            long endTime = Context.millis();
            System.out.println(title + ": " + (endTime - startTime));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static <T> T cast(Object any) {
        return (T) any;
    }

    public static class A<T> {

    }

    public static final class AConstructorInvoker implements ConstructorInvoker<A> {

        private final Constructor<A> constructor;

        {
            try {
                constructor = A.class.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public Constructor<A> getConstructor() {
            return constructor;
        }

        @Override
        public A invoke(Object... args) {
            return new A();
        }
    }

    interface RunThrow {

        void run() throws Exception;
    }
}
