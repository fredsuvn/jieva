package xyz.srclab.common.convert;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsUnsafe;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Converter to convert source object from source type to target type.
 * <p>
 * A {@link FsConverter} consists of a list of {@link Handler}s.
 * More specifically, the converter is composed of a prefix handler (maybe null), a list of common handlers,
 * and a suffix handler (maybe null).
 * <p>
 * The conversion is performed in the order of the prefix handler (if not null), common handlers,
 * and suffix converter (if not null). If any handler returns a non-{@link Fs#CONTINUE} and non-{@link Fs#BREAK} value,
 * that means the conversion is successful and that return value will be returned.
 * <p>
 * If the handler returns {@link Fs#CONTINUE},
 * it means that handler doesn't support current conversion and hands off to next handler;
 * If it returns {@link Fs#BREAK}, means this converter doesn't support current conversion.
 *
 * @author fredsuvn
 * @see Handler
 */
@ThreadSafe
public interface FsConverter {

    /**
     * Returns default converter, of which handlers are:
     * <ul>
     *     <li>Prefix handler: {@link xyz.srclab.common.convert.handlers.ReuseConvertHandler};</li>
     *     <li>Suffix handler: {@link xyz.srclab.common.convert.handlers.BeanConvertHandler};</li>
     *     <li>Common handlers:
     *     <ul>
     *         <li>{@link xyz.srclab.common.convert.handlers.DateConvertHandler};</li>
     *         <li>{@link xyz.srclab.common.convert.handlers.BooleanConvertHandler};</li>
     *         <li>{@link xyz.srclab.common.convert.handlers.NumberConvertHandler};</li>
     *         <li>{@link xyz.srclab.common.convert.handlers.StringConvertHandler};</li>
     *         <li>{@link xyz.srclab.common.convert.handlers.CollectConvertHandler};</li>
     *     </ul>
     *     </li>
     * </ul>
     * Those are constructed from empty constructor and in the order listed.
     */
    static FsConverter defaultConverter() {
        return FsUnsafe.ForConvert.DEFAULT_CONVERTER;
    }

    /**
     * Returns default conversion options with:
     * <ul>
     *     <li>
     *         Compatibility policy: {@link Options#REUSE_ASSIGNABLE};
     *     </li>
     * </ul>
     */
    static Options defaultOptions() {
        return FsUnsafe.ForConvert.DEFAULT_OPTIONS;
    }

    /**
     * Returns new converters with given prefix, suffix, common handlers and conversion options.
     *
     * @param prefix         prefix handler
     * @param suffix         suffix handler
     * @param commonHandlers common handlers
     * @param options        conversion options
     */
    static FsConverter newConverter(
        @Nullable Handler prefix,
        @Nullable Handler suffix,
        Iterable<Handler> commonHandlers,
        Options options
    ) {
        class FsConverterImpl implements FsConverter, Handler {

            private final @Nullable Handler pf;
            private final @Nullable Handler sf;
            private final List<Handler> chs;
            private final Options opts;

            FsConverterImpl(@Nullable Handler pf, @Nullable Handler sf, List<Handler> chs, Options opts) {
                this.pf = pf;
                this.sf = sf;
                this.chs = chs;
                this.opts = opts;
            }

            @Override
            public @Nullable Handler getPrefixHandler() {
                return pf;
            }

            @Override
            public @Nullable Handler getSuffixHandler() {
                return sf;
            }

            @Override
            public List<Handler> getCommonHandlers() {
                return chs;
            }

            @Override
            public Options getOptions() {
                return opts;
            }

            @Override
            public Handler asHandler() {
                return this;
            }

            @Override
            public @Nullable Object convert(
                @Nullable Object source, Type sourceType, Type targetType, Options opts, FsConverter converter) {
                Object value = convertObject(source, sourceType, targetType, opts);
                if (value == Fs.RETURN) {
                    return Fs.BREAK;
                }
                return value;
            }
        }
        return new FsConverterImpl(prefix, suffix, FsCollect.immutableList(commonHandlers), options);
    }

    /**
     * Returns a converters with given common handlers.
     *
     * @param commonHandlers common handlers
     */
    static FsConverter withHandlers(Iterable<Handler> commonHandlers) {
        return withHandlers(null, null, commonHandlers);
    }

    /**
     * Returns a converters with given prefix, suffix, common handlers.
     *
     * @param prefix         prefix handler
     * @param suffix         suffix handler
     * @param commonHandlers common handlers
     */
    static FsConverter withHandlers(
        @Nullable Handler prefix,
        @Nullable Handler suffix,
        Iterable<Handler> commonHandlers
    ) {
        return newConverter(prefix, suffix, commonHandlers, FsConverter.defaultOptions());
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
     * Returns conversion options.
     */
    Options getOptions();

    /**
     * Converts source object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     */
    @Nullable
    default <T> T convert(@Nullable Object source, Class<T> targetType) {
        return convert(source, targetType, getOptions());
    }

    /**
     * Converts source object to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    given options
     */
    @Nullable
    default <T> T convert(@Nullable Object source, Class<T> targetType, Options options) {
        return convertType(source, source == null ? Object.class : source.getClass(), targetType, options);
    }

    /**
     * Converts source object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType type reference of target type
     */
    @Nullable
    default <T> T convert(@Nullable Object source, TypeRef<T> targetType) {
        return convert(source, targetType, getOptions());
    }

    /**
     * Converts source object to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType type reference target type
     * @param options    given options
     */
    @Nullable
    default <T> T convert(@Nullable Object source, TypeRef<T> targetType, Options options) {
        return convertType(source, source == null ? Object.class : source.getClass(), targetType.getType(), options);
    }

    /**
     * Converts source object to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     */
    @Nullable
    default <T> T convert(@Nullable Object source, Type targetType) {
        return convert(source, targetType, getOptions());
    }

    /**
     * Converts source object to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    given options
     */
    @Nullable
    default <T> T convert(@Nullable Object source, Type targetType, Options options) {
        return convertType(source, source == null ? Object.class : source.getClass(), targetType, options);
    }

    /**
     * Converts source object from source type to target type.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     */
    @Nullable
    default <T> T convertType(@Nullable Object source, Type sourceType, Type targetType) {
        return convertType(source, sourceType, targetType, getOptions());
    }

    /**
     * Converts source object from source type to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @param options    given options
     */
    @Nullable
    default <T> T convertType(@Nullable Object source, Type sourceType, Type targetType, Options options) {
        Object value = convertObject(source, sourceType, targetType, options);
        if (value == Fs.RETURN) {
            return null;
        }
        return (T) value;
    }

    /**
     * Converts source object from source type to target type.
     * If return value is {@link Fs#RETURN}, it indicates current conversion is unsupported.
     * Any other return value indicates current conversion is successful and the return value is valid, including null.
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     */
    @Nullable
    default Object convertObject(@Nullable Object source, Type sourceType, Type targetType) {
        return convertObject(source, sourceType, targetType, getOptions());
    }

    /**
     * Converts source object from source type to target type with given options.
     * If return value is {@link Fs#RETURN}, it indicates current conversion is unsupported.
     * Any other return value indicates current conversion is successful and the return value is valid, including null.
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @param options    given options
     */
    @Nullable
    default Object convertObject(@Nullable Object source, Type sourceType, Type targetType, Options options) {
        Handler prefix = getPrefixHandler();
        if (prefix != null) {
            Object value = prefix.convert(source, sourceType, targetType, options, this);
            if (value == Fs.BREAK || value == Fs.RETURN) {
                return Fs.RETURN;
            }
            if (value != Fs.CONTINUE) {
                return value;
            }
        }
        for (Handler commonHandler : getCommonHandlers()) {
            Object value = commonHandler.convert(source, sourceType, targetType, options, this);
            if (value == Fs.BREAK || value == Fs.RETURN) {
                return Fs.RETURN;
            }
            if (value != Fs.CONTINUE) {
                return value;
            }
        }
        Handler suffix = getSuffixHandler();
        if (suffix != null) {
            Object value = suffix.convert(source, sourceType, targetType, options, this);
            if (value == Fs.BREAK || value == Fs.CONTINUE || value == Fs.RETURN) {
                return Fs.RETURN;
            }
            return value;
        }
        return Fs.RETURN;
    }

    /**
     * Converts source object from source type to target type.
     * If the conversion is unsupported, an {@link FsConvertException} will be thrown
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     */
    @Nullable
    default <T> T convertThrow(@Nullable Object source, Type sourceType, Type targetType) {
        return convertThrow(source, sourceType, targetType, getOptions());
    }

    /**
     * Converts source object from source type to target type with given options.
     * If the conversion is unsupported, an {@link FsConvertException} will be thrown
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @param options    given options
     */
    @Nullable
    default <T> T convertThrow(
        @Nullable Object source, Type sourceType, Type targetType, Options options) throws FsConvertException {
        Object value = convertObject(source, sourceType, targetType, options);
        if (value == Fs.RETURN) {
            throw new FsConvertException(sourceType, targetType);
        }
        return (T) value;
    }

    /**
     * Returns a new converter with handlers consist of the handlers from the current converter,
     * but inserts given handler at first index of common handlers.
     *
     * @param handler given handler
     */
    default FsConverter withCommonHandler(Handler handler) {
        List<Handler> common = getCommonHandlers();
        List<Handler> newCommon = new ArrayList<>(common.size() + 1);
        newCommon.add(handler);
        newCommon.addAll(common);
        return withHandlers(getPrefixHandler(), getSuffixHandler(), newCommon);
    }

    /**
     * Returns this converter as handler type.
     */
    Handler asHandler();

    /**
     * Handler of {@link FsConverter}.
     *
     * @author fredsuvn
     * @see FsConverter
     */
    @ThreadSafe
    interface Handler {

        /**
         * Converts source object from source type to target type.
         * <p>
         * If this method returns {@link Fs#CONTINUE},
         * it means that this handler doesn't support current conversion, but it will hand off to next handler.
         * <p>
         * If this method returns {@link Fs#BREAK},
         * it means that this handler doesn't support current conversion,
         * and the converter will break out from the conversion.
         * This indicates that the converter does not support current conversion.
         * <p>
         * Otherwise, any other return value is considered as the final conversion result.
         *
         * @param source     source object
         * @param sourceType source type
         * @param targetType target type
         * @param converter  converter in which this handler is located.
         */
        @Nullable
        Object convert(
            @Nullable Object source, Type sourceType, Type targetType, Options options, FsConverter converter);
    }

    /**
     * Options for conversion.
     */
    interface Options {

        /**
         * Returns options with given object reuse policy.
         *
         * @param reusePolicy given reuse policy, see {@link #reusePolicy()}
         */
        static Options withReusePolicy(int reusePolicy) {
            return new Options() {
                @Override
                public int reusePolicy() {
                    return reusePolicy;
                }
            };
        }

        /**
         * Policy value for object reuse:
         * <li>
         * If and only if target type is assignable from source type, return the source object.
         * </li>
         */
        int REUSE_ASSIGNABLE = 1;

        /**
         * Policy value for object reuse:
         * <li>
         * If and only if target type is equal to source type, return the source object.
         * </li>
         */
        int REUSE_EQUAL = 2;

        /**
         * Policy value for object reuse:
         * <li>
         * Never return the source object if the source object is not immutable.
         * If the source object is immutable, it may still be returned.
         * </li>
         */
        int NO_REUSE = 3;

        /**
         * Returns object reuse policy:
         * <ul>
         *     <li>{@link #REUSE_ASSIGNABLE};</li>
         *     <li>{@link #REUSE_EQUAL};</li>
         *     <li>{@link #NO_REUSE};</li>
         * </ul>
         */
        int reusePolicy();

        /**
         * Returns an Options of which {@link #reusePolicy()} is replaced by given policy.
         *
         * @param reusePolicy given reuse policy
         */
        default Options replaceReusePolicy(int reusePolicy) {
            if (reusePolicy() == reusePolicy) {
                return this;
            }
            return withReusePolicy(reusePolicy);
        }
    }
}
