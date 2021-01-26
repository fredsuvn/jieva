package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.asAny
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.collect.MapType
import xyz.srclab.common.collect.MapType.Companion.toMapType
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.invoke.Invoker.Companion.toInvoker
import xyz.srclab.common.reflect.*
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.*

/**
 * @author sunqian
 */
interface BeanResolver {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val resolveHandlers: List<BeanResolveHandler>
        @JvmName("resolveHandlers") get

    @JvmDefault
    fun resolve(type: Type): BeanType {
        return resolveBean(type, resolveHandlers)
    }

    @JvmDefault
    fun asMap(bean: Any): MutableMap<String, Any?> {
        return asMap(bean, CopyOptions.DEFAULT)
    }

    @JvmDefault
    fun asMap(bean: Any, copyOptions: CopyOptions): MutableMap<String, Any?> {
        return BeanAsMap(bean, resolve(bean.javaClass).properties, copyOptions)
    }

    @JvmDefault
    fun <T : Any> copyProperties(from: Any, to: T): T {
        return copyProperties(from, to, CopyOptions.DEFAULT)
    }

    @JvmDefault
    fun <T : Any> copyProperties(from: Any, to: T, converter: Converter): T {
        return copyProperties(from, to, CopyOptions.DEFAULT.withConverter(converter))
    }

    @JvmDefault
    fun <T : Any> copyProperties(from: Any, to: T, fromType: Type, toType: Type, converter: Converter): T {
        return copyProperties(from, to, CopyOptions.DEFAULT.withTypesConverter(fromType, toType, converter))
    }

    @JvmDefault
    fun <T : Any> copyPropertiesIgnoreNull(from: Any, to: T): T {
        return copyProperties(from, to, CopyOptions.IGNORE_NULL)
    }

    @JvmDefault
    fun <T : Any> copyPropertiesIgnoreNull(from: Any, to: T, converter: Converter): T {
        return copyProperties(from, to, CopyOptions.IGNORE_NULL.withConverter(converter))
    }

    @JvmDefault
    fun <T : Any> copyPropertiesIgnoreNull(from: Any, to: T, fromType: Type, toType: Type, converter: Converter): T {
        return copyProperties(from, to, CopyOptions.IGNORE_NULL.withTypesConverter(fromType, toType, converter))
    }

