package test.java.xyz.srclab.common.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Current;
import xyz.srclab.common.proxy.*;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ProxyTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testClassProxy() {
        Assert.assertEquals(ProxyClassFactory.DEFAULT, SpringProxyClassFactory.INSTANCE);
        doTestProxy(CglibProxyClassFactory.INSTANCE, TC.class);
        //doTestProxy(SpringProxyClassFactory.INSTANCE, TC.class);
    }

    @Test
    public void testInterfaceProxy() {
        //doTestProxy(CglibProxyClassFactory.INSTANCE, TI.class);
        doTestProxy(SpringProxyClassFactory.INSTANCE, TI.class);
        doTestProxy(JdkProxyClassFactory.INSTANCE, TI.class);
    }

    private <T extends TI> void doTestProxy(ProxyClassFactory proxyClassFactory, Class<T> type) {
        ProxyMethod<T> proxyMethod = new ProxyMethod<T>() {
            @NotNull
            @Override
            public String name() {
                return "hello";
            }

            @Override
            public Class<?>[] parameterTypes() {
                return new Class[]{String.class, String.class};
            }

            @Override
            public Object invoke(
                T proxied, @NotNull Method proxiedMethod, @Nullable Object[] args, @NotNull SuperInvoker superInvoker) {
                logger.log("method: {}, declaring class: {}", proxiedMethod, proxiedMethod.getDeclaringClass());
                return "proxy-> " + (type.isInterface() ? "interface" : superInvoker.invoke(args));
            }
        };
        ProxyClass<T> proxyClass = ProxyClass.newProxyClass(
            type, Arrays.asList(proxyMethod), Current.classLoader(), proxyClassFactory);
        Assert.assertEquals(
            proxyClass.newInstance().hello("a", "b"),
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
