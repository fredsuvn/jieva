package xyz.fsgek.common.bean;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.base.GekFlag;
import xyz.fsgek.common.bean.handlers.AbstractBeanResolveHandler;
import xyz.fsgek.common.bean.handlers.JavaBeanResolveHandler;
import xyz.fsgek.common.bean.handlers.RecordBeanResolveHandler;
import xyz.fsgek.common.cache.GekCache;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Resolver for {@link GekBean}, usually consists of {@link Handler}s.
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
public interface GekBeanResolver {


    /**
     * Returns default bean resolver of which handler is {@link JavaBeanResolveHandler}.
     *
     * @return default bean resolver
     */
    static GekBeanResolver defaultResolver() {
        return BeanResolverImpl.INSTANCE;
    }

    /**
     * Returns new bean resolver with given handler and {@link GekCache#softCache()}.
     * This method is equivalent to {@link #newResolver(Iterable)}:
     * <pre>
     *     return newResolver(Collections.singleton(handler));
     * </pre>
     *
     * @param handler given handler
     * @return new bean resolver
     */
    static GekBeanResolver newResolver(Handler handler) {
        return newResolver(Collections.singleton(handler));
    }

    /**
     * Returns new bean resolver with given handlers and {@link GekCache#softCache()}.
     * This method is equivalent to {@link #newResolver(Iterable, GekCache)}:
     * <pre>
     *     return newResolver(handlers, GekCache.softCache());
     * </pre>
     *
     * @param handlers given handlers
     * @return new bean resolver
     */
    static GekBeanResolver newResolver(Iterable<Handler> handlers) {
        return newResolver(handlers, GekCache.softCache());
    }

    /**
     * Returns new bean resolver with given handlers and cache option.
     * If given cache is null, returned resolver will not cache the resolved bean.
     *
     * @param handlers given handlers
     * @param cache    given cache
     * @return new bean resolver
     */
    static GekBeanResolver newResolver(Iterable<Handler> handlers, @Nullable GekCache<Type, GekBean> cache) {
        return new BeanResolverImpl(handlers, cache);
    }

    /**
     * Resolves given type to {@link GekBean}.
     *
     * @param type given type
     * @return resolved {@link GekBean}
     */
    GekBean resolve(Type type);

    /**
     * Wraps given map as a {@link GekBean}, of which type will be seen as Map&lt;String, Object&gt;.
     * This method is same with:
     * <pre>
     *     wrapMap(map, null);
     * </pre>
     *
     * @param map given map
     * @return wrapped {@link GekBean}
     * @see #wrap(Map, Type)
     */
    default GekBean wrap(Map<String, ?> map) {
        return wrap(map, null);
    }

    /**
     * Wraps given map as a {@link GekBean}, the key type of map type must be {@link String}.
     * If the given map type is null, the map type will be seen as Map&lt;String, Object&gt;.
     * <p>
     * Result of {@link GekBean#getProperties()} is immutable, but its content will be affected by given map.
     *
     * @param map     given map
     * @param mapType given map type
     * @return wrapped {@link GekBean}
     */
    default GekBean wrap(Map<String, ?> map, @Nullable Type mapType) {
        return new MapWrapper(map, mapType);
    }

    /**
     * Returns handlers of this resolver.
     *
     * @return handlers of this resolver
     */
    List<Handler> getHandlers();

    /**
     * Returns a new resolver of which handlers come from current resolver,
     * but inserts given handler at first index of handlers.
     *
     * @param handler given handler
     * @return a new resolver
     */
    default GekBeanResolver insertFirstHandler(Handler handler) {
        return insertHandler(0, handler);
    }

    /**
     * Returns a new resolver of which handlers come from current resolver,
     * but inserts given handler at specified index of handlers.
     *
     * @param index   specified index
     * @param handler given handler
     * @return a new resolver
     */
    GekBeanResolver insertHandler(int index, Handler handler);

    /**
     * Returns this resolver as {@link Handler}.
     *
     * @return this resolver as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link GekBeanResolver} for resolve bean.
     *
     * @author fredsuvn
     * @see GekBeanResolver
     */
    @ThreadSafe
    interface Handler {

        /**
         * Resolves bean from given resolving context, of which properties map
         * (from {@link ResolveContext#beanProperties()}) is readable and writeable.
         * Each handler can operate the properties map --
         * add new property or remove property which is resolved by previous handler.
         * <p>
         * In general, all handlers in the resolver ({@link #getHandlers()}) will be invoked this method sequentially.
         * If a handler want to prevent next resolving, return {@link GekFlag#BREAK} to tell the resolver to break and
         * finish resolving after current resolution. Other returned value will be ignored.
         *
         * @param context given resolving context
         * @see GekBeanResolver
         */
        @Nullable
        GekFlag resolve(ResolveContext context);
    }

    /**
     * Context of bean resolving.
     */
    interface ResolveContext {

        /**
         * Returns type of resolved bean.
         *
         * @return type of resolved bean
         */
        Type beanType();

        /**
         * Returns bean properties-base map in current resolving context.
         * The map through whole resolving process, store and share all properties-base for all handlers.
         * Each handler can add or remove or modify properties which is resolved by previous handler in this map.
         *
         * @return properties-base map in current resolving context
         */
        Map<String, GekPropertyBase> beanProperties();
    }
}
