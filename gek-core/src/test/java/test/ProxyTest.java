package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.invoke.Invoker;
import xyz.fslabo.common.proxy.TypeProxy;
import xyz.fslabo.common.proxy.TypeProxyBuilder;
import xyz.fslabo.common.proxy.TypeProxyMethod;

import java.lang.reflect.Method;

public class ProxyTest {

    @Test
    public void testProxy() {
        testProxy(TypeProxyBuilder.Engine.JDK);
        testProxy(TypeProxyBuilder.Engine.CGLIB);
        testProxy(TypeProxyBuilder.Engine.SPRING);
    }

    // @Test
    // public void testProxyPriority() {
    // }

    private void testProxy(TypeProxyBuilder.Engine engine) {
        ProxyMethod proxyMethod = new ProxyMethod();
        if (engine.supportClass()) {
            TypeProxy<FooClass> fcp = TypeProxy.newBuilder()
                .superClass(FooClass.class)
                .superInterfaces(FooInter.class)
                .proxyMethod("fi0", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fi1", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fi2", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fc0", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fc1", new Class[]{String.class}, proxyMethod)
                .engine(engine)
                .build();
            FooClass fc = fcp.newInstance();
            Assert.assertEquals(fc.fi0(""), "fi0-proxy");
            Assert.assertEquals(fc.fi1(""), "fi1-proxy");
            Assert.assertEquals(fc.fc0(""), "fc0-proxy");
            Assert.expectThrows(IllegalStateException.class, fc::fc1);
            Assert.expectThrows(IllegalStateException.class, fc::fi2);
        } else if (engine.supportInterface()) {
            TypeProxy<FooInter> fcp = TypeProxy.newBuilder()
                .superInterfaces(FooInter.class)
                .proxyMethod("fi0", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fi1", new Class[]{String.class}, proxyMethod)
                .proxyMethod("fi2", new Class[]{String.class}, proxyMethod)
                .engine(engine)
                .build();
            FooInter fi = fcp.newInstance();
            Assert.assertEquals(fi.fi0(""), "fi0-proxy");
            Assert.assertEquals(fi.fi1(""), "fi1-proxy");
            Assert.expectThrows(IllegalStateException.class, fi::fi2);
        }
    }

    public interface FooInter {

        default String fi0(String input) {
            return "fi0";
        }

        String fi1(String input);

        default void fi2() {
            throw new IllegalStateException();
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

    public static class ProxyMethod implements TypeProxyMethod {

        @Override
        public @Nullable Object invokeProxy(
            @Nullable Object inst, Method superMethod, Invoker superInvoker, Object... args) {
            Object result = superInvoker.invoke(inst, args);
            return result + "-proxy";
        }
    }
}
