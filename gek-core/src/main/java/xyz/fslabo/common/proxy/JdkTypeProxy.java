package xyz.fslabo.common.proxy;

import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.coll.JieColl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Predicate;

final class JdkTypeProxy<T> implements TypeProxy<T> {

    private final Class<?>[] superInterfaces;
    private final Map<MethodSignature, TypeProxyMethod> methodMap;

    JdkTypeProxy(Iterable<Class<?>> superInterfaces, Map<Predicate<Method>, TypeProxyMethod> proxyMap) {
        if (JieColl.isEmpty(superInterfaces)) {
            throw new TypeProxyException("No super interface to be proxied.");
        }
        this.superInterfaces = JieColl.toArray(superInterfaces, Class.class);
        if (JieColl.isEmpty(proxyMap)) {
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
                    TypeProxyMethod proxyMethod = methodMap.get(new MethodSignature(method));
                    if (proxyMethod == null) {
                        return method.invoke(proxy, args);
                    }
                    return proxyMethod.invokeProxy(proxy, method, (p, as) -> {
                        try {
                            return method.invoke(p, as);
                        } catch (Exception e) {
                            throw new TypeProxyException(e);
                        }
                    }, args);
                });
        }
        return Jie.as(inst);
    }

    private static final class MethodSignature {

        private final String name;
        private final Class<?>[] paramTypes;

        private MethodSignature(Method method) {
            this.name = method.getName();
            this.paramTypes = method.getParameterTypes();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this == o) {
                return true;
            }
            if (o instanceof MethodSignature) {
                MethodSignature om = (MethodSignature) o;
                return Objects.equals(this.name, om.name) && Arrays.equals(this.paramTypes, om.paramTypes);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, Arrays.hashCode(paramTypes));
        }
    }
}
