package test.proxy;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.proxy.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
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
    public void ss() throws Exception {
        AsmTypeProxy asmTypeProxy = new AsmTypeProxy();
        XX xx = asmTypeProxy.newProxyInstance(null, Arrays.asList(XX.class), m -> {
            if (!m.getName().startsWith("s")) {
                return null;
            }
            return new ProxyInvoker() {
                @Override
                public @Nullable Object invoke(@Nullable Object inst, Method proxiedMethod, ProxiedInvoker proxiedInvoker, Object... args) throws Throwable {
                    return "proxy-" + Arrays.toString(args);
                }
            };
        });
        System.out.println(xx.getClass());
        System.out.println(xx.sss());
        System.out.println(xx.sss2("q", "w"));
        // System.out.println(xx.ssss(false));
    }

    public static class XX {

        private final String s = "ss";

        public XX() {
        }

        public String sss() {
            return "sss";
        }

        public String sss2(String a, String b) {
            Object obj = "sss2";
            return (String) obj;
        }

        public Boolean ssss(boolean b) {
            return b;
        }

        public boolean ssss2(Boolean b) {
            return b;
        }

        public boolean ssss3(boolean b) {
            return b;
        }
    }

    public static interface A<T> {

        default T tt(T t) {
            return t;
        }
    }

    public static class B extends Number implements A<String>, Serializable {

        private ProxyInvoker[] invokers;
        private Method[] methods;

        public B() throws NoSuchMethodException {

        }

        public B(ProxyInvoker[] invokers, Method[] methods) throws NoSuchMethodException {
            this.invokers = invokers;
            this.methods = methods;
        }

        @Override
        public int intValue() {
            return 0;
        }

        @Override
        public long longValue() {
            return 0;
        }

        @Override
        public float floatValue() {
            return 0;
        }

        @Override
        public double doubleValue() {
            return 0;
        }

        public Object getTt(Object a, Object b) throws Throwable {
            ProxyInvoker invoker = invokers[6];
            Method method = methods[8];
            Object[] args = new Object[]{a, b};
            return invoker.invoke(this, method, null, args);
        }
    }

    public static class FooClass2 {

    }

    public static class FooChild extends FooClass2 implements FooInter<String, List<String>, Long> {

        private String str;

        @Override
        public String fi1(String input) {
            return "";
        }
    }
}