    @JvmDefault
    fun <T : Any> copyProperties(from: Any, to: T, copyOptions: CopyOptions): T {
        return when {
            from is Map<*, *> && to is MutableMap<*, *> -> {
                val fromType = copyOptions.fromType ?: Map::class.java
                val fromMapType = fromType.toMapType()
                val toType = copyOptions.toType ?: Map::class.java
                val toMapType = toType.toMapType()
                from.forEach { (k, v) ->
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
                    val toKey = copyOptions.converter.convert<Any>(k, fromMapType.keyType, toMapType.keyType)
                    if (!to.containsKey(toKey)) {
                        return@forEach
                    }
                    (to.asAny<MutableMap<Any, Any?>>())[toKey] =
                        copyOptions.converter.convert(v, fromMapType.valueType, toMapType.valueType)
                }
                to
            }
            from is Map<*, *> && to !is Map<*, *> -> {
                val fromType = copyOptions.fromType ?: Map::class.java
                val fromMapType = fromType.toMapType()
                val toSchema = resolve(to.javaClass)
                val toProperties = toSchema.properties
                from.forEach { (k, v) ->
                    if (!copyOptions.nameFilter(k)
                        || !copyOptions.fromTypeFilter(k, fromMapType.keyType, fromMapType.valueType)
                        || !copyOptions.fromValueFilter(k, fromMapType.keyType, fromMapType.valueType, v)
                    ) {
                        return@forEach
                    }
                    val toPropertyName =
                        copyOptions.converter.convert<String>(k, fromMapType.keyType, String::class.java)
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
                        to, copyOptions.converter.convert(v, fromMapType.valueType, toProperty.type)
                    )
                }
                to
            }
            from !is Map<*, *> && to is Map<*, *> -> {
                val fromSchema = resolve(from.javaClass)
                val toType = copyOptions.toType ?: Map::class.java
                val toMapType = toType.toMapType()
                val fromProperties = fromSchema.properties
                fromProperties.forEach { (name, fromProperty) ->
                    if (!copyOptions.nameFilter(name)
                        || !fromProperty.isReadable
                        || !copyOptions.fromTypeFilter(name, String::class.java, fromProperty.type)
                    ) {
                        return@forEach
                    }
                    val value = fromProperty.getValue<Any?>(from)
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
                    val toKey = copyOptions.converter.convert<Any>(name, String::class.java, toMapType.keyType)
                    if (!to.containsKey(toKey)) {
                        return@forEach
                    }
                    (to.asAny<MutableMap<Any, Any?>>())[toKey] =
                        copyOptions.converter.convert(value, fromProperty.type, toMapType.valueType)
                }
                to
            }
            from !is Map<*, *> && to !is Map<*, *> -> {
                val fromSchema = resolve(from.javaClass)
                val toSchema = resolve(to.javaClass)
                val fromProperties = fromSchema.properties
                val toProperties = toSchema.properties
                fromProperties.forEach { (name, fromProperty) ->
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
                    val value = fromProperty.getValue<Any?>(from)
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
                        to, copyOptions.converter.convert(value, fromProperty.type, toProperty.type)
                    )
                }
                to
            }
            else -> throw IllegalArgumentException("Copy failed from $from to $to.")
        }
    }

    @JvmDefault
    fun withPreResolveHandler(preResolveHandler: BeanResolveHandler): BeanResolver {
        return newBeanResolver(listOf(preResolveHandler).plus(resolveHandlers))
    }

    interface CopyOptions {

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val fromType: Type?
            @JvmName("fromType") get() = null

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val toType: Type?
            @JvmName("toType") get() = null

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val converter: Converter
            @JvmName("converter") get() = Converter.DEFAULT

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val nameFilter: (name: Any?) -> Boolean
            @JvmName("nameFilter") get() = { _ -> true }

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val fromTypeFilter: (name: Any?, fromNameType: Type, fromValueType: Type) -> Boolean
            @JvmName("fromTypeFilter") get() = { _, _, _ -> true }

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val fromValueFilter: (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean
            @JvmName("fromValueFilter") get() = { _, _, _, _ -> true }

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
            @JvmName("convertFilter") get() = { _, _, _, _, _, _ -> true }

        @JvmDefault
        fun withFromType(fromType: Type?): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
        }

        @JvmDefault
        fun withToType(toType: Type?): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
        }

        @JvmDefault
        fun withTypes(fromType: Type?, toType: Type?): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
        }

        @JvmDefault
        fun withConverter(converter: Converter): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
        }

        @JvmDefault
        fun withTypesConverter(fromType: Type?, toType: Type?, converter: Converter): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
        }

        @JvmDefault
        fun withNameFilter(
            nameFilter: (name: Any?) -> Boolean
        ): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
        }

        @JvmDefault
        fun withFromTypeFilter(
            fromTypeFilter: (name: Any?, fromNameType: Type, fromValueType: Type) -> Boolean
        ): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
        }

        @JvmDefault
        fun withFromValueFilter(
            fromValueFilter: (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean
        ): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
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
        ): CopyOptions {
            return with(fromType, toType, converter, nameFilter, fromTypeFilter, fromValueFilter, convertFilter)
        }

        companion object {

            @JvmField
            val DEFAULT = object : CopyOptions {}

            @JvmField
            val IGNORE_NULL = object : CopyOptions {
                override val fromValueFilter:
                            (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean =
                    { _, _, _, fromValue -> fromValue !== null }
            }

            @JvmField
            val DEFAULT_WITHOUT_CONVERSION = object : CopyOptions {
                override val convertFilter: (
                    name: Any?,
                    fromNameType: Type, fromValueType: Type, fromValue: Any?,
                    toNameType: Type, toValueType: Type
                ) -> Boolean =
                    { _, fromNameType, fromValueType, _, toNameType, toValueType ->
                        fromNameType == toNameType && fromValueType == toValueType
                    }
            }

            @JvmField
            val IGNORE_NULL_WITHOUT_CONVERSION = object : CopyOptions {
                override val fromValueFilter:
                            (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean =
                    { _, _, _, fromValue -> fromValue !== null }
                override val convertFilter: (
                    name: Any?,
                    fromNameType: Type, fromValueType: Type, fromValue: Any?,
                    toNameType: Type, toValueType: Type
                ) -> Boolean =
                    { _, fromNameType, fromValueType, _, toNameType, toValueType ->
                        fromNameType == toNameType && fromValueType == toValueType
                    }
            }

            @JvmStatic
            fun with(
                fromType: Type?,
                toType: Type?,
                converter: Converter,
                nameFilter: (name: Any?) -> Boolean,
                fromTypeFilter: (name: Any?, fromNameType: Type, fromValueType: Type) -> Boolean,
                fromValueFilter: (name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?) -> Boolean,
                convertFilter: (
                    name: Any?,
                    fromNameType: Type,
                    fromValueType: Type,
                    value: Any?,
                    toNameType: Type,
                    toValueType: Type,
                ) -> Boolean,
            ): CopyOptions {
                return object : CopyOptions {
                    override val fromType: Type? = fromType
                    override val toType: Type? = toType
                    override val converter: Converter = converter
                    override val nameFilter: (name: Any?) -> Boolean = nameFilter
                    override val fromTypeFilter: (
                        name: Any?, fromNameType: Type, fromValueType: Type
                    ) -> Boolean =
                        fromTypeFilter
                    override val fromValueFilter: (
                        name: Any?, fromNameType: Type, fromValueType: Type, fromValue: Any?
                    ) -> Boolean =
                        fromValueFilter
                    override val convertFilter: (
                        name: Any?,
                        fromNameType: Type,
                        fromValueType: Type,
                        value: Any?,
                        toNameType: Type,
                        toValueType: Type
                    ) -> Boolean =
                        convertFilter
                }
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT: BeanResolver = newBeanResolver(BeanResolveHandler.DEFAULTS)

        @JvmStatic
        fun newBeanResolver(resolveHandlers: Iterable<BeanResolveHandler>): BeanResolver {
            return object : BeanResolver {
                override val resolveHandlers = resolveHandlers.toList()
            }
        }

        private class BeanAsMap(
            private val bean: Any,
            private val properties: Map<String, PropertyType>,
            private val copyOptions: CopyOptions
        ) : AbstractMutableMap<String, Any?>() {

            private val mapSchema = copyOptions.toType?.toMapType() ?: MapType.RAW

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
                            mapSchema.keyType,
                            mapSchema.valueType
                        )
                    }
                    .mapTo(LinkedHashSet()) {
                        object : MutableMap.MutableEntry<String, Any?> {
                            override val key: String = it.key
                            override val value: Any?
                                get() = copyOptions.converter.convert(it.value.getValue(bean), mapSchema.valueType)

                            override fun setValue(newValue: Any?): Any? {
                                return it.value.setValue(
                                    bean,
                                    copyOptions.converter.convert(newValue, mapSchema.valueType)
                                )
                            }
                        }
                    }
            }

            override fun containsKey(key: String): Boolean {
                return properties.containsKey(key)
            }

            override fun get(key: String): Any? {
                val propertySchema = properties[key]
                if (propertySchema === null) {
                    return null
                }
                return copyOptions.converter.convert(propertySchema.getValue(bean), mapSchema.valueType)
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
                return propertySchema.setValue(bean, value)
            }

            override fun remove(key: String): Any? {
                throw UnsupportedOperationException()
            }
        }
    }
}

