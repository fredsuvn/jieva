package xyz.srclab.common.bean

import xyz.srclab.annotations.OutParam
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.Invoker
import xyz.srclab.common.base.Invoker.Companion.asInvoker
import xyz.srclab.common.base.asAny
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.collection.resolveMapSchema
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.reflect.*
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.*

/**
 * @author sunqian
 */
interface BeanResolver {

    fun resolve(type: Type): BeanSchema

    @JvmDefault
    fun asMap(bean: Any): MutableMap<String, Any?> {
        return asMap(bean, CopyOptions.DEFAULT)
    }

    @JvmDefault
    fun asMap(bean: Any, copyOptions: CopyOptions): MutableMap<String, Any?> {
        return BeanAsMap(bean, resolve(bean.javaClass).properties, copyOptions.converter)
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
        return copyProperties(from, to, CopyOptions.DEFAULT.with(fromType, toType, converter))
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
        return copyProperties(from, to, CopyOptions.IGNORE_NULL.with(fromType, toType, converter))
    }

    @JvmDefault
    fun <T : Any> copyProperties(from: Any, to: T, copyOptions: CopyOptions): T {
        return when {
            from is Map<*, *> && to is MutableMap<*, *> -> {
                val fromType = copyOptions.fromType ?: Map::class.java
                val fromMapSchema = fromType.resolveMapSchema()
                val toType = copyOptions.toType ?: Map::class.java
                val toMapSchema = toType.resolveMapSchema()
                from.forEach { (k, v) ->
                    if (!copyOptions.propertyNameFilter(k)
                        || !copyOptions.propertyTypeFilter(k, fromMapSchema.keyType, fromMapSchema.valueType)
                        || !copyOptions.propertyValueFilter(k, v, fromMapSchema.keyType, fromMapSchema.valueType)
                        || !copyOptions.propertyConvertFilter(
                            k,
                            v,
                            fromMapSchema.keyType,
                            fromMapSchema.valueType,
                            toMapSchema.keyType,
                            toMapSchema.valueType
                        )
                    ) {
                        return@forEach
                    }
                    val toKey = copyOptions.converter.convert<Any>(k, fromMapSchema.keyType, toMapSchema.keyType)
                    if (!to.containsKey(toKey)) {
                        return@forEach
                    }
                    (to as MutableMap<Any, Any?>)[toKey] =
                        copyOptions.converter.convert(v, fromMapSchema.valueType, toMapSchema.valueType)
                }
                to
            }
            from is Map<*, *> && to !is Map<*, *> -> {
                val fromType = copyOptions.fromType ?: Map::class.java
                val fromMapSchema = fromType.resolveMapSchema()
                val toSchema = resolve(to.javaClass)
                val toProperties = toSchema.properties
                from.forEach { (k, v) ->
                    if (!copyOptions.propertyNameFilter(k)
                        || !copyOptions.propertyTypeFilter(k, fromMapSchema.keyType, fromMapSchema.valueType)
                        || !copyOptions.propertyValueFilter(k, v, fromMapSchema.keyType, fromMapSchema.valueType)
                    ) {
                        return@forEach
                    }
                    val toPropertyName =
                        copyOptions.converter.convert<String>(k, fromMapSchema.keyType, String::class.java)
                    val toProperty = toProperties[toPropertyName]
                    if (toProperty === null || !toProperty.isWriteable) {
                        return@forEach
                    }
                    if (!copyOptions.propertyConvertFilter(
                            k,
                            v,
                            fromMapSchema.keyType,
                            fromMapSchema.valueType,
                            String::class.java,
                            toProperty.genericType
                        )
                    ) {
                        return@forEach
                    }
                    toProperty.setValue<Any?>(
                        to, copyOptions.converter.convert(v, fromMapSchema.valueType, toProperty.genericType)
                    )
                }
                to
            }
            from !is Map<*, *> && to is Map<*, *> -> {
                val fromSchema = resolve(from.javaClass)
                val toType = copyOptions.toType ?: Map::class.java
                val toMapSchema = toType.resolveMapSchema()
                val fromProperties = fromSchema.properties
                fromProperties.forEach { (name, property) ->
                    if (!copyOptions.propertyNameFilter(name)
                        || !property.isReadable
                        || !copyOptions.propertyTypeFilter(name, String::class.java, property.genericType)
                    ) {
                        return@forEach
                    }
                    val value = property.getValue<Any?>(from)
                    if (!copyOptions.propertyValueFilter(name, value, String::class.java, property.genericType)
                        || !copyOptions.propertyConvertFilter(
                            name,
                            value,
                            String::class.java,
                            toMapSchema.valueType,
                            toMapSchema.keyType,
                            toMapSchema.valueType
                        )
                    ) {
                        return@forEach
                    }
                    val toKey = copyOptions.converter.convert<Any>(name, String::class.java, toMapSchema.keyType)
                    if (!to.containsKey(toKey)) {
                        return@forEach
                    }
                    (to as MutableMap<Any, Any?>)[toKey] =
                        copyOptions.converter.convert(value, property.genericType, toMapSchema.valueType)
                }
                to
            }
            from !is Map<*, *> && to !is Map<*, *> -> {
                val fromSchema = resolve(from.javaClass)
                val toSchema = resolve(to.javaClass)
                val fromProperties = fromSchema.properties
                val toProperties = toSchema.properties
                fromProperties.forEach { (name, property) ->
                    if (!copyOptions.propertyNameFilter(name)
                        || !property.isReadable
                        || !copyOptions.propertyTypeFilter(name, String::class.java, property.genericType)
                    ) {
                        return@forEach
                    }
                    val toProperty = toProperties[name]
                    if (toProperty === null || !toProperty.isWriteable) {
                        return@forEach
                    }
                    val value = property.getValue<Any?>(from)
                    if (!copyOptions.propertyValueFilter(name, value, String::class.java, property.genericType)
                        || !copyOptions.propertyConvertFilter(
                            name,
                            value,
                            String::class.java,
                            property.genericType,
                            String::class.java,
                            toProperty.genericType
                        )
                    ) {
                        return@forEach
                    }
                    toProperty.setValue<Any?>(
                        to, copyOptions.converter.convert(value, property.genericType, toProperty.genericType)
                    )
                }
                to
            }
            else -> throw IllegalArgumentException("Copy failed from $from to $to.")
        }
    }

