package xyz.srclab.common.bean

import xyz.srclab.common.cache.Cache
import xyz.srclab.common.collect.asToList
import xyz.srclab.common.collect.toImmutableMap
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.typeArguments
import java.lang.reflect.Type

/**
 * Resolver for bean object.
 *
 * @author sunqian
 *
 * @see AbstractBeanResolver
 * @see BeanResolveHandler
 * @see BeanStyleBeanResolveHandler
 * @see NamingStyleBeanResolveHandler
 */
interface BeanResolver {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val resolveHandlers: List<BeanResolveHandler>
        @JvmName("resolveHandlers") get

    fun resolve(type: Type): BeanType

    @JvmDefault
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
 * Abstract [BeanResolver].
 */
abstract class AbstractBeanResolver : BeanResolver {

    private val cache = Cache.newFastCache<Type, BeanType>()

    override fun resolve(type: Type): BeanType {
        return cache.getOrLoad(type) {
            val beanType = BeanTypeImpl(it, emptyMap())
            val context = BeanResolveHandler.newContext(beanType, it.typeArguments())
            for (handler in resolveHandlers) {
                handler.resolve(context)
                if (context.isBroken) {
                    break
                }
            }
            beanType.properties = context.properties.toImmutableMap()
            beanType
        }
    }
}

private class BeanTypeImpl(
    override val type: Type,
    override var properties: Map<String, PropertyType>
) : BeanType {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BeanType) return false

        if (type != other.type) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + properties.hashCode()
        return result
    }

    override fun toString(): String {
        return "bean ${type.typeName}"
    }
}