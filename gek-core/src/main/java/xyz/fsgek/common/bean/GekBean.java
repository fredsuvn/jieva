package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Bean struct info of a type, usually resolved by {@link GekBeanResolver}.
 *
 * @author fredsuvn
 * @see GekBeanResolver
 */
@Immutable
@ThreadSafe
public interface GekBean {

    /**
     * Resolves given bean type to {@link GekBean} by {@link GekBeanResolver#defaultResolver()}.
     *
     * @param type given bean type
     * @return given type to {@link GekBean}
     */
    static GekBean resolve(Type type) {
        return GekBeanResolver.defaultResolver().resolve(type);
    }

    /**
     * Wraps given map as a {@link GekBean} by {@link GekBeanResolver#defaultResolver()},
     * of which type will be seen as Map&lt;String, Object&gt;.
     * This method is equivalent to:
     * <pre>
     *     wrap(map, null);
     * </pre>
     *
     * @param map given map
     * @return given map as a {@link GekBean}
     * @see #wrap(Map, Type)
     */
    static GekBean wrap(Map<String, ?> map) {
        return wrap(map, null);
    }

    /**
     * Wraps given map as a {@link GekBean} by {@link GekBeanResolver#defaultResolver()},
     * the key type of map type must be {@link String}.
     * If the given map type is null, the map type will be seen as Map&lt;String, Object&gt;.
     * <p>
     * Result of {@link GekBean#getProperties()} is immutable, but its content will be affected by given map.
     *
     * @param map     given map
     * @param mapType given map type
     * @return given map as a {@link GekBean}
     */
    static GekBean wrap(Map<String, ?> map, @Nullable Type mapType) {
        return GekBeanResolver.defaultResolver().wrap(map, mapType);
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for this interface.
     * This method uses result of {@link GekBean#getType()} to compare,
     * and work in conjunction with {@link #hashCode(GekBean)}.
     *
     * @param bean comparing bean
     * @param o    object to be compared
     * @return true if equal false otherwise
     */
    static boolean equals(GekBean bean, @Nullable Object o) {
        if (bean == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!bean.getClass().equals(o.getClass())) {
            return false;
        }
        GekBean other = (GekBean) o;
        return Objects.equals(bean.getType(), other.getType());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for this interface.
     * This method uses hash of {@link GekBean#getType()} to compute,
     * and work in conjunction with {@link #equals(GekBean, Object)}.
     *
     * @param bean property to be hashed
     * @return hash code of given property
     */
    static int hashCode(GekBean bean) {
        return bean.getType().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for this interface.
     *
     * @param bean bean to be stringified
     * @return a string describes given property
     */
    static String toString(GekBean bean) {
        return "bean{"
            + "type: " + bean.getType().getTypeName() + ", "
            + "properties: ["
            + bean.getProperties().entrySet().stream().map(Object::toString).collect(Collectors.joining(", "))
            + "]"
            + "}";
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
        return GekReflect.getRawType(getType());
    }

    /**
     * Returns all properties in this bean.
     *
     * @return all properties in this bean
     */
    @Immutable
    Map<String, GekProperty> getProperties();

    /**
     * Returns property with given name in this bean.
     *
     * @param name given name
     * @return property with given name in this bean
     */
    @Nullable
    default GekProperty getProperty(String name) {
        return getProperties().get(name);
    }
}
