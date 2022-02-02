package xyz.srclab.common.bean

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import xyz.srclab.common.reflect.rawClass
import java.io.Serializable
import java.lang.reflect.Type

/**
 * Represents bean type.
 *
 * @see PropertyType
 */
open class BeanType(
    val type: Type,
    val properties: Map<String, PropertyType>
) : Serializable {

    val classType: Class<*>
        get() = type.rawClass

    @Throws(NoSuchPropertyException::class)
    fun getProperty(name: String): PropertyType {
        return getPropertyOrNull(name) ?: throw NoSuchPropertyException(name)
    }

    fun getPropertyOrNull(name: String): PropertyType? {
        return properties[name]
    }

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

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}