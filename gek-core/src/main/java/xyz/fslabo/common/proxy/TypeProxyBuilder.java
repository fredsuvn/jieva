package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

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
public class TypeProxyBuilder<T> {

    private Class<?> superClass;
    private Iterable<Class<?>> superInterfaces;
    private Engine engine;
    private final Map<Predicate<Method>, TypeProxyMethod> proxyMap = new HashMap<>();

    /**
     * Sets super class of proxy class.
     *
     * @param superClass super class
     * @param <T1>       proxied type
     * @return this builder
     */
    public <T1 extends T> TypeProxyBuilder<T1> superClass(Class<?> superClass) {
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
    public <T1 extends T> TypeProxyBuilder<T1> superInterfaces(Class<?>... superInterfaces) {
        return superInterfaces(Arrays.asList(superInterfaces));
    }

    /**
     * Sets super interfaces of proxy class.
     *
     * @param superInterfaces super interfaces
     * @param <T1>            proxied type
     * @return this builder
     */
    public <T1 extends T> TypeProxyBuilder<T1> superInterfaces(Iterable<Class<?>> superInterfaces) {
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
    public <T1 extends T> TypeProxyBuilder<T1> proxyMethod(Predicate<Method> predicate, TypeProxyMethod proxyMethod) {
        proxyMap.put(predicate, proxyMethod);
        return Jie.as(this);
    }

    /**
     * Sets method of which name is given method name and parameter types are given parameter types should be proxied by
     * given proxy method.
     *
     * @param methodName  given method name
     * @param paramTypes  given parameter types
     * @param proxyMethod proxy method
     * @param <T1>        proxied type
     * @return this builder
     */
    public <T1 extends T> TypeProxyBuilder<T1> proxyMethod(
        String methodName, Class<?>[] paramTypes, TypeProxyMethod proxyMethod) {
        return proxyMethod(method ->
                Objects.equals(method.getName(), methodName)
                    && Arrays.equals(method.getParameterTypes(), paramTypes),
            proxyMethod
        );
    }

    /**
     * Sets proxy {@link Engine}.
     * <p>
     * By default, the builder will check the required libs in current runtime to choose an engine in priority:
     * {@link Engine#SPRING} &gt; {@link Engine#CGLIB} &gt; {@link Engine#JDK}.
     * <p>
     * Note the required libs need to be present in the runtime environment.
     *
     * @param engine proxy engine
     * @param <T1>   proxied type
     * @return this builder
     */
    public <T1 extends T> TypeProxyBuilder<T1> engine(Engine engine) {
        this.engine = engine;
        return Jie.as(this);
    }

    /**
     * Builds a new {@link TypeProxy}. This method is equivalent to ({@link #build(ClassLoader)}):
     * <pre>
     *     return build(null);
     * </pre>
     *
     * @param <T1> proxied type
     * @return a new {@link TypeProxy}
     */
    public <T1 extends T> TypeProxy<T1> build() {
        return build(null);
    }

    /**
     * Builds a new {@link TypeProxy} with specified class loader.
     *
     * @param <T1>   proxied type
     * @param loader specified class loader
     * @return a new {@link TypeProxy}
     */
    public <T1 extends T> TypeProxy<T1> build(@Nullable ClassLoader loader) {
        Engine actualEngine = engine == null ? Engine.getEngine(loader) : engine;
        return Jie.as(
            actualEngine.getProxySupplier().getTypeProxy(loader, superClass, superInterfaces, proxyMap)
        );
    }

    /**
     * Proxy engine.
     */
    public enum Engine {

        /**
         * <a href = "https://spring.io">Spring Framework</a>.
         */
        SPRING("org.springframework.cglib.proxy.Enhancer", true, true, SpringTypeProxy::new),
        /**
         * <a href = "https://github.com/cglib/cglib">cglib</a>.
         */
        CGLIB("net.sf.cglib.proxy.Enhancer", true, true, CglibTypeProxy::new),
        /**
         * JDK proxy.
         */
        JDK(null, false, true, JdkTypeProxy::new),
        ;

        private static final Engine DEFAULT_ENGINE = getEngine0(null);

        /**
         * Returns an engine with specified class loader, according to the declaration order of this enum (same with
         * {@link #values()}).
         *
         * @param loader specified class loader
         * @return an engine with specified class loader
         */
        public static Engine getEngine(@Nullable ClassLoader loader) {
            if (loader == null) {
                return DEFAULT_ENGINE;
            }
            return getEngine0(loader);
        }

        private static Engine getEngine0(@Nullable ClassLoader loader) {
            for (Engine value : values()) {
                if (JieReflect.classExists(value.dependencyClass(), loader)) {
                    return value;
                }
            }
            return JDK;
        }

        private final String dependencyClass;
        private final boolean supportClass;
        private final boolean supportInterface;
        private final ProxySupplier proxySupplier;

        Engine(String dependencyClass, boolean supportClass, boolean supportInterface, ProxySupplier proxySupplier) {
            this.dependencyClass = dependencyClass;
            this.supportClass = supportClass;
            this.supportInterface = supportInterface;
            this.proxySupplier = proxySupplier;
        }

        /**
         * Returns whether this engine supports proxying class.
         *
         * @return whether this engine supports proxying class
         */
        public boolean supportClass() {
            return supportClass;
        }

        /**
         * Returns whether this engine supports proxying interface.
         *
         * @return whether this engine supports proxying interface
         */
        public boolean supportInterface() {
            return supportInterface;
        }

        /**
         * Returns dependency class name of this engine, may be {@code null} if no extra dependency.
         *
         * @return dependency class name of this engine, may be {@code null} if no extra dependency
         */
        @Nullable
        public String dependencyClass() {
            return dependencyClass;
        }

        private ProxySupplier getProxySupplier() {
            return proxySupplier;
        }
    }

    interface ProxySupplier {
        TypeProxy<?> getTypeProxy(
            ClassLoader loader,
            @Nullable Class<?> superClass,
            @Nullable Iterable<Class<?>> superInterfaces, Map<Predicate<Method>,
            TypeProxyMethod> proxyMap
        );
    }
}
