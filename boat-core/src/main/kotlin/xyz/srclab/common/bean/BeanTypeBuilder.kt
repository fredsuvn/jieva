package xyz.srclab.common.bean

import java.lang.reflect.Type
import java.util.*

/**
 * Builder for [BeanType].
 */
open class BeanTypeBuilder(open val type: Type) {

    private val _properties: MutableMap<String, PropertyType> = LinkedHashMap()

    val properties: Map<String, PropertyType> = Collections.unmodifiableMap(_properties)

    open fun hasProperty(name: String): Boolean {
        return _properties.containsKey(name)
    }

    open fun getProperty(name: String): PropertyType? {
        return _properties[name]
    }

    open fun addProperty(propertyType: PropertyType) {
        _properties[propertyType.name] = propertyType
    }

    open fun build(): BeanType {
        return BeanType.newBeanType(type, properties)
    }
}