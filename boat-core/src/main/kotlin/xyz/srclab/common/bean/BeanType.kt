package xyz.srclab.common.bean

import lombok.EqualsAndHashCode
import xyz.srclab.common.base.defaultSerialVersion
import xyz.srclab.common.reflect.rawClass
import java.io.Serializable
import java.lang.reflect.Type

/**
 * Represents bean type.
 *
 * @see PropertyType
 */
@EqualsAndHashCode
open class BeanType(
    open val type: Type,
    open val properties: Map<String, PropertyType>
) : Serializable {

    open val classType: Class<*>
        get() = type.rawClass

    @Throws(NoSuchPropertyException::class)
    open fun getProperty(name: String): PropertyType {
        return getPropertyOrNull(name) ?: throw NoSuchPropertyException(name)
    }

    open fun getPropertyOrNull(name: String): PropertyType? {
        return properties[name]
    }

    override fun toString(): String {
        return "bean ${type.typeName}"
    }

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}