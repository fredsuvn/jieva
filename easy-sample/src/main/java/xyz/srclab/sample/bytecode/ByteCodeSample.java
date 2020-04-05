package xyz.srclab.sample.bytecode;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.common.bean.BeanClass;
import xyz.srclab.common.bean.BeanHelper;
import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.proxy.ProxyClass;
import xyz.srclab.common.reflect.method.MethodBody;
import xyz.srclab.common.test.asserts.AssertHelper;

public class ByteCodeSample {

    public void showProxy() {
        ProxyClass<A> proxyClass = ProxyClass.newBuilder(A.class)
                .overrideMethod("someMethod",
                        ArrayUtils.EMPTY_CLASS_ARRAY,
                        (MethodBody<String>) (object, method, args, invoker) -> "proxied: " + invoker.invoke(object)
                )
                .build();
        A a = proxyClass.newInstance();
        AssertHelper.printAssert(a.someMethod(), "proxied: someMethod");
    }

    public void showBean() throws Exception {
        BeanClass<A> beanClass = BeanClass.newBuilder(A.class)
                .addProperty("hello", String.class)
                .build();
        A a = beanClass.newInstance();
        BeanClass beanDescriptor = BeanHelper.resolve(a);
        beanDescriptor.getProperty("hello").setValue(a, "world");
        Object value = a.getClass().getMethod("getHello").invoke(a);
        AssertHelper.printAssert(value, "world");
    }

    public static class A {

        public String someMethod() {
            System.out.println("someMethod");
            return "someMethod";
        }
    }
}
