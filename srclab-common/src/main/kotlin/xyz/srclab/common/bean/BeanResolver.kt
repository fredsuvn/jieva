package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.convert.convertTo
import java.lang.reflect.Type

/**
 * @author sunqian
 */
interface BeanResolver {

    fun resolve(type: Type): BeanSchema

    fun asMap(bean: Any): MutableMap<String, Any?> {
        return asMap(bean, CopyOptions.DEFAULT)
    }

    fun asMap(bean: Any, copyOptions: CopyOptions): MutableMap<String, Any?>

    fun <T : Any> copyProperties(from: Any, to: T): T {
        return copyProperties(from, to, CopyOptions.DEFAULT)
    }

    fun <T : Any> copyPropertiesIgnoreNull(from: Any, to: T): T {
        return copyProperties(from, to, CopyOptions.IGNORE_NULL)
    }

    fun <T : Any> copyProperties(from: Any, to: T, copyOptions: CopyOptions): T

    interface CopyOptions {

        @Suppress(INAPPLICABLE_JVM_NAME)
        val fromType: Type?
            @JvmName("fromType") get() = null

        @Suppress(INAPPLICABLE_JVM_NAME)
        val toType: Type?
            @JvmName("toType") get() = null

        fun filterProperty(name: Any?): Boolean = true

        fun filterProperty(name: Any?, value: Any?): Boolean = true

        fun filterProperty(name: Any?, value: Any?, targetNameType: Type, targetValueType: Type): Boolean = true

        fun convertName(name: Any?, value: Any?, targetNameType: Type): Any? {
            return name.convertTo(targetNameType)
        }

        fun convertValue(name: Any?, value: Any?, targetValueType: Type): Any? {
            return value.convertTo(targetValueType)
        }

        fun withConverter(converter: Converter): CopyOptions {
            return with(this.fromType, this.toType, converter)
        }

        fun with(fromType: Type?, toType: Type?, converter: Converter): CopyOptions {
            return CopyOptionsWith(this, fromType, toType, converter)
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

            private class CopyOptionsWith(
                baseCopyOptions: CopyOptions,
                override val fromType: Type?,
                override val toType: Type?,
                private val converter: Converter
            ) : CopyOptions by baseCopyOptions {

                override fun convertName(name: Any?, value: Any?, targetNameType: Type): Any? {
                    return converter.convert(name, targetNameType)
                }

                override fun convertValue(name: Any?, value: Any?, targetValueType: Type): Any? {
                    return converter.convert(value, targetValueType)
                }
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT: BeanResolver = (null as BeanResolver)

        fun resolve(type: Type): BeanSchema

        fun asMap(bean: Any): MutableMap<String, Any?> {
            return asMap(bean, CopyOptions.DEFAULT)
        }

        fun asMap(bean: Any, copyOptions: CopyOptions): MutableMap<String, Any?>

        fun <T : Any> copyProperties(from: Any, to: T): T {
            return copyProperties(from, to, CopyOptions.DEFAULT)
        }

        fun <T : Any> copyPropertiesIgnoreNull(from: Any, to: T): T {
            return copyProperties(from, to, CopyOptions.IGNORE_NULL)
        }

        fun <T : Any> copyProperties(from: Any, to: T, copyOptions: CopyOptions): T
    }
}
