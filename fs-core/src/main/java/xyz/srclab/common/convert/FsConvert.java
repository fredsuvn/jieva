package xyz.srclab.common.convert;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;

/**
 * Conversion utilities, using {@link FsConverter#defaultConverter()}.
 *
 * @author fredsuvn
 * @see FsConverter
 */
public class FsConvert {

    /**
     * Converts source object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note the return value itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Class<T> targetType) {
        return FsConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note the return value itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    given options
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Class<T> targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convert(source, targetType, options);
    }

    /**
     * Converts source object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note the return value itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType type reference of target type
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, TypeRef<T> targetType) {
        return FsConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note the return value itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType type reference target type
     * @param options    given options
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, TypeRef<T> targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convert(source, targetType, options);
    }

    /**
     * Converts source object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note the return value itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Type targetType) {
        return FsConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note the return value itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    given options
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Type targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convert(source, targetType, options);
    }

    /**
     * Converts source object from source type to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note the return value itself may also be null.</b>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     */
    @Nullable
    public static <T> T convertByType(@Nullable Object source, Type sourceType, Type targetType) {
        return FsConverter.defaultConverter().convertByType(source, sourceType, targetType);
    }

    /**
     * Converts source object from source type to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note the return value itself may also be null.</b>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @param options    given options
     */
    @Nullable
    public static <T> T convertByType(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convertByType(source, sourceType, targetType, options);
    }

    /**
     * Converts source object from source type to target type.
     * If return value is {@link FsConverter#UNSUPPORTED}, it indicates current conversion is unsupported.
     * Any other return value indicates current conversion is successful and the return value is valid, including null.
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     */
    @Nullable
    public static Object convert(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convert(source, sourceType, targetType, options);
    }

    /**
     * Converts source object from source type to target type.
     * If current conversion is unsupported, an {@link UnsupportedConvertException} will be thrown
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     */
    @Nullable
    public static Object convertOrThrow(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options
    ) throws UnsupportedConvertException {
        return FsConverter.defaultConverter().convertOrThrow(source, sourceType, targetType, options);
    }
}
