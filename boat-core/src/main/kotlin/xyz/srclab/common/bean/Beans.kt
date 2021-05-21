@file:JvmName("Beans")
@file:JvmMultifileClass

package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.collect.MapType.Companion.toMapType
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.lang.asAny
import java.lang.reflect.Type

@JvmOverloads
fun Type.resolve(beanResolver: BeanResolver = BeanResolver.DEFAULT): BeanType {
    return beanResolver.resolve(this)
}

@JvmOverloads
fun <V> Any.asMap(
    valueType: Type = Any::class.java,
    beanResolver: BeanResolver = BeanResolver.DEFAULT,
    converter: Converter = Converter.DEFAULT
): MutableMap<String, V> {
    return BeanAsMap(this, valueType, beanResolver, converter).asAny()
}

@JvmOverloads
fun <T : Any> Any.copyProperties(
    @Written to: T,
    beanResolver: BeanResolver = BeanResolver.DEFAULT,
    converter: Converter = Converter.DEFAULT,
): T {
    return copyProperties(to, true, beanResolver, converter)
}

@JvmOverloads
fun <T : Any> Any.copyProperties(
    @Written to: T,
    toType: Type,
    beanResolver: BeanResolver = BeanResolver.DEFAULT,
    converter: Converter = Converter.DEFAULT,
): T {
    return copyProperties(to, toType, true, beanResolver, converter)
}

@JvmOverloads
fun <T : Any> Any.copyProperties(
    @Written to: T,
    copyNull: Boolean,
    beanResolver: BeanResolver = BeanResolver.DEFAULT,
    converter: Converter = Converter.DEFAULT,
): T {
    return copyProperties(to, to.javaClass, copyNull, beanResolver, converter)
}

@JvmOverloads
fun <T : Any> Any.copyProperties(
    @Written to: T,
    toType: Type,
    copyNull: Boolean,
    beanResolver: BeanResolver = BeanResolver.DEFAULT,
    converter: Converter = Converter.DEFAULT,
): T {
    return when {
        this is Map<*, *> && to is MutableMap<*, *> -> {
            val toMapType = toType.toMapType()
            this.forEach { (k, v) ->
                if (v === null && !copyNull) {
                    return@forEach
                }
                val toKey = converter.convert<Any>(k, toMapType.keyType)
                (to.asAny<MutableMap<Any, Any?>>())[toKey] = converter.convert(v, toMapType.valueType)
            }
            to
        }
        this is Map<*, *> && to !is Map<*, *> -> {
            val toBeanType = beanResolver.resolve(toType)
            val toProperties = toBeanType.properties
            this.forEach { (k, v) ->
                if (v === null && !copyNull) {
                    return@forEach
                }
                val toPropertyName = converter.convert<Any>(k, String::class.java)
                val toProperty = toProperties[toPropertyName]
                if (toProperty === null || !toProperty.isWriteable) {
                    return@forEach
                }
                toProperty.setValue(to, converter.convert(v, toProperty.type))
            }
            to
        }
        this !is Map<*, *> && to is MutableMap<*, *> -> {
            val fromBeanType = beanResolver.resolve(this.javaClass)
            val fromProperties = fromBeanType.properties
            val toMapType = toType.toMapType()
            fromProperties.forEach { (name, fromProperty) ->
                if (!fromProperty.isReadable) {
                    return@forEach
                }
                val value = fromProperty.getValue<Any?>(this)
                val toKey = converter.convert<Any>(name, toMapType.keyType)
                (to.asAny<MutableMap<Any, Any?>>())[toKey] = converter.convert(value, toMapType.valueType)
            }
            to
        }
        this !is Map<*, *> && to !is Map<*, *> -> {
            val fromBeanType = beanResolver.resolve(this.javaClass)
            val fromProperties = fromBeanType.properties
            val toBeanType = beanResolver.resolve(toType)
            val toProperties = toBeanType.properties
            fromProperties.forEach { (name, fromProperty) ->
                if (!fromProperty.isReadable) {
                    return@forEach
                }
                val toProperty = toProperties[name]
                if (toProperty === null || !toProperty.isWriteable) {
                    return@forEach
                }
                val value = fromProperty.getValue<Any?>(this)
                toProperty.setValue(to, converter.convert(value, toProperty.type))
            }
            to
        }
        else -> throw IllegalStateException("Unknown type, failed to copy properties from $this to $to.")
    }
}

private class BeanAsMap(
    private val bean: Any,
    private val valueType: Type,
    private val beanResolver: BeanResolver,
    private val converter: Converter
) : AbstractMutableMap<String, Any?>() {

    private val properties: Map<String, PropertyType> = run {
        beanResolver.resolve(bean.javaClass).properties
    }

    override val size: Int
        get() = entries.size

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> by lazy {
        properties.entries
            .filter { it.value.isReadable }
            .mapTo(LinkedHashSet()) {
                object : MutableMap.MutableEntry<String, Any?> {

                    override val key: String = it.key

                    override val value: Any?
                        get() = converter.convert(it.value.getValue(bean), valueType)

                    override fun setValue(newValue: Any?): Any? {
                        return it.value.setValueAndReturnOld(
                            bean,
                            converter.convert(newValue, valueType)
                        )
                    }
                }
            }
    }

    override fun containsKey(key: String): Boolean {
        return properties.containsKey(key)
    }

    override fun get(key: String): Any? {
        val propertyType = properties[key]
        if (propertyType === null) {
            return null
        }
        return converter.convert(propertyType.getValue(bean), valueType)
    }

    override fun isEmpty(): Boolean {
        return properties.isEmpty()
    }

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun put(key: String, value: Any?): Any? {
        val propertyType = properties[key]
        if (propertyType === null) {
            throw UnsupportedOperationException("Property $key doesn't exist.")
        }
        return propertyType.setValueAndReturnOld(bean, value)
    }

    override fun remove(key: String): Any? {
        throw UnsupportedOperationException()
    }
}