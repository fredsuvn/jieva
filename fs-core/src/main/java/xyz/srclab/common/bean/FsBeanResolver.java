package xyz.srclab.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.bean.handlers.DefaultBeanResolveHandler;
import xyz.srclab.common.cache.FsCache;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.FsType;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Resolver for {@link FsBean}.
 *
 * @author fredsuvn
 */
public interface FsBeanResolver {

    /**
     * Default map type for wrapping bean: Map&lt;String, Object>.
     */
    Type DEFAULT_MAP_BEAN_TYPE = new TypeRef<Map<String, Object>>() {
    }.getType();

    /**
     * Returns default bean resolver, of which handlers are:
     * <ul>
     *     <li>{@link DefaultBeanResolveHandler#DefaultBeanResolveHandler()}</li>
     * </ul>
     */
    static FsBeanResolver defaultResolver() {
        return FsUnsafe.ForBean.DEFAULT_RESOLVER;
    }

    /**
     * Returns new bean resolver with given handler.
     * This method is equivalent to {@link #newResolver(Iterable, boolean)}:
     * <pre>
     *     return newResolver(handlers, true);
     * </pre>
     *
     * @param handlers given handler
     */
    static FsBeanResolver newResolver(Iterable<Handler> handlers) {
        return newResolver(handlers, true);
    }

    /**
     * Returns new bean resolver with given handler and cache option.
     * If the cache option is true, returned resolver will hold a cache from {@link FsCache#newCache()}
     * and cache the resolved bean.
     *
     * @param handlers given handler
     * @param useCache cache option
     */
    static FsBeanResolver newResolver(Iterable<Handler> handlers, boolean useCache) {
        final class FsBeanResolverImpl implements FsBeanResolver {

            private final Iterable<Handler> handlers;
            private final FsCache<FsBean> cache;

            FsBeanResolverImpl(Iterable<Handler> handlers, boolean useCache) {
                this.handlers = FsCollect.toCollection(new LinkedList<>(), handlers);
                this.cache = useCache ? FsCache.newCache() : null;
            }

            @Override
            public FsBean resolve(Type type) {
                if (cache == null) {
                    return resolve0(type);
                }
                return cache.get(type, this::resolve0);
            }

            private FsBean resolve0(Type type) {
                FsBeanBuilderImpl builder = new FsBeanBuilderImpl(type);
                for (Handler handler : handlers) {
                    Object result = handler.resolve(builder);
                    if (!Objects.equals(Fs.CONTINUE, result)) {
                        break;
                    }
                }
                return builder.build();
            }

            final class FsBeanBuilderImpl implements Handler.BeanBuilder {

                private final Type type;
                private volatile Map<String, FsProperty> properties = new LinkedHashMap<>();
                private volatile boolean built = false;
                private Integer hash = null;
                private String toString = null;

                FsBeanBuilderImpl(Type type) {
                    this.type = type;
                }

                @Override
                public Type getType() {
                    return type;
                }

                @Override
                public Map<String, FsProperty> getProperties() {
                    return properties;
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) {
                        return true;
                    }
                    if (o == null || getClass() != o.getClass()) {
                        return false;
                    }
                    FsBeanBuilderImpl that = (FsBeanBuilderImpl) o;
                    return Objects.equals(type, that.type) && Objects.equals(properties, that.properties);
                }

                @Override
                public int hashCode() {
                    if (!built) {
                        return Objects.hash(type, properties);
                    }
                    if (hash == null) {
                        int finalHash = Objects.hash(type, properties);
                        hash = finalHash;
                        return finalHash;
                    }
                    return hash;
                }

                @Override
                public String toString() {
                    if (!built) {
                        return computeToString(false);
                    }
                    if (toString == null) {
                        String finalToString = computeToString(true);
                        toString = finalToString;
                        return finalToString;
                    }
                    return toString;
                }

                private String computeToString(boolean built) {
                    return (built ? "bean" : "beanBuilder") + "(" + properties.entrySet().stream()
                        .map(it -> it.getKey() + ": " + it.getValue()).collect(Collectors.joining(", ")) +
                        ")";
                }

                private FsBean build() {
                    Map<String, FsProperty> builtProperties = this.properties;
                    properties = Collections.unmodifiableMap(new LinkedHashMap<>(builtProperties));
                    built = true;
                    return this;
                }
            }
        }
        return new FsBeanResolverImpl(handlers, useCache);
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
     * If the given map type is null, the map type will be seen as Map&lt;String, Object>.
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
            private Map<String, FsProperty> properties;

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
            public synchronized Map<String, FsProperty> getProperties() {
                // first init
                if (properties == null) {
                    mapObject.forEach((k, v) -> {
                        FsProperty property = new FsMapPropertyImpl(k);
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
                        FsProperty property = new FsMapPropertyImpl(key);
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
                private FsProperty property;
            }

            final class FsMapPropertyImpl implements FsProperty {

                private final String key;

                private FsMapPropertyImpl(String key) {
                    this.key = key;
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
                    if (!(o instanceof FsMapPropertyImpl)) {
                        return false;
                    }
                    return Objects.equals(key, ((FsMapPropertyImpl) o).key)
                        && Objects.equals(FsMapBeanImpl.this, ((FsMapPropertyImpl) o).getOwner());
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
     * Handler of {@link FsBeanResolver}.
     * <p>
     * Note this interface doesn't support wrap-map methods such as {@link #wrapMap(Map, Type)}.
     *
     * @author fredsuvn
     * @see FsBeanResolver
     */
    interface Handler {

        /**
         * Resolves given bean builder, of which properties map is readable and writeable.
         * Each handler can build the properties map --
         * add new property or remove property which is resolved by previous handler.
         * <p>
         * Given bean builder itself is the final resolved {@link FsBean}, it is mutable in resolving process,
         * and finally become to immutable. That means, it can be directly used as the return value of
         * {@link FsProperty#getOwner()}.
         * <p>
         * If this method returns {@link Fs#CONTINUE}, means the resolving will continue to next handler.
         * Otherwise, the resolving will end (such as return null or {@link Fs#BREAK}) .
         *
         * @param builder given bean builder
         * @see FsBeanResolver
         */
        @Nullable
        Object resolve(BeanBuilder builder);

        interface BeanBuilder extends FsBean {
        }
    }
}
