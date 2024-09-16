package xyz.fslabo.common.reflect.proxy;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieSystem;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.invoke.Invoker;
import xyz.fslabo.common.invoke.InvokingException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * JDK implementation for {@link ProxyProvider}, based on {@link Proxy}, only supports interface proxy.
 *
 * @author fredsuvn
 */
public class JdkProxyProvider implements ProxyProvider {

    private static final Object[] EMPTY_ARGS = {};

    @Override
    public <T> T newProxyInstance(@Nullable ClassLoader loader, Iterable<Class<?>> proxied, MethodProxyHandler handler) {
        ClassLoader actualLoader = loader == null ? getClass().getClassLoader() : loader;
        Object proxy = Proxy.newProxyInstance(actualLoader, JieColl.toArray(proxied, Class.class), new InvocationHandlerImpl(proxied, handler));
        return Jie.as(proxy);
    }

    private static final class InvocationHandlerImpl implements InvocationHandler {

        private final MethodProxyHandler handler;
        private final Map<Method, JdkProxyInvoker> invokerMap = new HashMap<>();

        private InvocationHandlerImpl(Iterable<Class<?>> uppers, MethodProxyHandler handler) {
            this.handler = handler;
            for (Class<?> upper : uppers) {
                Method[] methods = upper.getMethods();
                for (Method method : methods) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        continue;
                    }
                    if (handler.proxy(method)) {
                        Invoker invoker = Invoker.handle(method);
                        JdkProxyInvoker jdkInvoker = new JdkProxyInvoker(method, invoker);
                        invokerMap.put(method, jdkInvoker);
                    }
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
            JdkProxyInvoker invoker = invokerMap.get(method);
            if (invoker == null) {
                return tryUnProxied(method);
            }
            return handler.invoke(proxy, method, args, invoker);
        }
    }

    private static final class JdkProxyInvoker implements ProxyInvoker {

        private final Method method;
        private final Invoker invoker;

        private JdkProxyInvoker(Method method, Invoker invoker) {
            this.method = method;
            this.invoker = invoker;
        }

        @Override
        public @Nullable Object invoke(Object inst, Object[] args) throws Throwable {
            return invoke0(invoker, inst, args);
        }

        @Override
        public @Nullable Object invokeSuper(Object[] args) throws Throwable {
            return tryUnProxied(method);
        }

        private Object invoke0(Invoker invoker, Object inst, @Nullable Object[] args) throws Throwable {
            try {
                Object[] actualArgs = args == null ? EMPTY_ARGS : args;
                return invoker.invoke(inst, actualArgs);
            } catch (InvokingException e) {
                throw e.getCause();
            }
        }
    }

    private static Object tryUnProxied(Method method) {
        if (method.isDefault()) {
            throw new ProxyException("Cannot invoke default method ("
                + method + ") in current java version: " + JieSystem.getJavaVersion() + ".");
        }
        throw new AbstractMethodError(method.toString());
    }
}
