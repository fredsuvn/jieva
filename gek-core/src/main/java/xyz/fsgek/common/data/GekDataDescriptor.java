package xyz.fsgek.common.data;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Descriptor of <b>data object</b>.
 * <p>
 * Data object is similar to Java Bean, but not exactly the same. A data object has some properties which are accessed
 * by getter and setter. The descriptor indicates its structure resolved by {@link GekDataResolver}.
 *
 * @author fredsuvn
 * @see GekDataResolver
 */
@Immutable
@ThreadSafe
public interface GekDataDescriptor {

    /**
     * Resolves given bean type to {@link GekDataDescriptor} by {@link GekDataResolver#defaultResolver()}.
     *
     * @param type given bean type
     * @return given type to {@link GekDataDescriptor}
     */
    static GekDataDescriptor resolve(Type type) {
        return GekDataResolver.defaultResolver().resolve(type);
    }

    /**
     * Wraps given map as a {@link GekDataDescriptor} by {@link GekDataResolver#defaultResolver()},
     * of which type will be seen as Map&lt;String, ?&gt;.
     * types of properties will be calculated dynamically by its value's {@link Class#getClass()} (or Object if null).
     * This method is same with:
     * <pre>
     *     wrapMap(map, null);
     * </pre>
     *
     * @param map given map
     * @return given map as a {@link GekDataDescriptor}
     * @see #wrap(Map, Type)
     */
    static GekDataDescriptor wrap(Map<String, ?> map) {
        return wrap(map, null);
    }

    /**
     * Wraps given map as a {@link GekDataDescriptor} by {@link GekDataResolver#defaultResolver()},
     * the type of keys of map type must be {@link String},
     * property type is specified by map type. If the given map type is null,
     * the map type will be seen as Map&lt;String, ?&gt;. If map type is Map&lt;String, ?&gt;,
     * types of properties will be calculated dynamically by its value's {@link Class#getClass()} (or Object if null).
     * <p>
     * Result of {@link GekDataDescriptor#getProperties()} is immutable, but its content will be affected by given map dynamically.
     *
     * @param map     given map
     * @param mapType given map type
     * @return given map as a {@link GekDataDescriptor}
     */
    static GekDataDescriptor wrap(Map<String, ?> map, @Nullable Type mapType) {
        return GekDataResolver.defaultResolver().wrap(map, mapType);
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for this interface.
     * This method uses result of {@link GekDataDescriptor#getType()} to compare:
     * <pre>
     *     return Objects.equals(bean.getType(), other.getType());
     * </pre>
     * And it works in conjunction with {@link #hashCode(GekDataDescriptor)}.
     *
     * @param bean comparing bean
     * @param o    object to be compared
     * @return true if equals false otherwise
     */
    static boolean equals(GekDataDescriptor bean, @Nullable Object o) {
        if (bean == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!bean.getClass().equals(o.getClass())) {
            return false;
        }
        GekDataDescriptor other = (GekDataDescriptor) o;
        return Objects.equals(bean.getType(), other.getType());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for this interface.
     * This method uses {@link Object#hashCode()} of {@link GekDataDescriptor#getType()} to compute:
     * <pre>
     *     return bean.getType().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(GekDataDescriptor, Object)}.
     *
     * @param bean property to be hashed
     * @return hash code of given bean
     */
    static int hashCode(GekDataDescriptor bean) {
        return bean.getType().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for this interface.
     *
     * @param dataDescriptor bean to be stringified
     * @return a string describes given property
     */
    static String toString(GekDataDescriptor dataDescriptor) {
        return "{@objId: " + Gek.systemHash(dataDescriptor) + ", "
            + "@objType: bean, "
            + "@objClass: " + dataDescriptor.getClass().getName() + ", "
            + "type: " + dataDescriptor.getType().getTypeName() + ", "
            + "properties: ["
            + dataDescriptor.getProperties().entrySet().stream().map(Object::toString).collect(Collectors.joining(", "))
            + "]"
            + "}";
    }

    /**
     * Returns type of data object, should be a {@link Class} or a {@link ParameterizedType}.
     *
     * @return type of data object, should be a {@link Class} or a {@link ParameterizedType}
     */
    Type getType();

    /**
     * Returns raw type of this data object.
     *
     * @return raw type of this data object
     */
    default Class<?> getRawType() {
        return GekReflect.getRawType(getType());
    }

    /**
     * Returns all properties in this data descriptor.
     *
     * @return all properties in this data descriptor
     */
    @Immutable
    Map<String, GekPropertyDescriptor> getProperties();

    /**
     * Returns property with given name in this data descriptor.
     *
     * @param name given name
     * @return property with given name in this data descriptor
     */
    @Nullable
    default GekPropertyDescriptor getProperty(String name) {
        return getProperties().get(name);
    }
}
