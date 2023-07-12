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
     * If current handler doesn't support this converting, return {@link #NOT_SUPPORTED}.
     * <p>
     * <b>NOTE:</b>
     * <ul>
     *     <li>
     *         Given object may be null;
     *     </li>
     *     <li>
     *         Converting result may be null;
     *     </li>
     * </ul>
     *
     * @param obj        given object
     * @param fromType   specified type
     * @param targetType target type
     * @param converter  context converter
     */
    @Nullable
    Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter);
}
