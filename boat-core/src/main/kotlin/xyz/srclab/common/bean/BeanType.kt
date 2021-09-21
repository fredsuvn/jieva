package xyz.srclab.common.bean

import java.lang.reflect.Type

/**
 * Represents bean type.
 *
 * @author sunqian
 *
 * @see PropertyType
 */
interface BeanType {

    val type: Type

    val properties: Map<String, PropertyType>

    @Throws(PropertyNotFoundException::class)
    fun getProperty(name: CharSequence): PropertyType {
        val nameString = name.toString()
        return properties[nameString] ?: throw PropertyNotFoundException(nameString)
    }

    companion object {

        @JvmStatic
        fun newBeanType(type: Type, properties: Map<String, PropertyType>): BeanType {
            return BeanTypeImpl(type, properties)
        }

        private class BeanTypeImpl(
            override val type: Type,
            override val properties: Map<String, PropertyType>
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
    }
}