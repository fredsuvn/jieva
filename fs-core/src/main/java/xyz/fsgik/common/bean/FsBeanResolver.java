package xyz.fsgik.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;
import xyz.fsgik.common.bean.handlers.AbstractBeanResolveHandler;
import xyz.fsgik.common.bean.handlers.JavaBeanResolveHandler;
import xyz.fsgik.common.bean.handlers.RecordBeanResolveHandler;
import xyz.fsgik.common.cache.FsCache;
import xyz.fsgik.common.collect.FsCollect;
import xyz.fsgik.common.reflect.FsReflect;
import xyz.fsgik.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Resolver for {@link FsBean}, usually consists of {@link Handler}s.
 * There are 2 built-in resolvers:
 * <ul>
 *     <li>{@link JavaBeanResolveHandler} (default handler);</li>
 *     <li>{@link RecordBeanResolveHandler};</li>
 * </ul>
 * And a skeletal implementation: {@link AbstractBeanResolveHandler}.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface FsBeanResolver {

    /**
     * Default map type for wrapping bean: Map&lt;String, Object&gt;.
     */
    Type DEFAULT_MAP_BEAN_TYPE = new TypeRef<Map<String, Object>>() {
    }.getType();

    /**
     * Returns default bean resolver of which handler is {@link JavaBeanResolveHandler}.
     *
     * @return default bean resolver
     */
    static FsBeanResolver defaultResolver() {
        return BeanResolverImpl.INSTANCE;
    }

    /**
     * Returns new bean resolver with given handler and {@link FsCache#softCache()}.
     * This method is equivalent to {@link #newResolver(Iterable)}:
     * <pre>
     *     return newResolver(Collections.singleton(handler));
     * </pre>
     *
     * @param handler given handler
     * @return new bean resolver
     */
    static FsBeanResolver newResolver(Handler handler) {
        return newResolver(Collections.singleton(handler));
    }

    /**
     * Returns new bean resolver with given handlers and {@link FsCache#softCache()}.
     * This method is equivalent to {@link #newResolver(Iterable, FsCache)}:
     * <pre>
     *     return newResolver(handlers, FsCache.softCache());
     * </pre>
     *
     * @param handlers given handlers
     * @return new bean resolver
     */
    static FsBeanResolver newResolver(Iterable<Handler> handlers) {
        return newResolver(handlers, FsCache.softCache());
    }

    /**
     * Returns new bean resolver with given handlers and cache option.
     * If given cache is null, returned resolver will not cache the resolved bean.
     *
     * @param handlers given handlers
     * @param cache    given cache
     * @return new bean resolver
     */
    static FsBeanResolver newResolver(Iterable<Handler> handlers, @Nullable FsCache<Type, FsBean> cache) {
        return new BeanResolverImpl(handlers, cache);
    }

    /**
     * Resolves given type to {@link FsBean}.
     *
     * @param type given type
     * @return resolved {@link FsBean}
     */
    FsBean resolve(Type type);

    /**
     * Wraps given map as a {@link FsBean}, of which type will be seen as Map&lt;String, Object&gt;.
     * This method is same with:
     * <pre>
     *     wrapMap(map, null);
     * </pre>
     *
     * @param map given map
     * @return wrapped {@link FsBean}
     * @see #wrapMap(Map, Type)
     */
    default FsBean wrapMap(Map<String, ?> map) {
        return wrapMap(map, null);
    }

    /**
     * Wraps given map as a {@link FsBean}, the key type of map type must be {@link String}.
     * If the given map type is null, the map type will be seen as {@link #DEFAULT_MAP_BEAN_TYPE}.
     * <p>
     * Result of {@link FsBean#getProperties()} is immutable, but content may be different for each time calling.
     * Because of the changes in given map, contents of return property map are also changed accordingly.
     *
     * @param map     given map
     * @param mapType given map type
     * @return wrapped {@link FsBean}
     */
    default FsBean wrapMap(Map<String, ?> map, @Nullable Type mapType) {
        final class MapBeanImpl implements FsBean {

            private final Map<String, Object> mapObject;
            private final Type mapType;
            private final Type valueType;
            private final Map<String, Node> propertyNodes = new LinkedHashMap<>();
            private int propertyVersion = 0;
            private Map<String, FsProperty> properties;

            private MapBeanImpl(Map<String, Object> map, @Nullable Type mapType) {
                this.mapObject = map;
                this.mapType = mapType == null ? DEFAULT_MAP_BEAN_TYPE : mapType;
                if (this.mapType == DEFAULT_MAP_BEAN_TYPE) {
                    this.valueType = Object.class;
                } else {
                    ParameterizedType parameterizedType = FsReflect.getGenericSuperType(mapType, Map.class);
                    if (parameterizedType == null) {
                        throw new IllegalArgumentException("Not a map type: " + mapType + ".");
                    }
                    Type[] types = parameterizedType.getActualTypeArguments();
                    if (!Objects.equals(String.class, types[0])) {
                        throw new IllegalArgumentException("Key type is not String: " + types[0] + ".");
                    }
                    this.valueType = types[1];
                }
            }

            @Override
            public Type getType() {
                return mapType;
            }

            @Override
            public synchronized Map<String, FsProperty> getProperties() {
                // first init
                if (properties == null) {
                    mapObject.forEach((k, v) -> {
                        FsProperty property = new MapPropertyImpl(k);
                        propertyNodes.put(k, new Node(propertyVersion, property));
                    });
                    properties = Collections.unmodifiableMap(
                        FsCollect.mapMap(propertyNodes, new LinkedHashMap<>(), name -> name, Node::getProperty)
                    );
                    return properties;
                }

                propertyVersion++;
                boolean hasNewNode = false;
                Set<String> keySet = mapObject.keySet();
                for (String key : keySet) {
                    Node node = propertyNodes.get(key);
                    if (node == null) {
                        FsProperty property = new MapPropertyImpl(key);
                        propertyNodes.put(key, new Node(propertyVersion, property));
                        hasNewNode = true;
                    } else {
                        node.version = propertyVersion;
                    }
                }
                if (!hasNewNode && properties.size() == keySet.size()) {
                    return properties;
                } else {
                    //remove expired nodes
                    Set<Map.Entry<String, Node>> nodes = new HashSet<>(propertyNodes.entrySet());
                    for (Map.Entry<String, Node> entry : nodes) {
                        Node node = entry.getValue();
                        if (node.version != propertyVersion) {
                            propertyNodes.remove(entry.getKey());
                        }
                    }
                }
                properties = Collections.unmodifiableMap(
                    FsCollect.mapMap(propertyNodes, new LinkedHashMap<>(), name -> name, Node::getProperty)
                );
                return properties;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof MapBeanImpl)) {
                    return false;
                }
                return Objects.equals(mapType, ((MapBeanImpl) o).mapType)
                    && Objects.equals(mapObject, ((MapBeanImpl) o).mapObject);
            }

            @Override
            public int hashCode() {
                return Objects.hash(mapType, mapObject.hashCode());
            }

            @Override
            public String toString() {
                return FsBean.toString(this);
            }

            @Data
            @AllArgsConstructor
            final class Node {
                private int version;
                private FsProperty property;
            }

            final class MapPropertyImpl implements FsProperty {

                private final String key;

                private MapPropertyImpl(String key) {
                    this.key = key;
                }

                @Override
                public String getName() {
                    return key;
                }

                @Override
                public @Nullable Object get(Object bean) {
                    return mapObject.get(key);
                }

                @Override
                public void set(Object bean, @Nullable Object value) {
                    mapObject.put(key, value);
                }

                @Override
                public Type getType() {
                    return valueType;
                }

                @Override
                public @Nullable Method getGetter() {
                    return null;
                }

                @Override
                public @Nullable Method getSetter() {
                    return null;
                }

                @Override
                public @Nullable Field getField() {
                    return null;
                }

                @Override
                public List<Annotation> getGetterAnnotations() {
                    return Collections.emptyList();
                }

                @Override
                public List<Annotation> getSetterAnnotations() {
                    return Collections.emptyList();
                }

                @Override
                public List<Annotation> getFieldAnnotations() {
                    return Collections.emptyList();
                }

                @Override
                public List<Annotation> getAnnotations() {
                    return Collections.emptyList();
                }

                @Override
                public FsBean getOwner() {
                    return MapBeanImpl.this;
                }

                @Override
                public boolean isReadable() {
                    return true;
                }

                @Override
                public boolean isWriteable() {
                    return true;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (!(o instanceof MapPropertyImpl)) {
                        return false;
                    }
                    return Objects.equals(key, ((MapPropertyImpl) o).key)
                        && Objects.equals(MapBeanImpl.this, ((MapPropertyImpl) o).getOwner());
                }

                @Override
                public int hashCode() {
                    return Objects.hash(key);
                }

                @Override
                public String toString() {
                    return FsProperty.toString(this);
                }
            }
        }
        return new MapBeanImpl((Map<String, Object>) map, mapType);
    }

    /**
     * Returns handlers of this resolver.
     *
     * @return handlers of this resolver
     */
    List<Handler> getHandlers();

    /**
     * Returns a new resolver of which handlers come from current resolver,
     * but inserts given handler at first index of handlers.
     * The returned resolver will share the cache with this resolver.
     *
     * @param handler given handler
     * @return a new resolver
     */
    default FsBeanResolver insertFirstHandler(Handler handler) {
        return insertHandler(0, handler);
    }

    /**
     * Returns a new resolver of which handlers come from current resolver,
     * but inserts given handler at specified index of handlers.
     * The returned resolver will share the cache with this resolver.
     *
     * @param index   specified index
     * @param handler given handler
     * @return a new resolver
     */
    FsBeanResolver insertHandler(int index, Handler handler);

    /**
     * Returns this resolver as {@link Handler}.
     *
     * @return this resolver as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link FsBeanResolver}.
     * <p>
     * Note this interface doesn't support wrap-map methods such as {@link #wrapMap(Map, Type)}.
     *
     * @author fredsuvn
     * @see FsBeanResolver
     */
    @ThreadSafe
    interface Handler {

        /**
         * Resolves bean from given resolving context, of which properties map
         * (from {@link ResolveContext#beanProperties()}) is readable and writeable.
         * Each handler can operate the properties map --
         * add new property or remove property which is resolved by previous handler.
         * <p>
         * In general, all handlers in the resolver ({@link #getHandlers()}) will be invoked this method sequentially.
         * If a handler want to prevent next resolving, call {@link ResolveContext#breakResolving()} to tell
         * the resolver to break and finish resolving after current resolution.
         *
         * @param context given resolving context
         * @see FsBeanResolver
         */
        @Nullable
        void resolve(ResolveContext context);
    }

    /**
     * Context of bean resolving.
     */
    interface ResolveContext {

        /**
         * Returns type of resolved bean.
         *
         * @return type of resolved bean
         */
        Type beanType();

        /**
         * Returns bean properties map in current resolving context.
         * The map through whole resolving process, store all properties from all handlers.
         * Each handler can add or remove or modify properties which is resolved by previous handler in this map.
         * <p>
         * Note: {@link FsPropertyBase} produced by handler must be immutable, it will be directly used by the final
         * generated {@link FsProperty} object, and any operations performed on the base object will affect the final
         * generated object.
         *
         * @return properties map in current resolving context
         */
        Map<String, FsPropertyBase> beanProperties();

        /**
         * Tells the bean resolver to break and finish resolving after this resolution.
         */
        void breakResolving();

        /**
         * Returns whether the resolver is told to break and finish resolving after this resolution.
         *
         * @return whether the resolver is told to break and finish resolving after this resolution
         */
        boolean isBreakResolving();
    }
}
