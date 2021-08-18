package xyz.srclab.common.bean

import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.util.*

/**
 * Represents bean property.
 *
 * @see BeanType
 */
interface PropertyType {

    @get:JvmName("name")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String

    @get:JvmName("type")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type

    @get:JvmName("ownerType")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val ownerType: BeanType

    @get:JvmName("isReadable")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val isReadable: Boolean
        get() {
            return getter !== null
        }

    @get:JvmName("isWriteable")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val isWriteable: Boolean
        get() {
            return setter !== null
        }

    @get:JvmName("getter")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val getter: Invoker?

    @get:JvmName("setter")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val setter: Invoker?

    @get:JvmName("field")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val field: Field?

    @get:JvmName("getterMethod")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val getterMethod: Method?

    @get:JvmName("setterMethod")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val setterMethod: Method?

    @get:JvmName("fieldAnnotations")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val fieldAnnotations: List<Annotation>
        get() {
            val f = this.field
            if (f === null) {
                return emptyList()
            }
            return f.annotations.toList()
        }

    @get:JvmName("getterAnnotations")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val getterAnnotations: List<Annotation>
        get() {
            val getterMethod = this.getterMethod
            if (getterMethod === null) {
                return emptyList()
            }
            return getterMethod.annotations.toList()
        }

    @get:JvmName("setterAnnotations")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val setterAnnotations: List<Annotation>
        get() {
            val setterMethod = this.setterMethod
            if (setterMethod === null) {
                return emptyList()
            }
            return setterMethod.annotations.toList()
        }

    fun <T> getValue(bean: Any): T {
        val getter = this.getter
        if (getter === null) {
            throw IllegalStateException("Property is not readable: $name")
        }
        return getter.invoke(bean)
    }

    fun setValue(bean: Any, value: Any?) {
        val setter = this.setter
        if (setter === null) {
            throw IllegalStateException("Property is not writeable: $name")
        }
        setter.invoke<Any?>(bean, value)
    }

    fun <T> setValueAndReturnOld(bean: Any, value: Any?): T? {
        val old = getValue<T?>(bean)
        setValue(bean, value)
        return old
    }

    companion object {

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

            private val _hashcode: Int by lazy { Objects.hash(ownerType, name, type) }

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

            override fun hashCode(): Int = _hashcode

            override fun toString(): String {
                return "${ownerType.type.typeName}.$name: ${type.typeName}"
            }
        }
    }
}