package xyz.srclab.common.bean

import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.common.reflect.searchFieldOrNull
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

    @get:JvmName("type")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type

    @get:JvmName("typeArguments")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val typeArguments: Map<TypeVariable<*>, Type>

    @get:JvmName("properties")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val properties: MutableMap<String, PropertyTypeBuilder>

    @get:JvmName("methods")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val methods: List<Method>

    @JvmDefault
    fun build(): BeanType {
        val properties: MutableMap<String, PropertyType> = HashMap()
        val beanType = BeanTypeImpl(type, Collections.unmodifiableMap(properties))
        for (propertyEntry in this.properties) {
            properties[propertyEntry.key] = propertyEntry.value.build(beanType)
        }
        return beanType
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
            return object : BeanTypeBuilder {
                override val type: Type = type
                override val typeArguments: Map<TypeVariable<*>, Type> = type.typeArguments
                override val properties: MutableMap<String, PropertyTypeBuilder> = HashMap()
                override val methods: List<Method> = type.rawClass.methods.asList()
            }
        }
    }
}

/**
 * Builder to create [PropertyType].
 */
interface PropertyTypeBuilder {

    @get:JvmName("name")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String

    @get:JvmName("type")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type

    @get:JvmName("getter")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val getter: Invoker?

    @get:JvmName("setter")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val setter: Invoker?

    @get:JvmName("backingField")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val backingField: () -> Field?

    @JvmDefault
    fun build(ownerType: BeanType): PropertyType {
        return PropertyTypeImpl(ownerType, name, type, getter, setter, backingField)
    }

    private class PropertyTypeImpl(
        override val ownerType: BeanType,
        override val name: String,
        override val type: Type,
        override val getter: Invoker?,
        override val setter: Invoker?,
        backingField: () -> Field?
    ) : PropertyType {

        override val backingField: Field? by lazy { backingField() }

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

        @JvmOverloads
        @JvmStatic
        fun newPropertyTypeBuilder(
            name: String,
            type: Type,
            getter: Invoker?,
            setter: Invoker?,
            backingField: () -> Field? = { type.rawClass.searchFieldOrNull(name, deep = true) }
        ): PropertyTypeBuilder {
            return object : PropertyTypeBuilder {
                override val name: String = name
                override val type: Type = type
                override val getter: Invoker? = getter
                override val setter: Invoker? = setter
                override val backingField: () -> Field? = backingField
            }
        }
    }
}