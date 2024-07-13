package xyz.fslabo.common.mapper;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.ThreadSafe;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.mapper.handlers.*;
import xyz.fslabo.common.ref.Val;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Mapper interface to map object from source type to target type.
 * <p>
 * A {@link Mapper} consists of a list of {@link Handler}s. In general, the mapper will call
 * {@link Handler#map(Object, Type, Type, Mapper, MapperOptions)} for each handler in {@link Mapper#getHandlers()}
 * sequentially. More detail of mapping process, see {@link Handler#map(Object, Type, Type, Mapper, MapperOptions)}.
 *
 * @author fredsuvn
 * @see Handler#map(Object, Type, Type, Mapper, MapperOptions)
 */
@ThreadSafe
public interface Mapper {

    /**
     * Returns default mapper, of which handlers are:
     * <ul>
     *     <li>{@link AssignableMapperHandler};</li>
     *     <li>{@link EnumConvertHandler};</li>
     *     <li>{@link DateConvertHandler};</li>
     *     <li>{@link BytesConvertHandler};</li>
     *     <li>{@link BooleanConvertHandler};</li>
     *     <li>{@link NumberMapperHandler};</li>
     *     <li>{@link StringMapperHandler};</li>
     *     <li>{@link CollectConvertHandler};</li>
     *     <li>{@link BeanConvertHandler};</li>
     * </ul>
     *
     * @return default converter
     */
    static Mapper defaultMapper() {
        return MapperImpl.DEFAULT_MAPPER;
    }

    /**
     * Returns new mapper with given handlers.
     *
     * @param middleHandlers handlers
     * @return new mapper
     */
    static Mapper withHandlers(Iterable<Handler> middleHandlers) {
        return new MapperImpl(middleHandlers);
    }

    /**
     * Returns actual result from {@link #mapObject(Object, Type, Type, MapperOptions)}.
     * The code is similar to the following:
     * <pre>
     *     if (result == null) {
     *         return null;
     *     }
     *     if (result instanceof Val) {
     *         return Jie.as(((Val&lt;?&gt;) result).get());
     *     }
     *     return Jie.as(result);
     * </pre>
     *
     * @param result result from {@link #mapObject(Object, Type, Type, MapperOptions)}
     * @param <T>    target type
     * @return the actual result
     */
    @Nullable
    static <T> T resolveResult(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof Val) {
            return Jie.as(((Val<?>) result).get());
        }
        return Jie.as(result);
    }

    /**
     * Maps source object from source type to target.
     * Returns null if mapping failed or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param options    mapping options
     * @param <T>        target type
     * @return converted object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, Class<T> targetType, MapperOptions options) {
        return map(source, (Type) targetType, options);
    }

    /**
     * Maps source object from source type to target type ref.
     * Returns null if mapping failed or the result itself is null.
     *
     * @param source        source object
     * @param targetTypeRef type reference target type
     * @param options       mapping options
     * @param <T>           target type
     * @return converted object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, TypeRef<T> targetTypeRef, MapperOptions options) {
        Object result = mapObject(source, source == null ? Object.class : source.getClass(), targetTypeRef.getType(), options);
        return resolveResult(result);
    }

    /**
     * Maps source object from source type to target type.
     * Returns null if mapping failed or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param options    mapping options
     * @param <T>        target type
     * @return mapping result
     */
    @Nullable
    default <T> T map(@Nullable Object source, Type targetType, MapperOptions options) {
        Object result = mapObject(source, source == null ? Object.class : source.getClass(), targetType, options);
        return resolveResult(result);
    }

    /**
     * Maps source object from source type to target type. The result of this method in 3 types:
     * <ul>
     *     <li>
     *         {@code null}: mapping failed;
     *     </li>
     *     <li>
     *         {@link Val}: mapping successful, the result is {@link Val#get()};
     *     </li>
     *     <li>
     *         {@code others}: mapping successful, the result is returned object;
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
    default Object mapObject(@Nullable Object source, Type sourceType, Type targetType, MapperOptions options) {
        for (Handler handler : getHandlers()) {
            Object value = handler.map(source, sourceType, targetType, this, options);
            if (value == Flag.CONTINUE) {
                continue;
            }
            if (value == Flag.BREAK) {
                return null;
            }
            if (value instanceof Val) {
                return ((Val<?>) value).get();
            }
            return value;
        }
        return null;
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
    Mapper withLastHandler(Handler handler);

    /**
     * Returns this mapper as {@link Handler}.
     *
     * @return this mapper as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link Mapper}, provides a default util method {@link #wrapResult(Object)}.
     *
     * @author fredsuvn
     * @see Mapper
     */
    @ThreadSafe
    interface Handler {

        /**
         * Maps object from source type to target type.
         * <p>
         * In general, all handlers in {@link Mapper#getHandlers()} will be invoked sequentially.
         * The result of this method in 4 types:
         * <ul>
         *     <li>
         *         {@link Flag#CONTINUE}: mapping failed, hands off to next handler;
         *     </li>
         *     <li>
         *         {@link Flag#BREAK}: mapping failed, breaks the handler chain;
         *     </li>
         *     <li>
         *         {@link Val}: mapping successful, the result is {@link Val#get()};
         *     </li>
         *     <li>
         *         {@code others}: mapping successful, the result is returned object;
         *     </li>
         * </ul>
         *
         * @param source     source object
         * @param sourceType source type
         * @param targetType target type
         * @param mapper     mapper of current context.
         * @param options    mapping options
         * @return converted object
         */
        Object map(@Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MapperOptions options);


        /**
         * This method is used to help wrap the actual result of this handler. The code is similar to the following:
         * <pre>
         *     if (result == null) {
         *         return Val.ofNull();
         *     }
         *     if ((result instanceof Flag) || (result instanceof Val)) {
         *         return Val.of(result);
         *     }
         *     return result;
         * </pre>
         *
         * @param result actual result of this handler
         * @return wrapped result
         */
        default Object wrapResult(@Nullable Object result) {
            if (result == null) {
                return Val.ofNull();
            }
            if ((result instanceof Flag) || (result instanceof Val)) {
                return Val.of(result);
            }
            return result;
        }
    }
}
