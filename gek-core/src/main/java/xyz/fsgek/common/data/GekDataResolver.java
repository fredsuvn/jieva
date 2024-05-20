package xyz.fsgek.common.data;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.GekFlag;
import xyz.fsgek.common.cache.GekCache;
import xyz.fsgek.common.collect.GekArray;
import xyz.fsgek.common.data.handlers.AbstractBeanResolveHandler;
import xyz.fsgek.common.data.handlers.JavaBeanResolveHandler;
import xyz.fsgek.common.data.handlers.RecordBeanResolveHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Data object resolver for {@link GekDataDescriptor}, usually consists of a {@link Handler} list.
 * There are 2 built-in resolvers:
 * <ul>
 *     <li>{@link JavaBeanResolveHandler} (default handler);</li>
 *     <li>{@link RecordBeanResolveHandler};</li>
 * </ul>
 * And a skeletal implementation: {@link AbstractBeanResolveHandler}.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekDataResolver {

    /**
     * Returns default data object resolver of which handler is {@link JavaBeanResolveHandler}.
     *
     * @return default data object resolver
     */
    static GekDataResolver defaultResolver() {
        return Impls.DEFAULT_RESOLVER;
    }

    /**
     * Returns new data object resolver with given handlers and {@link GekCache#softCache()}.
     * This method is equivalent to {@link #newResolver(Iterable, GekCache)}:
     * <pre>
     *     return newResolver(handlers, GekCache.softCache());
     * </pre>
     *
     * @param handlers given handlers
     * @return new data object resolver
     */
    static GekDataResolver newResolver(Handler... handlers) {
        return newResolver(GekArray.asList(handlers));
    }

    /**
     * Returns new data object resolver with given handlers and {@link GekCache#softCache()}.
     * This method is equivalent to {@link #newResolver(Iterable, GekCache)}:
     * <pre>
     *     return newResolver(handlers, GekCache.softCache());
     * </pre>
     *
     * @param handlers given handlers
     * @return new data object resolver
     */
    static GekDataResolver newResolver(Iterable<Handler> handlers) {
        return newResolver(handlers, GekCache.softCache());
    }

    /**
     * Returns new data object resolver with given handlers and cache.
     * If cache is null, returned resolver will not cache the result.
     *
     * @param handlers given handlers
     * @param cache    given cache
     * @return new data object resolver
     */
    static GekDataResolver newResolver(Iterable<Handler> handlers, @Nullable GekCache<Type, GekDataDescriptor> cache) {
        return Impls.newGekDataResolver(handlers, cache);
    }

    /**
     * Resolves given type to {@link GekDataDescriptor}.
     *
     * @param type given type
     * @return resolved {@link GekDataDescriptor}
     */
    GekDataDescriptor resolve(Type type);

    /**
     * Wraps given map as a {@link GekDataDescriptor}, of which type will be seen as Map&lt;String, ?&gt;,
     * types of properties will be calculated dynamically by its value's {@link Class#getClass()} (or Object if null).
     * This method is same with:
     * <pre>
     *     wrapMap(map, null);
     * </pre>
     *
     * @param map given map
     * @return wrapped {@link GekDataDescriptor}
     * @see #wrap(Map, Type)
     */
    default GekDataDescriptor wrap(Map<String, ?> map) {
        return wrap(map, null);
    }

    /**
     * Wraps given map as a {@link GekDataDescriptor}, the type of keys of map type must be {@link String},
     * property type is specified by map type. If the given map type is null,
     * the map type will be seen as Map&lt;String, ?&gt;. If map type is Map&lt;String, ?&gt;,
     * types of properties will be calculated dynamically by its value's {@link Class#getClass()} (or Object if null).
     * <p>
     * Result of {@link GekDataDescriptor#getProperties()} is immutable, but its content will be affected by given map dynamically.
     *
     * @param map     given map
     * @param mapType map type
     * @return wrapped {@link GekDataDescriptor}
     */
    default GekDataDescriptor wrap(Map<String, ?> map, @Nullable Type mapType) {
        return new MapWrapper(map, mapType);
    }

    /**
     * Returns handlers of this resolver.
     *
     * @return handlers of this resolver
     */
    List<Handler> getHandlers();

    /**
     * Returns a resolver with handler list consists of given handler as first one, followed by the handler list of
     * current resolver.
     *
     * @param handler given handler
     * @return a resolver with handler list consists of given handler as first one, followed by the handler list of
     * current resolver
     */
    GekDataResolver withFirstHandler(Handler handler);

    /**
     * Returns a resolver with handler list consists of the handler list of current resolver, followed by given handler
     * as last one.
     *
     * @param handler given handler
     * @return a resolver with handler list consists of the handler list of current resolver, followed by given handler
     * as last one
     */
    GekDataResolver withLastHandler(Handler handler);

    /**
     * Returns this resolver as {@link Handler}.
     *
     * @return this resolver as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link GekDataResolver}.
     *
     * @author fredsuvn
     * @see GekDataResolver
     */
    @ThreadSafe
    interface Handler {

        /**
         * Resolves type of data object from given context.
         * <p>
         * Property-base map from {@link Context#getProperties()} is readable and writeable.
         * Each handler can operate the map -- adding new property or removing old property which is resolved by
         * previous handler.
         * <p>
         * This method will be invoked for each handler in the resolver from {@link #getHandlers()} sequentially.
         * If a handler want to prevent the rest resolving, return {@link GekFlag#BREAK} to break invoking-sequence.
         * Other returned value will be ignored.
         *
         * @param context given resolving context
         * @see GekDataResolver
         */
        @Nullable
        GekFlag resolve(Context context);
    }

    /**
     * Resolving context of {@link GekDataResolver}.
     *
     * @author fredsuvn
     * @see GekDataResolver
     */
    interface Context {

        /**
         * Returns type of data object.
         *
         * @return type of data object
         */
        Type getType();

        /**
         * Returns bean property-base map in current resolving context.
         * <p>
         * The map through whole resolving process, store and share all property-base for all handlers.
         * Each handler can add or remove or modify property base info which is resolved by previous handler in this map.
         *
         * @return property-base map in current resolving context
         */
        Map<String, GekPropertyBase> getProperties();
    }
}
