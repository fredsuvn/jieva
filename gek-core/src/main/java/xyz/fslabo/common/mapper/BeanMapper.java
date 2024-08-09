package xyz.fslabo.common.mapper;

import xyz.fslabo.common.bean.BeanInfo;
import xyz.fslabo.common.bean.BeanProvider;
import xyz.fslabo.common.bean.PropertyInfo;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Mapper for {@link BeanInfo}, to copy properties from source object to dest object.
 *
 * @author fredsuvn
 */
public interface BeanMapper {

    /**
     * Returns default bean mapper.
     *
     * @return default bean mapper
     */
    static BeanMapper defaultMapper() {
        return BeanMapperImpl.DEFAULT_MAPPER;
    }

    /**
     * Copies properties from source object to dest object. Supports both bean object and {@link Map} object.
     * This method is equivalent to {@link #copyProperties(Object, Type, Object, Type, MapperOptions)}:
     * <pre>
     *     copyProperties(source, source.getClass(), dest, dest.getClass(), options);
     * </pre>
     *
     * @param source  source object
     * @param dest    dest object
     * @param options mapper options
     * @throws MapperException exception when copying
     */
    default void copyProperties(Object source, Object dest, MapperOptions options) throws MapperException {
        copyProperties(source, source.getClass(), dest, dest.getClass(), options);
    }

    /**
     * Copies properties from source object to dest object.
     * <p>
     * In default implementation:
     * <ul>
     *     <li>
     *         Supports both bean object and {@link Map} object;
     *     </li>
     *     <li>
     *         For bean object, bean info will be provided by {@link BeanProvider} first, type of properties' names
     *         is always {@link String} and types of properties' values are come from {@link PropertyInfo#getType()};
     *     </li>
     *     <li>
     *         For {@link Map} object, types of keys and values are come from {@code sourceType} or {@code destType};
     *     </li>
     * </ul>
     *
     * @param source     source object
     * @param sourceType type of source object
     * @param dest       dest object
     * @param destType   type of dest object
     * @param options    mapper options
     * @throws MapperException exception when copying
     */
    void copyProperties(
        Object source, Type sourceType,
        Object dest, Type destType,
        MapperOptions options
    ) throws MapperException;
}
