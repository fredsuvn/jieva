package xyz.srclab.common.bean;

import xyz.srclab.annotations.Immutable;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Bean info of object, usually resolved by {@link FsBeanResolver}.
 *
 * @author fredsuvn
 * @see FsBeanResolver
 */
@Immutable
@ThreadSafe
public interface FsBean {

    /**
     * Resolves given type to {@link FsBean} by {@link FsBeanResolver#defaultResolver()}.
     *
     * @param type given type
     */
    static FsBean resolve(Type type) {
        return FsBeanResolver.defaultResolver().resolve(type);
    }

    /**
     * Wraps given map as a {@link FsBean} by {@link FsBeanResolver#defaultResolver()},
     * of which type will be seen as Map&lt;String, Object>.
     * This method is equivalent to:
     * <pre>
     *     wrap(map, null);
     * </pre>
     *
     * @param map given map
     * @see #wrap(Map, Type)
     */
    static FsBean wrap(Map<String, ?> map) {
        return wrap(map, null);
    }

    /**
     * Wraps given map as a {@link FsBean} by {@link FsBeanResolver#defaultResolver()},
     * the key type of map type must be {@link String}.
     * If the given map type is null, the map type will be seen as Map&lt;String, Object>.
     * <p>
     * Result of {@link FsBean#getProperties()} is immutable, but content may be different for each time calling.
     * Because of the changes in given map, contents of return property map are also changed accordingly.
     *
     * @param map     given map
     * @param mapType given map type
     */
    static FsBean wrap(Map<String, ?> map, @Nullable Type mapType) {
        return FsBeanResolver.defaultResolver().wrapMap(map, mapType);
    }

    /**
     * Returns type of this bean.
     */
    Type getType();

    /**
     * Returns raw type of this bean.
     */
    default Class<?> getRawType() {
        return FsType.getRawType(getType());
    }

    /**
     * Returns all properties in this bean.
     */
    Map<String, FsBeanProperty> getProperties();

    /**
     * Returns property with given name in this bean.
     *
     * @param name given name
     */
    @Nullable
    default FsBeanProperty getProperty(String name) {
        return getProperties().get(name);
    }
}
