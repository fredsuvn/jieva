package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.VirtualInvoker
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.virtualInvoker
import xyz.srclab.common.collection.MapSchema
import xyz.srclab.common.collection.resolveMapSchema
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.convert.convertTo
import xyz.srclab.common.reflect.TypeRef
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

    interface CopyOptions {

        fun filterProperty(name: Any?): Boolean = true

        fun filterProperty(name: Any?, value: Any?): Boolean = true

        fun filterProperty(name: Any?, value: Any?, targetValueType: Type): Boolean = true

        fun filterProperty(name: Any?, value: Any?, targetKeyType: Type, targetValueType: Type): Boolean = true

        fun convertName(name: Any?, value: Any?, targetKeyType: Type): Any? {
            return name.convertTo(targetKeyType)
        }

        fun convertValue(name: Any?, value: Any?, targetValueType: Type): Any? {
            return value.convertTo(targetValueType)
        }

        companion object {

            @JvmField
            val DEFAULT = object : CopyOptions {}

            @JvmField
            val IGNORE_NULL = object : CopyOptions {
                override fun filterProperty(name: Any?, value: Any?): Boolean {
                    return value !== null
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun resolve(type: Type): BeanDef {
            return BeanDefImpl(type)
        }

        @JvmStatic
        fun toMap(bean: Any): Map<String, Any?> {
            val def = resolve(bean.javaClass)
            val result = mutableMapOf<String, Any?>()
            for (entry in def.properties) {
                val property = entry.value
                if (!property.isReadable) {
                    continue
                }
                result[property.name] = property.getValue(bean)
            }
            return result.toMap()
        }

        @JvmStatic
        fun <K, V> toMap(bean: Any, type: Type): Map<K, V> {
            return toMap(bean, type.resolveMapSchema(), CopyOptions.DEFAULT)
        }

        @JvmStatic
        fun <K, V> toMap(bean: Any, typeRef: TypeRef<Map<K, V>>): Map<K, V> {
            return toMap(bean, typeRef.type.resolveMapSchema(), CopyOptions.DEFAULT)
        }

        @JvmStatic
        fun <K, V> toMap(bean: Any, mapSchema: MapSchema, copyOptions: CopyOptions): Map<K, V> {
            val def = resolve(bean.javaClass)
            val result = mutableMapOf<K, V>()
            for (entry in def.properties) {
                val property = entry.value
                if (!property.isReadable) {
                    continue
                }
                val name = property.name
                if (!copyOptions.filterProperty(name)) {
                    continue
                }
                val value = property.getValue<Any?>(bean)
                if (!copyOptions.filterProperty(name, value)
                    || !copyOptions.filterProperty(name, value, mapSchema.keyType, mapSchema.valueType)
                ) {
                    continue
                }
                val mapKey = copyOptions.convertName(name, value, mapSchema.keyType).asAny<K>()
                val mapValue = copyOptions.convertValue(name, value, mapSchema.valueType).asAny<V>()
                result[mapKey] = mapValue
            }
            return result.toMap()
        }

        @JvmStatic
        @JvmOverloads
        fun <T : Any> propertiesToBean(from: Any, to: T, ignoreNull: Boolean = false): T {
            return if (ignoreNull) {
                propertiesToBean(from, to, CopyOptions.IGNORE_NULL)
            } else {
                propertiesToBean(from, to, CopyOptions.DEFAULT)
            }
        }

        @JvmStatic
        fun <T : Any> propertiesToBean(from: Any, to: T, copyOptions: CopyOptions): T {
            when (from) {
                is Map<*, *> -> {
                    val fromDef = from.asAny<Map<Any?, Any?>>()
                    val toDef = resolve(to.javaClass)
                    for (propertyEntry in fromDef) {
                        val name = propertyEntry.key.toString()
                        val value = propertyEntry.value
                        if (!copyOptions.filterProperty(name)
                            || !copyOptions.filterProperty(name, value)
                        ) {
                            continue
                        }
                        val toProperty = toDef.getProperty(name)
                        if (toProperty === null || !toProperty.isWriteable) {
                            continue
                        }
                        val toPropertyType = toProperty.genericType
                        if (!copyOptions.filterProperty(name, value, toPropertyType)) {
                            continue
                        }
                        toProperty.setValue(to, copyOptions.convertValue(name, value, toPropertyType))
                    }
                }
                !is Map<*, *> -> {
                    val fromDef = resolve(from.javaClass)
                    val toDef = resolve(to.javaClass)
                    for (propertyEntry in fromDef.properties) {
                        val fromProperty = propertyEntry.value
                        if (!fromProperty.isReadable) {
                            continue
                        }
                        val name = fromProperty.name
                        if (!copyOptions.filterProperty(name)) {
                            continue
                        }
                        val toProperty = toDef.getProperty(name)
                        if (toProperty === null || !toProperty.isWriteable) {
                            continue
                        }
                        val value = fromProperty.getValue<Any?>(from)
                        if (!copyOptions.filterProperty(name, value)) {
                            continue
                        }
                        val toPropertyType = toProperty.genericType
                        if (!copyOptions.filterProperty(name, value, toPropertyType)) {
                            continue
                        }
                        toProperty.setValue(to, copyOptions.convertValue(name, value, toPropertyType))
                    }
                }
            }
            return to
        }

        @JvmStatic
        @JvmOverloads
        fun <M : MutableMap<String, Any?>> propertiesToMap(from: Any, to: M, ignoreNull: Boolean = false): M {
            return if (ignoreNull) {
                propertiesToMap(from, to, MapSchema.BEAN_PATTERN, CopyOptions.IGNORE_NULL)
            } else {
                propertiesToMap(from, to, MapSchema.BEAN_PATTERN, CopyOptions.DEFAULT)
            }
        }

        @JvmStatic
        fun <K, V, M : MutableMap<K, V>> propertiesToMap(
            from: Any,
            to: M,
            toSchema: MapSchema,
            copyOptions: CopyOptions
        ): M {
            when (from) {
                is Map<*, *> -> {
                    val fromDef = from.asAny<Map<Any?, Any?>>()
                    for (propertyEntry in fromDef) {
                        val name = propertyEntry.key.toString()
                        val value = propertyEntry.value
                        if (!copyOptions.filterProperty(name)
                            || !copyOptions.filterProperty(name, value)
                            || !copyOptions.filterProperty(name, value, toSchema.keyType, toSchema.valueType)
                        ) {
                            continue
                        }
                        val mapKey = copyOptions.convertName(name, value, toSchema.keyType).asAny<K>()
                        if (!to.containsKey(mapKey)) {
                            continue
                        }
                        val mapValue = copyOptions.convertValue(name, value, toSchema.valueType).asAny<V>()
                        to[mapKey] = mapValue
                    }
                }
                !is Map<*, *> -> {
                    val fromDef = resolve(from.javaClass)
                    for (propertyEntry in fromDef.properties) {
                        val fromProperty = propertyEntry.value
                        if (!fromProperty.isReadable) {
                            continue
                        }
                        val name = fromProperty.name
                        if (!copyOptions.filterProperty(name)) {
                            continue
                        }
                        val value = fromProperty.getValue<Any?>(from)
                        if (!copyOptions.filterProperty(name, value)
                            || !copyOptions.filterProperty(name, value, toSchema.keyType, toSchema.valueType)
                        ) {
                            continue
                        }
                        val mapKey = copyOptions.convertName(name, value, toSchema.keyType).asAny<K>()
                        if (!to.containsKey(mapKey)) {
                            continue
                        }
                        val mapValue = copyOptions.convertValue(name, value, toSchema.valueType).asAny<V>()
                        to[mapKey] = mapValue
                    }
                }
            }
            return to
        }
    }
}

fun Type.resolveBean(): BeanDef {
    return BeanDef.resolve(this)
}

fun Any.beanToMap(): Map<String, Any?> {
    return BeanDef.toMap(this)
}

fun <K, V> Any.beanToMap(type: Type): Map<K, V> {
    return BeanDef.toMap(this, type)
}

fun <K, V> Any.beanToMap(typeRef: TypeRef<Map<K, V>>): Map<K, V> {
    return BeanDef.toMap(this, typeRef)
}

fun <K, V> Any.beanToMap(mapSchema: MapSchema, copyOptions: BeanDef.CopyOptions): Map<K, V> {
    return BeanDef.toMap(this, mapSchema, copyOptions)
}

fun <T : Any> Any.propertiesToBean(to: T, ignoreNull: Boolean = false): T {
    return BeanDef.propertiesToBean(this, to, ignoreNull)
}

fun <T : Any> Any.propertiesToBean(to: T, converter: Converter): T {
    return BeanDef.propertiesToBean(this, to)
}

fun <T : Any> Any.propertiesToBean(to: T, toType: Type, converter: Converter): T {
    return BeanDef.propertiesToBean(this, to)
}

fun <T : Any> Any.propertiesToBean(to: T, fromType: Type, toType: Type, converter: Converter): T {
    return BeanDef.propertiesToBean(this, to)
}

fun <T : Any> Any.propertiesToBean(to: T, copyOptions: BeanDef.CopyOptions): T {
    return BeanDef.propertiesToBean(this, to, copyOptions)
}

fun <M : MutableMap<String, Any?>> Any.propertiesToMap(to: M, ignoreNull: Boolean = false): M {
    return BeanDef.propertiesToMap(this, to, ignoreNull)
}

fun <K, V, M : MutableMap<K, V>> Any.propertiesToMap(to: M, toType: Type, converter: Converter): M {
    return BeanDef.propertiesToBean(this, to)
}

fun <K, V, M : MutableMap<K, V>> Any.propertiesToMap(to: M, fromType: Type, toType: Type, converter: Converter): M {
    return BeanDef.propertiesToBean(this, to)
}

fun <K, V, M : MutableMap<K, V>> Any.propertiesToMap(
    to: M,
    toSchema: MapSchema,
    copyOptions: BeanDef.CopyOptions = BeanDef.CopyOptions.DEFAULT
): M {
    return BeanDef.propertiesToBean(this, to, copyOptions)
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