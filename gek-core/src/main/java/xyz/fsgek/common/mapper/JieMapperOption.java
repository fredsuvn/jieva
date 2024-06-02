package xyz.fsgek.common.mapper;

import xyz.fsgek.common.base.GekOption;

/**
 * Options for gek bean operations.
 *
 * @author sunqian
 */
public interface JieMapperOption extends GekOption<JieMapperOption.Key, Object> {

    /**
     * Key of {@link JieMapperOption}.
     */
    enum Key {

        /**
         * To specify the {@link GekBeanProvider}.
         * Default is {@link GekBeanProvider#defaultProvider()}.
         */
        PROVIDER,

        /**
         * To specify the {@link JieMapper}.
         * Default is {@link JieMapper#defaultMapper()}.
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
         * To specify whether a {@link GekBeanCopyException} should be thrown when any part of conversion fails.
         * Default is true, if it is set to false, failed property copy will be ignored.
         */
        THROWN_IF_ANY_FAILS,

        /**
         * To specify whether the value should be copied into the dest map if the dest doesn't contain the key mapped by
         * the value. Default is true.
         */
        PUT_IF_NOT_CONTAINED,
    }
    //
    // /**
    //  * Option to specify the {@link GekBeanProvider}.
    //  * Default is {@link GekBeanProvider#defaultProvider()}.
    //  *
    //  * @param provider bean info provider
    //  * @return option to specify the {@link GekBeanProvider}
    //  */
    // static JieMapperOption provider(GekBeanProvider provider) {
    //     return new OptionImpl(Key.PROVIDER, provider);
    // }
    //
    // /**
    //  * Option to specify the {@link JieMapper}.
    //  * Default is {@link JieMapper#defaultMapper()}.
    //  *
    //  * @param converter data converter
    //  * @return option to specify the {@link JieMapper}
    //  */
    // static JieMapperOption converter(JieMapper converter) {
    //     return new OptionImpl(Key.CONVERTER, converter);
    // }
    //
    // /**
    //  * Option to specify ignored properties to copy.
    //  *
    //  * @param ignoreProperties ignored properties which don't need copy
    //  * @return option to specify ignored properties to copy
    //  */
    // static JieMapperOption ignoredProperties(Object... ignoreProperties) {
    //     return new OptionImpl(Key.IGNORED_PROPERTIES, ignoreProperties);
    // }
    //
    // /**
    //  * Option to specify ignored properties.
    //  *
    //  * @param ignoreProperties ignored properties which don't need copy
    //  * @return option to specify ignored properties
    //  */
    // static JieMapperOption ignoredProperties(Iterable<?> ignoreProperties) {
    //     return new OptionImpl(Key.IGNORED_PROPERTIES, ignoreProperties);
    // }
    //
    // /**
    //  * Option to specify whether the null value should be ignored to copy.
    //  * Default is false.
    //  *
    //  * @param ignoreNull whether the null value should be ignored to copy
    //  * @return option to specify whether the null value should be ignored to copy
    //  */
    // static JieMapperOption ignoreNull(boolean ignoreNull) {
    //     return new OptionImpl(Key.IGNORE_NULL, ignoreNull);
    // }
    //
    // /**
    //  * Option to specify whether a {@link GekConvertException} should be thrown when any part of conversion fails.
    //  * Default is true, if it is set to false, failed property copy will be ignored.
    //  *
    //  * @param thrownIfAnyFails whether a {@link GekConvertException} should be thrown when any part of conversion
    //  *                         fails
    //  * @return option to specify whether a {@link GekConvertException} should be thrown when any part of conversion
    //  * fails
    //  */
    // static JieMapperOption thrownIfConversionFails(boolean thrownIfAnyFails) {
    //     return new OptionImpl(Key.THROWN_IF_ANY_FAILS, thrownIfAnyFails);
    // }
    //
    // /**
    //  * Option to specify whether the value should be copied into the dest map if the dest doesn't contain the key mapped
    //  * by the value. Default is true.
    //  *
    //  * @param putIfNotContained whether the value should be copied into the dest map if the dest doesn't contain the key
    //  *                          mapped by the value
    //  * @return option to specify whether the value should be copied into the dest map if the dest doesn't contain the
    //  * key mapped by the value
    //  */
    // static JieMapperOption putIfNotContained(boolean putIfNotContained) {
    //     return new OptionImpl(Key.PUT_IF_NOT_CONTAINED, putIfNotContained);
    // }
}
