package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fs404.common.proxy.FsProxy;

public class ProxyTest {

    @Test
    public void testProxy() {
        testProxy(FsProxy.Builder.JDK_PROXY);
        testProxy(FsProxy.Builder.CGLIB_PROXY);
        testProxy(FsProxy.Builder.SPRING_PROXY);
    }

    @Test
    public void testProxyPriority() {
        testProxy(0);
    }

    private void testProxy(int proxyGenerator) {
        FsProxy<FooInter1> fooProxy = FsProxy.newBuilder()
            .superClass(FooClass.class)
            .superInterfaces(FooInter1.class, FooInter2.class)
            .proxyMethod("foo0", new Class[]{String.class},
                (args, sourceMethod, sourceInvocation) -> "foo0"
            )
            .proxyMethod("foo1", new Class[]{String.class},
                (args, sourceMethod, sourceInvocation) -> "foo1"
            )
            .proxyMethod("foo2", new Class[]{String.class},
                (args, sourceMethod, sourceInvocation) -> "foo2"
            )
            .proxyGenerator(proxyGenerator)
            .build();
        Assert.assertEquals(fooProxy.newInstance().foo1("fuck!"), "foo1");
        Assert.assertEquals(((FooInter2) (fooProxy.newInstance())).foo2("fuck!"), "foo2");
        if (proxyGenerator != FsProxy.Builder.JDK_PROXY) {
            Assert.assertEquals(((FooClass) (fooProxy.newInstance())).foo0("fuck!"), "foo0");
            Assert.assertEquals(((FooClass) (fooProxy.newInstance())).bar0("fuck!"), "fuck!");
        }
        System.out.println(fooProxy.newInstance().getClass());
    }

    public interface FooInter1 {

        String foo1(String input);
    }

    public interface FooInter2 {

        String foo2(String input);
    }

    public static class FooClass {

        public String foo0(String input) {
            return input;
        }

        public String bar0(String input) {
            return input;
        }
    }
}
