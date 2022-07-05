package xyz.srclab.common.bean

import lombok.EqualsAndHashCode
import xyz.srclab.common.base.asType
import xyz.srclab.common.base.defaultSerialVersion
import xyz.srclab.common.func.InstFunc
import java.io.Serializable
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Represents bean property.
 *
 * @see BeanType
 */
@EqualsAndHashCode
open class PropertyType(
    open val ownerType: BeanType,
    open val name: String,
    open val type: Type,
    open val getter: InstFunc?,
    open val setter: InstFunc?,
    open val field: Field?,
    open val getterMethod: Method?,
    open val setterMethod: Method?,
) : Serializable {

    open val isReadable: Boolean
        get() {
            return getter !== null
        }

    open val isWriteable: Boolean
        get() {
            return setter !== null
        }

    open val fieldAnnotations: List<Annotation> by lazy {
        val f = this.field
        if (f === null) {
            return@lazy emptyList()
        }
        f.annotations.toList()
    }

    open val getterAnnotations: List<Annotation> by lazy {
        val getterMethod = this.getterMethod
        if (getterMethod === null) {
            return@lazy emptyList()
        }
        getterMethod.annotations.toList()
    }

    open val setterAnnotations: List<Annotation> by lazy {
        val setterMethod = this.setterMethod
        if (setterMethod === null) {
            return@lazy emptyList()
        }
        setterMethod.annotations.toList()
    }

    open fun getValue(bean: Any): Any? {
        val getter = this.getter
        if (getter === null) {
            throw IllegalStateException("Property is not readable: $name")
        }
        return getter.invoke(bean)
    }

    open fun <T> getValueAsType(bean: Any): T {
        return getValue(bean).asType()
    }

    open fun setValue(bean: Any, value: Any?) {
        val setter = this.setter
        if (setter === null) {
            throw IllegalStateException("Property is not writeable: $name")
        }
        setter.invoke(bean, value)
    }

    override fun toString(): String {
        return "property: ${ownerType.type.typeName}.$name[${type.typeName}]"
    }

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}