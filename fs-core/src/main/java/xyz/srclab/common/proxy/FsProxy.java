package xyz.srclab.common.proxy;

import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.base.Fs;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Proxy for java type, to create new proxy instance.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface FsProxy<T> {

    static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    /**
     * Constructs new instance for this proxy class.
     */
    T newInstance();

    class Builder<T> {

        private Class<T> sourceClass;
        private Map<Predicate<Method>, FsProxyMethod> proxyMap = new HashMap<>();

        public <T1 extends T> Builder<T1> sourceClass(Class<T1> sourceClass) {
            this.sourceClass = (Class<T>) sourceClass;
            return Fs.as(this);
        }

        public <T1 extends T> Builder<T1> proxyMethod(Predicate<Method> predicate, FsProxyMethod proxyMethod) {
            proxyMap.put(predicate, proxyMethod);
            return Fs.as(this);
        }

        public <T1 extends T> Builder<T1> proxyMethod(String methodName, List<Class<?>> paramTypes, FsProxyMethod proxyMethod) {
            return proxyMethod(methodName, paramTypes.toArray(new Class[0]), proxyMethod);
        }

        public <T1 extends T> Builder<T1> proxyMethod(String methodName, Class<?>[] paramTypes, FsProxyMethod proxyMethod) {
            return proxyMethod(
                method -> Objects.equals(method.getName(), methodName) && Arrays.equals(method.getParameterTypes(), paramTypes),
                proxyMethod
            );
        }

        public <T1 extends T> FsProxy<T1> build() {
            return Fs.as(new JdkProxyImpl<>(sourceClass, proxyMap));
        }

        private static final class JdkProxyImpl<T> implements FsProxy<T> {

            private final Class<T> sourceClass;
            private final Map<Predicate<Method>, FsProxyMethod> proxyMap;

            private JdkProxyImpl(Class<T> sourceClass, Map<Predicate<Method>, FsProxyMethod> proxyMap) {
                this.sourceClass = sourceClass;
                this.proxyMap = new ConcurrentHashMap<>(proxyMap);
            }

            @Override
            public T newInstance() {
                Method[] methods = sourceClass.getMethods();
                Map<MethodSignature, FsProxyMethod> methodMap = new HashMap<>();
                proxyMap.forEach((predicate, proxy) -> {
                    for (Method method : methods) {
                        if (predicate.test(method)) {
                            methodMap.put(new MethodSignature(method), proxy);
                        }
                    }
                });
                Object proxy = Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[]{sourceClass},
                    (proxy1, method, args) -> {
                        FsProxyMethod proxyMethod = methodMap.get(new MethodSignature(method));
                        if (proxyMethod == null) {
                            return method.invoke(proxy1, args);
                        }
                        return proxyMethod.invoke(args, method, objs -> {
                            try {
                                return method.invoke(proxy1, objs);
                            } catch (Exception e) {
                                throw new FsProxyException(e);
                            }
                        });
                    });
                return Fs.as(proxy);
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
    }
}
