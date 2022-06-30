package test.java.xyz.srclab.common.proxy;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.func.StaticFunc;
import xyz.srclab.common.proxy.ClassProxy;
import xyz.srclab.common.proxy.ClassProxyProvider;
import xyz.srclab.common.proxy.ProxyInvoker;
import xyz.srclab.common.reflect.BClass;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ProxyTest {

    @Test
    public void testClassProxy() {
        Assert.assertEquals(ClassProxyProvider.defaultProvider(), ClassProxyProvider.springCglib());
        doTestProxy(ClassProxyProvider.cglib(), TC.class);
        //doTestProxy(SpringProxyClassFactory.INSTANCE, TC.class);
    }

    @Test
    public void testInterfaceProxy() {

        doTestProxy(ClassProxyProvider.springCglib(), TI.class);
        doTestProxy(ClassProxyProvider.jdk(), TI.class);
    }

    private <T extends TI> void doTestProxy(ClassProxyProvider factory, Class<T> type) {

        ProxyInvoker proxyInvoker = new ProxyInvoker() {

            @Override
            public boolean isTarget(@NotNull Method method) {
                return method.getName().equals("hello")
                    && Arrays.equals(method.getParameterTypes(), new Class[]{String.class, String.class});
            }

            @Override
            public Object invokeProxy(
                @NotNull Object sourceObject,
                @NotNull Method sourceMethod,
                @NotNull StaticFunc sourceInvoke,
                Object[] args
            ) {
                BLog.info("method: {}, declaring class: {}", sourceMethod, sourceMethod.getDeclaringClass());
                return "proxy-> " + (type.isInterface() ? "interface" : sourceInvoke.invoke(args));
            }
        };

        ClassProxy<T> proxyClass = ClassProxy.generate(type, proxyInvoker, BClass.defaultClassLoader(), factory);
        Assert.assertEquals(
            proxyClass.newInst().hello("a", "b"),
            type.isInterface() ? "proxy-> interface" : "proxy-> hello: a = a, b = b");
    }

    public static class TC implements TI {
        public String hello(String a, String b) {
            return "hello: a = " + a + ", b = " + b;
        }
    }

    public interface TI {
        String hello(String a, String b);
    }
}
