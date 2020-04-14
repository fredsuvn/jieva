package xyz.srclab.sample.proxy;

import xyz.srclab.common.proxy.ClassProxy;

public class ProxySample {

    public static void main(String[] args) {
        ClassProxy<A> classProxy = ClassProxy.newBuilder(A.class)
                .proxyMethod("someMethod", new Class<?>[0], (o, objects, method, methodInvoker) -> {
                    String result = "proxy method";
                    System.out.println(result);
                    return result;
                })
                .build();
        System.out.println(classProxy.newInstance().someMethod());
    }

    public interface A {

        default String someMethod() {
            System.out.println("someMethod");
            return "someMethod";
        }
    }
}
