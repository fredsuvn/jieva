package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.VirtualInvoker
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.virtualInvoker
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.reflect.findField
import xyz.srclab.common.reflect.genericTypeFor
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
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

        private val permitAllPropertiesFilter: (name: String) -> Boolean = { _ -> true }
        private val ignoreNullFilter: (name: String, value: Any?) -> Boolean = { _, value -> value !== null }
        private val permitNullFilter: (name: String, value: Any?) -> Boolean = { _, _ -> true }

        @JvmStatic
        fun resolve(type: Class<*>): BeanDef {
            return BeanDefImpl(type)
        }

        @JvmStatic
        fun toMap(bean: Any): Map<String, Any?> {
            val def = resolve(bean.javaClass)
            val map = mutableMapOf<String, Any?>()
            for (entry in def.properties) {
                val property = entry.value
                if (property.isReadable) {
                    map[property.name] = property.getValue(bean)
                }
            }
            return map.toMap()
        }

        @JvmStatic
        fun <K, V> toMap(bean: Any, typeRef: TypeRef<Map<K, V>>): Map<K, V> {
            return toMap(bean).asAny()
        }

        @JvmStatic
        fun <K, V> toMap(bean: Any, typeRef: TypeRef<Map<K, V>>, converter: Converter): Map<K, V> {
            val type = typeRef.type
            val parameterizedType = type.genericTypeFor(Map::class.java)
            if (parameterizedType !is ParameterizedType) {
                throw IllegalArgumentException("typeRef must be a parameterized type of Map: $typeRef")
            }
            val keyType = parameterizedType.actualTypeArguments[0]
            val valueType = parameterizedType.actualTypeArguments[1]
            val def = resolve(bean.javaClass)
            val map = mutableMapOf<K, V>()
            for (entry in def.properties) {
                val property = entry.value
                if (property.isReadable) {
                    val beanValue = property.getValue<Any?>(bean)
                    val key = converter.convert<K>(property.name, keyType)
                    val value = converter.convert<V>(beanValue, valueType)
                    map[key] = value
                }
            }
            return map.toMap()
        }

        @JvmStatic
        @JvmOverloads
        fun copyProperties(from: Any, to: Any, ignoreNull: Boolean = false) {
            if (ignoreNull) {
                copyProperties0(from, to, permitAllPropertiesFilter, permitNullFilter, null)
            } else {
                copyProperties0(from, to, permitAllPropertiesFilter, ignoreNullFilter, null)
            }
        }

        @JvmStatic
        @JvmOverloads
        fun copyProperties(from: Any, to: Any, ignoreNull: Boolean = false, converter: Converter) {
            if (ignoreNull) {
                copyProperties0(from, to, permitAllPropertiesFilter, permitNullFilter, converter)
            } else {
                copyProperties0(from, to, permitAllPropertiesFilter, ignoreNullFilter, converter)
            }
        }

        @JvmStatic
        fun copyProperties(from: Any, to: Any, propertyNameFilter: (name: String) -> Boolean) {
            copyProperties0(from, to, propertyNameFilter, permitNullFilter, null)
        }

        @JvmStatic
        fun copyProperties(from: Any, to: Any, converter: Converter, propertyNameFilter: (name: String) -> Boolean) {
            copyProperties0(from, to, propertyNameFilter, permitNullFilter, converter)
        }

        @JvmStatic
        fun copyProperties(
            from: Any,
            to: Any,
            propertyNameFilter: (name: String) -> Boolean,
            propertyValueFilter: (name: String, value: Any?) -> Boolean,
        ) {
            copyProperties0(from, to, propertyNameFilter, propertyValueFilter, null)
        }

        @JvmStatic
        fun copyProperties(
            from: Any,
            to: Any,
            propertyNameFilter: (name: String) -> Boolean,
            propertyValueFilter: (name: String, value: Any?) -> Boolean,
            converter: Converter,
        ) {
            copyProperties0(from, to, propertyNameFilter, propertyValueFilter, converter)
        }

        private fun copyProperties0(
            from: Any,
            to: Any,
            propertyNameFilter: (name: String) -> Boolean,
            propertyValueFilter: (name: String, value: Any?) -> Boolean,
            converter: Converter?,
        ) {
            when {
                from is Map<*, *> && to is MutableMap<*, *> -> {
                    val fromDef = from.asAny<Map<Any?, Any?>>()
                    val toDef = to.asAny<MutableMap<Any?, Any?>>()
                    for (propertyEntry in fromDef) {
                        val name = propertyEntry.key.toString()
                        val value = propertyEntry.value
                        if (!propertyNameFilter(name) || !propertyValueFilter(name, value) || !toDef.contains(name)) {
                            continue
                        }
                        toDef[name] = value
                    }
                }
                from is Map<*, *> && to !is MutableMap<*, *> -> {
                    val fromDef = from.asAny<Map<Any?, Any?>>()
                    val toDef = resolve(to.javaClass)
                    for (propertyEntry in fromDef) {
                        val name = propertyEntry.key.toString()
                        val value = propertyEntry.value
                        if (!propertyNameFilter(name) || !propertyValueFilter(name, value)) {
                            continue
                        }
                        val toProperty = toDef.getProperty(name)
                        if (toProperty !== null && toProperty.isWriteable) {
                            if (converter === null) {
                                toProperty.setValue(to, value)
                            } else {
                                toProperty.setValue(to, value, converter)
                            }
                        }
                    }
                }
                from !is Map<*, *> && to is MutableMap<*, *> -> {
                    val fromDef = resolve(from.javaClass)
                    val toDef = to.asAny<MutableMap<Any?, Any?>>()
                    for (propertyEntry in fromDef.properties) {
                        val fromProperty = propertyEntry.value
                        val name = fromProperty.name
                        if (!propertyNameFilter(name) || !fromProperty.isReadable) {
                            continue
                        }
                        val value = fromProperty.getValue<Any?>(from)
                        if (!propertyValueFilter(name, value) || !toDef.contains(name)) {
                            continue
                        }
                        toDef[name] = value
                    }
                }
                from !is Map<*, *> && to !is MutableMap<*, *> -> {
                    val fromDef = resolve(from.javaClass)
                    val toDef = resolve(to.javaClass)
                    for (propertyEntry in fromDef.properties) {
                        val fromProperty = propertyEntry.value
                        val name = fromProperty.name
                        if (!propertyNameFilter(name) || !fromProperty.isReadable) {
                            continue
                        }
                        val toProperty = toDef.getProperty(name)
                        if (toProperty !== null && toProperty.isWriteable) {
                            val value = fromProperty.getValue<Any?>(from)
                            if (!propertyValueFilter(name, value)) {
                                continue
                            }
                            if (converter === null) {
                                toProperty.setValue(to, value)
                            } else {
                                toProperty.setValue(to, value, converter)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Class<*>.resolveBean(): BeanDef {
    return BeanDef.resolve(this)
}

fun Any.beanToMap(): Map<String, Any?> {
    return BeanDef.toMap(this)
}

fun <K, V> Any.beanToMap(typeRef: TypeRef<Map<K, V>>): Map<K, V> {
    return BeanDef.toMap(this, typeRef)
}

fun <K, V> Any.beanToMap(typeRef: TypeRef<Map<K, V>>, converter: Converter): Map<K, V> {
    return BeanDef.toMap(this, typeRef, converter)
}

fun Any.copyProperties(to: Any, ignoreNull: Boolean = false) {
    BeanDef.copyProperties(this, to, ignoreNull)
}

fun Any.copyProperties(to: Any, ignoreNull: Boolean = false, converter: Converter) {
    BeanDef.copyProperties(this, to, ignoreNull, converter)
}

fun Any.copyProperties(to: Any, propertyNameFilter: (name: String) -> Boolean) {
    BeanDef.copyProperties(this, to, propertyNameFilter)
}

fun Any.copyProperties(to: Any, converter: Converter, propertyNameFilter: (name: String) -> Boolean) {
    BeanDef.copyProperties(this, to, converter, propertyNameFilter)
}

fun Any.copyProperties(
    to: Any,
    propertyNameFilter: (name: String) -> Boolean,
    propertyValueFilter: (name: String, value: Any?) -> Boolean,
) {
    BeanDef.copyProperties(this, to, propertyNameFilter, propertyValueFilter)
}

fun Any.copyProperties(
    to: Any,
    propertyNameFilter: (name: String) -> Boolean,
    propertyValueFilter: (name: String, value: Any?) -> Boolean,
    converter: Converter,
) {
    BeanDef.copyProperties(this, to, propertyNameFilter, propertyValueFilter, converter)
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

    fun <T> getValue(bean: Any): T

    fun setValue(bean: Any, value: Any?)

    fun setValue(bean: Any, value: Any?, converter: Converter) {
        setValue(bean, converter.convert(value, genericType))
    }
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

    override fun <T> getValue(bean: Any): T {
        val g = getter
        return if (g !== null) {
            g.invokeVirtual(bean)
        } else {
            throw UnsupportedOperationException("This property is not readable: $name")
        }
    }

    override fun setValue(bean: Any, value: Any?) {
        val s = setter
        if (s !== null) {
            s.invokeVirtual<Any?>(bean, value)
        } else {
            throw UnsupportedOperationException("This property is not writeable: $name")
        }
    }
}