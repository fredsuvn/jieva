package xyz.srclab.common.bean;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Bean info for java object.
 *
 * @author fredsuvn
 */
public interface FsBeanInfo {

    /**
     * Creates new dest object and copies properties from given source object to dest object.
     * <p>
     * Returns null if failed.
     *
     * @param src       given source object
     * @param srcType   type of source object
     * @param destType  type of dest object
     * @param resolver  bean resolver to resolve object
     * @param converter converter to convert different property's types
     */
    @Nullable
    static <T> T copyProperties(
        Object src, Type srcType, Type destType, FsBeanResolver resolver, FsConverter converter) {
        return null;
    }

    /**
     * Resolves given type to bean structure by {@link FsBeanResolver#defaultResolver()}.
     *
     * @param type given type
     */
    static FsBeanInfo resolve(Type type) {
        return FsBeanResolver.defaultResolver().resolve(type);
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
