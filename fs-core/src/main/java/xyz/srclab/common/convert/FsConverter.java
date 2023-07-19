package xyz.srclab.common.convert;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Converter to convert object from specified type to target type.
 * <p>
 * A {@link FsConverter} consists of a list of {@link Handler}s.
 * More specifically, the converter is composed of a prefix handler (maybe null), a list of common handlers,
 * and a suffix handler (maybe null).
 * <p>
 * The conversion is performed in the order of the prefix handler (if not null), common handlers,
 * and suffix converter (if not null). If any handler returns a non-{@link #CONTINUE} and non-{@link #BREAK} value,
 * the conversion is successful and that value will be returned.
 * <p>
 * If a handler returns {@link #CONTINUE}, it means that handler doesn't support current conversion and hand off to
 * next handler; If returns {@link #BREAK}, it means this converter doesn't support current conversion.
 *
 * @author fredsuvn
 * @see Handler
 */
public interface FsConverter {

    /**
     * Represents current conversion is not supported by this handler and will be handed off to next handler.
     */
    Object CONTINUE = new Object();

    /**
     * Represents current conversion is not supported by current converter and the conversion will be finished.
     */
    Object BREAK = new Object();

    /**
     * Represents current conversion is not supported by current converter.
     */
    Object UNSUPPORTED = new Object();

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
     * Returns prefix handler.
     */
    @Nullable
    Handler getPrefixHandler();

    /**
     * Returns prefix handler.
     */
    @Nullable
    Handler getSuffixHandler();

    /**
     * Returns common handlers.
     */
    List<Handler> getCommonHandlers();

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
        Object value = convert(obj, obj.getClass(), targetType);
        if (value == UNSUPPORTED) {
            throw new UnsupportedConvertException(obj.getClass(), targetType);
        }
        return (T) value;
    }

    /**
     * Converts given object from specified type to target type.
     * If the converting is unsupported, return {@link #UNSUPPORTED}, any other return value means the conversion is
     * successful.
     *
     * @param obj        given object
     * @param fromType   specified type
     * @param targetType target type
     */
    @Nullable
    default Object convert(@Nullable Object obj, Type fromType, Type targetType) {
        Handler prefix = getPrefixHandler();
        if (prefix != null) {
            Object value = prefix.convert(obj, fromType, targetType, this);
            if (value == BREAK) {
                return UNSUPPORTED;
            }
            if (value != CONTINUE) {
                return value;
            }
        }
        for (Handler commonHandler : getCommonHandlers()) {
            Object value = commonHandler.convert(obj, fromType, targetType, this);
            if (value == BREAK) {
                return UNSUPPORTED;
            }
            if (value != CONTINUE) {
                return value;
            }
        }
        Handler suffix = getSuffixHandler();
        if (suffix != null) {
            Object value = suffix.convert(obj, fromType, targetType, this);
            if (value == BREAK) {
                throw new UnsupportedConvertException(fromType, targetType);
            }
            if (value != CONTINUE) {
                return value;
            }
        }
        return UNSUPPORTED;
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

    /**
     * Handler of {@link FsConvert}.
     *
     * @author fredsuvn
     * @see FsConverter
     */
    interface Handler {

        /**
         * Converts given object from specified type to target type.
         * <p>
         * If this method returns {@link #CONTINUE}, it means that this handler doesn't support current conversion,
         * but it will hand off to next handler.
         * <p>
         * If this method returns {@link #BREAK}, it means that this handler doesn't support current conversion,
         * and the converter will break current conversion. This indicates that the converter does not support current
         * conversion.
         * <p>
         * Otherwise, any other return value are considered as the final conversion result, returned by current
         * converter.
         *
         * @param obj        given object
         * @param fromType   specified type
         * @param targetType target type
         * @param converter  context converter
         */
        @Nullable
        Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter);
    }
}
