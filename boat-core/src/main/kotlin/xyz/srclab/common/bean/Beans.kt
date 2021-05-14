@file:JvmName("Beans")
@file:JvmMultifileClass

package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.collect.MapType
import xyz.srclab.common.collect.MapType.Companion.toMapType
import xyz.srclab.common.convert.Converter
import java.lang.reflect.Type

private val defaultResolver = BeanResolver.DEFAULT

fun Type.resolve(): BeanType {
    return defaultResolver.resolve(this)
}

fun Any.asMap(
    beanResolver: BeanResolver,
    converter: Converter
): MutableMap<String, Any?> {
    return asMap(
        BeanCopyOptions.DEFAULT.toBuilder()
            .beanResolver(beanResolver)
            .converter(converter)
            .build()
    )
}

@JvmOverloads
fun Any.asMap(
    copyOptions: BeanCopyOptions = BeanCopyOptions.DEFAULT
): MutableMap<String, Any?> {
    return BeanAsMap(this, copyOptions)
}

fun <T : Any> Any.copyProperties(@Written to: T): T {
    return copyProperties(to, BeanCopyOptions.DEFAULT)
}

fun <T : Any> Any.copyProperties(@Written to: T, fromType: Type, toType: Type): T {
    return copyProperties(to, BeanCopyOptions.DEFAULT.withFromToTypes(fromType, toType))
}

fun <T : Any> Any.copyPropertiesIgnoreNull(@Written to: T): T {
    return copyProperties(to, BeanCopyOptions.IGNORE_NULL)
}

fun <T : Any> Any.copyPropertiesIgnoreNull(@Written to: T, fromType: Type, toType: Type): T {
    return copyProperties(to, BeanCopyOptions.IGNORE_NULL.withFromToTypes(fromType, toType))
}

fun <T : Any> Any.copyProperties(@Written to: T, copyOptions: BeanCopyOptions): T {
    val beanResolver: BeanResolver = copyOptions.beanResolver ?: BeanResolver.DEFAULT
    val converter: Converter = copyOptions.converter ?: Converter.DEFAULT
    return when {
        this is Map<*, *> && to is MutableMap<*, *> -> {
            val fromType = copyOptions.fromType ?: Map::class.java
            val fromMapType = fromType.toMapType()
            val toType = copyOptions.toType ?: Map::class.java
            val toMapType = toType.toMapType()
            this.forEach { (k, v) ->
                if (!copyOptions.nameFilter(k)
                    || !copyOptions.fromTypeFilter(k, fromMapType.keyType, fromMapType.valueType)
                    || !copyOptions.fromValueFilter(k, fromMapType.keyType, fromMapType.valueType, v)
                    || !copyOptions.convertFilter(
                        k,
                        fromMapType.keyType,
                        fromMapType.valueType,
                        v,
                        toMapType.keyType,
                        toMapType.valueType
                    )
                ) {
                    return@forEach
                }
                val toKey = converter.convert<Any>(k, fromMapType.keyType, toMapType.keyType)
                if (!copyOptions.putPropertyForMap && !to.containsKey(toKey)) {
                    return@forEach
                }
                (to.asAny<MutableMap<Any, Any?>>())[toKey] =
                    converter.convert(v, fromMapType.valueType, toMapType.valueType)
            }
            to
        }
        this is Map<*, *> && to !is Map<*, *> -> {
            val fromType = copyOptions.fromType ?: Map::class.java
            val fromMapType = fromType.toMapType()
            val toType = beanResolver.resolve(copyOptions.toType ?: to.javaClass)
            val toProperties = toType.properties
            this.forEach { (k, v) ->
                if (!copyOptions.nameFilter(k)
                    || !copyOptions.fromTypeFilter(k, fromMapType.keyType, fromMapType.valueType)
                    || !copyOptions.fromValueFilter(k, fromMapType.keyType, fromMapType.valueType, v)
                ) {
                    return@forEach
                }
                val toPropertyName =
                    converter.convert<String>(k, fromMapType.keyType, String::class.java)
                val toProperty = toProperties[toPropertyName]
                if (toProperty === null || !toProperty.isWriteable) {
                    return@forEach
                }
                if (!copyOptions.convertFilter(
                        k,
                        fromMapType.keyType,
                        fromMapType.valueType,
                        v,
                        String::class.java,
                        toProperty.type
                    )
                ) {
                    return@forEach
                }
                toProperty.setValue<Any?>(
                    to, converter.convert(v, fromMapType.valueType, toProperty.type)
                )
            }
            to
        }
        this !is Map<*, *> && to is Map<*, *> -> {
            val fromType = beanResolver.resolve(copyOptions.fromType ?: this.javaClass)
            val toType = copyOptions.toType ?: Map::class.java
            val toMapType = toType.toMapType()
            val fromProperties = fromType.properties
            fromProperties.forEach { (name, fromProperty) ->
                if (!copyOptions.includeClassProperty && name == "class") {
                    return@forEach
                }
                if (!copyOptions.nameFilter(name)
                    || !fromProperty.isReadable
                    || !copyOptions.fromTypeFilter(name, String::class.java, fromProperty.type)
                ) {
                    return@forEach
                }
                val value = fromProperty.getValue<Any?>(this)
                if (!copyOptions.fromValueFilter(name, String::class.java, fromProperty.type, value)
                    || !copyOptions.convertFilter(
                        name,
                        String::class.java,
                        fromProperty.type,
                        value,
                        toMapType.keyType,
                        toMapType.valueType
                    )
                ) {
                    return@forEach
                }
                val toKey = converter.convert<Any>(name, String::class.java, toMapType.keyType)
                if (!copyOptions.putPropertyForMap && !to.containsKey(toKey)) {
                    return@forEach
                }
                (to.asAny<MutableMap<Any, Any?>>())[toKey] =
                    converter.convert(value, fromProperty.type, toMapType.valueType)
            }
            to
        }
        this !is Map<*, *> && to !is Map<*, *> -> {
            val fromType = beanResolver.resolve(copyOptions.fromType ?: this.javaClass)
            val toType = beanResolver.resolve(copyOptions.toType ?: to.javaClass)
            val fromProperties = fromType.properties
            val toProperties = toType.properties
            fromProperties.forEach { (name, fromProperty) ->
                if (!copyOptions.includeClassProperty && name == "class") {
                    return@forEach
                }
                if (!copyOptions.nameFilter(name)
                    || !fromProperty.isReadable
                    || !copyOptions.fromTypeFilter(name, String::class.java, fromProperty.type)
                ) {
                    return@forEach
                }
                val toProperty = toProperties[name]
                if (toProperty === null || !toProperty.isWriteable) {
                    return@forEach
                }
                val value = fromProperty.getValue<Any?>(this)
                if (!copyOptions.fromValueFilter(name, String::class.java, fromProperty.type, value)
                    || !copyOptions.convertFilter(
                        name,
                        String::class.java,
                        fromProperty.type,
                        value,
                        String::class.java,
                        toProperty.type
                    )
                ) {
                    return@forEach
                }
                toProperty.setValue<Any?>(
                    to, converter.convert(value, fromProperty.type, toProperty.type)
                )
            }
            to
        }
        else -> throw IllegalStateException("Unknown type, failed to copy properties from $this to $to.")
    }
}

