package xyz.srclab.common.bean

import xyz.srclab.common.base.CachingProductBuilder
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.convert.Converter
import java.lang.reflect.Type

interface BeanCopyOptions {

    /**
     * Default: false.
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val includeClassProperty: Boolean
        @JvmName("includeClassProperty") get

    /**
     * Default: true.
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val putPropertyForMap: Boolean
        @JvmName("putPropertyForMap") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val fromType: Type?
        @JvmName("fromType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val toType: Type?
        @JvmName("toType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val beanResolver: BeanResolver?
        @JvmName("beanResolver") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val converter: Converter?
        @JvmName("converter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val nameFilter: (name: Any?) -> Boolean
        @JvmName("nameFilter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val fromTypeFilter: (name: Any?, fromNameType: Type, fromValueType: Type) -> Boolean
        @JvmName("fromTypeFilter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val fromValueFilter: (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean
        @JvmName("fromValueFilter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val convertFilter: (
        name: Any?,
        fromNameType: Type,
        fromValueType: Type,
        value: Any?,
        toNameType: Type,
        toValueType: Type,
    ) -> Boolean
        @JvmName("convertFilter") get

    @JvmDefault
    fun toBuilder(): Builder {
        return Builder()
            .includeClassProperty(includeClassProperty)
            .putPropertyForMap(putPropertyForMap)
            .fromType(fromType)
            .toType(toType)
            .beanResolver(beanResolver)
            .converter(converter)
            .nameFilter(nameFilter)
            .fromTypeFilter(fromTypeFilter)
            .fromValueFilter(fromValueFilter)
            .convertFilter(convertFilter)
    }

    @JvmDefault
    fun withFromType(fromType: Type?): BeanCopyOptions {
        return toBuilder().fromType(fromType).build()
    }

    @JvmDefault
    fun withToType(toType: Type?): BeanCopyOptions {
        return toBuilder().toType(toType).build()
    }

    @JvmDefault
    fun withFromToTypes(fromType: Type?, toType: Type?): BeanCopyOptions {
        return toBuilder().fromType(fromType).toType(toType).build()
    }

    @JvmDefault
    fun withBeanResolver(beanResolver: BeanResolver): BeanCopyOptions {
        return toBuilder().beanResolver(beanResolver).build()
    }

    @JvmDefault
    fun withConverter(converter: Converter): BeanCopyOptions {
        return toBuilder().converter(converter).build()
    }

    @JvmDefault
    fun withNameFilter(
        nameFilter: (name: Any?) -> Boolean
    ): BeanCopyOptions {
        return toBuilder().nameFilter(nameFilter).build()
    }

    @JvmDefault
    fun withFromTypeFilter(
        fromTypeFilter: (name: Any?, fromNameType: Type, fromValueType: Type) -> Boolean
    ): BeanCopyOptions {
        return toBuilder().fromTypeFilter(fromTypeFilter).build()
    }

    @JvmDefault
    fun withFromValueFilter(
        fromValueFilter: (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean
    ): BeanCopyOptions {
        return toBuilder().fromValueFilter(fromValueFilter).build()
    }

    @JvmDefault
    fun withConvertFilter(
        convertFilter: (
            name: Any?,
            fromNameType: Type,
            fromValueType: Type,
            value: Any?,
            toNameType: Type,
            toValueType: Type,
        ) -> Boolean
    ): BeanCopyOptions {
        return toBuilder().convertFilter(convertFilter).build()
    }

    @JvmDefault
    fun withIgnoreNull(): BeanCopyOptions {
        return toBuilder().ignoreNull().build()
    }

    class Builder : CachingProductBuilder<BeanCopyOptions>() {

        private var includeClassProperty: Boolean = false
        private var putPropertyForMap: Boolean = true
        private var fromType: Type? = null
        private var toType: Type? = null
        private var beanResolver: BeanResolver? = null
        private var converter: Converter? = null
        private var nameFilter: (name: Any?) -> Boolean = { _ -> true }
        private var fromTypeFilter: (name: Any?, fromNameType: Type, fromValueType: Type) -> Boolean =
            { _, _, _ -> true }
        private var fromValueFilter: (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean =
            { _, _, _, _ -> true }
        private var convertFilter: (
            name: Any?,
            fromNameType: Type,
            fromValueType: Type,
            value: Any?,
            toNameType: Type,
            toValueType: Type,
        ) -> Boolean = { _, _, _, _, _, _ -> true }

        fun includeClassProperty(includeClassProperty: Boolean) = apply {
            this.includeClassProperty = includeClassProperty
        }

        fun putPropertyForMap(putPropertyForMap: Boolean) = apply {
            this.putPropertyForMap = putPropertyForMap
        }

        fun fromType(fromType: Type?) = apply {
            this.fromType = fromType
        }

        fun toType(toType: Type?) = apply {
            this.toType = toType
        }

        fun beanResolver(beanResolver: BeanResolver?) = apply {
            this.beanResolver = beanResolver
        }

        fun converter(converter: Converter?) = apply {
            this.converter = converter
        }

        fun nameFilter(nameFilter: (name: Any?) -> Boolean) = apply {
            this.nameFilter = nameFilter
        }

        fun fromTypeFilter(
            fromTypeFilter: (name: Any?, fromNameType: Type, fromValueType: Type) -> Boolean
        ) = apply {
            this.fromTypeFilter = fromTypeFilter
        }

        fun fromValueFilter(
            fromValueFilter: (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean
        ) = apply {
            this.fromValueFilter = fromValueFilter
        }

        fun convertFilter(
            convertFilter: (
                name: Any?,
                fromNameType: Type,
                fromValueType: Type,
                value: Any?,
                toNameType: Type,
                toValueType: Type,
            ) -> Boolean
        ) = apply {
            this.convertFilter = convertFilter
        }

        fun ignoreNull() = apply {
            fromValueFilter { _, _, _, fromValue -> fromValue !== null }
        }

        override fun buildNew(): BeanCopyOptions {
            return object : BeanCopyOptions {
                override val includeClassProperty: Boolean = this@Builder.includeClassProperty
                override val putPropertyForMap: Boolean = this@Builder.putPropertyForMap
                override val fromType: Type? = this@Builder.fromType
                override val toType: Type? = this@Builder.toType
                override val beanResolver: BeanResolver? = this@Builder.beanResolver
                override val converter: Converter? = this@Builder.converter
                override val nameFilter: (name: Any?) -> Boolean = this@Builder.nameFilter
                override val fromTypeFilter: (name: Any?, fromNameType: Type, fromValueType: Type) -> Boolean =
                    this@Builder.fromTypeFilter
                override val fromValueFilter:
                    (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean =
                    this@Builder.fromValueFilter
                override val convertFilter: (
                    name: Any?, fromNameType: Type, fromValueType: Type, value: Any?, toNameType: Type, toValueType: Type
                ) -> Boolean =
                    this@Builder.convertFilter
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT = Builder().build()

        @JvmField
        val IGNORE_NULL = DEFAULT.withIgnoreNull()

        @JvmField
        val DEFAULT_WITHOUT_CONVERSION =
            DEFAULT.withConvertFilter { _, fromNameType, fromValueType, _, toNameType, toValueType ->
                fromNameType == toNameType && fromValueType == toValueType
            }

        @JvmField
        val IGNORE_NULL_WITHOUT_CONVERSION =
            IGNORE_NULL.withConvertFilter { _, fromNameType, fromValueType, _, toNameType, toValueType ->
                fromNameType == toNameType && fromValueType == toValueType
            }
    }
}