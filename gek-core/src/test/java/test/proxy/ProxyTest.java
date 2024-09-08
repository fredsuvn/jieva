package test.proxy;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureWriter;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.proxy.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
        Assert.assertEquals(f2.fi0(""), "fi0");
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

    @Test
    public void ss() throws Exception{
        Type ot = Type.getType(List.class);
        System.out.println(ot.getInternalName());
        System.out.println(ot.getDescriptor());
        Type it = Type.getType(int.class);
        System.out.println(it.getInternalName());
        System.out.println(it.getDescriptor());
        Type vt = Type.getType(void.class);
        System.out.println(vt.getInternalName());
        System.out.println(vt.getDescriptor());
        Type mt = Type.getType(ProxyTest.class.getMethod("sss", List.class));
        System.out.println(mt.getInternalName());
        System.out.println(mt.getDescriptor());
        System.out.println(Arrays.toString(mt.getArgumentTypes()));
        System.out.println(mt.getReturnType());
        SignatureWriter sw = new SignatureWriter();
        sw.visitClassType(ot.getInternalName());
        // sw.visitFormalTypeParameter("T");
        sw.visitTypeVariable("T");
        //sw.visitArrayType();
        sw.visitEnd();
        System.out.println(sw);
        System.out.println(K.class);
    }

    public static abstract class X<T>{

    }

    public static abstract class K<T, U extends String & CharSequence, F extends T> extends X<Number> implements List<String>, Serializable {
        private int i = 0;
        private int[] ia = null;
        private List<? extends T> list1;
        private List<? super String> list2 = new ArrayList<>();
        private List<String> list6;
        private List<U> list3 = new ArrayList<>();
        private List<? extends T>[] lista;
        private List[] lista2;

        private K k1;
        private K<T,U,F> k2;

        public K() {
        }

        public K(String a) {
        }

        public K(T t, U u) {
        }

        public int ii(int i) {
            return 0;
        }

        public String ss(U u) throws Exception {
            super.toString();
            return null;
        }

        public void sss(U u) throws Exception {
        }

        public String sss(U u, T t) throws Exception {
            return null;
        }

        public void sss() throws Exception {
        }

        public String sss(List<? extends U> u, List<? super T> t) throws Exception {
            return null;
        }
    }

    public List<? extends String> sss(List<? extends String> s) {
        return null;
    }

    public interface FooInter {

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
    }

    public static class FooClass implements FooInter {

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
    }

    public static class ProxyMethod implements ProxyInvoker {

        @Override
        public @Nullable Object invoke(
            @Nullable Object inst, Method proxiedMethod, ProxiedInvoker proxiedInvoker, Object... args) throws Throwable {
            Object result = proxiedInvoker.invoke(inst, args);
            return result + "-proxy";
        }
    }
}
