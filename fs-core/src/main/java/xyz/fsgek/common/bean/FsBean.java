package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.reflect.FsReflect;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Collectors;

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
     * @return given type to {@link FsBean}
     */
    static FsBean resolve(Type type) {
        return FsBeanResolver.defaultResolver().resolve(type);
    }

    /**
     * Wraps given map as a {@link FsBean} by {@link FsBeanResolver#defaultResolver()},
     * of which type will be seen as Map&lt;String, Object&gt;.
     * This method is equivalent to:
     * <pre>
     *     wrap(map, null);
     * </pre>
     *
     * @param map given map
     * @return given map as a {@link FsBean}
     * @see #wrap(Map, Type)
     */
    static FsBean wrap(Map<String, ?> map) {
        return wrap(map, null);
    }

    /**
     * Wraps given map as a {@link FsBean} by {@link FsBeanResolver#defaultResolver()},
     * the key type of map type must be {@link String}.
     * If the given map type is null, the map type will be seen as Map&lt;String, Object&gt;.
     * <p>
     * Result of {@link FsBean#getProperties()} is immutable, but content may be different for each time calling.
     * Because of the changes in given map, contents of return property map are also changed accordingly.
     *
     * @param map     given map
     * @param mapType given map type
     * @return given map as a {@link FsBean}
     */
    static FsBean wrap(Map<String, ?> map, @Nullable Type mapType) {
        return FsBeanResolver.defaultResolver().wrapMap(map, mapType);
    }

    /**
     * Returns a string describes given bean.
     *
     * @param bean given bean
     * @return a string describes given bean
     */
    static String toString(FsBean bean) {
        return "bean["
            + bean.getProperties().entrySet().stream().map(Object::toString).collect(Collectors.joining(", "))
            + "]";
    }

    /**
     * Returns type of this bean.
     *
     * @return type of this bean
     */
    Type getType();

    /**
     * Returns raw type of this bean.
     *
     * @return raw type of this bean
     */
    default Class<?> getRawType() {
        return FsReflect.getRawType(getType());
    }

    /**
     * Returns all properties in this bean.
     *
     * @return all properties in this bean
     */
    @Immutable
    Map<String, FsProperty> getProperties();

    /**
     * Returns property with given name in this bean.
     *
     * @param name given name
     * @return property with given name in this bean
     */
    @Nullable
    default FsProperty getProperty(String name) {
        return getProperties().get(name);
    }
}
