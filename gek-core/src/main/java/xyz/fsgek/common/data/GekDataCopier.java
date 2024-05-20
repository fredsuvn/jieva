package xyz.fsgek.common.data;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekOption;
import xyz.fsgek.common.convert.GekConverter;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Data properties copier, to copy properties from source object to dest object.
 *
 * @author fredsuvn
 */
public interface GekDataCopier {

    /**
     * Returns default data copier.
     *
     * @return default data copier
     */
    static GekDataCopier defaultCopier() {
        return Impls.DEFAULT_COPIER;
    }

    /**
     * Copies properties from source object to dest object.
     * <p>
     * For default implementation:
     * <ul>
     *     <li>
     *         Supports both common data object and {@link Map} object;
     *     </li>
     *     <li>
     *         The types specified by params {@code sourceType} and {@code destType};
     *     </li>
     *     <li>
     *         For common data object, it will be resolved by {@link GekDataResolver} first, type of properties' names
     *         is {@link String} and types of properties' values are come from {@link GekPropertyDescriptor#getType()};
     *     </li>
     *     <li>
     *         For {@link Map} object, type of properties' names and values are come from {@code destType};
     *     </li>
     *     <li>
     *         Options are specified by {@link GekDataOption}, including:
     *         <ul>
     *             <li>{@link GekDataOption#resolver(GekDataResolver)}</li>
     *             <li>{@link GekDataOption#converter(GekConverter)}</li>
     *             <li>{@link GekDataOption#ignoredProperties(Object...)}, {@link GekDataOption#ignoredProperties(Iterable)}</li>
     *             <li>{@link GekDataOption#ignoreNull(boolean)}</li>
     *             <li>{@link GekDataOption#thrownIfConversionFails(boolean)}</li>
     *             <li>{@link GekDataOption#putIfNotContained(boolean)}</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param source     source object
     * @param sourceType source type
     * @param dest       dest object
     * @param destType   dest type
     * @param options    copying options
     * @throws GekDataCopyException exception when copying
     */
    void copyProperties(
        @Nullable Object source, Type sourceType,
        @Nullable Object dest, Type destType,
        GekOption<?, ?>... options
    ) throws GekDataCopyException;
}
