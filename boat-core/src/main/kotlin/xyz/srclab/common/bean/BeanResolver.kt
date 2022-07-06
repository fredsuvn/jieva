package xyz.srclab.common.bean

import xyz.srclab.common.bean.BeanResolver.Companion.CachedBeanResolver
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.collect.asToList
import xyz.srclab.common.collect.plusBefore
import java.lang.reflect.Type

/**
 * Resolver for bean.
 *
 * Note default methods implementations will not cache result, use [CachedBeanResolver] if you need caching.
 *
 * @see BeanResolveHandler
 * @see BeanStyleBeanResolveHandler
 * @see RecordStyleBeanResolveHandler
 * @see CachedBeanResolver
 */
interface BeanResolver {

    /**
     * Handlers of this resolver.
     */
    val resolveHandlers: List<BeanResolveHandler>

    /**
     * Resolves [type] to [BeanType].
     */
    fun resolve(type: Type): BeanType {
        val properties = LinkedHashMap<String, PropertyType>()
        val beanType = BeanType(type, properties)
        val context = BeanResolveContext(type, beanType, properties)
        for (resolveHandler in resolveHandlers) {
            resolveHandler.resolve(context)
            if (context.isBreak) {
                break
            }
        }
        return beanType
    }

    companion object {

        /**
         * Creates a new [BeanResolver] with given [resolveHandlers] and [cache].
         */
        @JvmOverloads
        @JvmStatic
        fun newBeanResolver(
            resolveHandlers: Iterable<BeanResolveHandler> = BeanResolveHandler.DEFAULTS,
            cache: Cache<Type, BeanType> = Cache.ofWeak()
        ): BeanResolver {
            return CachedBeanResolver(resolveHandlers.asToList(), cache)
        }

        /**
         * Returns a new [BeanResolver] consists of given [handler] followed by existed [BeanResolveHandler]s.
         */
        @JvmStatic
        fun BeanResolver.extend(handler: BeanResolveHandler): BeanResolver {
            return newBeanResolver(this.resolveHandlers.plusBefore(0, handler))
        }

        /**
         * [BeanResolver] implementation which can cache resolved [BeanType].
         */
        private class CachedBeanResolver(
            override val resolveHandlers: List<BeanResolveHandler>,
            private val cache: Cache<Type, BeanType>
        ) : BeanResolver {
            override fun resolve(type: Type): BeanType {
                return cache.get(type) {
                    super.resolve(type)
                }
            }
        }
    }
}