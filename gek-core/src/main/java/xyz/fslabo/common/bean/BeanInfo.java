package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.ThreadSafe;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Information about the properties and methods of <b>Jie Bean</b>, commonly using {@link BeanProvider} to get the
 * instance.
 * <p>
 * Jie bean is similar to Java PropertyInfo, but not exactly the same. A jie bean consist of a set of properties and
 * methods, but unlike java bean, jie bean do not have indexed properties, listeners, events and any other specific
 * methods and rules.
 * <p>
 * Jie bean is just the set of properties and methods, the properties are defined by property accessors, and the methods
 * are rest of non-property accessor methods.
 *
 * @author fredsuvn
 * @see BeanProvider
 */
@Immutable
@ThreadSafe
public interface BeanInfo {

    /**
     * Returns bean info of given type by {@link BeanProvider#defaultProvider()}.
     *
     * @param type given type
     * @return bean info of given type
     */
    static BeanInfo get(Type type) {
        return BeanProvider.defaultProvider().getBeanInfo(type);
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
        return JieReflect.getRawType(getType());
    }

    /**
     * Returns all property infos as map of this bean info.
     *
     * @return all property infos as map of this bean info
     */
    @Immutable
    Map<String, PropertyInfo> getProperties();

    /**
     * Returns property info with given name in this bean info.
     *
     * @param name given name
     * @return property info with given name in this bean info
     */
    @Nullable
    default PropertyInfo getProperty(String name) {
        return getProperties().get(name);
    }

    /**
     * Returns all method infos of this bean info.
     *
     * @return all method infos of this bean info
     */
    @Immutable
    List<MethodInfo> getMethods();
}
