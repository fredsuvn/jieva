package xyz.srclab.sample.reflect;

import xyz.srclab.common.reflect.SignatureHelper;
import xyz.srclab.common.reflect.invoke.InvokerHelper;
import xyz.srclab.common.reflect.invoke.MethodInvoker;

public class ReflectSample {

    public static void main(String[] args) {
        MethodInvoker invoker = InvokerHelper.getMethodInvoker(A.class, "hello");
        System.out.println(invoker.invoke(new A()));

        System.out.println(SignatureHelper.signClass(A.class));
    }

    public static class A {

        public String hello() {
            return "hello";
        }
    }
}
