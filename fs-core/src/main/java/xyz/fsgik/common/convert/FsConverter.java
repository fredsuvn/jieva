package xyz.fsgik.common.convert;

import lombok.EqualsAndHashCode;
import xyz.fsgik.annotations.Immutable;
import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;
import xyz.fsgik.common.base.Fs;
import xyz.fsgik.common.collect.FsCollect;
import xyz.fsgik.common.convert.handlers.*;
import xyz.fsgik.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Converter to convert object from source type to target type.
 * <p>
 * A {@link FsConverter} consists of a list of {@link Handler}s and a set of {@link Options}.
 * The handlers are composed of a prefix handler (maybe null), a list of middle handlers,
 * and a suffix handler (maybe null).
 * <p>
 * The conversion is performed in the order of
 * <pre>
 *     prefix handler (if not null) -> middle handlers -> suffix converter (if not null)
 * </pre>
 * If any handler returns a non-{@link Fs#CONTINUE} and non-{@link Fs#BREAK} value,
 * that means the conversion is successful and that return value will be returned.
 * <p>
 * If a handler returns {@link Fs#CONTINUE},
 * it means that handler doesn't support current conversion and hands off to next handler;
 * If the last handler (maybe suffix handler), or any other handler returns {@link Fs#BREAK},
 * current conversion will be broken and failed.
 *
 * @author fredsuvn
 * @see Handler
 */
@ThreadSafe
public interface FsConverter {

    /**
     * Returns default converter, of which handlers are:
     * <ul>
     *     <li>Prefix handler: {@link ReuseConvertHandler};</li>
     *     <li>Suffix handler: {@link BeanConvertHandler};</li>
     *     <li>Common handlers:
     *     <ul>
     *         <li>{@link EnumConvertHandler};</li>
     *         <li>{@link DateConvertHandler};</li>
     *         <li>{@link BytesConvertHandler};</li>
     *         <li>{@link BooleanConvertHandler};</li>
     *         <li>{@link NumberConvertHandler};</li>
     *         <li>{@link StringConvertHandler};</li>
     *         <li>{@link CollectConvertHandler};</li>
     *     </ul>
     *     </li>
     * </ul>
     * Those are constructed from empty constructor and in the order listed.
     */
    static FsConverter defaultConverter() {
        return ConverterImpl.INSTANCE;
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
        return Options.Builder.DEFAULT;
    }

    /**
     * Returns new converters with given prefix handler, suffix handler, middle handlers and conversion options.
     *
     * @param prefixHandler  prefix handler
     * @param suffixHandler  suffix handler
     * @param middleHandlers common handlers
     * @param options        conversion options
     */
    static FsConverter newConverter(
        @Nullable Handler prefixHandler,
        @Nullable Handler suffixHandler,
        Iterable<Handler> middleHandlers,
        Options options
    ) {
        return new ConverterImpl(prefixHandler, suffixHandler, middleHandlers, options);
    }

    /**
     * Returns new options builder.
     */
    static Options.Builder optionsBuilder() {
        return new Options.Builder();
    }

    /**
     * Returns prefix handler.
     */
    @Nullable
    Handler getPrefixHandler();

    /**
     * Returns suffix handler.
     */
    @Nullable
    Handler getSuffixHandler();

    /**
     * Returns middles handlers.
     */
    @Immutable
    List<Handler> getMiddleHandlers();

    /**
     * Returns all handlers in order by: prefix handler -> middle handlers -> suffix handler.
     */
    @Immutable
    List<Handler> getHandlers();

    /**
     * Returns conversion options.
     */
    Options getOptions();

    /**
     * Converts source object to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     */
    @Nullable
    default <T> T convert(@Nullable Object source, Class<T> targetType) {
        return convertType(source, source == null ? Object.class : source.getClass(), targetType);
    }

    /**
     * Converts source object to target type with given options.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source        source object
     * @param targetTypeRef type reference target type
     */
    @Nullable
    default <T> T convert(@Nullable Object source, TypeRef<T> targetTypeRef) {
        return convertType(source, source == null ? Object.class : source.getClass(), targetTypeRef.getType());
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
        return convertType(source, source == null ? Object.class : source.getClass(), targetType);
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
        Object value = convertObject(source, sourceType, targetType);
        if (value == Fs.RETURN) {
            return null;
        }
        return (T) value;
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
        Object value = convertObject(source, sourceType, targetType);
        if (value == Fs.RETURN) {
            throw new FsConvertException(sourceType, targetType);
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
        for (Handler handler : getHandlers()) {
            Object value = handler.convert(source, sourceType, targetType, this);
            if (value == Fs.BREAK) {
                return Fs.RETURN;
            }
            if (value != Fs.CONTINUE) {
                return value;
            }
        }
        return Fs.RETURN;
    }

    /**
     * Returns a converter of which handlers and options come from current converter,
     * but the prefix handler is replaced by given handler.
     *
     * @param handler given handler
     */
    default FsConverter withPrefixHandler(Handler handler) {
        return newConverter(handler, getSuffixHandler(), getMiddleHandlers(), getOptions());
    }

    /**
     * Returns a converter of which handlers and options come from current converter,
     * but the suffix handler is replaced by given handler.
     *
     * @param handler given handler
     */
    default FsConverter withSuffixHandler(Handler handler) {
        return newConverter(getPrefixHandler(), handler, getMiddleHandlers(), getOptions());
    }

    /**
     * Returns a converter of which handlers and options come from current converter,
     * but inserts given handler at first index of middle handlers.
     *
     * @param handler given handler
     */
    default FsConverter insertFirstMiddleHandler(Handler handler) {
        return insertMiddleHandler(0, handler);
    }

    /**
     * Returns a converter of which handlers and options come from current converter,
     * but inserts given handler at specified index of middle handlers.
     *
     * @param index   specified index
     * @param handler given handler
     */
    default FsConverter insertMiddleHandler(int index, Handler handler) {
        List<Handler> middleHandlers = getMiddleHandlers();
        if (FsCollect.isEmpty(middleHandlers)) {
            return newConverter(getPrefixHandler(), getSuffixHandler(), Collections.singleton(handler), getOptions());
        }
        List<Handler> newMiddleHandlers = new ArrayList<>(middleHandlers.size() + 1);
        newMiddleHandlers.addAll(middleHandlers);
        newMiddleHandlers.add(index, handler);
        return newConverter(getPrefixHandler(), getSuffixHandler(), newMiddleHandlers, getOptions());
    }

    /**
     * Returns a converter of which handlers come from current converter,
     * but the options will be replaced by given options.
     *
     * @param options given options
     */
    default FsConverter withOptions(Options options) {
        if (Objects.equals(getOptions(), options)) {
            return this;
        }
        return newConverter(getPrefixHandler(), getSuffixHandler(), getMiddleHandlers(), options);
    }

    /**
     * Returns this converter as a {@link Handler}.
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
         * Converts object from source type to target type.
         * <p>
         * If this method returns {@link Fs#CONTINUE},
         * it means that this handler doesn't support current conversion, but it will hand off to next handler.
         * <p>
         * If this method returns {@link Fs#BREAK},
         * it means that this handler doesn't support current conversion, and it will break out the handler chain.
         * This will cause the conversion failure.
         * <p>
         * Otherwise, other return value is the valid final conversion result.
         *
         * @param source     source object
         * @param sourceType source type
         * @param targetType target type
         * @param converter  converter to help convert unsupported part of object for this handler.
         */
        @Nullable
        Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter);
    }

    /**
     * Options for conversion.
     */
    interface Options {

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

        /**
         * Builder for {@link Options}.
         */
        class Builder {

            private static final Options DEFAULT = new Builder().build();

            private int reusePolicy = REUSE_ASSIGNABLE;

            /**
             * Sets object reuse policy:
             * <ul>
             *     <li>{@link #REUSE_ASSIGNABLE} (default);</li>
             *     <li>{@link #REUSE_EQUAL};</li>
             *     <li>{@link #NO_REUSE};</li>
             * </ul>
             */
            public Builder reusePolicy(int reusePolicy) {
                this.reusePolicy = reusePolicy;
                return this;
            }

            public Options build() {
                return new OptionImpl(reusePolicy);
            }

            @EqualsAndHashCode
            private static final class OptionImpl implements Options {

                private final int reusePolicy;

                private OptionImpl(int reusePolicy) {
                    this.reusePolicy = reusePolicy;
                }

                @Override
                public int reusePolicy() {
                    return reusePolicy;
                }
            }
        }
    }
}
