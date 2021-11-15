@file:JvmName("Beans")
@file:JvmMultifileClass

package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.collect.BMapType.Companion.toMapType
import xyz.srclab.common.collect.toUnmodifiable
import xyz.srclab.common.convert.Converter
import java.lang.reflect.Type

@JvmName("resolve")
@JvmOverloads
fun Type.resolveBean(beanResolver: BeanResolver = BeanResolver.DEFAULT): BeanType {
    return beanResolver.resolve(this)
}

@JvmOverloads
fun Any.asMap(beanResolver: BeanResolver = BeanResolver.DEFAULT): BeanMap {
    return BeanMap(this, beanResolver.resolve(this.javaClass))
}

fun Any.asMap(beanType: BeanType): BeanMap {
    return BeanMap(this, beanType)
}

@JvmOverloads
fun Any?.toMap(beanResolver: BeanResolver = BeanResolver.DEFAULT): Map<String, Any?> {
    if (this === null) {
        return emptyMap()
    }
    return toMutableMap(beanResolver).toUnmodifiable()
}

@JvmOverloads
fun Any?.toMutableMap(beanResolver: BeanResolver = BeanResolver.DEFAULT): MutableMap<String, Any?> {
    if (this === null) {
        return LinkedHashMap()
    }
    return LinkedHashMap(asMap(beanResolver))
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
            val toMap = to.asTyped<MutableMap<Any?, Any?>>()
            val toMapType = toType.toMapType()
            this.forEach { (k, v) ->
                if (v === null && !copyNull) {
                    return@forEach
                }
                val toKey = converter.convertOrNull<Any>(k, toMapType.keyType)
                val toValue = converter.convertOrNull<Any>(v, toMapType.valueType)
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
                val toPropertyName = converter.convertOrNull<Any>(k, String::class.java)
                if (toPropertyName === null) {
                    return@forEach
                }
                val toProperty = toProperties[toPropertyName]
                if (toProperty === null || !toProperty.isWriteable) {
                    return@forEach
                }
                toProperty.setValue(to, converter.convertOrNull(v, toProperty.type))
            }
            to
        }
        this !is Map<*, *> && to is MutableMap<*, *> -> {
            val fromBeanType = beanResolver.resolve(this.javaClass)
            val fromProperties = fromBeanType.properties
            val toMap = to.asTyped<MutableMap<Any?, Any?>>()
            val toMapType = toType.toMapType()
            fromProperties.forEach { (name, fromProperty) ->
                if (!fromProperty.isReadable) {
                    return@forEach
                }
                val value = fromProperty.getValue(this)
                if (value === null && !copyNull) {
                    return@forEach
                }
                val toKey = converter.convertOrNull<Any>(name, toMapType.keyType)
                val toValue = converter.convertOrNull<Any>(value, toMapType.valueType)
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
                toProperty.setValue(to, converter.convertOrNull(value, toProperty.type))
            }
            to
        }
        else -> throw IllegalStateException("Cannot copy properties from $this to $to.")
    }
}