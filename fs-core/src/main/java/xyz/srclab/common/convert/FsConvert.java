package xyz.srclab.common.convert;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;

/**
 * Conversion utilities.
 *
 * @author fredsuvn
 * @see FsConverter
 */
public class FsConvert {

    /**
     * Converts given object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>
     * Note the return value itself may also be null.
     * </b>
     *
     * @param obj        given object
     * @param targetType target type
     */
    @Nullable
    public static <T> T convert(@Nullable Object obj, Class<T> targetType) {
        return FsConverter.defaultConverter().convert(obj, targetType);
    }

    /**
     * Converts given object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>
     * Note the return value itself may also be null.
     * </b>
     *
     * @param obj        given object
     * @param targetType ref of target type
     */
    @Nullable
    public static <T> T convert(@Nullable Object obj, TypeRef<T> targetType) {
        return FsConverter.defaultConverter().convert(obj, targetType);
    }

    /**
     * Converts given object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>
     * Note the return value itself may also be null.
     * </b>
     *
     * @param obj        given object
     * @param targetType target type
     */
    @Nullable
    public static <T> T convert(@Nullable Object obj, Type targetType) {
        return FsConverter.defaultConverter().convert(obj, targetType);
    }
}
