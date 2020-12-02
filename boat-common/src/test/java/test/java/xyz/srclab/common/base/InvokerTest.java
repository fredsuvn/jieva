package test.java.xyz.srclab.common.base;

import org.testng.annotations.Test;

import java.lang.reflect.Constructor;

/**
 * @author sunqian
 */
public class InvokerTest {

    @Test
    public void testInvoker() throws Exception {
        Constructor<?> constructorPub = A.class.getConstructor();

    }

    private static class A {

        public A() {
            System.out.println("A()");
        }

        private A(String a) {
            System.out.println("A(a)");
        }

        public void aPublic() {
            System.out.println("aPublic");
        }

        private void aPrivate() {
            System.out.println("aPrivate");
        }
    }
}
