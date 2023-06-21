package xyz.srclab.common.convert;

import xyz.srclab.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Handler for {@link FsConvert}.
 *
 * @author fredsuvn
 */
public interface FsConvertHandler {

    /**
     * Return value represents current converting is not supported.
     */
    Object NOT_SUPPORTED = "(╯‵□′)╯︵┻━┻";

    /**
     * Convert given object from specified type to target type.
     * If current handler cannot convert or convert failed, it will return {@link #NOT_SUPPORTED},
     * otherwise, it returns converted value.
     * <p>
     * Note given object and the return value may be null.
     *
     * @param obj        given object
     * @param fromType   specified type
     * @param targetType target type
     * @param converter  context converter
     */
    @Nullable
    Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter);
}