    fun withPreResolveHandler(preResolveHandler: BeanResolveHandler): BeanResolver

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
        val propertyNameFilter: (name: Any?) -> Boolean
            @JvmName("propertyNameFilter") get() = { _ -> true }

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val propertyTypeFilter: (name: Any?, nameType: Type, valueType: Type) -> Boolean
            @JvmName("propertyTypeFilter") get() = { _, _, _ -> true }

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val propertyValueFilter: (name: Any?, value: Any?, nameType: Type, valueType: Type) -> Boolean
            @JvmName("propertyValueFilter") get() = { _, _, _, _ -> true }

        @Suppress(INAPPLICABLE_JVM_NAME)
        @JvmDefault
        val propertyConvertFilter: (
            name: Any?, value: Any?,
            nameType: Type, valueType: Type,
            targetNameType: Type, targetValueType: Type
        ) -> Boolean
            @JvmName("propertyConvertFilter") get() = { _, _, _, _, _, _ -> true }

        @JvmDefault
        fun withTypes(fromType: Type?, toType: Type?): CopyOptions {
            return with(fromType, toType, this.converter)
        }

        @JvmDefault
        fun withConverter(converter: Converter): CopyOptions {
            return with(this.fromType, this.toType, converter)
        }

        @JvmDefault
        fun with(fromType: Type?, toType: Type?, converter: Converter): CopyOptions {
            return object : CopyOptions by this {
                override val fromType: Type? = fromType
                override val toType: Type? = toType
                override val converter: Converter = converter
            }
        }

        companion object {

            @JvmField
            val DEFAULT = object : CopyOptions {}

            @JvmField
            val IGNORE_NULL = object : CopyOptions {
                override val propertyValueFilter:
                            (name: Any?, value: Any?, nameType: Type, valueType: Type) -> Boolean =
                    { _, value, _, _ -> value !== null }
            }

            @JvmField
            val DEFAULT_WITHOUT_CONVERSION = object : CopyOptions {
                override val propertyConvertFilter: (
                    name: Any?, value: Any?,
                    nameType: Type, valueType: Type,
                    targetNameType: Type, targetValueType: Type
                ) -> Boolean =
                    { _, _, nameType, valueType, targetNameType, targetValueType ->
                        nameType == targetNameType && valueType == targetValueType
                    }
            }

            @JvmField
            val IGNORE_NULL_WITHOUT_CONVERSION = object : CopyOptions {
                override val propertyValueFilter:
                            (name: Any?, value: Any?, nameType: Type, valueType: Type) -> Boolean =
                    { _, value, _, _ -> value !== null }
                override val propertyConvertFilter: (
                    name: Any?, value: Any?,
                    nameType: Type, valueType: Type,
                    targetNameType: Type, targetValueType: Type
                ) -> Boolean =
                    { _, _, nameType, valueType, targetNameType, targetValueType ->
                        nameType == targetNameType && valueType == targetValueType
                    }
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT: BeanResolver = newBeanResolver(BeanResolveHandler.DEFAULTS)

        @JvmStatic
        fun newBeanResolver(resolveHandlers: Iterable<BeanResolveHandler>): BeanResolver {
            return BeanResolverImpl(resolveHandlers)
        }

        private class BeanResolverImpl(private val handlers: Iterable<BeanResolveHandler>) : BeanResolver {

            private val cache = Cache.newFastCache<Type, BeanSchema>()

            override fun resolve(type: Type): BeanSchema {
                return cache.getOrLoad(type) {
                    val context = Context(type, LinkedHashMap())
                    for (handler in handlers) {
                        handler.resolve(context)
                    }
                    BeanSchemaImpl(type, context.beanProperties.toMap())
                }
            }

            override fun withPreResolveHandler(preResolveHandler: BeanResolveHandler): BeanResolver {
                return BeanResolverImpl(listOf(preResolveHandler).plus(handlers))
            }

            private class Context(
                override val beanType: Type,
                override val beanProperties: MutableMap<String, PropertySchema>
            ) : BeanResolveHandler.ResolveContext

            private class BeanSchemaImpl(
                override val genericType: Type,
                override val properties: Map<String, PropertySchema>,
            ) : BeanSchema {

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is BeanSchema) return false
                    if (genericType != other.genericType) return false
                    if (properties != other.properties) return false
                    return true
                }

                override fun hashCode(): Int {
                    var result = genericType.hashCode()
                    result = 31 * result + properties.hashCode()
                    return result
                }

                override fun toString(): String {
                    return genericType.typeName
                }
            }
        }

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

interface BeanResolveHandler {

