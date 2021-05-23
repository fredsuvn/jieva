package xyz.srclab.common.bean

import xyz.srclab.common.convert.Converter
import java.lang.reflect.Type

open class BeanMap(
    private val bean: Any,
    private val valueType: Type,
    private val beanResolver: BeanResolver,
    private val converter: Converter
) : AbstractMutableMap<String, Any?>() {

    private val properties: Map<String, PropertyType> = run {
        beanResolver.resolve(bean.javaClass).properties.filter { it.key != "class" }
    }

    override val size: Int
        get() = entries.size

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> by lazy {
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
        return propertyType.setValueAndReturnOld(
            bean,
            converter.convert(value, propertyType.type)
        )
    }

    override fun remove(key: String): Any? {
        throw UnsupportedOperationException()
    }
}