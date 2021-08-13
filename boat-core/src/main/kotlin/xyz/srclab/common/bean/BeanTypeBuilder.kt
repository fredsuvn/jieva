package xyz.srclab.common.bean

import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.common.reflect.typeArguments
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.*

/**
 * Builder to create [BeanType].
 */
interface BeanTypeBuilder {

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

    private class PropertyTypeImpl(
        override val ownerType: BeanType,
        override val name: String,
        override val type: Type,
        override val getter: Invoker?,
        override val setter: Invoker?,
        override val field: Field?,
        override val getterMethod: Method?,
        override val setterMethod: Method?,
    ) : PropertyType {

        override val fieldAnnotations: List<Annotation> by lazy { super.fieldAnnotations }
        override val getterAnnotations: List<Annotation> by lazy { super.getterAnnotations }
        override val setterAnnotations: List<Annotation> by lazy { super.setterAnnotations }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PropertyType) return false
            if (ownerType != other.ownerType) return false
            if (name != other.name) return false
            if (type != other.type) return false
            return true
        }

        override fun hashCode(): Int {
            var result = ownerType.hashCode()
            result = 31 * result + name.hashCode()
            return result
        }

        override fun toString(): String {
            return "$name: ${ownerType.type.typeName}.${type.typeName}"
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
                override val preparedBeanType: BeanType = preparedBeanType
                override val type: Type = type
                override val typeArguments: Map<TypeVariable<*>, Type> = type.typeArguments
                override val properties: MutableMap<String, PropertyType> = properties
                override val methods: List<Method> = type.rawClass.methods.asList()
            }
        }

        @JvmStatic
        fun newPropertyType(
            ownerType: BeanType,
            name: String,
            type: Type,
            getter: Invoker?,
            setter: Invoker?,
            field: Field?,
            getterMethod: Method?,
            setterMethod: Method?,
        ): PropertyType {
            return PropertyTypeImpl(ownerType, name, type, getter, setter, field, getterMethod, setterMethod)
        }
    }
}