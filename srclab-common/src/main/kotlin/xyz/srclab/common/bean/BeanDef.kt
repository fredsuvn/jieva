package xyz.srclab.common.bean

import xyz.srclab.common.base.VirtualInvoker
import java.beans.PropertyDescriptor
import java.lang.reflect.Field
import java.lang.reflect.Type

/**
 * @author sunqian
 */
interface BeanDef {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val type: Class<*>
        @JvmName("type") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val genericType: Type
        @JvmName("genericType") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val properties: Map<String, PropertyDef>
        @JvmName("properties") get

    fun getProperty(name: String): PropertyDef? {
        return properties[name]
    }

    companion object {

        fun resolve(any: Any): BeanDef {
            TODO()
        }
    }
}


interface PropertyDef {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val name: String
        @JvmName("name") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val type: Class<*>
        @JvmName("type") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val genericType: Type
        @JvmName("genericType") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val owner: Class<*>
        @JvmName("owner") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val isReadable: Boolean
        @JvmName("isReadable") get() {
            return getter !== null
        }

    @Suppress("INAPPLICABLE_JVM_NAME")
    val isWriteable: Boolean
        @JvmName("isWriteable") get() {
            return setter !== null
        }

    @Suppress("INAPPLICABLE_JVM_NAME")
    val getter: VirtualInvoker?
        @JvmName("getter") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val setter: VirtualInvoker?
        @JvmName("setter") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val field: Field?
        @JvmName("field") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val fieldAnnotations: List<Annotation>
        @JvmName("fieldAnnotations") get

    fun <T> get(bean: Any): T

    fun set(bean: Any, value: Any?)
}

private class PropertyDefImpl(
    override val owner: Class<*>,
    private val descriptor:PropertyDescriptor
) : PropertyDef {

    override val name: String
    override val type: Class<*>
    override val genericType: Type
    override val getter: VirtualInvoker?
    override val setter: VirtualInvoker?
    override val field: Field?
    override val fieldAnnotations: List<Annotation>

    init {
        name = descriptor.name
        type = descriptor.propertyType
    }
}