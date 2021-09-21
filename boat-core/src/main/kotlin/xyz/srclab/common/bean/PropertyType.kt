package xyz.srclab.common.bean

import xyz.srclab.common.invoke.Invoker
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

    val name: String

    val type: Type

    val ownerType: BeanType

    val isReadable: Boolean
        get() {
            return getter !== null
        }

    val isWriteable: Boolean
        get() {
            return setter !== null
        }

    val getter: Invoker?

    val setter: Invoker?

    val field: Field?

    val getterMethod: Method?

    val setterMethod: Method?

    val fieldAnnotations: List<Annotation>
        get() {
            val f = this.field
            if (f === null) {
                return emptyList()
            }
            return f.annotations.toList()
        }

    val getterAnnotations: List<Annotation>
        get() {
            val getterMethod = this.getterMethod
            if (getterMethod === null) {
                return emptyList()
            }
            return getterMethod.annotations.toList()
        }

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