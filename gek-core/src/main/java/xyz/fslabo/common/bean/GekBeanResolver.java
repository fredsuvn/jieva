package xyz.fslabo.common.bean;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.annotations.ThreadSafe;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.bean.handlers.AbstractBeanResolverHandler;
import xyz.fslabo.common.bean.handlers.JavaBeanResolverHandler;
import xyz.fslabo.common.bean.handlers.NonGetterPrefixResolverHandler;
import xyz.fslabo.common.bean.handlers.NonPrefixResolverHandler;
import xyz.fslabo.common.collect.JieArray;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Resolver for {@link GekBeanInfo}, usually consists of a {@link Handler} list.
 * There are 3 built-in resolvers:
 * <ul>
 *     <li>{@link JavaBeanResolverHandler} (default handler);</li>
 *     <li>{@link NonGetterPrefixResolverHandler};</li>
 *     <li>{@link NonPrefixResolverHandler};</li>
 * </ul>
 * And a skeletal implementation: {@link AbstractBeanResolverHandler}.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekBeanResolver {

    /**
     * Returns default bean resolver of which handler is {@link JavaBeanResolverHandler}.
     *
     * @return default bean resolver
     */
    static GekBeanResolver defaultResolver() {
        return ResolverImpl.DEFAULT_RESOLVER;
    }

    /**
     * Returns new bean resolver with given handlers.
     *
     * @param handlers given handlers
     * @return new bean resolver
     */
    static GekBeanResolver withHandlers(Handler... handlers) {
        return withHandlers(JieArray.asList(handlers));
    }

    /**
     * Returns new bean resolver with given handlers.
     *
     * @param handlers given handlers
     * @return new bean resolver
     */
    static GekBeanResolver withHandlers(Iterable<Handler> handlers) {
        return new ResolverImpl(handlers);
    }

    /**
     * Resolves given type to {@link GekBeanInfo}.
     *
     * @param type given type
     * @return resolved {@link GekBeanInfo}
     */
    GekBeanInfo resolve(Type type);

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
    GekBeanResolver withFirstHandler(Handler handler);

    /**
     * Returns a resolver with handler list consists of the handler list of current resolver, followed by given handler
     * as last one.
     *
     * @param handler given handler
     * @return a resolver with handler list consists of the handler list of current resolver, followed by given handler
     * as last one
     */
    GekBeanResolver withLastHandler(Handler handler);

    /**
     * Returns this resolver as {@link Handler}.
     *
     * @return this resolver as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link GekBeanResolver}.
     *
     * @author fredsuvn
     * @see GekBeanResolver
     */
    @ThreadSafe
    interface Handler {

        /**
         * Resolves type of bean from given context.
         * <p>
         * Property-base map from {@link Context#getProperties()} and method-base list from {@link Context#getMethods()}
         * are readable and writeable. Each handler can operate the map/list -- adding or removing or modifying the
         * member-base which is resolved by previous handler.
         * <p>
         * This method will be invoked for each handler in the resolver from {@link #getHandlers()} sequentially.
         * If a handler want to prevent the rest resolving, return {@link Flag#BREAK} to break invoking-sequence.
         * Other returned value will be ignored.
         *
         * @param context given resolving context
         * @see GekBeanResolver
         */
        @Nullable
        Flag resolve(Context context);
    }

    /**
     * Resolving context of {@link GekBeanResolver}.
     *
     * @author fredsuvn
     * @see GekBeanResolver
     */
    interface Context {

        /**
         * Returns type of bean.
         *
         * @return type of bean
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

        /**
         * Returns bean method-base list in current resolving context.
         * <p>
         * The list through whole resolving process, store and share all method-base for all handlers.
         * Each handler can add or remove or modify method base info which is resolved by previous handler in this list.
         *
         * @return method-base map in current resolving context
         */
        List<GekMethodBase> getMethods();
    }
}
