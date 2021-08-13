package xyz.srclab.common.bean

import xyz.srclab.common.cache.Cache
import xyz.srclab.common.collect.asToList
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.Next
import java.lang.reflect.Type

/**
 * Resolver for bean object.
 *
 * @author sunqian
 *
 * @see AbstractCachingBeanResolver
 * @see BeanResolveHandler
 * @see BeanStyleBeanResolveHandler
 * @see RecordStyleBeanResolveHandler
 */
interface BeanResolver {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val resolveHandlers: List<BeanResolveHandler>
        @JvmName("resolveHandlers") get

    fun resolve(type: Type): BeanType {
        val builder = BeanTypeBuilder.newBeanTypeBuilder(type)
        for (resolveHandler in resolveHandlers) {
            when (resolveHandler.resolve(builder)) {
                Next.CONTINUE -> continue
                else -> break
            }
        }
        return builder.build()
    }

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
            return object : AbstractCachingBeanResolver() {
                override val resolveHandlers: List<BeanResolveHandler> = resolveHandlers.asToList()
            }
        }
    }
}

/**
 * Abstract [BeanResolver] which can cache resolved [BeanType] previous created.
 */
abstract class AbstractCachingBeanResolver @JvmOverloads constructor(
    private val cache: Cache<Type, BeanType> = Cache.newFastCache()
) : BeanResolver {

    override fun resolve(type: Type): BeanType {
        return cache.getOrLoad(type) {
            super.resolve(type)
        }
    }
}