package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This interface represents a simple type of java bean struct, which has a set of property structs,
 * each of the property consists of a name-value pair, the name is unique in same bean.
 * <p>
 * A bean corresponds to a {@link Type}, commonly resolved by {@link GekBeanResolver}.
 * The most common type of bean is a Java class that is filled with getters and setters which specify their properties.
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
     * of which type will be seen as Map&lt;String, ?&gt;.
     * types of properties will be calculated dynamically by its value's {@link Class#getClass()} (or Object if null).
     * This method is same with:
     * <pre>
     *     wrapMap(map, null);
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
     * the type of keys of map type must be {@link String},
     * property type is specified by map type. If the given map type is null,
     * the map type will be seen as Map&lt;String, ?&gt;. If map type is Map&lt;String, ?&gt;,
     * types of properties will be calculated dynamically by its value's {@link Class#getClass()} (or Object if null).
     * <p>
     * Result of {@link GekBean#getProperties()} is immutable, but its content will be affected by given map dynamically.
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
     * Returns type of this bean, should be {@link Class} or {@link ParameterizedType}.
     *
     * @return type of this bean, should be {@link Class} or {@link ParameterizedType}
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
