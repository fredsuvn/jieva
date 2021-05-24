package sample.java.xyz.srclab.core.proxy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.annotations.Test;
import xyz.srclab.common.proxy.ProxyClass;
import xyz.srclab.common.proxy.ProxyMethod;
import xyz.srclab.common.proxy.SuperInvoker;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ProxySample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testProxy() {
        ProxyClass<Object> proxyClass = ProxyClass.newProxyClass(
            Object.class,
            Arrays.asList(
                new ProxyMethod<Object>() {
                    @NotNull
                    @Override
                    public String name() {
                        return "toString";
                    }

                    @NotNull
                    @Override
                    public Class<?>[] parameterTypes() {
                        return new Class[0];
                    }

                    @Nullable
                    @Override
                    public Object invoke(
                        Object proxied,
                        @NotNull Method proxiedMethod,
                        @Nullable Object[] args, @NotNull SuperInvoker superInvoker
                    ) {
                        return "Proxy[super: " + superInvoker.invoke(args) + "]";
                    }
                }
            )
        );
        String s = proxyClass.newInstance().toString();
        //Proxy[super: net.sf.cglib.empty.Object$$EnhancerByCGLIB$$4926690c@256f38d9]
        logger.log("s: {}", s);
    }
}
