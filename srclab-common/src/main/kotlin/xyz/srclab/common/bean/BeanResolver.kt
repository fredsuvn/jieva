package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.convert.Converter
import java.lang.reflect.Type

/**
 * @author sunqian
 */
interface BeanResolver {

    fun resolve(type: Type): BeanSchema

    fun asMap(bean: Any): MutableMap<String, Any?> {
        return asMap(bean, CopyOptions.DEFAULT)
    }

    fun asMap(bean: Any, copyOptions: CopyOptions): MutableMap<String, Any?> {
        return BeanAsMap(bean, resolve(bean.javaClass).properties, copyOptions.converter)
    }

    fun <T : Any> copyProperties(from: Any, to: T): T {
        return copyProperties(from, to, CopyOptions.DEFAULT)
    }

    fun <T : Any> copyProperties(from: Any, to: T, converter: Converter): T {
        return copyProperties(from, to, CopyOptions.DEFAULT.withConverter(converter))
    }

    fun <T : Any> copyProperties(from: Any, to: T, fromType: Type, toType: Type, converter: Converter): T {
        return copyProperties(from, to, CopyOptions.DEFAULT.with(fromType, toType, converter))
    }

    fun <T : Any> copyPropertiesIgnoreNull(from: Any, to: T): T {
        return copyProperties(from, to, CopyOptions.IGNORE_NULL)
    }

    fun <T : Any> copyPropertiesIgnoreNull(from: Any, to: T, converter: Converter): T {
        return copyProperties(from, to, CopyOptions.IGNORE_NULL.withConverter(converter))
    }

    fun <T : Any> copyPropertiesIgnoreNull(from: Any, to: T, fromType: Type, toType: Type, converter: Converter): T {
        return copyProperties(from, to, CopyOptions.IGNORE_NULL.with(fromType, toType, converter))
    }

    fun <T : Any> copyProperties(from: Any, to: T, copyOptions: CopyOptions): T {

    }

    interface CopyOptions {

        @Suppress(INAPPLICABLE_JVM_NAME)
        val fromType: Type?
            @JvmName("fromType") get() = null

        @Suppress(INAPPLICABLE_JVM_NAME)
        val toType: Type?
            @JvmName("toType") get() = null

        @Suppress(INAPPLICABLE_JVM_NAME)
        val converter: Converter
            @JvmName("converter") get() = Converter.DEFAULT

        fun filterProperty(name: Any?): Boolean = true

        fun filterProperty(name: Any?, value: Any?): Boolean = true

        fun filterProperty(name: Any?, value: Any?, targetNameType: Type, targetValueType: Type): Boolean = true

        fun withConverter(converter: Converter): CopyOptions {
            return with(this.fromType, this.toType, converter)
        }

        fun with(fromType: Type?, toType: Type?, converter: Converter): CopyOptions {
            return copyOptionsWith(this, fromType, toType, converter)
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

            @JvmStatic
            @JvmOverloads
            fun copyOptionsWith(
                baseCopyOptions: CopyOptions = DEFAULT,
                fromType: Type?,
                toType: Type?,
                converter: Converter
            ): CopyOptions {
                return CopyOptionsWith(baseCopyOptions, fromType, toType, converter)
            }

            private class CopyOptionsWith(
                baseCopyOptions: CopyOptions,
                override val fromType: Type?,
                override val toType: Type?,
                override val converter: Converter
            ) : CopyOptions by baseCopyOptions
        }
    }

    companion object {

        @JvmField
        val DEFAULT: BeanResolver = (null as BeanResolver)

        private class BeanAsMap(
            private val bean: Any,
            private val properties: Map<String, PropertySchema>,
            private val converter: Converter
        ) : AbstractMutableMap<String, Any?>() {

            override val size: Int
                get() = properties.size

            override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> by lazy {
                properties.entries.mapTo(LinkedHashSet()) {
                    object : MutableMap.MutableEntry<String, Any?> {
                        override val key: String = it.key
                        override val value: Any?
                            get() = it.value.getValue(bean)

                        override fun setValue(newValue: Any?): Any? {
                            return it.value.setValue(bean, newValue, converter)
                        }
                    }
                }
            }

            override fun containsKey(key: String): Boolean {
                return properties.containsKey(key)
            }

            override fun get(key: String): Any? {
                val propertySchema = properties[key]
                return propertySchema?.getValue(bean)
            }

            override fun isEmpty(): Boolean {
                return properties.isEmpty()
            }

            override fun clear() {
                throw UnsupportedOperationException()
            }

            override fun put(key: String, value: Any?): Any? {
                val propertySchema = properties[key]
                if (propertySchema === null) {
                    throw UnsupportedOperationException("Property $key doesn't exist.")
                }
                return propertySchema.setValue(bean, value, converter)
            }

            override fun remove(key: String): Any? {
                throw UnsupportedOperationException()
            }
        }
    }
}
