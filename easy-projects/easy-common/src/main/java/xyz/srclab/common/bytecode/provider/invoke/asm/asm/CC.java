package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Method;

/**
 * @author sunqian
 */
public class CC implements MethodInvoker, FunctionInvoker {

    private final Method method;

    public CC(Method method) {
        this.method = method;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public @Nullable Object invoke(Object... args) {
        A.sstatic();
        return null;
    }

    @Override
    public @Nullable Object invoke(@Nullable Object object, Object... args) {
        A a = (A) object;
        a.ss((Short) args[0], (String) args[1]);
        return null;
    }

    public static class A {

        public static void sstatic() {

        }

        public void ss(short a1, String a2) {
            System.out.println("ss: " + a1 + a2);
        }
    }
}
