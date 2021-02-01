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
    public void testProxy() {
        doTestProxy(JdkProxyClassFactory.INSTANCE);
    }

    private void doTestProxy(ProxyClassFactory proxyClassFactory) {
        ProxyMethod<TP> proxyMethod = new ProxyMethod<TP>() {
            @NotNull
            @Override
            public String name() {
                return "hello";
            }

            @Override
            public Class<?>[] parametersTypes() {
                return new Class[]{String.class, String.class};
            }

            @Override
            public Object invoke(
                    TP proxy, @NotNull Method method, @Nullable Object[] args, @NotNull SuperInvoker superInvoker) {
                logger.log("method: {}, declaring class: {}", method, method.getDeclaringClass());
                return "proxy-> " + superInvoker.invoke(args);
            }
        };
        ProxyClass<TP> proxyClass = ProxyClass.newProxyClass(
                TP.class, Arrays.asList(proxyMethod), Current.classLoader(), proxyClassFactory);
        Assert.assertEquals(proxyClass.newInstance().hello("a", "b"), "proxy-> hello: a = a, b = b");
    }

    public static class TP {

        public String hello(String a, String b) {
            return "hello: a = " + a + ", b = " + b;
        }
    }
}
