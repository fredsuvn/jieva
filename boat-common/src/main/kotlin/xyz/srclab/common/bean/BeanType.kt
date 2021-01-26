package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type

/**
 * @author sunqian
 */
interface BeanType {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type
        @JvmName("type") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val rawClass: Class<*>
        @JvmName("rawClass") get() = type.rawClass

    @Suppress(INAPPLICABLE_JVM_NAME)
    val properties: Map<String, PropertyType>
        @JvmName("properties") get

    @JvmDefault
    fun property(name: String): PropertyType? {
        return properties[name]
    }

    companion object {

        @JvmStatic
        fun newBeanSchema(genericType: Type, properties: Map<String, PropertyType>): BeanType {
            return BeanTypeImpl(genericType, properties)
        }

        private class BeanTypeImpl(
            override val type: Type,
            override val properties: Map<String, PropertyType>,
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