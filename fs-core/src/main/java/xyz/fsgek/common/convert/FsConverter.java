package xyz.fsgek.common.convert;

import lombok.EqualsAndHashCode;
import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.Fs;
import xyz.fsgek.common.base.FsFlag;
import xyz.fsgek.common.base.FsWrapper;
import xyz.fsgek.common.collect.FsCollect;
import xyz.fsgek.common.convert.handlers.*;
import xyz.fsgek.common.reflect.TypeRef;

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
 *     prefix handler (if not null) -&gt; middle handlers -&gt; suffix converter (if not null)
 * </pre>
 * More detail of conversion process, see {@link Handler#convert(Object, Type, Type, FsConverter)}.
 *
 * @author fredsuvn
 * @see Handler#convert(Object, Type, Type, FsConverter)
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
     *
     * @return default converter
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
     *
     * @return default conversion options
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
     * @return new converters
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
     *
     * @return new options builder
     */
    static Options.Builder optionsBuilder() {
        return new Options.Builder();
    }

    /**
     * Returns actual result of conversion from {@link #convertType(Object, Type, Type)}.
     *
     * @param result result from {@link #convertType(Object, Type, Type)}
     * @param <T>    target type
     * @return actual result of conversion
     */
    static <T> T getResult(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof FsWrapper) {
            return Fs.as(((FsWrapper<?>) result).get());
        }
        return Fs.as(result);
    }

    /**
     * Returns prefix handler.
     *
     * @return prefix handler
     */
    @Nullable
    Handler getPrefixHandler();

    /**
     * Returns suffix handler.
     *
     * @return suffix handler
     */
    @Nullable
    Handler getSuffixHandler();

    /**
     * Returns middles handlers.
     *
     * @return middles handlers
     */
    @Immutable
    List<Handler> getMiddleHandlers();

    /**
     * Returns all handlers in order by: prefix handler -&gt; middle handlers -&gt; suffix handler.
     *
     * @return all handlers
     */
    @Immutable
    List<Handler> getHandlers();

    /**
     * Returns conversion options.
     *
     * @return conversion options
     */
    Options getOptions();

    /**
     * Converts source object from source type to target type,
     * return null if conversion is unsupported or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     */
    @Nullable
    default <T> T convert(@Nullable Object source, Class<T> targetType) {
        return getResult(
            convertType(source, source == null ? Object.class : source.getClass(), targetType));
    }

    /**
     * Converts source object from source type to target type,
     * return null if conversion is unsupported or the result itself is null.
     *
     * @param source        source object
     * @param targetTypeRef type reference target type
     * @param <T>           target type
     * @return converted object or null
     */
    @Nullable
    default <T> T convert(@Nullable Object source, TypeRef<T> targetTypeRef) {
        return getResult(
            convertType(source, source == null ? Object.class : source.getClass(), targetTypeRef.getType()));
    }

    /**
     * Converts source object from source type to target type,
     * return null if conversion is unsupported or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     */
    @Nullable
    default <T> T convert(@Nullable Object source, Type targetType) {
        return getResult(
            convertType(source, source == null ? Object.class : source.getClass(), targetType));
    }

    /**
     * Converts source object from source type to target type.
     * The result of this method in 3 types: {@code null}, {@link FsWrapper} and others:
     * <ul>
     *     <li>
     *         {@code null}: means this converter can not do this conversion;
     *     </li>
     *     <li>
     *         {@link FsWrapper}: wrapped object (from {@link FsWrapper#get()}) is actual result of conversion,
     *         including {@code null} and {@link FsWrapper} itself;
     *     </li>
     *     <li>
     *         Others: any other type of returned object is the actual result of conversion.
     *     </li>
     * </ul>
     * Using {@link #getResult(Object)} can get actual result object from wrapper.
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @return converted object or null
     */
    @Nullable
    default Object convertType(@Nullable Object source, Type sourceType, Type targetType) {
        for (Handler handler : getHandlers()) {
            Object value = handler.convert(source, sourceType, targetType, this);
            if (value == null || value == FsFlag.CONTINUE) {
                continue;
            }
            if (value == FsFlag.BREAK || value == FsFlag.RETURN) {
                return null;
            }
            if (value == FsFlag.NULL) {
                return FsWrapper.empty();
            }
            return value;
        }
        return null;
    }

    /**
     * Returns a converter of which handlers and options come from current converter,
     * but the prefix handler is replaced by given handler.
     *
     * @param handler given handler
     * @return the converter
     */
    default FsConverter withPrefixHandler(Handler handler) {
        return newConverter(handler, getSuffixHandler(), getMiddleHandlers(), getOptions());
    }

    /**
     * Returns a converter of which handlers and options come from current converter,
     * but the suffix handler is replaced by given handler.
     *
     * @param handler given handler
     * @return the converter
     */
    default FsConverter withSuffixHandler(Handler handler) {
        return newConverter(getPrefixHandler(), handler, getMiddleHandlers(), getOptions());
    }

    /**
     * Returns a converter of which handlers and options come from current converter,
     * but inserts given handler at first index of middle handlers.
     *
     * @param handler given handler
     * @return the converter
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
     * @return the converter
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
     * @return the converter
     */
    default FsConverter withOptions(Options options) {
        if (Objects.equals(getOptions(), options)) {
            return this;
        }
        return newConverter(getPrefixHandler(), getSuffixHandler(), getMiddleHandlers(), options);
    }

    /**
     * Returns this converter as {@link Handler}.
     * For method {@link Handler#convert(Object, Type, Type, FsConverter)} of result handler, the 4th parameter
     * {@link FsConverter} can be passed as {@code null}, in which case the 4th parameter will be auto passed with
     * "this" -- the current converter itself.
     *
     * @return this converter as {@link Handler}
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
         * In general, all handlers in the converter ({@link #getHandlers()}) will be invoked this method sequentially.
         * The result of this method in 4 types: {@code null}, {@link FsFlag}, {@link FsWrapper} and others.
         * <p>
         * If this method returns {@code null}, means this handler can not do this conversion, and it will hand off to
         * next handler; If this method returns an instance of {@link FsFlag}, means:
         * <ul>
         *     <li>
         *         {@link FsFlag#CONTINUE}: same as {@code null},
         *         means that this handler can not do this conversion, and it will hand off to next handler;
         *     </li>
         *     <li>
         *         {@link FsFlag#BREAK} or {@link FsFlag#RETURN}:
         *         means that this handler can not do the conversion, and it will break out the handler chain.
         *         This will cause the conversion failure in this converter;
         *     </li>
         *     <li>
         *         {@link FsFlag#NULL} or {@link FsWrapper#empty()}:
         *         means the result of this conversion is clearly {@code null};
         *     </li>
         *     <li>
         *         Other instance of {@link FsFlag} is a wrong result, will throw a {@link FsConvertException}.
         *     </li>
         * </ul>
         * If this method returns a {@link FsWrapper}, wrapped object (from {@link FsWrapper#get()}) is actual result
         * of conversion, including {@link FsFlag}, {@code null} and {@link FsWrapper} itself.
         * Any other type of returned object is the actual result of conversion.
         *
         * @param source     source object
         * @param sourceType source type
         * @param targetType target type
         * @param converter  converter to help convert unsupported part of object for this handler.
         * @return converted object or null
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
         * <ul>
         *     <li>
         *         If and only if target type is assignable from source type, return the source object.
         *     </li>
         * </ul>
         */
        int REUSE_ASSIGNABLE = 1;
        /**
         * Policy value for object reuse:
         * <ul>
         *     <li>
         *         If and only if target type is equal to source type, return the source object.
         *     </li>
         * </ul>
         */
        int REUSE_EQUAL = 2;
        /**
         * Policy value for object reuse:
         * <ul>
         *     <li>
         *         Never return the source object if the source object is not immutable.
         *         If the source object is immutable, it may still be returned.
         *     </li>
         * </ul>
         */
        int NO_REUSE = 3;

        /**
         * Returns options with given object reuse policy.
         *
         * @param reusePolicy given reuse policy, see {@link #reusePolicy()}
         * @return options with given object reuse policy
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
         *
         * @return object reuse policy
         */
        int reusePolicy();

        /**
         * Returns an Options of which {@link #reusePolicy()} is replaced by given policy.
         *
         * @param reusePolicy given reuse policy
         * @return an Options of which {@link #reusePolicy()} is replaced by given policy
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
             *
             * @param reusePolicy reuse policy
             * @return this builder
             */
            public Builder reusePolicy(int reusePolicy) {
                this.reusePolicy = reusePolicy;
                return this;
            }

            /**
             * Builds the options
             *
             * @return built options
             */
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
