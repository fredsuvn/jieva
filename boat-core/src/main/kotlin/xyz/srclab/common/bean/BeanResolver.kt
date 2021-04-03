package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.NamingCase
import xyz.srclab.common.base.asAny
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.collect.MapType
import xyz.srclab.common.collect.MapType.Companion.toMapType
import xyz.srclab.common.collect.asToList
import xyz.srclab.common.collect.toImmutableMap
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.invoke.Invoker.Companion.toInvoker
import xyz.srclab.common.reflect.*
import java.lang.reflect.*

/**
 * @author sunqian
 *
 * @see BeanResolveHandler
 * @see BeanStyleBeanResolveHandler
 * @see NamingStyleBeanResolveHandler
 */
interface BeanResolver {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val resolveHandlers: List<BeanResolveHandler>
        @JvmName("resolveHandlers") get

    @JvmDefault
    fun resolve(type: Type): BeanType {
        return DEFAULT.resolve(type)
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
                    if (!copyOptions.putPropertyForMap && !to.containsKey(toKey)) {
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
                val toType = resolve(copyOptions.toType ?: to.javaClass)
                val toProperties = toType.properties
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
                val fromType = resolve(copyOptions.fromType ?: from.javaClass)
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
                    if (!copyOptions.putPropertyForMap && !to.containsKey(toKey)) {
                        return@forEach
                    }
                    (to.asAny<MutableMap<Any, Any?>>())[toKey] =
                        copyOptions.converter.convert(value, fromProperty.type, toMapType.valueType)
                }
                to
            }
            from !is Map<*, *> && to !is Map<*, *> -> {
                val fromType = resolve(copyOptions.fromType ?: from.javaClass)
                val toType = resolve(copyOptions.toType ?: to.javaClass)
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
            else -> throw IllegalStateException("Unknown type, failed to copy properties from $from to $to.")
        }
    }

    @JvmDefault
    fun withPreResolveHandler(preResolveHandler: BeanResolveHandler): BeanResolver {
        return newBeanResolver(listOf(preResolveHandler).plus(resolveHandlers))
    }

    interface CopyOptions {

        /**
         * Default: false.
         */
        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val includeClassProperty: Boolean
            @JvmName("includeClassProperty") get() = false

        /**
         * Default: true.
         */
        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val putPropertyForMap: Boolean
            @JvmName("putPropertyForMap") get() = true

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
            return BeanResolverImpl(resolveHandlers.asToList())
        }

        private class BeanAsMap(
            private val bean: Any,
            private val properties: Map<String, PropertyType>,
            private val copyOptions: CopyOptions
        ) : AbstractMutableMap<String, Any?>() {

            private val mapType = copyOptions.toType?.toMapType() ?: MapType.RAW

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
                                get() = copyOptions.converter.convert(it.value.getValue(bean), mapType.valueType)

                            override fun setValue(newValue: Any?): Any? {
                                return it.value.setValue(
                                    bean,
                                    copyOptions.converter.convert(newValue, mapType.valueType)
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
                return copyOptions.converter.convert(propertyType.getValue(bean), mapType.valueType)
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
    }
}

private class BeanResolverImpl(override val resolveHandlers: List<BeanResolveHandler>) : BeanResolver {

    private val cache = Cache.newFastCache<Type, BeanType>()

    override fun resolve(type: Type): BeanType {
        return cache.getOrLoad(type) {
            val beanType = BeanTypeImpl(it, emptyMap())
            val context = BeanResolveHandler.newContext(beanType, it.typeArguments())
            for (handler in resolveHandlers) {
                handler.resolve(context)
            }
            beanType.properties = context.properties.toImmutableMap()
            beanType
        }
    }
}

private class BeanTypeImpl(
    override val type: Type,
    override var properties: Map<String, PropertyType>
) : BeanType {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BeanType) return false

        if (type != other.type) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + properties.hashCode()
        return result
    }

    override fun toString(): String {
        return "bean ${type.typeName}"
    }
}

/**
 * Handler to resolve specified bean type.
 *
 * @see AbstractBeanResolveHandler
 * @see BeanStyleBeanResolveHandler
 * @see NamingStyleBeanResolveHandler
 */
interface BeanResolveHandler {

    fun resolve(@Written context: Context)

    interface Context {

        @Suppress(INAPPLICABLE_JVM_NAME)
        val beanType: BeanType
            @JvmName("beanType") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val properties: MutableMap<String, PropertyType>
            @JvmName("properties") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val methods: List<Method>
            @JvmName("methods") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val typeArguments: Map<TypeVariable<*>, Type>
            @JvmName("typeArguments") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val isBroken: Boolean
            @JvmName("isBroken") get

        /**
         * Stop and break resolving processing.
         */
        fun breakResolving()
    }

    companion object {

        @JvmField
        val DEFAULTS: List<BeanResolveHandler> = listOf(
            BeanStyleBeanResolveHandler
        )

        @JvmStatic
        fun newContext(beanType: BeanType, typeArguments: Map<TypeVariable<*>, Type>): Context {
            return ContextImpl(beanType, typeArguments)
        }

        private class ContextImpl(
            override val beanType: BeanType,
            override val typeArguments: Map<TypeVariable<*>, Type>
        ) : Context {

            private var _isBroken: Boolean = false

            override val properties: MutableMap<String, PropertyType> by lazy {
                LinkedHashMap()
            }

            override val methods: List<Method> by lazy {
                beanType.rawClass.methods.asList()
            }

            override val isBroken: Boolean
                get() = _isBroken

            override fun breakResolving() {
                _isBroken = true
            }
        }
    }
}

