package test.java.xyz.srclab.common.proxy;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Contexts;
import xyz.srclab.common.logging.Logs;
import xyz.srclab.common.proxy.*;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ProxyTest {

    @Test
    public void testClassProxy() {
        Assert.assertEquals(ProxyClassGenerator.DEFAULT, SpringProxyClassGenerator.INSTANCE);
        doTestProxy(CglibProxyClassGenerator.INSTANCE, TC.class);
        //doTestProxy(SpringProxyClassFactory.INSTANCE, TC.class);
    }

    @Test
    public void testInterfaceProxy() {
        //doTestProxy(CglibProxyClassFactory.INSTANCE, TI.class);
        doTestProxy(SpringProxyClassGenerator.INSTANCE, TI.class);
        doTestProxy(JdkProxyClassGenerator.INSTANCE, TI.class);
    }

    private <T extends TI> void doTestProxy(ProxyClassGenerator proxyClassGenerator, Class<T> type) {

        ProxyMethod<T> proxyMethod = new ProxyMethod<T>() {

            @Override
            public boolean isProxy(@NotNull Method method) {
                return method.getName().equals("hello")
                    && Arrays.equals(method.getParameterTypes(), new Class[]{String.class, String.class});
            }

            @Override
            public Object invoke(
                @NotNull T proxied,
                @NotNull Method proxiedMethod,
                @NotNull SourceInvoker superInvoke,
                Object @NotNull [] args
            ) {
                Logs.info("method: {}, declaring class: {}", proxiedMethod, proxiedMethod.getDeclaringClass());
                return "proxy-> " + (type.isInterface() ? "interface" : superInvoke.invoke(args));
            }
        };

        ProxyClass<T> proxyClass = ProxyClass.generate(
            type, Arrays.asList(proxyMethod), Contexts.currentClassLoader(), proxyClassGenerator);
        Assert.assertEquals(
            proxyClass.instantiate().hello("a", "b"),
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
