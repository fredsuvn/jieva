package test.java.xyz.srclab.common.invoke;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.invoke.*;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sunqian
 */
public class InvokerTest {

    @Test
    public void testReflectedInvoker() {
        testInvokerGenerator(ReflectInvokerGenerator.INSTANCE);
    }

    @Test
    public void testMethodHandlerInvoker() {
        testInvokerGenerator(MethodHandlerInvokerGenerator.INSTANCE);
    }

    @Test
    public void testInvoke() throws Exception {
        Method method = A.class.getMethod("a1");
        Invoke invoke = Invoker.ofMethod(method).prepareFor(new A());
        Assert.assertEquals(invoke.start(), "a1");
    }

    private void testInvokerGenerator(InvokerGenerator generator) {
        A a1 = generator.ofConstructor(A.class).invoke(null);
        Assert.assertEquals(
            A.stack.get(0),
            "A()"
        );
        A a2 = generator.ofConstructor(A.class, String.class).enforce(null, "123");
        Assert.assertEquals(
            A.stack,
            Collects.newList("A()", "A(123)")
        );

        A a = new A();
        Assert.assertEquals(
            generator.ofMethod(I.class, "i1").invoke(a),
            "i1"
        );
        Assert.assertEquals(
            generator.ofMethod(I.class, "i2", String.class).invoke(a, "123"),
            "i2: 123"
        );

        Assert.assertEquals(
            generator.ofMethod(A.class, "i1").invoke(a),
            "i1"
        );
        Assert.assertEquals(
            generator.ofMethod(A.class, "i2", String.class).invoke(a, "123"),
            "i2: 123"
        );

        Assert.assertEquals(
            generator.ofMethod(A.class, "a1").invoke(a),
            "a1"
        );
        Assert.assertThrows(IllegalAccessException.class, () ->
            generator.forMethod(A.class, "a2").invoke(a));
        Assert.assertEquals(
            generator.ofMethod(A.class, "a2").enforce(a),
            "a2"
        );
        Assert.assertEquals(
            generator.ofMethod(A.class, "a3", String.class).invoke(a, "123"),
            "a3: 123"
        );
        Assert.assertThrows(IllegalAccessException.class, () ->
            generator.forMethod(A.class, "a4", String.class).invoke(a, "123"));
        Assert.assertEquals(
            generator.ofMethod(A.class, "a4", String.class).enforce(a, "123"),
            "a4: 123"
        );

        Assert.assertEquals(
            generator.ofMethod(I.class, "i3").invoke(a),
            "i3"
        );
        Assert.assertEquals(
            generator.ofMethod(I.class, "i4", String.class).invoke(a, "123"),
            "i4: 123"
        );
        Assert.assertEquals(
            generator.ofMethod(A.class, "i3").invoke(a),
            "i3"
        );
        Assert.assertEquals(
            generator.ofMethod(A.class, "i4", String.class).invoke(a, "123"),
            "i4: 123"
        );

        generator.ofMethod(A.class, "av").invoke(a);
        Assert.assertEquals(
            A.stack,
            Collects.newList("A()", "A(123)", "av")
        );
        A.stack.clear();
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
