package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Invoker;
import xyz.srclab.common.base.InvokerProvider;
import xyz.srclab.common.base.MethodHandlerInvokerProvider;
import xyz.srclab.common.base.ReflectedInvokerProvider;
import xyz.srclab.common.test.TestMarker;

/**
 * @author sunqian
 */
public class InvokerTest {

    @Test
    public void testInvoker() {
        A a1 = Invoker.forConstructor(A.class).invoke(null);
        Assert.assertEquals(
                a1.getMark("A()"),
                "A()"
        );
        A a2 = Invoker.forConstructor(A.class, String.class).forceInvoke(null, "123");
        Assert.assertEquals(
                a2.getMark("A(123)"),
                "A(123)"
        );

        A a = new A();
        Assert.assertEquals(
                Invoker.forMethod(I.class, "i1").invoke(a),
                "i1"
        );
        Assert.assertEquals(
                Invoker.forMethod(I.class, "i2", String.class).invoke(a, "123"),
                "i2: 123"
        );

        Assert.assertEquals(
                Invoker.forMethod(A.class, "i1").invoke(a),
                "i1"
        );
        Assert.assertEquals(
                Invoker.forMethod(A.class, "i2", String.class).invoke(a, "123"),
                "i2: 123"
        );

        Assert.assertEquals(
                Invoker.forMethod(A.class, "a1").invoke(a),
                "a1"
        );
        Assert.assertThrows(IllegalAccessException.class, () ->
                Invoker.forMethod(A.class, "a2").invoke(a));
        Assert.assertEquals(
                Invoker.forMethod(A.class, "a2").forceInvoke(a),
                "a2"
        );
        Assert.assertEquals(
                Invoker.forMethod(A.class, "a3", String.class).invoke(a, "123"),
                "a3: 123"
        );
        Assert.assertThrows(IllegalAccessException.class, () ->
                Invoker.forMethod(A.class, "a4", String.class).invoke(a, "123"));
        Assert.assertEquals(
                Invoker.forMethod(A.class, "a4", String.class).forceInvoke(a, "123"),
                "a4: 123"
        );

        Assert.assertEquals(
                Invoker.forMethod(I.class, "i3").invoke(a),
                "i3"
        );
        Assert.assertEquals(
                Invoker.forMethod(I.class, "i4", String.class).invoke(a, "123"),
                "i4: 123"
        );
        Assert.assertEquals(
                Invoker.forMethod(A.class, "i3").invoke(a),
                "i3"
        );
        Assert.assertEquals(
                Invoker.forMethod(A.class, "i4", String.class).invoke(a, "123"),
                "i4: 123"
        );

        Invoker.forMethod(A.class, "av").invoke(a);
        Assert.assertEquals(
                a.getMark("av"),
                "av"
        );
    }

