package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Option;
import xyz.fslabo.common.mapper.JieMapper;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Properties copier for {@link GekBeanInfo}, to copy properties from source object to dest object.
 *
 * @author fredsuvn
 */
public interface GekBeanCopier {

    /**
     * Returns default bean copier.
     *
     * @return default bean copier
     */
    static GekBeanCopier defaultCopier() {
        return CopierImpl.DEFAULT_COPIER;
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
     *         The types specified by params {@code sourceType} and {@code destType};
     *     </li>
     *     <li>
     *         For bean object, bean info will be provided by {@link GekBeanProvider} first, type of properties' names
     *         is {@link String} and types of properties' values are come from {@link GekPropertyInfo#getType()};
     *     </li>
     *     <li>
     *         For {@link Map} object, type of properties' names and values are come from {@code destType};
     *     </li>
     *     <li>
     *         Options are specified by {@link BeanOption}, including:
     *         <ul>
     *             <li>{@link BeanOption#provider(GekBeanProvider)}</li>
     *             <li>{@link BeanOption#converter(JieMapper)}</li>
     *             <li>{@link BeanOption#ignoredProperties(Object...)}, {@link BeanOption#ignoredProperties(Iterable)}</li>
     *             <li>{@link BeanOption#ignoreNull(boolean)}</li>
     *             <li>{@link BeanOption#thrownIfConversionFails(boolean)}</li>
     *             <li>{@link BeanOption#putIfNotContained(boolean)}</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param source     source object
     * @param sourceType source type
     * @param dest       dest object
     * @param destType   dest type
     * @param options    copying options
     * @throws GekBeanCopyException exception when copying
     */
    void copyProperties(
        @Nullable Object source, Type sourceType,
        @Nullable Object dest, Type destType,
        Option<?, ?>... options
    ) throws GekBeanCopyException;
}
