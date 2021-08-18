package xyz.srclab.common.bean

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.common.reflect.typeArguments
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.*

/**
 * Builder to create [BeanType].
 */
interface BeanTypeBuilder {

    @get:JvmName("isComplete")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val isComplete: Boolean

    @get:JvmName("preparedBeanType")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val preparedBeanType: BeanType

    @get:JvmName("type")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type

    @get:JvmName("typeArguments")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val typeArguments: Map<TypeVariable<*>, Type>

    @get:JvmName("properties")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val properties: MutableMap<String, PropertyType>

    @get:JvmName("methods")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val methods: List<Method>

    /**
     * Force complete current resolver, prevent left resolver handler.
     */
    fun complete()

    fun build(): BeanType {
        return preparedBeanType
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

    companion object {
        @JvmStatic
        fun newBeanTypeBuilder(
            type: Type
        ): BeanTypeBuilder {
            val properties: MutableMap<String, PropertyType> = HashMap()
            val beanTypeProperties = Collections.unmodifiableMap(properties)
            val preparedBeanType = BeanTypeImpl(type, beanTypeProperties)
            return object : BeanTypeBuilder {

                private var complete = false

                override val isComplete: Boolean get() = complete
                override val preparedBeanType: BeanType = preparedBeanType
                override val type: Type = type
                override val typeArguments: Map<TypeVariable<*>, Type> = type.typeArguments
                override val properties: MutableMap<String, PropertyType> = properties
                override val methods: List<Method> = type.rawClass.methods.asList()

                override fun complete() {
                    complete = true
                }
            }
        }
    }
}