package xyz.fs404.common.proxy;

import xyz.fs404.annotations.ThreadSafe;
import xyz.fs404.common.base.Fs;
import xyz.fs404.common.collect.FsCollect;
import xyz.fs404.common.reflect.FsType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Predicate;

/**
 * Proxy for java type, to create new proxy instance.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface FsProxy<T> {

    /**
     * Returns new builder.
     */
    static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    /**
     * Constructs new instance for this proxy class.
     */
    T newInstance();

    /**
     * Builder for {@link FsProxy}.
     * The instance built by this builder only supports the proxy of methods in {@link Class#getMethods()},
     * and supports using JDK proxy or cglib to proxy.
     * <p>
     * Note JDK proxy only supports proxying interfaces.
     */
    class Builder<T> {

        /**
         * JDK proxy.
         */
        public static final int JDK_PROXY = 1;

        /**
         * Cglib proxy.
         */
        public static final int CGLIB_PROXY = 2;

        /**
         * Spring proxy.
         */
        public static final int SPRING_PROXY = 3;

        private Class<?> superClass;
        private Iterable<Class<?>> superInterfaces;
        private Map<Predicate<Method>, FsProxyMethod> proxyMap = new HashMap<>();
        private int proxyGenerator = 0;

        /**
         * Sets super class of proxy class.
         *
         * @param superClass super class
         */
        public <T1 extends T> Builder<T1> superClass(Class<?> superClass) {
            this.superClass = superClass;
            return Fs.as(this);
        }

        /**
         * Sets super interfaces of proxy class.
         *
         * @param superInterfaces super interfaces
         */
        public <T1 extends T> Builder<T1> superInterfaces(Class<?>... superInterfaces) {
            return superInterfaces(Arrays.asList(superInterfaces));
        }

        /**
         * Sets super interfaces of proxy class.
         *
         * @param superInterfaces super interfaces
         */
        public <T1 extends T> Builder<T1> superInterfaces(Iterable<Class<?>> superInterfaces) {
            this.superInterfaces = superInterfaces;
            return Fs.as(this);
        }

        /**
         * Sets methods to be proxied, the methods pass given predicate should be proxied by given proxy method.
         *
         * @param predicate   given predicate
         * @param proxyMethod proxy method
         */
        public <T1 extends T> Builder<T1> proxyMethod(Predicate<Method> predicate, FsProxyMethod proxyMethod) {
            proxyMap.put(predicate, proxyMethod);
            return Fs.as(this);
        }

        /**
         * Sets method of which name is given method name and parameter types are given parameter types
         * should be proxied by given proxy method.
         *
         * @param methodName  given method name
         * @param paramTypes  given parameter types
         * @param proxyMethod proxy method
         */
        public <T1 extends T> Builder<T1> proxyMethod(String methodName, List<Class<?>> paramTypes, FsProxyMethod proxyMethod) {
            return proxyMethod(methodName, paramTypes.toArray(new Class[0]), proxyMethod);
        }

        /**
         * Sets method of which name is given method name and parameter types are given parameter types
         * should be proxied by given proxy method.
         *
         * @param methodName  given method name
         * @param paramTypes  given parameter types
         * @param proxyMethod proxy method
         */
        public <T1 extends T> Builder<T1> proxyMethod(String methodName, Class<?>[] paramTypes, FsProxyMethod proxyMethod) {
            return proxyMethod(
                method -> Objects.equals(method.getName(), methodName) && Arrays.equals(method.getParameterTypes(), paramTypes),
                proxyMethod
            );
        }

        /**
         * Sets proxy generator:
         * <ul>
         *     <li>{@link #JDK_PROXY}: using jdk proxy;</li>
         *     <li>{@link #CGLIB_PROXY}: using cglib proxy;</li>
         *     <li>{@link #SPRING_PROXY}: using spring proxy;</li>
         * </ul>
         * By default, the builder will check the required libs in current runtime to choose a generator in priority:
         * spring > cglib > JDK.
         * <p>
         * Note the required libs need to be present in the runtime environment.
         *
         * @param proxyGenerator proxy generator
         */
        public <T1 extends T> Builder<T1> proxyGenerator(int proxyGenerator) {
            this.proxyGenerator = proxyGenerator;
            return Fs.as(this);
        }

        /**
         * Builds {@link FsProxy}.
         */
        public <T1 extends T> FsProxy<T1> build() {
            if (proxyGenerator == JDK_PROXY) {
                return new JdkProxyImpl<>(superInterfaces, proxyMap);
            }
            if (proxyGenerator == CGLIB_PROXY) {
                return new CglibProxyImpl<>(superClass, superInterfaces, proxyMap);
            }
            if (hasSpring()) {
                return new SpringProxyImpl<>(superClass, superInterfaces, proxyMap);
            }
            if (hasCglib()) {
                return new CglibProxyImpl<>(superClass, superInterfaces, proxyMap);
            }
            return new JdkProxyImpl<>(superInterfaces, proxyMap);
        }

        private boolean hasSpring() {
            return FsType.hasClass("org.springframework.cglib.proxy.Enhancer");
        }

        private boolean hasCglib() {
            return FsType.hasClass("net.sf.cglib.proxy.Enhancer");
        }

        private static final class JdkProxyImpl<T> implements FsProxy<T> {

            private final Class<?>[] superInterfaces;
            private final Map<MethodSignature, FsProxyMethod> methodMap;

            private JdkProxyImpl(Iterable<Class<?>> superInterfaces, Map<Predicate<Method>, FsProxyMethod> proxyMap) {
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
    }
}
