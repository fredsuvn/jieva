package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.invoke.Invoker;
import xyz.fslabo.common.invoke.InvokingException;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        private final Set<Method> proxiedSet = new HashSet<>();
        private final Map<Method, Invoker> invokerMap = new HashMap<>();

        private InvocationHandlerImpl(Iterable<Class<?>> uppers, MethodProxyHandler handler) {
            this.handler = handler;
            for (Class<?> upper : uppers) {
                Method[] methods = upper.getMethods();
                for (Method method : methods) {
                    if (Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                        continue;
                    }
                    invokerMap.put(method, Invoker.handle(method));
                    if (handler.proxy(method)) {
                        proxiedSet.add(method);
                    }
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
            Invoker invoker = invokerMap.get(method);
            if (!proxiedSet.contains(method)) {
                return invoke0(invoker, proxy, args);
            }
            return handler.invoke(proxy, method, args, new ProxyInvoker() {
                @Override
                public @Nullable Object invoke(Object inst, Object[] args) throws Throwable {
                    return invoke0(invoker, inst, args);
                }

                @Override
                public @Nullable Object invokeSuper(Object[] args) throws Throwable {
                    throw new AbstractMethodError(method.toString());
                }
            });
        }

        private Object invoke0(Invoker invoker, Object inst, @Nullable Object[] args) throws Throwable {
            try {
                Object[] actualArgs = args == null ? EMPTY_ARGS : args;
                return invoker.invoke(inst, actualArgs);
            } catch (InvokingException e) {
                Throwable cause = e.getCause();
                if (cause instanceof InvocationTargetException) {
                    throw cause.getCause();
                }
                throw cause;
            }
        }
    }
}
