package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieSystem;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.invoke.JieInvoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

final class JdkTypeProxy<T> implements TypeProxy<T> {

    private static final Object[] EMPTY_ARGS = {};

    private final ClassLoader loader;
    private final Class<?>[] superInterfaces;
    private final Map<Method, ProxyInvoker> methodMap;

    JdkTypeProxy(
        ClassLoader loader,
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> superInterfaces,
        Map<Predicate<Method>, ProxyInvoker> proxyMap
    ) {
        if (JieColl.isEmpty(superInterfaces)) {
            throw new ProxyException("No super interface.");
        }
        this.loader = loader;
        this.superInterfaces = JieColl.toArray(superInterfaces, Class.class);
        if (JieColl.isEmpty(proxyMap)) {
            this.methodMap = Collections.emptyMap();
        } else {
            this.methodMap = new HashMap<>();
            for (Class<?> superInterface : superInterfaces) {
                Method[] methods = superInterface.getMethods();
                proxyMap.forEach((predicate, proxy) -> {
                    for (Method method : methods) {
                        if (predicate.test(method)) {
                            methodMap.put(method, proxy);
                        }
                    }
                });
            }
        }
    }

    @Override
    public T newInstance() {
        Object inst = Proxy.newProxyInstance(
            loader == null ? this.getClass().getClassLoader() : loader,
            superInterfaces,
            new InvocationHandlerImpl());
        return Jie.as(inst);
    }

    private final class InvocationHandlerImpl implements InvocationHandler {

        private final Map<Method, ProxiedInvoker> defaultHandles = new ConcurrentHashMap<>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (methodMap.isEmpty()) {
                return invokeSuper(proxy, method, args);
            }
            ProxyInvoker proxyMethod = methodMap.get(method);
            if (proxyMethod == null) {
                return invokeSuper(proxy, method, args);
            }
            return proxyMethod.invoke(proxy, method, (p, as) -> invokeSuper(p, method, as), args);
        }

        private Object invokeSuper(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) {
                ProxiedInvoker invoker = getSuperInvoker(method);
                return invoker.invoke(proxy, Jie.orDefault(args, EMPTY_ARGS));
            }
            throw new ProxyException("Method is abstract: " + method);
        }

        private ProxiedInvoker getSuperInvoker(Method method) {
            return defaultHandles.computeIfAbsent(method, ProxiedInvokerImpl::new);
        }

        private MethodHandles.Lookup lookup(Method method) throws Throwable {
            if (JieSystem.isJava8Higher()) {
                return MethodHandles.lookup();
            }
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            return constructor.newInstance(method.getDeclaringClass())
                .in(method.getDeclaringClass());
        }

        private final class ProxiedInvokerImpl implements ProxiedInvoker {

            private final MethodHandle handle;

            private ProxiedInvokerImpl(Method method) {
                try {
                    this.handle = lookup(method).unreflectSpecial(method, method.getDeclaringClass());
                } catch (Throwable e) {
                    throw new ProxyException(e);
                }
            }

            @Override
            public @Nullable Object invoke(@Nullable Object inst, Object... args) throws Throwable {
                return JieInvoke.invokeVirtual(handle, inst, args);
            }
        }
    }
}
