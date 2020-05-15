package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Context;
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

    public static void main(String[] args) throws Exception {
        A a = new A();
        AsmInvokerGeneratorImpl impl = new AsmInvokerGeneratorImpl();

        Constructor<String> constructor = String.class.getConstructor(String.class);
        ConstructorInvoker<String> constructorInvoker = impl.newConstructorInvoker(constructor);
        System.out.println(constructorInvoker.invoke("123", "456"));

        Method ssMethod = A.class.getMethod("ss", int.class, String.class);
        MethodInvoker ss = impl.newMethodInvoker(ssMethod);
        @Nullable Object result = ss.invoke(a, 2, "222");
        System.out.println("ss result: " + result);

        Method ssvMethod = A.class.getMethod("ssv", int.class, String.class);
        MethodInvoker ssv = impl.newMethodInvoker(ssvMethod);
        result = ssv.invoke(a, 2, "222");
        System.out.println("ssv result: " + result);

        Method ssStaticMethod = A.class.getMethod("ssStatic", int.class, String.class);
        FunctionInvoker ssStatic = impl.newFunctionInvoker(ssStaticMethod);
        result = ssStatic.invoke(2, "222");
        System.out.println("ssStatic result: " + result);

        Method ssvStaticMethod = A.class.getMethod("ssvStatic", int.class, String.class);
        FunctionInvoker ssvStatic = impl.newFunctionInvoker(ssvStaticMethod);
        result = ssvStatic.invoke(2, "222");
        System.out.println("ssvStatic result: " + result);

        System.out.println("-----------------------");

        long times = 10000000000L;
        int arg0 = 10086;
        String arg1 = "hehe";
        test("ssv direct", times, () -> {
            a.ssv(arg0, arg1);
        });
        test("ssvStatic direct", times, () -> {
            A.ssvStatic(arg0, arg1);
        });
        test("ssv invoke", times, () -> {
            ssv.invoke(a, arg0, arg1);
        });
        test("ssvStatic invoke", times, () -> {
            ssvStatic.invoke(arg0, arg1);
        });
        test("ssv reflect", times, () -> {
            ssvMethod.invoke(a, arg0, arg1);
        });
        test("ssvStatic reflect", times, () -> {
            ssvStaticMethod.invoke(null, arg0, arg1);
        });
    }

    private static void test(String title, long times, RunnableThrows runnable) {
        long start = Context.millis();
        try {
            for (int i = 0; i < times; i++) {
                runnable.run();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        long end = Context.millis();
        System.out.println("test " + title + ": " + (end - start) + " ms");
    }

    interface RunnableThrows {
        void run() throws Exception;
    }

    public static class A {

        public static void ssStatic(int i, String msg) {
            System.out.println(ssvStatic(i, msg));
        }

        public static String ssvStatic(int i, String msg) {
            return "static message: " + msg + ", i: " + i;
        }

        public void ss(int i, String msg) {
            System.out.println(ssv(i, msg));
        }

        public String ssv(int i, String msg) {
            return "message: " + msg + ", i: " + i;
        }
    }
}
