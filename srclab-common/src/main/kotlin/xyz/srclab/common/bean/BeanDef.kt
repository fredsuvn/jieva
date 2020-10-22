package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.VirtualInvoker
import xyz.srclab.common.base.virtualInvoker
import xyz.srclab.common.reflect.findField
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * @author sunqian
 */
interface BeanDef {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Class<*>
        @JvmName("type") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val properties: Map<String, PropertyDef>
        @JvmName("properties") get

    fun getProperty(name: String): PropertyDef? {
        return properties[name]
    }

    companion object {

        @JvmStatic
        fun resolve(type: Class<*>): BeanDef {
            return BeanDefImpl(type)
        }
    }
}

fun Class<*>.resolveBean(): BeanDef {
    return BeanDef.resolve(this)
}

interface PropertyDef {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Class<*>
        @JvmName("type") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val genericType: Type
        @JvmName("genericType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val owner: Class<*>
        @JvmName("owner") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isReadable: Boolean
        @JvmName("isReadable") get() {
            return getter !== null
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isWriteable: Boolean
        @JvmName("isWriteable") get() {
            return setter !== null
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val getter: VirtualInvoker?
        @JvmName("getter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val setter: VirtualInvoker?
        @JvmName("setter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val field: Field?
        @JvmName("field") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val fieldAnnotations: List<Annotation>
        @JvmName("fieldAnnotations") get

    fun <T> get(bean: Any): T

    fun set(bean: Any, value: Any?)
}

private class BeanDefImpl(override val type: Class<*>) : BeanDef {

    override val properties: Map<String, PropertyDef> by lazy { tryProperties() }

    private fun tryProperties(): Map<String, PropertyDef> {
        val beanInfo = Introspector.getBeanInfo(type)
        val properties = LinkedHashMap<String, PropertyDef>()
        for (propertyDescriptor in beanInfo.propertyDescriptors) {
            val propertyDef = PropertyDefImpl(type, propertyDescriptor)
            properties[propertyDef.name] = propertyDef
        }
        return properties.toMap()
    }
}

private class PropertyDefImpl(
    override val owner: Class<*>,
    descriptor: PropertyDescriptor
) : PropertyDef {

    override val name: String = descriptor.name
    override val type: Class<*> = descriptor.propertyType
    override val genericType: Type by lazy { tryGenericType() }
    override val getter: VirtualInvoker? by lazy { tryGetter() }
    private val getterMethod: Method? = descriptor.readMethod
    override val setter: VirtualInvoker? by lazy { trySetter() }
    private val setterMethod: Method? = descriptor.writeMethod
    override val field: Field? by lazy { tryField() }
    override val fieldAnnotations: List<Annotation> by lazy { tryFieldAnnotations() }

    private fun tryGenericType(): Type {
        return if (getterMethod !== null) {
            getterMethod.genericReturnType
        } else {
            setterMethod!!.genericParameterTypes[0]
        }
    }

    private fun tryGetter(): VirtualInvoker? {
        return if (getterMethod === null) null else virtualInvoker(getterMethod)
    }

    private fun trySetter(): VirtualInvoker? {
        return if (setterMethod === null) null else virtualInvoker(setterMethod)
    }

    private fun tryField(): Field? {
        return owner.findField(name, declared = true, deep = true)
    }

    private fun tryFieldAnnotations(): List<Annotation> {
        val f = field
        return if (f === null) emptyList() else f.annotations.asList()
    }

    override fun <T> get(bean: Any): T {
        val g = getter
        return if (g !== null) {
            g.invokeVirtual(bean)
        } else {
            throw UnsupportedOperationException("This property is not readable: $name")
        }
    }

    override fun set(bean: Any, value: Any?) {
        val s = setter
        if (s !== null) {
            s.invokeVirtual<Any?>(bean, value)
        } else {
            throw UnsupportedOperationException("This property is not writeable: $name")
        }
    }
}