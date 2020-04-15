package xyz.srclab.sample.bytecode;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.common.bean.BeanHelper;
import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.enhance.EnhancedClass;
import xyz.srclab.common.reflect.invoke.MethodInvoker;
import xyz.srclab.common.reflect.method.ProxyMethod;

import java.lang.reflect.Method;

public class ByteCodeSample {

    public static void main(String[] args) {
        BeanClass<A> beanClass = BeanClass.newBuilder(A.class)
                .addProperty("b", String.class)
                .build();
        A a1 = beanClass.newInstance();
        BeanHelper.setPropertyValue(a1, "b", "bbb");
        System.out.println(BeanHelper.getPropertyValue(a1, "b"));

        EnhancedClass<A> enhancedClass = EnhancedClass.newBuilder(A.class)
                .overrideMethod("someMethod", ArrayUtils.EMPTY_CLASS_ARRAY, new ProxyMethod() {
                    @Override
                    public Object invoke(Object o, Object[] objects, Method method, MethodInvoker methodInvoker) {
                        return "proxy: " + methodInvoker.invoke(o, objects);
                    }
                })
                .build();
        A a2 = enhancedClass.newInstance();
        System.out.println(a2.someMethod());
    }

    public static class A {

        private String a;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String someMethod() {
            System.out.println("someMethod");
            return "someMethod";
        }
    }
}
