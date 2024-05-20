package xyz.fsgek.common.data;

import xyz.fsgek.common.base.GekOption;
import xyz.fsgek.common.convert.GekConvertException;
import xyz.fsgek.common.convert.GekConverter;

/**
 * Options for gek data.
 *
 * @author sunqian
 */
public interface GekDataOption extends GekOption<GekDataOption.Key, Object> {

    /**
     * Key of {@link GekDataOption}.
     */
    enum Key {

        /**
         * To specify the {@link GekDataResolver}.
         * Default is {@link GekDataResolver#defaultResolver()}.
         */
        RESOLVER,

        /**
         * To specify the {@link GekConverter}.
         * Default is {@link GekConverter#defaultConverter()}.
         */
        CONVERTER,

        /**
         * To specify ignored properties to copy.
         */
        IGNORED_PROPERTIES,

        /**
         * To specify whether the null value should be ignored to copy.
         * Default is false.
         */
        IGNORE_NULL,

        /**
         * To specify whether a {@link GekDataCopyException} should be thrown when any part of conversion fails.
         * Default is true, if it is set to false, failed property copy will be ignored.
         */
        THROWN_IF_ANY_FAILS,

        /**
         * To specify whether the value should be copied into the dest map if the dest doesn't contain the key mapped by
         * the value. Default is true.
         */
        PUT_IF_NOT_CONTAINED,
    }

    /**
     * Option to specify the {@link GekDataResolver}.
     * Default is {@link GekDataResolver#defaultResolver()}.
     *
     * @param resolver data resolver
     * @return option to specify the {@link GekDataResolver}
     */
    static GekDataOption resolver(GekDataResolver resolver) {
        return Impls.newGekDataOption(Key.RESOLVER, resolver);
    }

    /**
     * Option to specify the {@link GekConverter}.
     * Default is {@link GekConverter#defaultConverter()}.
     *
     * @param converter data converter
     * @return option to specify the {@link GekConverter}
     */
    static GekDataOption converter(GekConverter converter) {
        return Impls.newGekDataOption(Key.CONVERTER, converter);
    }

    /**
     * Option to specify ignored properties to copy.
     *
     * @param ignoreProperties ignored properties which don't need copy
     * @return option to specify ignored properties to copy
     */
    static GekDataOption ignoredProperties(Object... ignoreProperties) {
        return Impls.newGekDataOption(Key.IGNORED_PROPERTIES, ignoreProperties);
    }

    /**
     * Option to specify ignored properties.
     *
     * @param ignoreProperties ignored properties which don't need copy
     * @return option to specify ignored properties
     */
    static GekDataOption ignoredProperties(Iterable<?> ignoreProperties) {
        return Impls.newGekDataOption(Key.IGNORED_PROPERTIES, ignoreProperties);
    }

    /**
     * Option to specify whether the null value should be ignored to copy.
     * Default is false.
     *
     * @param ignoreNull whether the null value should be ignored to copy
     * @return option to specify whether the null value should be ignored to copy
     */
    static GekDataOption ignoreNull(boolean ignoreNull) {
        return Impls.newGekDataOption(Key.IGNORE_NULL, ignoreNull);
    }

    /**
     * Option to specify whether a {@link GekConvertException} should be thrown when any part of conversion fails.
     * Default is true, if it is set to false, failed property copy will be ignored.
     *
     * @param thrownIfAnyFails whether a {@link GekConvertException} should be thrown when any part of conversion
     *                         fails
     * @return option to specify whether a {@link GekConvertException} should be thrown when any part of conversion
     * fails
     */
    static GekDataOption thrownIfConversionFails(boolean thrownIfAnyFails) {
        return Impls.newGekDataOption(Key.THROWN_IF_ANY_FAILS, thrownIfAnyFails);
    }

    /**
     * Option to specify whether the value should be copied into the dest map if the dest doesn't contain the key mapped
     * by the value. Default is true.
     *
     * @param putIfNotContained whether the value should be copied into the dest map if the dest doesn't contain the key
     *                          mapped by the value
     * @return option to specify whether the value should be copied into the dest map if the dest doesn't contain the
     * key mapped by the value
     */
    static GekDataOption putIfNotContained(boolean putIfNotContained) {
        return Impls.newGekDataOption(Key.PUT_IF_NOT_CONTAINED, putIfNotContained);
    }
}
