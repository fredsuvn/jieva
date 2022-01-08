package xyz.srclab.common.bean

import java.lang.reflect.Type
import java.util.*

/**
 * Builder for [BeanType].
 */
open class BeanTypeBuilder(val type: Type) {

    private val _properties: MutableMap<String, PropertyType> = LinkedHashMap()

    val properties: Map<String, PropertyType> = Collections.unmodifiableMap(_properties)

    fun hasProperty(name: String): Boolean {
        return _properties.containsKey(name)
    }

    fun getProperty(name: String): PropertyType? {
        return _properties[name]
    }

    fun addProperty(propertyType: PropertyType) {
        _properties[propertyType.name] = propertyType
    }

    fun build(): BeanType {
        return BeanType.newBeanType(type, properties)
    }
}