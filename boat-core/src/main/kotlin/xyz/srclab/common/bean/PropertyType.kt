package xyz.srclab.common.bean

import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type

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
}