private val cache = Cache.newFastCache<Type, BeanType>()

private fun resolveBean(type: Type, resolveHandlers: List<BeanResolveHandler>): BeanType {
    return cache.getOrLoad(type) {
        val context = BeanResolveHandler.newContext(type)
        for (handler in resolveHandlers) {
            handler.resolve(context)
        }
        BeanType.newBeanSchema(type, context.beanProperties.toMap())
    }
}

interface BeanResolveHandler {

    fun resolve(context: Context)

    interface Context {

        @Suppress(INAPPLICABLE_JVM_NAME)
        val beanType: Type
            @JvmName("beanType") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val beanProperties: MutableMap<String, PropertyType>
            @JvmName("beanProperties") get
    }

    companion object {

        @JvmField
        val DEFAULTS: List<BeanResolveHandler> = listOf(
            BeanAccessorMethodResolveHandler
        )

        @JvmStatic
        fun newContext(beanType: Type): Context {
            return object : Context {
                override val beanType = beanType
                override val beanProperties = mutableMapOf<String, PropertyType>()
            }
        }
    }
}

object BeanAccessorMethodResolveHandler : BeanResolveHandler {

    private val cache = Cache.newFastCache<Pair<Type, PropertyDescriptor>, PropertyType>()

    override fun resolve(context: BeanResolveHandler.Context) {
        val beanInfo = Introspector.getBeanInfo(context.beanType.rawClassOrNull)
        val typeVariableTable by lazy { context.beanType.findTypeArguments() }
        val beanProperties = context.beanProperties
        for (propertyDescriptor in beanInfo.propertyDescriptors) {
            if (beanProperties.containsKey(propertyDescriptor.name)) {
                continue
            }
            val property = cache.getOrLoad(context.beanType to propertyDescriptor) {
                PropertyTypeImpl(
                    context.beanType,
                    propertyDescriptor,
                    typeVariableTable,
                )
            }
            beanProperties[propertyDescriptor.name] = property
        }
    }

