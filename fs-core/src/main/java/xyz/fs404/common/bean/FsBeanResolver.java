package xyz.fs404.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.fs404.annotations.Nullable;
import xyz.fs404.annotations.ThreadSafe;
import xyz.fs404.common.base.Fs;
import xyz.fs404.common.bean.handlers.JavaBeanResolveHandler;
import xyz.fs404.common.bean.handlers.RecordBeanResolveHandler;
import xyz.fs404.common.cache.FsCache;
import xyz.fs404.common.collect.FsCollect;
import xyz.fs404.common.reflect.FsType;
import xyz.fs404.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Resolver for {@link FsBean}, usually consists of {@link Handler}s.
 * There are 3 built-in resolvers:
 * <ul>
 *     <li>{@link JavaBeanResolveHandler} (default handler);</li>
 *     <li>{@link RecordBeanResolveHandler};</li>
 * </ul>
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface FsBeanResolver {

    /**
     * Default map type for wrapping bean: Map&lt;String, Object>.
     */
    Type DEFAULT_MAP_BEAN_TYPE = new TypeRef<Map<String, Object>>() {
    }.getType();

    /**
     * Returns default bean resolver of which handler is {@link JavaBeanResolveHandler}.
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
     */
    static FsBeanResolver newResolver(Iterable<Handler> handlers, @Nullable FsCache<Type, FsBean> cache) {
        return new BeanResolverImpl(handlers, cache);
    }

    /**
     * Resolves given type to {@link FsBean}.
     *
     * @param type given type
     */
    FsBean resolve(Type type);

    /**
     * Wraps given map as a {@link FsBean}, of which type will be seen as Map&lt;String, Object>.
     * This method is same with:
     * <pre>
     *     wrapMap(map, null);
     * </pre>
     *
     * @param map given map
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
     */
    default FsBean wrapMap(Map<String, ?> map, @Nullable Type mapType) {
        final class FsMapBeanImpl implements FsBean {

            private final Map<String, Object> mapObject;
            private final Type mapType;
            private final Type valueType;
            private final Map<String, Node> propertyNodes = new LinkedHashMap<>();
            private int propertyVersion = 0;
            private Map<String, FsBeanProperty> properties;

            private FsMapBeanImpl(Map<String, Object> map, @Nullable Type mapType) {
                this.mapObject = map;
                this.mapType = mapType == null ? DEFAULT_MAP_BEAN_TYPE : mapType;
                if (this.mapType == DEFAULT_MAP_BEAN_TYPE) {
                    this.valueType = Object.class;
                } else {
                    ParameterizedType parameterizedType = FsType.getGenericSuperType(mapType, Map.class);
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
            public synchronized Map<String, FsBeanProperty> getProperties() {
                // first init
                if (properties == null) {
                    mapObject.forEach((k, v) -> {
                        FsBeanProperty property = new FsMapBeanPropertyImpl(k);
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
                        FsBeanProperty property = new FsMapBeanPropertyImpl(key);
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
                if (!(o instanceof FsMapBeanImpl)) {
                    return false;
                }
                return Objects.equals(mapType, ((FsMapBeanImpl) o).mapType)
                    && Objects.equals(mapObject, ((FsMapBeanImpl) o).mapObject);
            }

            @Override
            public int hashCode() {
                return Objects.hash(mapType, mapObject.hashCode());
            }

            @Override
            public String toString() {
                return "bean(" + getProperties().entrySet().stream()
                    .map(it -> it.getKey() + ": " + it.getValue()).collect(Collectors.joining(", ")) +
                    ")";
            }

            @Data
            @AllArgsConstructor
            final class Node {
                private int version;
                private FsBeanProperty property;
            }

            final class FsMapBeanPropertyImpl implements FsBeanProperty {

                private final String key;

                private FsMapBeanPropertyImpl(String key) {
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
                    return FsMapBeanImpl.this;
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
                    if (!(o instanceof FsMapBeanPropertyImpl)) {
                        return false;
                    }
                    return Objects.equals(key, ((FsMapBeanPropertyImpl) o).key)
                        && Objects.equals(FsMapBeanImpl.this, ((FsMapBeanPropertyImpl) o).getOwner());
                }

                @Override
                public int hashCode() {
                    return Objects.hash(key);
                }

                @Override
                public String toString() {
                    return "BeanProperty(name=" +
                        key +
                        ", type=" +
                        valueType +
                        ", ownerType=" +
                        getOwner().getType() +
                        ")";
                }
            }
        }
        return new FsMapBeanImpl((Map<String, Object>) map, mapType);
    }

    /**
     * Returns handlers of this resolver.
     */
    List<Handler> getHandlers();

    /**
     * Returns a new resolver of which handlers come from current resolver,
     * but inserts given handler at first index of handlers.
     * The returned resolver will share the cache with this resolver.
     *
     * @param handler given handler
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
     */
    FsBeanResolver insertHandler(int index, Handler handler);

    /**
     * Returns this resolver as a {@link Handler}.
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
         * Resolves given bean builder, of which properties map is readable and writeable.
         * Each handler can build the properties map --
         * add new property or remove property which is resolved by previous handler.
         * <p>
         * Given bean builder itself is the final resolved {@link FsBean}, it is mutable in resolving process,
         * and finally it will become immutable.
         * That means, it can be set to returned value of {@link FsBeanProperty#getOwner()} for property implementation.
         * <p>
         * If this method returns {@link Fs#CONTINUE}, means the resolving will continue to next handler.
         * Otherwise, the resolving will end (such as return null or {@link Fs#BREAK}) .
         *
         * @param builder given bean builder
         * @see FsBeanResolver
         */
        @Nullable
        Object resolve(BeanBuilder builder);
    }

    /**
     * Builder and subtype of {@link FsBean}.
     * <p>
     * The parameter instance in {@link Handler#resolve(BeanBuilder)} is the final instance of {@link FsBean}
     * (it is mutable at the beginning, but becomes immutable after resolving),
     * that is, it can be set into {@link FsBeanProperty#getOwner()} for property implementation.
     *
     * @see Handler#resolve(BeanBuilder)
     */
    interface BeanBuilder extends FsBean {
    }
}
