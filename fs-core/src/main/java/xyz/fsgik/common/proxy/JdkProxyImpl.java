package xyz.fsgik.common.proxy;

import xyz.fsgik.common.base.Fs;
import xyz.fsgik.common.collect.FsCollect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Predicate;

final class JdkProxyImpl<T> implements FsProxy<T> {

    private final Class<?>[] superInterfaces;
    private final Map<MethodSignature, FsProxyMethod> methodMap;

    JdkProxyImpl(Iterable<Class<?>> superInterfaces, Map<Predicate<Method>, FsProxyMethod> proxyMap) {
        if (FsCollect.isEmpty(superInterfaces)) {
            throw new FsProxyException("No super interface to be proxied.");
        }
        this.superInterfaces = FsCollect.toArray(superInterfaces, Class.class);
        if (FsCollect.isEmpty(proxyMap)) {
            this.methodMap = Collections.emptyMap();
            return;
        }
        this.methodMap = new HashMap<>();
        for (Class<?> superInterface : superInterfaces) {
            Method[] methods = superInterface.getMethods();
            proxyMap.forEach((predicate, proxy) -> {
                for (Method method : methods) {
                    if (predicate.test(method)) {
                        methodMap.put(new MethodSignature(method), proxy);
                    }
                }
            });
        }
    }

    @Override
    public T newInstance() {
        Object inst;
        if (methodMap.isEmpty()) {
            inst = Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                superInterfaces,
                (proxy, method, args) -> method.invoke(proxy, args));
        } else {
            inst = Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                superInterfaces,
                (proxy, method, args) -> {
                    FsProxyMethod proxyMethod = methodMap.get(new MethodSignature(method));
                    if (proxyMethod == null) {
                        return method.invoke(proxy, args);
                    }
                    return proxyMethod.invokeProxy(args, method, objs -> {
                        try {
                            return method.invoke(proxy, objs);
                        } catch (InvocationTargetException e) {
                            throw e.getCause();
                        }
                    });
                });
        }
        return Fs.as(inst);
    }

    private static final class MethodSignature {

        private final String name;
        private final List<Class<?>> paramTypes;

        private int hash = 0;

        private MethodSignature(Method method) {
            this.name = method.getName();
            this.paramTypes = Arrays.asList(method.getParameterTypes());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodSignature that = (MethodSignature) o;
            return Objects.equals(name, that.name) && Objects.equals(paramTypes, that.paramTypes);
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = Objects.hash(name, paramTypes);
                if (hash == 0) {
                    hash = 1;
                }
            }
            return hash;
        }
    }
}