    @Test
    public void testReflectedInvoker() {
        InvokerProvider ip = ReflectedInvokerProvider.INSTANCE;

        A a1 = ip.forConstructor(A.class).invoke(null);
        Assert.assertEquals(
                a1.getMark("A()"),
                "A()"
        );
        A a2 = ip.forConstructor(A.class, String.class).forceInvoke(null, "123");
        Assert.assertEquals(
                a2.getMark("A(123)"),
                "A(123)"
        );

        A a = new A();
        Assert.assertEquals(
                ip.forMethod(I.class, "i1").invoke(a),
                "i1"
        );
        Assert.assertEquals(
                ip.forMethod(I.class, "i2", String.class).invoke(a, "123"),
                "i2: 123"
        );

        Assert.assertEquals(
                ip.forMethod(A.class, "i1").invoke(a),
                "i1"
        );
        Assert.assertEquals(
                ip.forMethod(A.class, "i2", String.class).invoke(a, "123"),
                "i2: 123"
        );

        Assert.assertEquals(
                ip.forMethod(A.class, "a1").invoke(a),
                "a1"
        );
        Assert.assertThrows(IllegalAccessException.class, () ->
                ip.forMethod(A.class, "a2").invoke(a));
        Assert.assertEquals(
                ip.forMethod(A.class, "a2").forceInvoke(a),
                "a2"
        );
        Assert.assertEquals(
                ip.forMethod(A.class, "a3", String.class).invoke(a, "123"),
                "a3: 123"
        );
        Assert.assertThrows(IllegalAccessException.class, () ->
                ip.forMethod(A.class, "a4", String.class).invoke(a, "123"));
        Assert.assertEquals(
                ip.forMethod(A.class, "a4", String.class).forceInvoke(a, "123"),
                "a4: 123"
        );

        Assert.assertEquals(
                ip.forMethod(I.class, "i3").invoke(a),
                "i3"
        );
        Assert.assertEquals(
                ip.forMethod(I.class, "i4", String.class).invoke(a, "123"),
                "i4: 123"
        );
        Assert.assertEquals(
                ip.forMethod(A.class, "i3").invoke(a),
                "i3"
        );
        Assert.assertEquals(
                ip.forMethod(A.class, "i4", String.class).invoke(a, "123"),
                "i4: 123"
        );

        ip.forMethod(A.class, "av").invoke(a);
        Assert.assertEquals(
                a.getMark("av"),
                "av"
        );
    }

    @Test
    public void testMethodHandlerInvoker() {
        InvokerProvider ip = MethodHandlerInvokerProvider.INSTANCE;

        A a1 = ip.forConstructor(A.class).invoke(null);
        Assert.assertEquals(
                a1.getMark("A()"),
                "A()"
        );
        A a2 = ip.forConstructor(A.class, String.class).forceInvoke(null, "123");
        Assert.assertEquals(
                a2.getMark("A(123)"),
                "A(123)"
        );

        A a = new A();
        Assert.assertEquals(
                ip.forMethod(I.class, "i1").invoke(a),
                "i1"
        );
        Assert.assertEquals(
                ip.forMethod(I.class, "i2", String.class).invoke(a, "123"),
                "i2: 123"
        );

        Assert.assertEquals(
                ip.forMethod(A.class, "i1").invoke(a),
                "i1"
        );
        Assert.assertEquals(
                ip.forMethod(A.class, "i2", String.class).invoke(a, "123"),
                "i2: 123"
        );

        Assert.assertEquals(
                ip.forMethod(A.class, "a1").invoke(a),
                "a1"
        );
        //Assert.assertThrows(IllegalAccessException.class, () ->
        //        ip.forMethod(A.class, "a2").invoke(a));
        Assert.assertEquals(
                ip.forMethod(A.class, "a2").forceInvoke(a),
                "a2"
        );
        Assert.assertEquals(
                ip.forMethod(A.class, "a3", String.class).invoke(a, "123"),
                "a3: 123"
        );
        //Assert.assertThrows(IllegalAccessException.class, () ->
        //        ip.forMethod(A.class, "a4", String.class).invoke(a, "123"));
        Assert.assertEquals(
                ip.forMethod(A.class, "a4", String.class).forceInvoke(a, "123"),
                "a4: 123"
        );

        Assert.assertEquals(
                ip.forMethod(I.class, "i3").invoke(a),
                "i3"
        );
        Assert.assertEquals(
                ip.forMethod(I.class, "i4", String.class).invoke(a, "123"),
                "i4: 123"
        );
        Assert.assertEquals(
                ip.forMethod(A.class, "i3").invoke(a),
                "i3"
        );
        Assert.assertEquals(
                ip.forMethod(A.class, "i4", String.class).invoke(a, "123"),
                "i4: 123"
        );

        ip.forMethod(A.class, "av").invoke(a);
        Assert.assertEquals(
                a.getMark("av"),
                "av"
        );
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

    public static class A implements I, TestMarker {

        public A() {
            this.mark("A()", "A()");
        }

        private A(String a) {
            this.mark("A(" + a + ")", "A(" + a + ")");
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
            this.mark("av", "av");
        }
    }
}
