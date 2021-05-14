package xyz.srclab.common.bean

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Field
import java.lang.reflect.Type

interface PropertyType {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type
        @JvmName("type") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val rawClass: Class<*>
        @JvmName("rawClass") get() = type.rawClass

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val ownerType: BeanType
        @JvmName("ownerType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isReadable: Boolean
        @JvmName("isReadable") get() {
            return getter !== null
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isWriteable: Boolean
        @JvmName("isWriteable") get() {
            return setter !== null
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val getter: Invoker?
        @JvmName("getter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val setter: Invoker?
        @JvmName("setter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val hasBackingField: Boolean
        @JvmName("hasBackingField") get() {
            return backingField !== null
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val backingField: Field?
        @JvmName("backingField") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val backingFieldAnnotations: List<Annotation>
        @JvmName("backingFieldAnnotations") get

    fun <T> getValue(bean: Any): T

    fun <T> setValue(bean: Any, value: Any?): T
}