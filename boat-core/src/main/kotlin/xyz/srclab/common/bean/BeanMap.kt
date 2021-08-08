package xyz.srclab.common.bean

import xyz.srclab.common.convert.Converter
import xyz.srclab.common.lang.asAny
import java.lang.reflect.Type

/**
 * A [Map] which is associated with a `bean`,
 * of which keys are properties' names of the bean, values are properties' value.
 */
open class BeanMap<T>(
    private val bean: Any,
    private val valueType: Type,
    beanResolver: BeanResolver,
    private val converter: Converter
) : AbstractMutableMap<String, T>() {

    @get:JvmName("beanType")
    val beanType: BeanType = beanResolver.resolve(bean.javaClass)

    private val properties: Map<String, PropertyType> =
        beanType.properties.filter { it.key != "class" }

    override val size: Int
        get() = entries.size

    override val entries: MutableSet<MutableMap.MutableEntry<String, T>> by lazy {
        properties.entries
            .filter { it.value.isReadable }
            .mapTo(LinkedHashSet()) {
                val propertyType = it.value
                object : MutableMap.MutableEntry<String, Any?> {

                    override val key: String = it.key

                    override val value: Any?
                        get() = converter.convert(propertyType.getValue(bean), valueType)

                    override fun setValue(newValue: Any?): Any? {
                        return propertyType.setValueAndReturnOld(
                            bean,
                            converter.convert(newValue, propertyType.type)
                        )
                    }
                }
            }
            .asAny()
    }

    override fun containsKey(key: String): Boolean {
        return properties.containsKey(key)
    }

    override fun get(key: String): T {
        val propertyType = properties[key]
        if (propertyType === null) {
            return null.asAny()
        }
        return converter.convert(propertyType.getValue(bean), valueType)
    }

    override fun isEmpty(): Boolean {
        return properties.isEmpty()
    }

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun put(key: String, value: T): T {
        val propertyType = properties[key]
        if (propertyType === null) {
            throw UnsupportedOperationException("Property $key doesn't exist.")
        }
        return propertyType.setValueAndReturnOld<T>(
            bean,
            converter.convert(value, propertyType.type)
        ).asAny()
    }

    override fun remove(key: String): T {
        throw UnsupportedOperationException()
    }
}