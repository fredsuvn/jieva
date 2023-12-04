package xyz.fsgek.common.bean;

import xyz.fsgek.common.convert.GekConvertException;
import xyz.fsgek.common.convert.GekConverter;

/**
 * Copy options for {@link GekBeanCopier#defaultCopier()}.
 * Including keys:
 * <ul>
 *     <li>{@link #KEY_OF_BEAN_RESOLVER};</li>
 *     <li>{@link #KEY_OF_CONVERTER};</li>
 *     <li>{@link #KEY_OF_THROW_IF_CONVERT_FAILED};</li>
 *     <li>{@link #KEY_OF_PUT_IF_NOT_CONTAINED};</li>
 *     <li>{@link #KEY_OF_IGNORE_PROPERTIES};</li>
 *     <li>{@link #KEY_OF_IGNORE_NULL};</li>
 * </ul>
 *
 * @author fredsuvn
 */
public class CopyOptions {

    /**
     * Key of {@link #beanResolver(GekBeanResolver)}.
     */
    public static final int KEY_OF_BEAN_RESOLVER = 1;

    /**
     * Key of {@link #converter(GekConverter)}.
     */
    public static final int KEY_OF_CONVERTER = 2;

    /**
     * Key of {@link #throwIfConversionFailed(boolean)}. Default is true.
     */
    public static final int KEY_OF_THROW_IF_CONVERT_FAILED = 3;

    /**
     * Key of {@link #putIfNotContained(boolean)}. Default is true.
     */
    public static final int KEY_OF_PUT_IF_NOT_CONTAINED = 4;

    /**
     * Key of {@link #ignoreProperties(String...)} and {@link #ignoreProperties(Iterable)}.
     */
    public static final int KEY_OF_IGNORE_PROPERTIES = 5;

    /**
     * Key of {@link #ignoreNull(boolean)}. Default is false.
     */
    public static final int KEY_OF_IGNORE_NULL = 6;

    /**
     * Option to set bean resolver.
     *
     * @param resolver bean resolver
     * @return option to set bean resolver
     */
    public static GekBeanCopier.Option beanResolver(GekBeanResolver resolver) {
        return GekBeanCopier.Option.of(KEY_OF_BEAN_RESOLVER, resolver);
    }

    /**
     * Option to set converter.
     *
     * @param converter converter
     * @return option to set converter
     */
    public static GekBeanCopier.Option converter(GekConverter converter) {
        return GekBeanCopier.Option.of(KEY_OF_CONVERTER, converter);
    }

    /**
     * Option to set whether throw exception if property converting failed.
     *
     * @param throwIfConversionFailed whether throw exception if property converting failed,
     *                                true throw {@link GekConvertException}, false to ignore
     * @return option to whether throw exception if property converting failed
     */
    public static GekBeanCopier.Option throwIfConversionFailed(boolean throwIfConversionFailed) {
        return GekBeanCopier.Option.of(KEY_OF_THROW_IF_CONVERT_FAILED, throwIfConversionFailed);
    }

    /**
     * Option to set whether put value if destination is a map and doesn't contain that property.
     *
     * @param putIfNotContained whether put value if destination is a map and doesn't contain that property
     * @return option to whether put value if destination is a map and doesn't contain that property
     */
    public static GekBeanCopier.Option putIfNotContained(boolean putIfNotContained) {
        return GekBeanCopier.Option.of(KEY_OF_PUT_IF_NOT_CONTAINED, putIfNotContained);
    }

    /**
     * Option to set ignored properties which don't need copy.
     *
     * @param ignoreProperties ignored properties which don't need copy
     * @return option to set ignored properties which don't need copy
     */
    public static GekBeanCopier.Option ignoreProperties(String... ignoreProperties) {
        return GekBeanCopier.Option.of(KEY_OF_IGNORE_PROPERTIES, ignoreProperties);
    }

    /**
     * Option to set ignored properties which don't need copy.
     *
     * @param ignoreProperties ignored properties which don't need copy
     * @return option to set ignored properties which don't need copy
     */
    public static GekBeanCopier.Option ignoreProperties(Iterable<String> ignoreProperties) {
        return GekBeanCopier.Option.of(KEY_OF_IGNORE_PROPERTIES, ignoreProperties);
    }

    /**
     * Option to set whether ignore null value of source.
     *
     * @param ignoreNull whether ignore null value of source
     * @return option set whether ignore null value of source
     */
    public static GekBeanCopier.Option ignoreNull(boolean ignoreNull) {
        return GekBeanCopier.Option.of(KEY_OF_IGNORE_NULL, ignoreNull);
    }
}
