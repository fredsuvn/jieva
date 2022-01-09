package test.java.xyz.srclab.common.proxy;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.func.StaticFunc;
import xyz.srclab.common.proxy.ClassProxy;
import xyz.srclab.common.proxy.ClassProxyFactory;
import xyz.srclab.common.proxy.ProxyMethod;
import xyz.srclab.common.reflect.BClass;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ProxyTest {

    @Test
    public void testClassProxy() {
        Assert.assertEquals(ClassProxyFactory.defaultFactory(), ClassProxyFactory.spring());
        doTestProxy(ClassProxyFactory.cglib(), TC.class);
        //doTestProxy(SpringProxyClassFactory.INSTANCE, TC.class);
    }

    @Test
    public void testInterfaceProxy() {
        //doTestProxy(CglibProxyClassFactory.INSTANCE, TI.class);
        doTestProxy(ClassProxyFactory.spring(), TI.class);
        doTestProxy(ClassProxyFactory.jdk(), TI.class);
    }

    private <T extends TI> void doTestProxy(ClassProxyFactory factory, Class<T> type) {

        ProxyMethod proxyMethod = new ProxyMethod() {

            @Override
            public boolean isProxy(@NotNull Method method) {
                return method.getName().equals("hello")
                    && Arrays.equals(method.getParameterTypes(), new Class[]{String.class, String.class});
            }

            @Override
            public Object invoke(
                @NotNull Object sourceObject,
                @NotNull Method sourceMethod,
                @NotNull StaticFunc sourceInvoke,
                Object[] args
            ) {
                BLog.info("method: {}, declaring class: {}", sourceMethod, sourceMethod.getDeclaringClass());
                return "proxy-> " + (type.isInterface() ? "interface" : sourceInvoke.invoke(args));
            }
        };

        ClassProxy<T> proxyClass = ClassProxy.generate(
            type, Arrays.asList(proxyMethod), BClass.currentClassLoader(), factory);
        Assert.assertEquals(
            proxyClass.create().hello("a", "b"),
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
