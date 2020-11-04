package xyz.srclab.common.bean

import xyz.srclab.common.base.VirtualInvoker
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.kotlin.compile.COMPILE_INAPPLICABLE_JVM_NAME
import java.lang.reflect.Field
import java.lang.reflect.Type

/**
 * @author sunqian
 */
interface BeanSchema {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val type: Class<*>
        @JvmName("type") get() = genericType.rawClass

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val genericType: Type
        @JvmName("genericType") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val properties: Map<String, PropertySchema>
        @JvmName("properties") get

    fun getProperty(name: String): PropertySchema? {
        return properties[name]
    }
}

interface PropertySchema {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val type: Class<*>
        @JvmName("type") get() = genericType.rawClass

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val genericType: Type
        @JvmName("genericType") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val ownerType: Class<*>
        @JvmName("ownerType") get() = genericOwnerType.rawClass

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val genericOwnerType: Type
        @JvmName("genericOwnerType") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val isReadable: Boolean
        @JvmName("isReadable") get() {
            return getter !== null
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val isWriteable: Boolean
        @JvmName("isWriteable") get() {
            return setter !== null
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val getter: VirtualInvoker?
        @JvmName("getter") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val setter: VirtualInvoker?
        @JvmName("setter") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val field: Field?
        @JvmName("field") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val fieldAnnotations: List<Annotation>
        @JvmName("fieldAnnotations") get

    fun <T> getValue(bean: Any): T

    fun <T> setValue(bean: Any, value: Any?): T

    fun <T> setValue(bean: Any, value: Any?, converter: Converter): T? {
        return setValue(bean, converter.convert(value, genericType))
    }
}