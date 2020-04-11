//package xyz.srclab.sample.proxy;
//
//import org.apache.commons.lang3.ArrayUtils;
//import xyz.srclab.common.proxy.ClassProxy;
//import xyz.srclab.common.reflect.method.MethodBody;
//import xyz.srclab.common.test.asserts.AssertHelper;
//
//public class ProxySample {
//
//    public void showProxy() {
//        ClassProxy<A> classProxy = ClassProxy.newBuilder(A.class)
//                .proxyMethod("someMethod",
//                        ArrayUtils.EMPTY_CLASS_ARRAY,
//                        (MethodBody<String>) (object, method, args, invoker) -> "proxied: " + invoker.invoke(object)
//                )
//                .build();
//        A a = classProxy.newInstance();
//        AssertHelper.printAssert(a.someMethod(), "proxied: someMethod");
//    }
//
//    public static class A {
//
//        public String someMethod() {
//            System.out.println("someMethod");
//            return "someMethod";
//        }
//    }
//}
