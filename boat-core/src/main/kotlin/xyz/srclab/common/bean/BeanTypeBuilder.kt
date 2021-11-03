package xyz.srclab.common.bean

import xyz.srclab.common.base.CacheableBuilder
import xyz.srclab.common.collect.toUnmodifiable
import java.lang.reflect.Type

/**
 * Builder for [BeanType].
 */
class BeanTypeBuilder(val type: Type) : CacheableBuilder<BeanType>() {

    private val _properties: MutableMap<String, PropertyType> = LinkedHashMap()

    val properties: Map<String, PropertyType> = _properties.toUnmodifiable()

    fun hasProperty(name: String): Boolean {
        return _properties.containsKey(name)
    }

    fun getProperty(name: String): PropertyType? {
        return _properties[name]
    }

    fun addProperty(propertyType: PropertyType) {
        _properties[propertyType.name] = propertyType
        this.commit()
    }

    override fun buildNew(): BeanType {
        return BeanType.newBeanType(type, properties)
    }
}