package xyz.srclab.common.bean

import xyz.srclab.common.cache.Cache
import xyz.srclab.common.collect.asToList
import java.lang.reflect.Type

/**
 * Resolver for bean object.
 *
 * @author sunqian
 *
 * @see AbstractBeanResolver
 * @see BeanResolveHandler
 * @see BeanStyleBeanResolveHandler
 * @see RecordStyleBeanResolveHandler
 */
interface BeanResolver {

    val resolveHandlers: List<BeanResolveHandler>

    fun resolve(type: Type): BeanType

    fun withPreResolveHandler(preResolveHandler: BeanResolveHandler): BeanResolver {
        return newBeanResolver(listOf(preResolveHandler).plus(resolveHandlers))
    }

    companion object {

        @JvmField
        val DEFAULT: BeanResolver = newBeanResolver(BeanResolveHandler.DEFAULTS)

        @JvmStatic
        fun newBeanResolver(
            resolveHandlers: Iterable<BeanResolveHandler>,
        ): BeanResolver {
            return object : AbstractBeanResolver() {
                override val resolveHandlers: List<BeanResolveHandler> = resolveHandlers.asToList()
            }
        }
    }
}

/**
 * Abstract [BeanResolver] which can cache resolved [BeanType] previous created.
 */
abstract class AbstractBeanResolver @JvmOverloads constructor(
    private val cache: Cache<Type, BeanType> = Cache.weakCache()
) : BeanResolver {

    override fun resolve(type: Type): BeanType {
        return cache.getOrLoad(type) {
            resolve0(type)
        }
    }

    private fun resolve0(type: Type): BeanType {
        val builder = BeanResolveContext.newBeanResolveContext(type)
        for (resolveHandler in resolveHandlers) {
            resolveHandler.resolve(builder)
            if (builder.isComplete) {
                break
            }
        }
        return builder.build()
    }
}