private class BeanAsMap(
    private val bean: Any,
    private val copyOptions: BeanCopyOptions
) : AbstractMutableMap<String, Any?>() {

    private val mapType = copyOptions.toType?.toMapType() ?: MapType.RAW
    private val converter: Converter = copyOptions.converter ?: Converter.DEFAULT

    private val properties: Map<String, PropertyType> = run {
        val beanResolver: BeanResolver = copyOptions.beanResolver ?: BeanResolver.DEFAULT
        beanResolver.resolve(bean.javaClass).properties
    }

    override val size: Int
        get() = entries.size

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> by lazy {
        properties.entries
            .filter {
                val flag = it.value.isReadable
                    && copyOptions.nameFilter(it.key)
                    && copyOptions.fromTypeFilter(it.key, String::class.java, it.value.type)
                if (!flag) {
                    return@filter false
                }
                val value = it.value.getValue<Any?>(bean)
                copyOptions.fromValueFilter(
                    it.key,
                    String::class.java,
                    it.value.type,
                    value
                )
                    && copyOptions.convertFilter(
                    it.key,
                    String::class.java,
                    it.value.type,
                    value,
                    mapType.keyType,
                    mapType.valueType
                )
            }
            .mapTo(LinkedHashSet()) {
                object : MutableMap.MutableEntry<String, Any?> {
                    override val key: String = it.key
                    override val value: Any?
                        get() = converter.convert(it.value.getValue(bean), mapType.valueType)

                    override fun setValue(newValue: Any?): Any? {
                        return it.value.setValue(
                            bean,
                            converter.convert(newValue, mapType.valueType)
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
        return converter.convert(propertyType.getValue(bean), mapType.valueType)
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
        return propertyType.setValue(bean, value)
    }

    override fun remove(key: String): Any? {
        throw UnsupportedOperationException()
    }
}