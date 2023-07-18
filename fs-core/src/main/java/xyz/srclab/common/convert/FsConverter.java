package xyz.srclab.common.convert;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Converter to convert object from one type to another.
 * <p>
 * A {@link FsConverter} consists of a list of handlers -- {@link FsConvertHandler}.
 * The conversion operation is performed by the handlers one by one in the order of the list,
 * until a non-{@link FsConvertHandler#NOT_SUPPORTED} result is obtained or all the handlers have been exhausted.
 * If any handler returns a non-{@link FsConvertHandler#NOT_SUPPORTED} result, the result will be obtained by converter
 * and remaining handlers will not be called.
 * If all handlers return {@link FsConvertHandler#NOT_SUPPORTED}, it indicates that the operation is
 * not supported by that converter.
 *
 * @author fredsuvn
 */
public interface FsConverter {

    /**
     * Returns a converters with given handlers.
     *
     * @param handlers given handlers
     */
    static FsConverter withHandlers(Iterable<FsConvertHandler> handlers) {
        List<FsConvertHandler> list = Collections.unmodifiableList(
            FsCollect.toCollection(new ArrayList<>(), handlers)
        );
        return () -> list;
    }

    /**
     * Returns convert handlers of this converter.
     */
    List<FsConvertHandler> convertHandlers();

    /**
     * Converts given object to target type.
     * If the converting is unsupported, an {@link UnsupportedConvertException} thrown.
     *
     * @param obj        given object
     * @param targetType target type
     */
    @Nullable
    default <T> T convert(Object obj, Class<T> targetType) throws UnsupportedConvertException {
        return convert(obj, (Type) targetType);
    }

    /**
     * Converts given object to target type.
     * If the converting is unsupported, an {@link UnsupportedConvertException} thrown.
     *
     * @param obj        given object
     * @param targetType target type
     */
    @Nullable
    default <T> T convert(Object obj, TypeRef<T> targetType) throws UnsupportedConvertException {
        return convert(obj, targetType.getType());
    }

    /**
     * Converts given object to target type.
     * If the converting is unsupported, an {@link UnsupportedConvertException} thrown.
     *
     * @param obj        given object
     * @param targetType target type
     */
    @Nullable
    default <T> T convert(Object obj, Type targetType) throws UnsupportedConvertException {
        return convert(obj, obj.getClass(), targetType);
    }

    /**
     * Converts given object from specified type to target type.
     * If the converting is unsupported, an {@link UnsupportedConvertException} thrown.
     *
     * @param obj        given object
     * @param fromType   specified type
     * @param targetType target type
     */
    @Nullable
    default <T> T convert(@Nullable Object obj, Type fromType, Type targetType) throws UnsupportedConvertException {
        for (FsConvertHandler convertHandler : convertHandlers()) {
            Object result = convertHandler.convert(obj, fromType, targetType, this);
            if (result != FsConvertHandler.NOT_SUPPORTED) {
                return (T) result;
            }
        }
        throw new UnsupportedConvertException("Unsupported converting from " + fromType + " to " + targetType + ".");
    }

    /**
     * Returns a new converter with handlers consist of the handlers from the current converter,
     * and given handler added at the head.
     *
     * @param handler given handler
     */
    default FsConverter addHeadHandler(FsConvertHandler handler) {
        List<FsConvertHandler> old = convertHandlers();
        List<FsConvertHandler> handlers = new ArrayList<>(old.size() + 1);
        handlers.add(0, handler);
        return withHandlers(Collections.unmodifiableList(handlers));
    }
}