    fun resolve(context: ResolveContext)

    interface ResolveContext {

        @Suppress(INAPPLICABLE_JVM_NAME)
        val beanType: Type
            @JvmName("beanType") get

        @Suppress(INAPPLICABLE_JVM_NAME)
        val beanProperties: MutableMap<String, PropertySchema>
            @JvmName("beanProperties") get
    }

    companion object {

        @JvmField
        val DEFAULTS: List<BeanResolveHandler> = listOf(
            BeanAccessorMethodResolveHandler
        )
    }
}

object BeanAccessorMethodResolveHandler : BeanResolveHandler {

    private val cache = Cache.newFastCache<Pair<Type, PropertyDescriptor>, PropertySchema>()

    override fun resolve(context: BeanResolveHandler.ResolveContext) {
        val beanInfo = Introspector.getBeanInfo(context.beanType.rawClass)
        val typeVariableTable = context.beanType.mapTypeVariables()
        val beanProperties = context.beanProperties
        for (propertyDescriptor in beanInfo.propertyDescriptors) {
            if (beanProperties.containsKey(propertyDescriptor.name)) {
                continue
            }
            val property = cache.getOrLoad(context.beanType to propertyDescriptor) {
                PropertySchemaImpl(
                    context.beanType,
                    propertyDescriptor,
                    typeVariableTable,
                )
            }
            beanProperties[propertyDescriptor.name] = property
        }
    }

    private class PropertySchemaImpl(
        override val genericOwnerType: Type,
        descriptor: PropertyDescriptor,
        private val typeVariableTable: Map<TypeVariable<*>, Type>,
    ) : PropertySchema {

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
            return findActualType(type)
        }

        private fun findActualType(type: Type): Type {

            fun needTransform(@OutParam array: Array<Type>): Boolean {
                var needTransform = false
                for (i in array.indices) {
                    val oldType = array[i]
                    val newType = findActualType(oldType)
                    if (oldType != newType) {
                        needTransform = true
                        array[i] = newType
                    }
                }
                return needTransform
            }

            return when (type) {
                is TypeVariable<*> -> {
                    val result = type.findActualType(genericOwnerType, typeVariableTable) ?: type
                    if (result is TypeVariable<*>) {
                        return result
                    }
                    return findActualType(result)
                }
                is ParameterizedType -> {
                    val actualTypeArguments = type.actualTypeArguments
                    if (!needTransform(actualTypeArguments)) {
                        return type
                    }
                    return parameterizedType(type.rawType, actualTypeArguments, type.ownerType)
                }
                is WildcardType -> {
                    val upperBounds = type.upperBounds
                    if (!upperBounds.isObjectUpperBound) {
                        if (!needTransform(upperBounds)) {
                            return type
                        }
                        return wildcardTypeWithUpperBounds(upperBounds)
                    }
                    val lowerBounds = type.lowerBounds
                    if (lowerBounds.isNotEmpty()) {
                        if (!needTransform(lowerBounds)) {
                            return type
                        }
                        return wildcardTypeWithLowerBounds(lowerBounds)
                    }
                    return type
                }
                is GenericArrayType -> {
                    val componentType = type.genericComponentType
                    val newType = findActualType(componentType)
                    if (componentType == newType) {
                        return type
                    }
                    return type.genericArrayType()
                }
                else -> type
            }
        }

        private fun tryGetter(): Invoker? {
            return if (getterMethod === null) null else getterMethod.asInvoker()
        }

        private fun trySetter(): Invoker? {
            return if (setterMethod === null) null else setterMethod.asInvoker()
        }

        private fun tryField(): Field? {
            return ownerType.searchField(name, deep = true)
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
            if (other !is PropertySchema) return false
            if (genericOwnerType != other.genericOwnerType) return false
            if (name != other.name) return false
            if (genericType != other.genericType) return false
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