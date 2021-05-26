package xyz.srclab.common.bean

import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.lang.reflect.Field
import java.lang.reflect.Type

/**
 * Represents property type of a bean.
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
    @JvmDefault
    val ownerType: BeanType

    @get:JvmName("isReadable")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isReadable: Boolean
        get() {
            return getter !== null
        }

    @get:JvmName("isWriteable")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
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

    @get:JvmName("hasBackingField")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val hasBackingField: Boolean
        get() {
            return backingField !== null
        }

    @get:JvmName("backingField")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val backingField: Field?

    @get:JvmName("backingFieldAnnotations")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val backingFieldAnnotations: List<Annotation>
        get() {
            val backingField = this.backingField
            if (backingField === null) {
                return emptyList()
            }
            return backingField.annotations.toList()
        }

    @JvmDefault
    fun <T> getValue(bean: Any): T {
        val getter = this.getter
        if (getter === null) {
            throw IllegalStateException("Property is not readable: $name")
        }
        return getter.invoke(bean)
    }

    @JvmDefault
    fun setValue(bean: Any, value: Any?) {
        val setter = this.setter
        if (setter === null) {
            throw IllegalStateException("Property is not writeable: $name")
        }
        setter.invoke<Any?>(bean, value)
    }

    @JvmDefault
    fun <T> setValueAndReturnOld(bean: Any, value: Any?): T? {
        val old = getValue<T?>(bean)
        setValue(bean, value)
        return old
    }
}