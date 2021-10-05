package xyz.srclab.common.bean

import xyz.srclab.common.cache.Cache
import java.lang.reflect.Type

/**
 * [BeanResolver] implementation which can cache resolved [BeanType].
 */
open class CachedBeanResolver(
    override val resolveHandlers: List<BeanResolveHandler>,
    private val cache: Cache<Type, BeanType>
) : BeanResolver {

    override fun resolve(type: Type): BeanType {
        return cache.getOrLoad(type) {
            super.resolve(type)
        }
    }
}