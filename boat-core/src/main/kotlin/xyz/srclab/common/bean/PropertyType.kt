package xyz.srclab.common.bean

import xyz.srclab.common.base.asType
import xyz.srclab.common.base.defaultSerialVersion
import xyz.srclab.common.invoke.InstInvoke
import java.io.Serializable
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Represents bean property.
 *
 * @see BeanType
 */
open class PropertyType(
    val ownerType: BeanType,
    val name: String,
    val type: Type,
    val getter: InstInvoke?,
    val setter: InstInvoke?,
    val field: Field?,
    val getterMethod: Method?,
    val setterMethod: Method?,
) : Serializable {

    val isReadable: Boolean
        get() {
            return getter !== null
        }

    val isWriteable: Boolean
        get() {
            return setter !== null
        }

    val fieldAnnotations: List<Annotation> by lazy {
        val f = this.field
        if (f === null) {
            return@lazy emptyList()
        }
        f.annotations.toList()
    }

    val getterAnnotations: List<Annotation> by lazy {
        val getterMethod = this.getterMethod
        if (getterMethod === null) {
            return@lazy emptyList()
        }
        getterMethod.annotations.toList()
    }

    val setterAnnotations: List<Annotation> by lazy {
        val setterMethod = this.setterMethod
        if (setterMethod === null) {
            return@lazy emptyList()
        }
        setterMethod.annotations.toList()
    }

    fun getValue(bean: Any): Any? {
        val getter = this.getter
        if (getter === null) {
            throw IllegalStateException("Property is not readable: $name")
        }
        return getter.invoke(bean)
    }

    fun <T> getTypedValue(bean: Any): T {
        return getValue(bean).asType()
    }

    fun setValue(bean: Any, value: Any?) {
        val setter = this.setter
        if (setter === null) {
            throw IllegalStateException("Property is not writeable: $name")
        }
        setter.invoke(bean, value)
    }

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
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "property: ${ownerType.type.typeName}.$name[${type.typeName}]"
    }

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}