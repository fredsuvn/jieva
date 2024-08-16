package xyz.fslabo.common.mapper;

import xyz.fslabo.annotations.Immutable;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.ThreadSafe;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.mapper.handlers.*;
import xyz.fslabo.common.ref.Val;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Mapper interface to map object from source type to target type. A {@link Mapper} typically has a list of
 * {@link Handler}s, and in default implementation, the {@link Handler}s provide actual map operation for core methods
 * {@link #map(Object, Type, Type, MappingOptions)} and
 * {@link #mapProperty(Object, Type, Type, PropertyInfo, MappingOptions)}.
 *
 * @author fredsuvn
 * @see Handler#map(Object, Type, Type, Mapper, MappingOptions)
 * @see Handler#mapProperty(Object, Type, Type, PropertyInfo, Mapper, MappingOptions)
 */
@ThreadSafe
public interface Mapper {

    /**
     * Returns default mapper, of which handlers are:
     * <ul>
     *     <li>{@link AssignableMapperHandler};</li>
     *     <li>{@link EnumMapperHandler};</li>
     *     <li>{@link TypedMapperHandler};</li>
     *     <li>{@link CollectionMappingHandler};</li>
     *     <li>{@link BeanMapperHandler};</li>
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
     * Returns actual result from {@link #map(Object, Type, Type, MappingOptions)}.
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
     * @param result result from {@link #map(Object, Type, Type, MappingOptions)}
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
     * Maps source object from source type to target, returns null if mapping failed or the result itself is null.
     * This method is equivalent to:
     * <pre>
     *     return map(source, (Type) targetType, options);
     * </pre>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    mapping options
     * @param <T>        target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, Class<T> targetType, MappingOptions options) {
        return map(source, (Type) targetType, options);
    }

    /**
     * Maps source object from source type to target type ref, returns null if mapping failed or the result itself is null.
     * This method is equivalent to:
     * <pre>
     *     Object result = map(source, source == null ? Object.class : source.getClass(), targetTypeRef.getType(), options);
     *     return resolveResult(result);
     * </pre>
     *
     * @param source        source object
     * @param targetTypeRef type reference target type
     * @param options       mapping options
     * @param <T>           target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, TypeRef<T> targetTypeRef, MappingOptions options) {
        Object result = map(source, source == null ? Object.class : source.getClass(), targetTypeRef.getType(), options);
        return resolveResult(result);
    }

    /**
     * Maps source object from source type to target type, returns null if mapping failed or the result itself is null.
     * This method is equivalent to:
     * <pre>
     *     Object result = map(source, source == null ? Object.class : source.getClass(), targetType, options);
     *     return resolveResult(result);
     * </pre>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    mapping options
     * @param <T>        target type
     * @return mapped object or null
     */
    @Nullable
    default <T> T map(@Nullable Object source, Type targetType, MappingOptions options) {
        Object result = map(source, source == null ? Object.class : source.getClass(), targetType, options);
        return resolveResult(result);
    }

    /**
     * Maps source object from source type to target type.
     * The result of this method in 3 types:
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
     * In the default implementation, this method will invoke
     * {@link Handler#map(Object, Type, Type, Mapper, MappingOptions)} for each handler in {@link Mapper#getHandlers()}
     * sequentially. It is equivalent to:
     * <pre>
     *     for (Handler handler : getHandlers()) {
     *         Object value = handler.map(source, sourceType, targetType, this, options);
     *         if (value == Flag.CONTINUE) {
     *             continue;
     *         }
     *         if (value == Flag.BREAK) {
     *             return null;
     *         }
     *         if (value instanceof Val) {
     *             return ((Val<?>) value).get();
     *         }
     *         return value;
     *     }
     *     return null;
     * </pre>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @param options    mapping options
     * @return mapped object or null
     */
    @Nullable
    default Object map(@Nullable Object source, Type sourceType, Type targetType, MappingOptions options) {
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
     * Maps source object from source type to target property. The target type is specified in current context, may not
     * equal to {@link PropertyInfo#getType()} of target property.
     * The result of this method in 3 types:
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
     * In the default implementation, this method will invoke
     * {@link Handler#mapProperty(Object, Type, Type, PropertyInfo, Mapper, MappingOptions)} for each handler in
     * {@link Mapper#getHandlers()} sequentially. It is equivalent to:
     * <pre>
     *     for (Handler handler : getHandlers()) {
     *         Object value = handler.mapProperty(source, sourceType, targetType, this, options);
     *         if (value == Flag.CONTINUE) {
     *             continue;
     *         }
     *         if (value == Flag.BREAK) {
     *             return null;
     *         }
     *         if (value instanceof Val) {
     *             return ((Val<?>) value).get();
     *         }
     *         return value;
     *     }
     *     return null;
     * </pre>
     *
     * @param source         source object
     * @param sourceType     source type
     * @param targetProperty target property
     * @param targetType     target type
     * @param options        mapping options
     * @return mapped object or null
     */
    @Nullable
    default Object mapProperty(
        @Nullable Object source,
        Type sourceType,
        Type targetType,
        PropertyInfo targetProperty,
        MappingOptions options
    ) {
        for (Handler handler : getHandlers()) {
            Object value = handler.mapProperty(source, sourceType, targetType, targetProperty, this, options);
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
     * Handler of {@link Mapper} to provide map operation.
     * <p>
     * This interface also provides a default util method {@link #wrapResult(Object)}.
     *
     * @author fredsuvn
     * @see Mapper
     */
    @ThreadSafe
    interface Handler {

        /**
         * Maps object from source type to target type.
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
        Object map(
            @Nullable Object source,
            Type sourceType,
            Type targetType,
            Mapper mapper,
            MappingOptions options
        );

        /**
         * Maps object from source type to the target type of target property. The target type is specified in current
         * context, may not equal to {@link PropertyInfo#getType()} of target property.
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
         * @param source         source object
         * @param sourceType     source type
         * @param targetType     target type
         * @param targetProperty target property
         * @param mapper         mapper of current context.
         * @param options        mapping options
         * @return converted object
         */
        Object mapProperty(
            @Nullable Object source,
            Type sourceType,
            Type targetType,
            PropertyInfo targetProperty,
            Mapper mapper,
            MappingOptions options
        );


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
