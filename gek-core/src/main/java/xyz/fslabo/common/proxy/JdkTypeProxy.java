package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.JieUnsafe;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.invoke.JieInvoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

final class JdkTypeProxy<T> implements TypeProxy<T> {

    static Object createInterfaceProxy(
        ClassLoader loader,
        Iterable<Class<?>> superInterfaces,
        Map<Predicate<Method>, ProxyInvoker> proxyMap
    ) {
        Map<Method, ProxyInvoker> proxyInvokerMap = buildProxyInvokerMap(superInterfaces, proxyMap);
        Map<Method, ProxiedInvoker> proxiedInvokerMap = buildProxiedInvokerMap(superInterfaces);
        return Proxy.newProxyInstance(
            loader,
            JieColl.toArray(superInterfaces, Class.class),
            new InvocationHandlerImpl(proxyInvokerMap, proxiedInvokerMap));
    }

    private static Map<Method, ProxyInvoker> buildProxyInvokerMap(
        Iterable<Class<?>> superInterfaces,
        Map<Predicate<Method>, ProxyInvoker> proxyMap
    ) {
        Map<Method, ProxyInvoker> result;
        if (JieColl.isEmpty(proxyMap)) {
            result = Collections.emptyMap();
        } else {
            result = new HashMap<>();
            for (Class<?> superInterface : superInterfaces) {
                Method[] methods = superInterface.getMethods();
                for (Map.Entry<Predicate<Method>, ProxyInvoker> entry : proxyMap.entrySet()) {
                    Predicate<Method> predicate = entry.getKey();
                    ProxyInvoker invoker = entry.getValue();
                    for (Method method : methods) {
                        if (predicate.test(method)) {
                            result.put(method, invoker);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static Map<Method, ProxiedInvoker> buildProxiedInvokerMap(Iterable<Class<?>> superInterfaces) {
        Map<Method, ProxiedInvoker> result = new HashMap<>();
        for (Class<?> superInterface : superInterfaces) {
            MethodHandles.Lookup lookup = null;
            Method[] methods = superInterface.getMethods();
            for (Method method : methods) {
                if (!method.isDefault()) {
                    continue;
                }
                if (lookup == null) {
                    lookup = JieUnsafe.lookup(method.getDeclaringClass());
                }
                result.put(method, new ProxiedInvokerImpl(lookup, method));
            }
        }
        return result.isEmpty() ? Collections.emptyMap() : result;
    }


    private static final Object[] EMPTY_ARGS = {};

    private final ClassLoader loader;
    private final Class<?>[] superInterfaces;
    private final Map<Method, ProxyInvoker> proxyInvokerMap;
    private final Map<Method, ProxiedInvoker> proxiedInvokerMap;

    JdkTypeProxy(
        ClassLoader loader,
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> superInterfaces,
        Map<Predicate<Method>, ProxyInvoker> proxyMap
    ) {
        if (JieColl.isEmpty(superInterfaces)) {
            throw new ProxyException("No super interface.");
        }
        this.loader = Jie.orDefault(loader, getClass().getClassLoader());
        this.superInterfaces = JieColl.toArray(superInterfaces, Class.class);
        this.proxyInvokerMap = buildProxyInvokerMap(superInterfaces, proxyMap);
        this.proxiedInvokerMap = buildProxiedInvokerMap(superInterfaces);
    }

    @Override
    public T newInstance() {
        Object inst = Proxy.newProxyInstance(
            loader, superInterfaces,
            new InvocationHandlerImpl(proxyInvokerMap, proxiedInvokerMap));
        return Jie.as(inst);
    }

    private static final class InvocationHandlerImpl implements InvocationHandler {

        private final Map<Method, ProxyInvoker> proxyInvokerMap;
        private final Map<Method, ProxiedInvoker> proxiedInvokerMap;

        private InvocationHandlerImpl(
            Map<Method, ProxyInvoker> proxyInvokerMap, Map<Method, ProxiedInvoker> proxiedInvokerMap) {
            this.proxyInvokerMap = proxyInvokerMap;
            this.proxiedInvokerMap = proxiedInvokerMap;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            ProxyInvoker proxyMethod = proxyInvokerMap.get(method);
            if (proxyMethod == null) {
                return invokeSuper(proxy, method, args);
            }
            return null;//proxyMethod.invoke(proxy, method, (p, as) -> invokeSuper(p, method, as), args);
        }

        private Object invokeSuper(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isDefault()) {
                ProxiedInvoker invoker = proxiedInvokerMap.get(method);
                if (invoker != null) {
                    return invoker.invoke(proxy, Jie.orDefault(args, EMPTY_ARGS));
                }
            }
            throw new ProxyException("Method is abstract: " + method);
        }
    }

    private static final class ProxiedInvokerImpl implements ProxiedInvoker {

        private final MethodHandle handle;

        private ProxiedInvokerImpl(MethodHandles.Lookup lookup, Method method) {
            try {
                this.handle = lookup.unreflectSpecial(method, method.getDeclaringClass());
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
