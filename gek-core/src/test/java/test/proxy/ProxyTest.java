package test.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.proxy.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ProxyTest {

    // @Test
    // public void testProxyBuilder() {
    //     testProxyBuilder(ProxyBuilder.Engine.CGLIB);
    //     testProxyBuilder(ProxyBuilder.Engine.SPRING);
    //     ProxyBuilder<FooInter> b = TypeProxy.newBuilder().engine(ProxyBuilder.Engine.JDK);
    //     Assert.expectThrows(ProxyException.class, b::build);
    //
    //     ProxyMethod proxyMethod = new ProxyMethod();
    //     b.superInterfaces(FooInter.class);
    //     b.build().newInstance();
    //     b.classLoader(ProxyTest.class.getClassLoader());
    //     b.build().newInstance();
    //     b.proxyMethod("d0", new Class[]{}, proxyMethod);
    //     b.proxyMethod("d1", new Class[]{}, proxyMethod);
    //     b.proxyMethod("d2", new Class[]{}, proxyMethod);
    //     FooInter fi = b.build().newInstance();
    //     Assert.assertEquals(fi.d0(), "d0-proxy");
    //     Assert.assertEquals(fi.d0(), "d0-proxy");
    //     Assert.assertEquals(fi.d1(), "d1-proxy");
    //     Assert.assertEquals(fi.d1(), "d1-proxy");
    // }
    //
    // private void testProxyBuilder(ProxyBuilder.Engine engine) {
    //     ProxyBuilder<FooClass> b1 = TypeProxy.newBuilder().engine(engine);
    //     Assert.expectThrows(ProxyException.class, b1::build);
    //     b1.superClass(FooClass.class);
    //     FooClass f1 = b1.build().newInstance();
    //     Assert.assertEquals(f1.fi0(""), "fi0");
    //     ProxyBuilder<FooInter> b2 = TypeProxy.newBuilder().engine(engine);
    //     b2.superInterfaces(FooInter.class);
    //     FooInter f2 = b2.build().newInstance();
    //     // Assert.assertEquals(f2.fi0(""), "fi0");
    // }
    //
    // @Test
    // public void testProxy() {
    //     testProxy(ProxyBuilder.Engine.JDK);
    //     testProxy(ProxyBuilder.Engine.CGLIB);
    //     testProxy(ProxyBuilder.Engine.SPRING);
    //     testProxy(null);
    // }
    //
    // private void testProxy(@Nullable ProxyBuilder.Engine engine) {
    //     ProxyMethod proxyMethod = new ProxyMethod();
    //     ProxyBuilder.Engine te = engine == null ? ProxyBuilder.Engine.getEngine(null) : engine;
    //     if (te.supportClass()) {
    //         TypeProxy<FooClass> fcp = TypeProxy.newBuilder()
    //             .superClass(FooClass.class)
    //             .superInterfaces(FooInter.class)
    //             .proxyMethod("fi0", new Class[]{String.class}, proxyMethod)
    //             .proxyMethod("fi1", new Class[]{String.class}, proxyMethod)
    //             .proxyMethod("fi2", new Class[]{}, proxyMethod)
    //             .proxyMethod("fc0", new Class[]{String.class}, proxyMethod)
    //             .proxyMethod("fc1", new Class[]{}, proxyMethod)
    //             .engine(engine)
    //             .build();
    //         FooClass fc = fcp.newInstance();
    //         Assert.assertEquals(fc.fi0(""), "fi0-proxy");
    //         Assert.assertEquals(fc.fi1(""), "fi1-proxy");
    //         Assert.assertEquals(fc.fc0(""), "fc0-proxy");
    //         Assert.expectThrows(IllegalStateException.class, fc::fc1);
    //         Assert.expectThrows(IllegalStateException.class, fc::fi2);
    //     } else if (te.supportInterface()) {
    //         TypeProxy<FooInter> fcp = TypeProxy.newBuilder()
    //             .superInterfaces(FooInter.class)
    //             .proxyMethod("fi0", new Class[]{String.class}, proxyMethod)
    //             .proxyMethod("fi1", new Class[]{String.class}, proxyMethod)
    //             .proxyMethod("fi2", new Class[]{}, proxyMethod)
    //             .engine(engine)
    //             .build();
    //         FooInter fi = fcp.newInstance();
    //         Assert.assertEquals(fi.fi0(""), "fi0-proxy");
    //         Assert.expectThrows(ProxyException.class, () -> fi.fi1(""));
    //         Assert.expectThrows(IllegalStateException.class, fi::fi2);
    //     }
    // }

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

    // public static class ProxyMethod implements ProxyInvoker {
    //
    //     @Override
    //     public @Nullable Object invoke(
    //         @Nullable Object inst, Method proxiedMethod, ProxiedInvoker proxiedInvoker, Object... args) throws Throwable {
    //         Object result = proxiedInvoker.invoke(inst, args);
    //         return result + "-proxy";
    //     }
    // }


    @Test
    public void testAsmProxyProvider() throws Exception {
        Class1 inst = new Class1();
        TestHandler testHandler = new TestHandler(inst);
        AsmProxyProvider proxyProvider = new AsmProxyProvider();
        Object proxy = proxyProvider.newProxyInstance(null, Arrays.asList(Class1.class, Inter1.class, Inter2.class), testHandler);
        testClass1((Class1) proxy);
        testInter1((Inter1) proxy);
        testInter2((Inter2) proxy);
    }

    @Test
    public void testJdkProxyProvider() throws Exception {
        Inter12 inst = new Inter12();
        TestHandler testHandler = new TestHandler(inst);
        JdkProxyProvider proxyProvider = new JdkProxyProvider();
        Object proxy = proxyProvider.newProxyInstance(null, Arrays.asList(Inter1.class, Inter2.class), testHandler);
        testInter1((Inter1) proxy);
        testInter2((Inter2) proxy);
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
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4'), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8), TestHandler.superStack.get(0) + "-proxy");
        Assert.assertEquals(proxy.ppc_String(true, (byte) 2, (short) 3, '4', 5, 6, 7, 8, "9"), TestHandler.superStack.get(0) + "-proxy");
        proxy.ppc_void();
        Assert.assertEquals("ppc_void", TestHandler.superStack.get(0));
        Assert.assertEquals("ppc_void", TestHandler.superStack.get(1));
    }

    private void testInter1(Inter1 inter1) {
        Assert.expectThrows(AbstractMethodError.class, inter1::ppi1_boolean);
        Assert.expectThrows(AbstractMethodError.class, () -> inter1.ppi1_double(1, "a"));
        Assert.assertEquals(inter1.ppi1_String(1, 1, "s"), TestHandler.superStack.get(0) + "-proxy");
    }

    private void testInter2(Inter2 inter2) {
        Assert.expectThrows(AbstractMethodError.class, inter2::ppi2_boolean);
        Assert.expectThrows(AbstractMethodError.class, () -> inter2.ppi2_double(1, "a"));
        Assert.assertEquals(inter2.ppi2_String(1, 1, "s"), TestHandler.superStack.get(0) + "-proxy");
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
            return result2;
        }
    }

    public interface Inter1 {

        boolean ppi1_boolean();

        double ppi1_double(int i, String a);

        default String ppi1_String(int i, double d, String s) {
            return i + d + s;
        }
    }

    public interface Inter2 {

        boolean ppi2_boolean();

        double ppi2_double(int i, String a);

        default String ppi2_String(int i, double d, String s) {
            return i + d + s;
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

    public static class Inter12 implements Inter1, Inter2 {

        @Override
        public boolean ppi1_boolean() {
            return false;
        }

        @Override
        public double ppi1_double(int i, String a) {
            return 66;
        }

        @Override
        public boolean ppi2_boolean() {
            return false;
        }

        @Override
        public double ppi2_double(int i, String a) {
            return 88;
        }
    }

    public static class Class11 extends Class1 implements Inter1, Inter2 {

        private final MethodProxyHandler handler;
        private final Method[] methods;
        private final ProxyInvoker[] invokers;

        public Class11() {
            this.handler = null;
            this.methods = null;
            this.invokers = null;
        }

        public Class11(MethodProxyHandler handler, Method[] methods) {
            this.handler = handler;
            this.methods = methods;
            this.invokers = new ProxyInvoker[methods.length];
            for (int i = 0; i < invokers.length; i++) {
                invokers[i] = new InvokerImpl(i);
            }
        }

        // public Object callSuper(Object inst, int i, Object[] args) {
        //     switch (i) {
        //         case 0:
        //             super.ppc_boolean();
        //     }
        // }

        @Override
        public boolean ppi1_boolean() {
            try {
                return (Boolean) handler.invoke(this, methods[0], new Object[]{}, invokers[0]);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public double ppi1_double(int i, String a) {
            return 0;
        }

        @Override
        public boolean ppi2_boolean() {
            return false;
        }

        @Override
        public double ppi2_double(int i, String a) {
            return 0;
        }

        private class InvokerImpl implements ProxyInvoker {

            private final int i;

            private InvokerImpl(int i) {
                this.i = i;
            }

            @Override
            public @Nullable Object invoke(Object inst, Object[] args) throws Throwable {
                return null;
            }

            @Override
            public @Nullable Object invokeSuper(Object[] args) throws Throwable {
                return null;
            }
        }
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
                return null;//(String) invoker.invoke(this, method, proxiedInvoker, args);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }

        public Object callSuper(int i, Object inst, Object[] args) {
            Class1 c1 = (Class1) inst;
            c1.ppc_boolean();
            switch (i) {
                case 0:
                    return super.ppc_boolean();
                case 1:
                    return super.ppc_String((Boolean) args[0], (Byte) args[1], (Short) args[2], (Character) args[3], (Integer) args[4], (Long) args[5]);
            }
            return super.ppc_boolean();
        }
    }


    @Test
    public void testCglib() {
        UserServiceProxy.main();
    }

    public static class UserService {
        public String getUser() {
            return "User";
        }
    }

    public static class UserServiceProxy implements MethodInterceptor {

        private Object target;

        public UserServiceProxy(Object target) {
            this.target = target;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("Before method call: " + method.getName());
            // 调用原始对象的方法
            Object result = proxy.invoke(target, args);
            System.out.println(proxy.invokeSuper(obj, args));
            System.out.println("After method call: " + method.getName());
            return result;
        }

        public static void main() {
            // 创建 CGLIB 的 Enhancer 实例
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(UserService.class);
            enhancer.setCallback(new UserServiceProxy(new UserService()));

            // 创建代理对象
            UserService proxy = (UserService) enhancer.create();

            // 使用代理对象
            System.out.println(proxy.getUser());
        }
    }
}
