package test.java.xyz.srclab.common.invoke;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.BList;
import xyz.srclab.common.invoke.InvokeProvider;
import xyz.srclab.common.reflect.BConstructor;
import xyz.srclab.common.reflect.BMethod;

import java.util.LinkedList;
import java.util.List;

/**
 * @author sunqian
 */
public class FuncTest {

    @Test
    public void testInvoke() throws Exception {
        testInvokerGenerator(InvokeProvider.defaultProvider());
    }

    @Test
    public void testReflected() throws Exception {
        testInvokerGenerator(InvokeProvider.withReflected());
    }

    @Test
    public void testUnreflected() throws Exception {
        testInvokerGenerator(InvokeProvider.withUnreflected());
    }

    private synchronized void testInvokerGenerator(InvokeProvider factory) throws Exception {
        A.stack.clear();

        A a1 = factory.getStaticInvoke(BConstructor.getConstructor(A.class)).invokeTyped();
        Assert.assertNotNull(a1);
        Assert.assertEquals(
            A.stack.get(0),
            "A()"
        );
        A a2 = factory.getStaticInvoke(BConstructor.getDeclaredConstructor(A.class, String.class), true).invokeTyped("123");
        Assert.assertNotNull(a2);
        Assert.assertEquals(
            A.stack,
            BList.newList("A()", "A(123)")
        );

        A a = new A();
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(I.class, "i1")).invoke(a),
            "i1"
        );
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(I.class, "i2", String.class)).invoke(a, "123"),
            "i2: 123"
        );

        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(A.class, "i1")).invoke(a),
            "i1"
        );
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(A.class, "i2", String.class)).invoke(a, "123"),
            "i2: 123"
        );

        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(A.class, "a1")).invoke(a),
            "a1"
        );
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getDeclaredMethod(A.class, "a2"), true).invoke(a),
            "a2"
        );
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(A.class, "a3", String.class)).invoke(a, "123"),
            "a3: 123"
        );
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getDeclaredMethod(A.class, "a4", String.class), true).invoke(a, "123"),
            "a4: 123"
        );

        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(I.class, "i3")).invoke(a),
            "i3"
        );
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(I.class, "i4", String.class)).invoke(a, "123"),
            "i4: 123"
        );
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(A.class, "i3")).invoke(a),
            "i3"
        );
        Assert.assertEquals(
            factory.getInstInvoke(BMethod.getMethod(A.class, "i4", String.class)).invoke(a, "123"),
            "i4: 123"
        );

        factory.getInstInvoke(BMethod.getMethod(A.class, "av")).invoke(a);
        Assert.assertEquals(
            A.stack,
            BList.newList("A()", "A(123)", "A()", "av")
        );

        if (factory != InvokeProvider.withUnreflected()) {
            Assert.assertThrows(IllegalAccessException.class, () ->
                factory.getInstInvoke(BMethod.getDeclaredMethod(A.class, "a2")).invoke(a));
            Assert.assertThrows(IllegalAccessException.class, () ->
                factory.getInstInvoke(BMethod.getDeclaredMethod(A.class, "a4", String.class)).invoke(a, "123"));
        }
    }

    public interface I {

        default String i1() {
            return "i1";
        }

        default String i2(String a) {
            return "i2: " + a;
        }

        String i3();

        String i4(String a);
    }

    public static class A implements I {

        static final List<String> stack = new LinkedList<>();

        public A() {
            stack.add("A()");
        }

        private A(String a) {
            stack.add("A(" + a + ")");
        }

        public String a1() {
            return "a1";
        }

        private String a2() {
            return "a2";
        }

        public String a3(String a) {
            return "a3: " + a;
        }

        private String a4(String a) {
            return "a4: " + a;
        }

        @Override
        public String i3() {
            return "i3";
        }

        @Override
        public String i4(String a) {
            return "i4: " + a;
        }

        public void av() {
            stack.add("av");
        }
    }
}