/**
 * Bean style:
 * * getter: getXxx()
 * * setter: setXxx(Xxx)
 */
object BeanStyleBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        context: BeanResolveHandler.Context,
        getters: MutableMap<String, PropertyInvoker>,
        setters: MutableMap<String, PropertyInvoker>,
    ) {
        val methods = context.methods
        for (method in methods) {
            if (method.isBridge) {
                continue
            }
            val name = method.name
            if (name.length <= 3) {
                continue
            }
            if (name.startsWith("get") && method.parameterCount == 0) {
                val propertyName =
                    NamingCase.UPPER_CAMEL.convertTo(name.substring(3, name.length), NamingCase.LOWER_CAMEL)
                val type = method.genericReturnType.eraseTypeVariables(context.typeArguments)
                getters[propertyName] = PropertyInvoker(type, method.toInvoker())
                continue
            }
            if (name.startsWith("set") && method.parameterCount == 1) {
                val propertyName =
                    NamingCase.UPPER_CAMEL.convertTo(name.substring(3, name.length), NamingCase.LOWER_CAMEL)
                val type = method.genericParameterTypes[0].eraseTypeVariables(context.typeArguments)
                setters[propertyName] = PropertyInvoker(type, method.toInvoker())
                continue
            }
        }
    }
}

/**
 * Naming style:
 * * getter: xxx()
 * * setter: xxx(xxx)
 */
object NamingStyleBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        context: BeanResolveHandler.Context,
        getters: MutableMap<String, PropertyInvoker>,
        setters: MutableMap<String, PropertyInvoker>,
    ) {
        val methods = context.methods
        for (method in methods) {
            if (method.isBridge || method.declaringClass == Any::class.java) {
                continue
            }
            val name = method.name
            if (method.parameterCount == 0) {
                val type = method.genericReturnType.eraseTypeVariables(context.typeArguments)
                getters[name] = PropertyInvoker(type, method.toInvoker())
                continue
            }
            if (method.parameterCount == 1) {
                val type = method.genericParameterTypes[0].eraseTypeVariables(context.typeArguments)
                setters[name] = PropertyInvoker(type, method.toInvoker())
                continue
            }
        }
    }
}

abstract class AbstractBeanResolveHandler : BeanResolveHandler {

    private val cache = Cache.newFastCache<Pair<BeanType, String>, PropertyType>()

    override fun resolve(context: BeanResolveHandler.Context) {
        if (context.isBroken) {
            return
        }
        val getters: MutableMap<String, PropertyInvoker> = LinkedHashMap()
        val setters: MutableMap<String, PropertyInvoker> = LinkedHashMap()
        resolveAccessors(context, getters, setters)
        val properties = context.properties
        for (getter in getters) {
            val propertyName = getter.key
            val getterInvoker = getter.value.invoker
            val setterPropertyInvoker = setters[propertyName]
            if (setterPropertyInvoker === null) {
                properties[propertyName] = createProperty(context, propertyName, getter.value.type, getterInvoker, null)
                continue
            }
            val setterInvoker = setterPropertyInvoker.invoker
            if (getter.value.type == setterPropertyInvoker.type) {
                properties[propertyName] =
                    createProperty(context, propertyName, setterPropertyInvoker.type, getterInvoker, setterInvoker)
                continue
            }
            setters.remove(propertyName)
        }
        for (setter in setters) {
            val propertyName = setter.key
            val setterPropertyInvoker = setter.value
            properties[propertyName] =
                createProperty(context, propertyName, setterPropertyInvoker.type, null, setterPropertyInvoker.invoker)
        }
    }

    protected abstract fun resolveAccessors(
        @Written context: BeanResolveHandler.Context,
        getters: MutableMap<String, PropertyInvoker>,
        setters: MutableMap<String, PropertyInvoker>,
    )

    private fun createProperty(
        context: BeanResolveHandler.Context,
        name: String,
        type: Type,
        getterInvoker: Invoker?,
        setterInvoker: Invoker?
    ): PropertyType {
        return cache.getOrLoad(context.beanType to name) {
            PropertyTypeImpl(context.beanType, name, type, getterInvoker, setterInvoker)
        }
    }

    class PropertyInvoker(val type: Type, val invoker: Invoker)
}

private class PropertyTypeImpl(
    override val ownerType: BeanType,
    override val name: String,
    override val type: Type,
    override val getter: Invoker?,
    override val setter: Invoker?,
) : PropertyType {

    override val backingField: Field? by lazy { tryBackingField() }
    override val backingFieldAnnotations: List<Annotation> by lazy { tryBackingFieldAnnotations() }

    private fun tryBackingField(): Field? {
        return ownerType.rawClass.searchFieldOrNull(name, deep = true)
    }

    private fun tryBackingFieldAnnotations(): List<Annotation> {
        val f = backingField
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

        if (ownerType != other.ownerType) return false
        if (name != other.name) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ownerType.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "$name: ${ownerType.type.typeName}.${type.typeName}"
    }
}