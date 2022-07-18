/**
 * Bean utilities.
 */
@file:JvmName("BtBean")

package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.base.asType
import xyz.srclab.common.collect.MapType.Companion.toMapType
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.convert.defaultConverter
import java.lang.reflect.Type

private var defaultResolver: BeanResolver = BeanResolver.newBeanResolver()

fun defaultResolver(): BeanResolver {
    return defaultResolver
}

fun setDefaultResolver(resolver: BeanResolver) {
    defaultResolver = resolver
}

@JvmName("resolve")
@JvmOverloads
fun Type.resolveBean(beanResolver: BeanResolver = defaultResolver): BeanType {
    return beanResolver.resolve(this)
}

@JvmOverloads
fun Any.asBeanMap(beanResolver: BeanResolver = defaultResolver): BeanMap {
    return BeanMap(this, beanResolver.resolve(this.javaClass))
}

fun Any.asBeanMap(beanType: BeanType): BeanMap {
    return BeanMap(this, beanType)
}

@JvmOverloads
fun <T : Any> Any.copyProperties(
    @Written to: T,
    beanResolver: BeanResolver = defaultResolver,
    converter: Converter = defaultConverter(),
): T {
    return copyProperties(to, true, beanResolver, converter)
}

@JvmOverloads
fun <T : Any> Any.copyProperties(
    @Written to: T,
    toType: Type,
    beanResolver: BeanResolver = defaultResolver,
    converter: Converter = defaultConverter(),
): T {
    return copyProperties(to, toType, true, beanResolver, converter)
}

@JvmOverloads
fun <T : Any> Any.copyProperties(
    @Written to: T,
    copyNull: Boolean,
    beanResolver: BeanResolver = defaultResolver,
    converter: Converter = defaultConverter(),
): T {
    return copyProperties(to, to.javaClass, copyNull, beanResolver, converter)
}

@JvmOverloads
fun <T : Any> Any.copyProperties(
    @Written to: T,
    toType: Type,
    copyNull: Boolean,
    beanResolver: BeanResolver = defaultResolver,
    converter: Converter = defaultConverter(),
): T {
    return when {
        this is Map<*, *> && to is MutableMap<*, *> -> {
            val toMap = to.asType<MutableMap<Any?, Any?>>()
            val toMapType = toType.toMapType()
            this.forEach { (k, v) ->
                if (v === null && !copyNull) {
                    return@forEach
                }
                val toKey = converter.convert<Any>(k, toMapType.keyType)
                val toValue = converter.convert<Any>(v, toMapType.valueType)
                toMap[toKey] = toValue
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
                if (toPropertyName === null) {
                    return@forEach
                }
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
            val toMap = to.asType<MutableMap<Any?, Any?>>()
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
                val toValue = converter.convert<Any>(value, toMapType.valueType)
                toMap[toKey] = toValue
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
        else -> throw IllegalStateException("Cannot copy properties from $this to $to.")
    }
}