    private class PropertyTypeImpl(
        override val genericOwnerType: Type,
        descriptor: PropertyDescriptor,
        private val typeVariableTable: Map<TypeVariable<*>, Type>,
    ) : PropertyType {

        override val name: String = descriptor.name
        override val genericType: Type by lazy { tryGenericType() }
        override val getter: Invoker? by lazy { tryGetter() }
        override val setter: Invoker? by lazy { trySetter() }
        override val field: Field? by lazy { tryField() }
        override val fieldAnnotations: List<Annotation> by lazy { tryFieldAnnotations() }

        private val getterMethod: Method? = descriptor.readMethod
        private val setterMethod: Method? = descriptor.writeMethod

        private fun tryGenericType(): Type {
            val type = if (getterMethod !== null) {
                getterMethod.genericReturnType
            } else {
                setterMethod!!.genericParameterTypes[0]
            }
            return type.eraseTypeVariables(typeVariableTable)
        }

        private fun tryGetter(): Invoker? {
            return if (getterMethod === null) null else getterMethod.toInvoker()
        }

        private fun trySetter(): Invoker? {
            return if (setterMethod === null) null else setterMethod.toInvoker()
        }

        private fun tryField(): Field? {
            return ownerType.searchFieldOrNull(name, deep = true)
        }

        private fun tryFieldAnnotations(): List<Annotation> {
            val f = field
            return if (f === null) emptyList() else f.annotations.asList()
        }

        override fun <T> getValue(bean: Any): T {
            val g = getter
            return if (g !== null) {
                g.invoke(bean)
            } else {
                throw UnsupportedOperationException("This property is not readable: $name")
            }
        }

        override fun <T> setValue(bean: Any, value: Any?): T {
            val s = setter
            if (s === null) {
                throw UnsupportedOperationException("This property is not writeable: $name")
            }
            var old: T? = null
            val g = getter
            if (g !== null) {
                old = g.invoke(bean)
            }
            s.invoke<Any?>(bean, value)
            return old.asAny()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is PropertyType) return false
            if (genericOwnerType != other.genericOwnerType) return false
            if (name != other.name) return false
            if (genericType != other.type) return false
            return true
        }

        override fun hashCode(): Int {
            var result = genericOwnerType.hashCode()
            result = 31 * result + name.hashCode()
            result = 31 * result + genericType.hashCode()
            return result
        }

        override fun toString(): String {
            return "$name: ${genericType.typeName}"
        }
    }
}