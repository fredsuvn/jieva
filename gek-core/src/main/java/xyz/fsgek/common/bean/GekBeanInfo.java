package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.reflect.GekReflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Information about the properties and methods of <b>Gek Bean</b>, commonly using {@link GekBeanProvider} to get
 * the instance.
 * <p>
 * Gek bean is similar to Java Bean, but not exactly the same. A gek bean consist of a set of properties and methods,
 * but unlike java bean, gek bean do not have indexed properties, listeners, events and any other specific methods and
 * rules.
 * <p>
 * Gek bean is just the set of properties and methods, the properties of a bean are defined by property accessors, while
 * methods that are not property accessors belong to the bean's methods.
 *
 * @author fredsuvn
 * @see GekBeanProvider
 */
@Immutable
@ThreadSafe
public interface GekBeanInfo {

    /**
     * Returns bean info of given type by {@link GekBeanProvider#defaultProvider()}.
     *
     * @param type given type
     * @return bean info of given type
     */
    static GekBeanInfo get(Type type) {
        return GekBeanProvider.defaultProvider().getBeanInfo(type);
    }

    /**
     * Utility method which is a simple implementing of {@link Object#equals(Object)} for {@link GekBeanInfo}.
     * This method uses result of {@link GekBeanInfo#getType()} to compare.
     * The code is similar to the following:
     * <pre>
     *     return Objects.equals(bean.getType(), other.getType());
     * </pre>
     * And it works in conjunction with {@link #hashCode(GekBeanInfo)}.
     *
     * @param beanInfo comparing bean info
     * @param o        object to be compared
     * @return true if equals false otherwise
     */
    static boolean equals(GekBeanInfo beanInfo, @Nullable Object o) {
        if (beanInfo == o) {
            return true;
        }
        if (o == null || !beanInfo.getClass().equals(o.getClass())) {
            return false;
        }
        GekBeanInfo other = (GekBeanInfo) o;
        return Objects.equals(beanInfo.getType(), other.getType());
    }

    /**
     * Utility method which is a simple implementing of {@link Object#hashCode()} for {@link GekBeanInfo}.
     * This method uses {@link Object#hashCode()} of {@link GekBeanInfo#getType()} to compute.
     * The code is similar to the following:
     * <pre>
     *     return bean.getType().hashCode();
     * </pre>
     * And it works in conjunction with {@link #equals(GekBeanInfo, Object)}.
     *
     * @param beanInfo bean info to be hashed
     * @return hash code of given bean
     */
    static int hashCode(GekBeanInfo beanInfo) {
        return beanInfo.getType().hashCode();
    }

    /**
     * Utility method which is a simple implementing of {@link Object#toString()} for {@link GekBeanInfo}.
     * The code is similar to the following:
     * <pre>
     *     return beanInfo.getType().getTypeName();
     * </pre>
     *
     * @param beanInfo bean info to be string description
     * @return a string description for given descriptor
     */
    static String toString(GekBeanInfo beanInfo) {
        return beanInfo.getType().getTypeName();
    }

    /**
     * Returns type of bean, should be a {@link Class} or a {@link ParameterizedType}.
     *
     * @return type of bean, should be a {@link Class} or a {@link ParameterizedType}
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
     * Returns all property infos as map of this bean info.
     *
     * @return all property infos as map of this bean info
     */
    @Immutable
    Map<String, GekPropertyInfo> getProperties();

    /**
     * Returns property info with given name in this bean info.
     *
     * @param name given name
     * @return property info with given name in this bean info
     */
    @Nullable
    default GekPropertyInfo getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * Returns all method infos of this bean info.
     *
     * @return all method infos of this bean info
     */
    @Immutable
    List<GekMethodInfo> getMethods();
}
