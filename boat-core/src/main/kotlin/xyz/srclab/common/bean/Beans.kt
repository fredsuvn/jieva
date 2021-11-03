@file:JvmName("Beans")
@file:JvmMultifileClass

package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.base.asAny
import xyz.srclab.common.collect.MapType.Companion.toMapType
import xyz.srclab.common.convert.Converter
import java.lang.reflect.Type

@JvmOverloads
fun Type.resolve(beanResolver: BeanResolver = BeanResolver.DEFAULT): BeanType {
    return beanResolver.resolve(this)
}

@JvmOverloads
fun <T> Any.asMap(
    valueType: Type = Any::class.java,
    beanResolver: BeanResolver = BeanResolver.DEFAULT,
    converter: Converter = Converter.DEFAULT
): BeanMap<T> {
    return BeanMap(this, valueType, beanResolver, converter)
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
                val value = fromProperty.getValue(this)
                if (value === null && !copyNull) {
                    return@forEach
                }
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
                val value = fromProperty.getValue(this)
                if (value === null && !copyNull) {
                    return@forEach
                }
                toProperty.setValue(to, converter.convert(value, toProperty.type))
            }
            to
        }
        else -> throw IllegalStateException("Unknown type, failed to copy properties from $this to $to.")
    }
}