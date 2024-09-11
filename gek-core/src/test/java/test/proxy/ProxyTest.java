package test.proxy;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.proxy.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ProxyTest {

    @Test
    public void testProxyBuilder() {
        testProxyBuilder(ProxyBuilder.Engine.CGLIB);
        testProxyBuilder(ProxyBuilder.Engine.SPRING);
        ProxyBuilder<FooInter> b = TypeProxy.newBuilder().engine(ProxyBuilder.Engine.JDK);
        Assert.expectThrows(ProxyException.class, b::build);

        ProxyMethod proxyMethod = new ProxyMethod();
        b.superInterfaces(FooInter.class);
        b.build().newInstance();
        b.classLoader(ProxyTest.class.getClassLoader());
        b.build().newInstance();
        b.proxyMethod("d0", new Class[]{}, proxyMethod);
        b.proxyMethod("d1", new Class[]{}, proxyMethod);
        b.proxyMethod("d2", new Class[]{}, proxyMethod);
        FooInter fi = b.build().newInstance();
        Assert.assertEquals(fi.d0(), "d0-proxy");
        Assert.assertEquals(fi.d0(), "d0-proxy");
        Assert.assertEquals(fi.d1(), "d1-proxy");
        Assert.assertEquals(fi.d1(), "d1-proxy");
    }

    private void testProxyBuilder(ProxyBuilder.Engine engine) {
        ProxyBuilder<FooClass> b1 = TypeProxy.newBuilder().engine(engine);
        Assert.expectThrows(ProxyException.class, b1::build);
        b1.superClass(FooClass.class);
        FooClass f1 = b1.build().newInstance();
        Assert.assertEquals(f1.fi0(""), "fi0");
        ProxyBuilder<FooInter> b2 = TypeProxy.newBuilder().engine(engine);
        b2.superInterfaces(FooInter.class);
        FooInter f2 = b2.build().newInstance();
        // Assert.assertEquals(f2.fi0(""), "fi0");
    }

    @Test
    public void testProxy() {
        testProxy(ProxyBuilder.Engine.JDK);
        testProxy(ProxyBuilder.Engine.CGLIB);
        testProxy(ProxyBuilder.Engine.SPRING);
        testProxy(null);
    }

    private void testProxy(@Nullable ProxyBuilder.Engine engine) {
        ProxyMethod proxyMethod = new ProxyMethod();
        ProxyBuilder.Engine te = engine == null ? ProxyBuilder.Engine.getEngine(null) : engine;
        if (te.supportClass()) {
            TypeProxy<FooClass> fcp = TypeProxy.newBuilder()
                .superClass(FooClass.class)
                .superInterfaces(FooInter.class)
                .proxyMethod("fi0", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fi1", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fi2", new Class[]{}, proxyMethod)
                .proxyMethod("fc0", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fc1", new Class[]{}, proxyMethod)
                .engine(engine)
                .build();
            FooClass fc = fcp.newInstance();
            Assert.assertEquals(fc.fi0(""), "fi0-proxy");
            Assert.assertEquals(fc.fi1(""), "fi1-proxy");
            Assert.assertEquals(fc.fc0(""), "fc0-proxy");
            Assert.expectThrows(IllegalStateException.class, fc::fc1);
            Assert.expectThrows(IllegalStateException.class, fc::fi2);
        } else if (te.supportInterface()) {
            TypeProxy<FooInter> fcp = TypeProxy.newBuilder()
                .superInterfaces(FooInter.class)
                .proxyMethod("fi0", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fi1", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fi2", new Class[]{}, proxyMethod)
                .engine(engine)
                .build();
            FooInter fi = fcp.newInstance();
            Assert.assertEquals(fi.fi0(""), "fi0-proxy");
            Assert.expectThrows(ProxyException.class, () -> fi.fi1(""));
            Assert.expectThrows(IllegalStateException.class, fi::fi2);
        }
    }

    public interface FooInter<T, L extends List<? extends String>, M extends Number> {

        default String fi0(String input) {
            return "fi0";
        }

        String fi1(String input);

        default void fi2() {
            throw new IllegalStateException();
        }

        default String d0() {
            return "d0";
        }

        default String d1() {
            return "d1";
        }

        default String d2() {
            return "d2";
        }

        default String doDefault(T t, L l, M m) {
            return "default-" + t + l + m;
        }
    }

    public static class FooClass implements FooInter<String, List<String>, Long> {

        public String fc0(String input) {
            return "fc0";
        }

        public void fc1() {
            throw new IllegalStateException();
        }

        @Override
        public String fi1(String input) {
            return "fi1";
        }

        @Override
        public String doDefault(String t, List<String> l, Long m) {
            return "foo-" + t + l + m;
        }
    }

    public static class ProxyMethod implements ProxyInvoker {

        @Override
        public @Nullable Object invoke(
            @Nullable Object inst, Method proxiedMethod, ProxiedInvoker proxiedInvoker, Object... args) throws Throwable {
            Object result = proxiedInvoker.invoke(inst, args);
            return result + "-proxy";
        }
    }


    @Test
    public void testProvider() {
        testProxyInst(new AsmProxyProvider(), true);
    }

    private void testProxyInst(ProxyProvider provider, boolean supportProxyClass) {
        Object inst = provider.newProxyInstance(
            getClass().getClassLoader(),
            Arrays.asList(Class1.class, Inter1.class, Inter2.class),
            method -> method.getName().startsWith("pp") ? TestProxyInvoker.SINGLETON : null
        );
        if (supportProxyClass) {
            testClass1((Class1) inst);
        }
        testInter1((Inter1) inst);
        testInter2((Inter2) inst);
    }

    private void testClass1(Class1 class1) {
        Assert.assertEquals(class1.ppc_boolean(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_byte(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_short(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_char(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_int(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_long(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_float(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_double(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_String(), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_String(true), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_String(true, (byte) 2), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_String(true, (byte) 2, (short) 3), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_String(true, (byte) 2, (short) 3, '4'), TestProxyInvoker.stack.get(0));
        Assert.assertEquals(class1.ppc_String(true, (byte) 2, (short) 3, '4', 5), TestProxyInvoker.stack.get(0));
        // Assert.assertEquals(class1.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6), TestProxyInvoker.stack.get(0));
        // Assert.assertEquals(class1.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7), TestProxyInvoker.stack.get(0));
        // Assert.assertEquals(class1.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8), TestProxyInvoker.stack.get(0));
        // Assert.assertEquals(class1.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8, "9"), TestProxyInvoker.stack.get(0));
        class1.ppc_void();
        Assert.assertEquals("ppc_void", TestProxyInvoker.stack.get(0));
        Assert.assertEquals(null, TestProxyInvoker.stack.get(1));
        System.out.println(Class2.class);
    }

    private void testInter1(Inter1 inter1) {
        // System.out.println(inter1.ppi10());
        // System.out.println(inter1.ppi11(1, ""));
        System.out.println(inter1.ppi12(1, "b"));
    }

    private void testInter2(Inter2 inter2) {
        // System.out.println(inter2.ppi20());
        // System.out.println(inter2.ppi21(1, ""));
        System.out.println(inter2.ppi22(1, "b"));
    }


    public static class TestProxyInvoker implements ProxyInvoker {

        public static final TestProxyInvoker SINGLETON = new TestProxyInvoker();

        public static final List<Object> stack = new LinkedList<>();

        @Override
        public @Nullable Object invoke(
            @Nullable Object inst, Method proxiedMethod, ProxiedInvoker proxiedInvoker, Object... args) throws Throwable {
            stack.clear();
            Object result = proxiedInvoker.invoke(inst, args);
            stack.add(result);
            return result;
        }
    }


    public interface Inter1 {

        int ppi10();

        int ppi11(int i, String a);

        default String ppi12(int i, String a) {
            return a + 1;
        }
    }

    public interface Inter2 {

        int ppi20();

        int ppi21(int i, String a);

        default String ppi22(int i, String a) {
            return a + 1;
        }
    }

    public static class Class1 {

        public boolean ppc_boolean() {
            return true;
        }

        public byte ppc_byte() {
            return 1;
        }

        public short ppc_short() {
            return 1;
        }

        public char ppc_char() {
            return 1;
        }

        public int ppc_int() {
            return 1;
        }

        public long ppc_long() {
            return 1;
        }

        public float ppc_float() {
            return 1;
        }

        public double ppc_double() {
            return 1;
        }

        public void ppc_void() {
            TestProxyInvoker.stack.add("ppc_void");
        }

        public String ppc_String() {
            return "1";
        }

        public String ppc_String(boolean a) {
            return "1" + a;
        }

        public String ppc_String(boolean a, byte b) {
            return "" + a + b;
        }

        public String ppc_String(boolean a, byte b, short c) {
            return "" + a + b + c;
        }

        public String ppc_String(boolean a, byte b, short c, char d) {
            return "" + a + b + c + d;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e) {
            return "" + a + b + c + d + e;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f) {
            return "" + a + b + c + d + e + f;
        }

        // public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g) {
        //     return "" + a + b + c + d + e + f + g;
        // }
        //
        // public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g, double h) {
        //     return "" + a + b + c + d + e + f + g + h;
        // }
        //
        // public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g, double h, String i) {
        //     return "" + a + b + c + d + e + f + g + h + i;
        // }
    }

    public static class Class2 extends Class1 {

        private final ProxyInvoker[] invokers;
        private final Method[] methods;
        private final ProxiedInvoker[] proxiedInvokers;

        public Class2(ProxyInvoker[] invokers, Method[] methods, ProxiedInvoker[] proxiedInvokers) {
            this.invokers = invokers;
            this.methods = methods;
            this.proxiedInvokers = proxiedInvokers;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f) {
            ProxyInvoker invoker = invokers[0];
            Method method = methods[0];
            ProxiedInvoker proxiedInvoker = proxiedInvokers[0];
            Object[] args = new Object[6];
            args[0] = a;
            args[1] = b;
            args[2] = c;
            args[3] = d;
            args[4] = e;
            args[5] = f;
            try {
                return (String) invoker.invoke(this, method, proxiedInvoker, args);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }

        public Object callSuper(int i, Object inst, Object[] args) {
            switch (i) {
                case 0:
                    return super.ppc_boolean();
                case 1:
                    return null;//super.ppc_String((Boolean) args[0], (Byte) args[1], (Short) args[2], (Character) args[3], (Integer) args[4], (Long) args[5]);
            }
            return super.ppc_boolean();
        }
    }
}
