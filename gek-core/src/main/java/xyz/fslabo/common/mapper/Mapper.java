package xyz.fslabo.common.mapper;

import lombok.EqualsAndHashCode;
import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.ThreadSafe;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.GekObject;
import xyz.fsgek.common.mapper.handlers.*;
import xyz.fslabo.common.ref.Val;
import xyz.fslabo.common.reflect.TypeRef;
import xyz.fslabo.common.mapper.handlers.*;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Mapper to map object from source type to target type.
 * <p>
 * A {@link Mapper} consists of a list of {@link Handler}s. In general, the mapper will call
 * {@link Handler#map(Object, Type, Type, Mapper, MapperOption...)} for each handler in
 * {@link Mapper#getHandlers()} sequentially. More detail of mapping process, see
 * {@link Handler#map(Object, Type, Type, Mapper, MapperOption...)}.
 *
 * @author fredsuvn
 * @see Handler#map(Object, Type, Type, Mapper, MapperOption...)
 */
@ThreadSafe
public interface Mapper {

    /**
     * Returns default mapper, of which handlers are:
     * <ul>
     *     <li>{@link ReuseConvertHandler};</li>
     *     <li>{@link EnumConvertHandler};</li>
     *     <li>{@link DateConvertHandler};</li>
     *     <li>{@link BytesConvertHandler};</li>
     *     <li>{@link BooleanConvertHandler};</li>
     *     <li>{@link NumberConvertHandler};</li>
     *     <li>{@link StringConvertHandler};</li>
     *     <li>{@link CollectConvertHandler};</li>
     *     <li>{@link BeanConvertHandler};</li>
     * </ul>
     *
     * @return default converter
     */
    static Mapper defaultMapper() {
        return BeanMapperImpl.DEFAULT_MAPPER;
    }

    /**
     * Returns new mapper with given handlers.
     *
     * @param middleHandlers handlers
     * @return new mapper
     */
    static Mapper withHandlers(Iterable<Handler> middleHandlers) {
        return new BeanMapperImpl(middleHandlers);
    }

    /**
     * Returns actual result from {@link #mapObject(Object, Type, MapperOptions)}.
     * The code is similar to the following:
     * <pre>
     *     if (result == null || result == Flag.UNSUPPORTED) {
     *         return null;
     *     }
     *     if (result instanceof Val) {
     *         return Jie.as(((Val&lt;?&gt;) result).get());
     *     }
     *     return Jie.as(result);
     * </pre>
     *
     * @param result result from {@link #mapObject(Object, Type, MapperOptions)}
     * @param <T>    target type
     * @return the actual result
     */
    @Nullable
    static <T> T resolveResult(Object result) {
        if (result == null || result == Flag.UNSUPPORTED) {
            return null;
        }
        if (result instanceof Val) {
            return Jie.as(((Val<?>) result).get());
        }
        return Jie.as(result);
    }

    /**
     * Maps source object from source type to target.
     * Returns null if mapping is unsupported or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param options    mapping options
     * @param <T>        target type
     * @return converted object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, Class<T> targetType, MapperOption... options) {
        return map(source, (Type) targetType, options);
    }

    /**
     * Maps source object from source type to target type ref.
     * Returns null if mapping is unsupported or the result itself is null.
     *
     * @param source        source object
     * @param targetTypeRef type reference target type
     * @param options       mapping options
     * @param <T>           target type
     * @return converted object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, TypeRef<T> targetTypeRef, MapperOption... options) {
        Object result = mapObject(source, source == null ? Object.class : source.getClass(), targetTypeRef.getType(), options);
        return resolveResult(result);
    }

    /**
     * Maps source object from source type to target type.
     * Returns null if mapping is unsupported or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param options    mapping options
     * @param <T>        target type
     * @return mapping result
     */
    @Nullable
    default <T> T map(@Nullable Object source, Type targetType, MapperOption... options) {
        Object result = mapObject(source, source == null ? Object.class : source.getClass(), targetType, options);
        return resolveResult(result);
    }

    /**
     * Maps source object from source type to target type. The result of this method in 3 types:
     * <ul>
     *     <li>
     *         {@link Flag#UNSUPPORTED}: fails and unsupported to map;
     *     </li>
     *     <li>
     *         {@link Val}: mapping successful, the result is {@link Val#get()};
     *     </li>
     *     <li>
     *         {@code others}: mapping successful, the result is result object, including {@code null};
     *     </li>
     * </ul>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @param options    mapping options
     * @return converted object or null
     */
    @Nullable
    default Object mapObject(@Nullable Object source, Type targetType, MapperOptions options) {
        for (Handler handler : getHandlers()) {
            Object value = handler.map(source, sourceType, targetType, this, options);
            if (value == Flag.CONTINUE) {
                continue;
            }
            if (value == Flag.BREAK) {
                return Flag.UNSUPPORTED;
            }
            if (value instanceof GekObject) {
                return ((GekObject) value).getValue();
            }
            return value;
        }
        return Flag.UNSUPPORTED;
    }

    /**
     * Returns all handlers.
     *
     * @return all handlers
     */
    @Immutable
    List<Handler> getHandlers();

    /**
     * Returns a mapper with handler list consists of given handler as first one, followed by the handler list of
     * current resolver.
     *
     * @param handler given handler
     * @return a mapper with handler list consists of given handler as first one, followed by the handler list of
     * current resolver
     */
     Mapper withFirstHandler(Handler handler);

    /**
     * Returns a mapper with handler list consists of the handler list of current resolver, followed by given handler
     * as last one.
     *
     * @param handler given handler
     * @return a mapper with handler list consists of the handler list of current resolver, followed by given handler
     * as last one
     */
     Mapper withLastHandler(Handler handler) ;

    /**
     * Returns this mapper as {@link Handler}.
     *
     * @return this mapper as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link Mapper}.
     *
     * @author fredsuvn
     * @see Mapper
     */
    @ThreadSafe
    interface Handler {

        /**
         * Maps object from source type to target type.
         * <p>
         * In general, this method of all handlers in {@link Mapper#getHandlers()} will be invoked sequentially.
         * The result of this method in 4 types:
         * <ul>
         *     <li>
         *         {@link Flag#CONTINUE}: fails to map, hands off to next handler;
         *     </li>
         *     <li>
         *         {@link Flag#BREAK}: fails to map, breaks the handler chain;
         *     </li>
         *     <li>
         *         {@link GekObject}: mapping successful, the result is {@link GekObject#getValue()};
         *     </li>
         *     <li>
         *         {@code others}: mapping successful, the result is result object, including {@code null};
         *     </li>
         * </ul>
         *
         * @param source     source object
         * @param sourceType source type
         * @param targetType target type
         * @param mapper     mapper of current context.
         * @param options    mapping options
         * @return converted object or null
         */
        @Nullable
        Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MapperOption... options);
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
