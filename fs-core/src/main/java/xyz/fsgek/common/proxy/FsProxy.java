package xyz.fsgek.common.proxy;

import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.reflect.FsReflect;
import xyz.fsgek.common.base.Fs;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * Proxy for java type, to create new proxy instance.
 *
 * @param <T> proxied type
 * @author fredsuvn
 */
@ThreadSafe
public interface FsProxy<T> {

    /**
     * Returns new builder of {@link FsProxy}.
     *
     * @param <T> proxied type
     * @return new builder
     */
    static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    /**
     * Constructs and returns new instance of this proxy class.
     *
     * @return new instance of this proxy class
     */
    T newInstance();

    /**
     * Builder for {@link FsProxy}.
     * The instance built by this builder only supports the proxy of methods in {@link Class#getMethods()},
     * and supports using JDK proxy or cglib to proxy.
     * <p>
     * Note JDK proxy only supports proxying interfaces.
     *
     * @param <T> proxied type
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
        private final Map<Predicate<Method>, FsProxyMethod> proxyMap = new HashMap<>();
        private int proxyGenerator = 0;

        /**
         * Sets super class of proxy class.
         *
         * @param superClass super class
         * @param <T1>       proxied type
         * @return this builder
         */
        public <T1 extends T> Builder<T1> superClass(Class<?> superClass) {
            this.superClass = superClass;
            return Fs.as(this);
        }

        /**
         * Sets super interfaces of proxy class.
         *
         * @param superInterfaces super interfaces
         * @param <T1>            proxied type
         * @return this builder
         */
        public <T1 extends T> Builder<T1> superInterfaces(Class<?>... superInterfaces) {
            return superInterfaces(Arrays.asList(superInterfaces));
        }

        /**
         * Sets super interfaces of proxy class.
         *
         * @param superInterfaces super interfaces
         * @param <T1>            proxied type
         * @return this builder
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
         * @param <T1>        proxied type
         * @return this builder
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
         * @param <T1>        proxied type
         * @return this builder
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
         * @param <T1>        proxied type
         * @return this builder
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
         * spring &gt; cglib &gt; JDK.
         * <p>
         * Note the required libs need to be present in the runtime environment.
         *
         * @param proxyGenerator proxy generator
         * @param <T1>           proxied type
         * @return this builder
         */
        public <T1 extends T> Builder<T1> proxyGenerator(int proxyGenerator) {
            this.proxyGenerator = proxyGenerator;
            return Fs.as(this);
        }

        /**
         * Builds {@link FsProxy}.
         *
         * @param <T1> proxied type
         * @return built {@link FsProxy}
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
            return FsReflect.hasClass("org.springframework.cglib.proxy.Enhancer");
        }

        private boolean hasCglib() {
            return FsReflect.hasClass("net.sf.cglib.proxy.Enhancer");
        }
    }
}
