package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.ThreadSafe;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Type proxy, to create proxy instance of proxied type.
 *
 * @param <T> proxied type
 * @author fredsuvn
 */
@ThreadSafe
public interface TypeProxy<T> {

    /**
     * Returns a new {@link TypeProxy}.
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
     * Builder for {@link TypeProxy}.
     * <p>
     * The instance built by this builder only supports the proxy of methods in {@link Class#getMethods()}, and supports
     * using {@code JDK proxy} or {@code cglib} to proxy.
     * <p>
     * Note {@code JDK proxy} only supports proxying interfaces.
     *
     * @param <T> proxied type
     */
    class Builder<T> {

        /**
         * Use engine of JDK proxy.
         */
        public static final int ENGINE_JDK = 0;

        /**
         * Use engine of cglib proxy.
         */
        public static final int ENGINE_CGLIB = 1;

        /**
         * Use engine of spring proxy.
         */
        public static final int ENGINE_SPRING = 2;

        private Class<?> superClass;
        private Iterable<Class<?>> superInterfaces;
        private final Map<Predicate<Method>, TypeProxyMethod> proxyMap = new HashMap<>();
        private int engine = 0;

        /**
         * Sets super class of proxy class.
         *
         * @param superClass super class
         * @param <T1>       proxied type
         * @return this builder
         */
        public <T1 extends T> Builder<T1> superClass(Class<?> superClass) {
            this.superClass = superClass;
            return Jie.as(this);
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
            return Jie.as(this);
        }

        /**
         * Sets methods to be proxied, the methods which can pass given predicate will be proxied by given proxy method.
         *
         * @param predicate   given predicate
         * @param proxyMethod proxy method
         * @param <T1>        proxied type
         * @return this builder
         */
        public <T1 extends T> Builder<T1> proxyMethod(Predicate<Method> predicate, TypeProxyMethod proxyMethod) {
            proxyMap.put(predicate, proxyMethod);
            return Jie.as(this);
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
        public <T1 extends T> Builder<T1> proxyMethod(
            String methodName, Class<?>[] paramTypes, TypeProxyMethod proxyMethod) {
            return proxyMethod(method ->
                    Objects.equals(method.getName(), methodName)
                        && Arrays.equals(method.getParameterTypes(), paramTypes),
                proxyMethod
            );
        }

        /**
         * Sets proxy engine:
         * <ul>
         *     <li>{@link #ENGINE_JDK};</li>
         *     <li>{@link #ENGINE_CGLIB};</li>
         *     <li>{@link #ENGINE_SPRING};</li>
         * </ul>
         * By default, the builder will check the required libs in current runtime to choose an engine in priority:
         * spring &gt; cglib &gt; JDK.
         * <p>
         * Note the required libs need to be present in the runtime environment.
         *
         * @param engine proxy engine
         * @param <T1>   proxied type
         * @return this builder
         */
        public <T1 extends T> Builder<T1> engine(int engine) {
            this.engine = engine;
            return Jie.as(this);
        }

        /**
         * Builds a new {@link TypeProxy}.
         *
         * @param <T1> proxied type
         * @return a new {@link TypeProxy}
         */
        public <T1 extends T> TypeProxy<T1> build() {
            switch (engine) {
                case ENGINE_JDK:
                    return new JdkTypeProxy<>(superInterfaces, proxyMap);
                case ENGINE_CGLIB:
                    return new CglibTypeProxy<>(superClass, superInterfaces, proxyMap);
                case ENGINE_SPRING:
                    return new SpringTypeProxy<>(superClass, superInterfaces, proxyMap);
            }
            if (hasSpring()) {
                return new SpringTypeProxy<>(superClass, superInterfaces, proxyMap);
            }
            if (hasCglib()) {
                return new CglibTypeProxy<>(superClass, superInterfaces, proxyMap);
            }
            return new JdkTypeProxy<>(superInterfaces, proxyMap);
        }

        private boolean hasSpring() {
            return JieReflect.hasClass("org.springframework.cglib.proxy.Enhancer");
        }

        private boolean hasCglib() {
            return JieReflect.hasClass("net.sf.cglib.proxy.Enhancer");
        }
    }
}
