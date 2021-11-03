package xyz.srclab.common.bean

import xyz.srclab.common.base.CacheableBuilder
import xyz.srclab.common.collect.toUnmodifiable
import java.lang.reflect.Type

/**
 * Builder for [BeanType].
 */
class BeanTypeBuilder(type: Type? = null) : CacheableBuilder<BeanType>() {

    private var _type: Type? = type
    private val _properties: MutableMap<String, PropertyType> = LinkedHashMap()

    val properties: Map<String, PropertyType> = _properties.toUnmodifiable()

    var type: Type?
        get() = _type
        set(value) {
            _type = value
            this.commit()
        }

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
        val type = _type
        if (type === null) {
            throw IllegalStateException("type of BeanType is not set.")
        }
        return BeanType.newBeanType(type, properties)
    }
}