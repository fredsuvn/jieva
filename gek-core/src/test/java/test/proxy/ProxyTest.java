package test.proxy;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.proxy.AsmProxyProvider;
import xyz.fslabo.common.proxy.JdkProxyProvider;
import xyz.fslabo.common.proxy.MethodProxyHandler;
import xyz.fslabo.common.proxy.ProxyInvoker;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ProxyTest {

    @Test
    public void testAsmProxyProvider() throws Exception {
        Class1 inst = new Class1();
        TestHandler testHandler = new TestHandler(inst);
        AsmProxyProvider proxyProvider = new AsmProxyProvider();
        Object proxy = proxyProvider.newProxyInstance(null, Arrays.asList(Class1.class, Inter1.class, Inter2.class), testHandler);
        testClass1((Class1) proxy);
        testInter1((Inter1<?>) proxy);
        testInter2((Inter2<?>) proxy);
        Inter1<?> i1 = (Inter1<?>) proxy;
        Assert.assertEquals(i1.ppi_String(), "Inter1-proxy");
        Assert.assertEquals(i1.ppi_String(), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(i1.ppi1t_String(), "Inter1_t-proxy");
        Assert.assertEquals(i1.ppi_String(), TestHandler.superStack.get(0) + "-proxy");
        Inter2<?> i2 = (Inter2<?>) proxy;
        Assert.assertEquals(i2.ppi_String(), "Inter1-proxy");
        Assert.assertEquals(i2.ppi_String(), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(i2.ppi2t_Integer(), 8);
        Assert.assertEquals(i2.ppi2t_Integer(), (Integer) TestHandler.superStack.get(0) + 1);
    }

    @Test
    public void testJdkProxyProvider() throws Exception {
        Inter12 inst = new Inter12();
        TestHandler testHandler = new TestHandler(inst);
        JdkProxyProvider proxyProvider = new JdkProxyProvider();
        Object proxy = proxyProvider.newProxyInstance(null, Arrays.asList(Inter1.class, Inter2.class), testHandler);
        Inter1<?> i1 = (Inter1<?>) proxy;
        Assert.expectThrows(AbstractMethodError.class, i1::ppi1_boolean);
        Assert.expectThrows(AbstractMethodError.class, i1::ppi_String);
        Inter2<?> i2 = (Inter2<?>) proxy;
        Assert.expectThrows(AbstractMethodError.class, i2::ppi2_boolean);
        Assert.expectThrows(AbstractMethodError.class, i2::ppi_String);
        MethodProxyHandler handler = new MethodProxyHandler() {

            @Override
            public boolean proxy(Method method) {
                return method.getName().startsWith("pp");
            }

            @Override
            public @Nullable Object invoke(Object proxy, Method method, Object[] args, ProxyInvoker invoker) throws Throwable {
                if (method.getName().endsWith("_boolean")) {
                    return true;
                }
                if (method.getName().endsWith("_double")) {
                    return args[0];
                }
                if (method.getName().endsWith("_String")) {
                    return "proxy";
                }
                if (method.getName().endsWith("_Integer")) {
                    return 6;
                }
                return null;
            }
        };
        Object proxy2 = proxyProvider.newProxyInstance(getClass().getClassLoader(), Arrays.asList(Inter1.class, Inter2.class), handler);
        Inter1<?> i11 = (Inter1<?>) proxy2;
        Assert.assertEquals(i11.ppi1_boolean(), true);
        Assert.assertEquals(i11.ppi1_double(6.6, "a"), 6.6);
        Assert.assertEquals(i11.ppi_String(), "proxy");
        Inter2<?> i22 = (Inter2<?>) proxy2;
        Assert.assertEquals(i22.ppi2_boolean(), true);
        Assert.assertEquals(i22.ppi2_double(7.7, "a"), 7.7);
        Assert.assertEquals(i22.ppi_String(), "proxy");
    }

    private void testClass1(Class1 proxy) {
        Assert.assertEquals(proxy.ppc_boolean(), TestHandler.superStack.get(0));
        Assert.assertEquals(proxy.ppc_byte(), TestHandler.superStack.get(0));
        Assert.assertEquals(proxy.ppc_short(), TestHandler.superStack.get(0));
        Assert.assertEquals(proxy.ppc_char(), TestHandler.superStack.get(0));
        Assert.assertEquals(proxy.ppc_int(), TestHandler.superStack.get(0));
        Assert.assertEquals(proxy.ppc_long(), TestHandler.superStack.get(0));
        Assert.assertEquals(proxy.ppc_float(), TestHandler.superStack.get(0));
        Assert.assertEquals(proxy.ppc_double(), TestHandler.superStack.get(0));
        Assert.assertEquals(proxy.ppc_String(), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true), true + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2), "" + true + 2 + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3), "" + true + 2 + 3 + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4'), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4'), "" + true + 2 + 3 + '4' + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5), "" + true + 2 + 3 + '4' + 5 + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6), "" + true + 2 + 3 + '4' + 5 + 6 + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7), "" + true + 2 + 3 + '4' + 5 + 6 + 7.0 + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8), "" + true + 2 + 3 + '4' + 5 + 6 + 7.0 + 8.0 + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8, "9"), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8, "9"), "" + true + 2 + 3 + '4' + 5 + 6 + 7.0 + 8.0 + "9" + "-proxy");
        proxy.ppc_void();
        Assert.assertEquals("ppc_void", TestHandler.superStack.get(0));
        Assert.assertEquals("ppc_void", TestHandler.superStack.get(1));
    }

    private void testInter1(Inter1<?> inter1) {
        Assert.expectThrows(AbstractMethodError.class, inter1::ppi1_boolean);
        Assert.expectThrows(AbstractMethodError.class, () -> inter1.ppi1_double(1, "a"));
        Assert.assertEquals(inter1.ppi1_String(1, 1, "s"), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(inter1.ppi1_String(1, 1, "s"), 1 + 1.0 + "s" + "-proxy");
    }

    private void testInter2(Inter2<?> inter2) {
        Assert.expectThrows(AbstractMethodError.class, inter2::ppi2_boolean);
        Assert.expectThrows(AbstractMethodError.class, () -> inter2.ppi2_double(1, "a"));
        Assert.assertEquals(inter2.ppi2_String(1, 1, "s"), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(inter2.ppi2_String(1, 1, "s"), 1 + 1.0 + "s" + "-proxy");
    }

    public static class TestHandler implements MethodProxyHandler {

        public static final List<Object> superStack = new LinkedList<>();

        private final Object inst;

        public TestHandler(Object inst) {
            this.inst = inst;
        }

        @Override
        public boolean proxy(Method method) {
            return method.getName().startsWith("pp");
        }

        @Override
        public @Nullable Object invoke(Object proxy, Method method, Object[] args, ProxyInvoker invoker) throws Throwable {
            superStack.clear();
            if (Objects.equals(method.getReturnType(), void.class)) {
                invoker.invoke(inst, args);
                invoker.invokeSuper(args);
                return null;
            }
            Object result1 = invoker.invoke(inst, args);
            Object result2 = invoker.invokeSuper(args);
            Assert.assertEquals(result1, result2);
            superStack.add(result2);
            if (method.getName().endsWith("_String")) {
                return result2 + "-proxy";
            }
            if (method.getName().endsWith("_Integer")) {
                return (Integer) result2 + 1;
            }
            return result2;
        }
    }

    public interface Inter1<T> {

        boolean ppi1_boolean();

        double ppi1_double(double d, String a);

        default String ppi1_String(int i, double d, String s) {
            return i + d + s;
        }

        default String ppi_String() {
            return "Inter1";
        }

        default T ppi1t_String() {
            return (T) "Inter1_t";
        }
    }

    public interface Inter2<T> {

        boolean ppi2_boolean();

        double ppi2_double(double d, String a);

        default String ppi2_String(int i, double d, String s) {
            return i + d + s;
        }

        default String ppi_String() {
            return "Inter2";
        }

        default T ppi2t_Integer() {
            return (T) (new Integer(7));
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
            TestHandler.superStack.add("ppc_void");
        }

        public String ppc_String() {
            return "1";
        }

        public String ppc_String(boolean a) {
            return "" + a;
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

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g) {
            return "" + a + b + c + d + e + f + g;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g, double h) {
            return "" + a + b + c + d + e + f + g + h;
        }

        public String ppc_String(boolean a, byte b, short c, char d, int e, long f, float g, double h, String i) {
            return "" + a + b + c + d + e + f + g + h + i;
        }
    }

    public static class Inter12 implements Inter1<String>, Inter2<Integer> {

        @Override
        public boolean ppi1_boolean() {
            return false;
        }

        @Override
        public double ppi1_double(double d, String a) {
            return 66;
        }

        @Override
        public String ppi_String() {
            return "Inter12";
        }

        @Override
        public Integer ppi2t_Integer() {
            return 6;
        }

        @Override
        public String ppi1t_String() {
            return "Inter12_t";
        }

        @Override
        public boolean ppi2_boolean() {
            return false;
        }

        @Override
        public double ppi2_double(double d, String a) {
            return 88;
        }
    }
}
