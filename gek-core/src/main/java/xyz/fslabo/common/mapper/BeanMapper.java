package xyz.fslabo.common.mapper;

import xyz.fslabo.common.bean.BeanCopyException;
import xyz.fslabo.common.bean.BeanInfo;
import xyz.fslabo.common.bean.BeanProperty;
import xyz.fslabo.common.bean.BeanProvider;

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
    static BeanMapper defaultCopier() {
        return BeanMapperImpl.DEFAULT_MAPPER;
    }

    /**
     * Copies properties from source object to dest object.
     * <p>
     * For default implementation:
     * <ul>
     *     <li>
     *         Supports both bean object and {@link Map} object;
     *     </li>
     *     <li>
     *         For bean object, bean info will be provided by {@link BeanProvider} first, type of properties' names
     *         is {@link String} and types of properties' values are come from {@link BeanProperty#getType()};
     *     </li>
     *     <li>
     *         For {@link Map} object, type of properties' names and values are come from {@link Map} itself or options;
     *     </li>
     * </ul>
     *
     * @param source  source object
     * @param dest    dest object
     * @param options copying options
     * @throws BeanCopyException exception when copying
     */
    void copyProperties(Object source, Object dest, MapperOptions options) throws MapperException;
}
