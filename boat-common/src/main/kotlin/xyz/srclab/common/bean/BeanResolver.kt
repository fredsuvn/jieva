package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.Invoker.Companion.toVirtualInvoker
import xyz.srclab.common.base.VirtualInvoker
import xyz.srclab.common.base.asAny
import xyz.srclab.common.collection.componentTypeToArray
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

    fun resolveSchema(type: Type): BeanSchema

    fun asMap(bean: Any): MutableMap<String, Any?> {
        return asMap(bean, CopyOptions.DEFAULT)
    }

    fun asMap(bean: Any, copyOptions: CopyOptions): MutableMap<String, Any?> {
        return BeanAsMap(bean, resolveSchema(bean.javaClass).properties, copyOptions.converter)
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
        return when {
            from is Map<*, *> && to is MutableMap<*, *> -> {
                val toType = copyOptions.toType ?: Map::class.java
                val toMapSchema = toType.resolveMapSchema()
                from.forEach { (k, v) ->
                    if (!copyOptions.filterProperty(k)
                        || !copyOptions.filterProperty(k, v)
                        || !copyOptions.filterProperty(k, v, toMapSchema.keyType, toMapSchema.valueType)
                    ) {
                        return@forEach
                    }
                    val toKey = copyOptions.converter.convert<Any>(k, toMapSchema.keyType)
                    if (!to.containsKey(toKey)) {
                        return@forEach
                    }
                    (to as MutableMap<Any, Any?>)[toKey] = copyOptions.converter.convert(v, toMapSchema.valueType)
                }
                to
            }
            from is Map<*, *> && to !is Map<*, *> -> {
                val toSchema = resolveSchema(to.javaClass)
                val toProperties = toSchema.properties
                from.forEach { (k, v) ->
                    if (!copyOptions.filterProperty(k)
                        || !copyOptions.filterProperty(k, v)
                    ) {
                        return@forEach
                    }
                    val toPropertyName = copyOptions.converter.convert(k, String::class.java)
                    val toProperty = toProperties[toPropertyName]
                    if (toProperty === null || !toProperty.isWriteable) {
                        return@forEach
                    }
                    if (!copyOptions.filterProperty(k, v, String::class.java, toProperty.genericType)) {
                        return@forEach
                    }
                    toProperty.setValue<Any?>(to, v, copyOptions.converter)
                }
                to
            }
            from !is Map<*, *> && to is Map<*, *> -> {
                val fromSchema = resolveSchema(from.javaClass)
                val toType = copyOptions.toType ?: Map::class.java
                val toMapSchema = toType.resolveMapSchema()
                val fromProperties = fromSchema.properties
                fromProperties.forEach { (name, property) ->
                    if (!property.isReadable) {
                        return@forEach
                    }
                    if (!copyOptions.filterProperty(name)) {
                        return@forEach
                    }
                    val value = property.getValue<Any?>(from)
                    if (!copyOptions.filterProperty(name, value)
                        || !copyOptions.filterProperty(name, value, toMapSchema.keyType, toMapSchema.valueType)
                    ) {
                        return@forEach
                    }
                    val toKey = copyOptions.converter.convert<Any>(name, toMapSchema.keyType)
                    if (!to.containsKey(toKey)) {
                        return@forEach
                    }
                    (to as MutableMap<Any, Any?>)[toKey] = copyOptions.converter.convert(value, toMapSchema.valueType)
                }
                to
            }
            from !is Map<*, *> && to !is Map<*, *> -> {
                val fromSchema = resolveSchema(from.javaClass)
                val toSchema = resolveSchema(to.javaClass)
                val fromProperties = fromSchema.properties
                val toProperties = toSchema.properties
                fromProperties.forEach { (name, property) ->
                    if (!property.isReadable) {
                        return@forEach
                    }
                    if (!copyOptions.filterProperty(name)) {
                        return@forEach
                    }
                    val toProperty = toProperties[name]
                    if (toProperty === null || !toProperty.isWriteable) {
                        return@forEach
                    }
                    val value = property.getValue<Any?>(from)
                    if (!copyOptions.filterProperty(name, value)
                        || !copyOptions.filterProperty(name, value, String::class.java, toProperty.genericType)
                    ) {
                        return@forEach
                    }
                    toProperty.setValue<Any?>(to, value, copyOptions.converter)
                }
                to
            }
            else -> throw IllegalArgumentException("Copy failed from $from to $to.")
        }
    }

    fun withPreResolveHandler(preResolveHandler: BeanResolveHandler): BeanResolver

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
        val DEFAULT: BeanResolver = newBeanResolver(BeanResolveHandler.DEFAULTS)

        @JvmStatic
        fun newBeanResolver(resolveHandlers: Iterable<BeanResolveHandler>): BeanResolver {
            return BeanResolverImpl(resolveHandlers)
        }

        private class BeanResolverImpl(private val handlers: Iterable<BeanResolveHandler>) : BeanResolver {

            override fun resolveSchema(type: Type): BeanSchema {
                val context = Context(type, LinkedHashMap())
                for (handler in handlers) {
                    handler.resolveSchema(context)
                }
                return object : BeanSchema {
                    override val genericType: Type = type
                    override val properties: Map<String, PropertySchema> = context.beanProperties.toMap()
                }
            }

            override fun withPreResolveHandler(preResolveHandler: BeanResolveHandler): BeanResolver {
                return BeanResolverImpl(listOf(preResolveHandler).plus(handlers))
            }

            private class Context(
                override val beanType: Type,
                override val beanProperties: MutableMap<String, PropertySchema>
            ) : BeanResolveHandler.ResolveContext
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

    fun resolveSchema(context: ResolveContext)

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

    override fun resolveSchema(context: BeanResolveHandler.ResolveContext) {
        val beanInfo = Introspector.getBeanInfo(context.beanType.rawClass)
        val beanProperties = context.beanProperties
        for (propertyDescriptor in beanInfo.propertyDescriptors) {
            if (beanProperties.containsKey(propertyDescriptor.name)) {
                continue
            }
            val property = PropertySchemaImpl(context.beanType, propertyDescriptor)
            beanProperties[propertyDescriptor.name] = property
        }
    }

    private class PropertySchemaImpl(
        override val genericOwnerType: Type,
        descriptor: PropertyDescriptor
    ) : PropertySchema {

        override val name: String = descriptor.name
        override val genericType: Type by lazy { tryGenericType() }
        override val getter: VirtualInvoker? by lazy { tryGetter() }
        private val getterMethod: Method? = descriptor.readMethod
        override val setter: VirtualInvoker? by lazy { trySetter() }
        private val setterMethod: Method? = descriptor.writeMethod
        override val field: Field? by lazy { tryField() }
        override val fieldAnnotations: List<Annotation> by lazy { tryFieldAnnotations() }

        private fun tryGenericType(): Type {
            val type = if (getterMethod !== null) {
                getterMethod.genericReturnType
            } else {
                setterMethod!!.genericParameterTypes[0]
            }
            return findActualType(type)
        }

        private fun findActualType(type: Type): Type {
            return when (type) {
                is TypeVariable<*> -> type.findActualType(genericOwnerType) ?: type
                is ParameterizedType -> {
                    val actualTypeArguments = type.actualTypeArguments
                    var needTransform = false
                    for (i in actualTypeArguments.indices) {
                        val oldType = actualTypeArguments[i]
                        val newType = when (oldType) {
                            is ParameterizedType -> findActualType(oldType)
                            is TypeVariable<*> -> findActualType(oldType)
                            is WildcardType -> findActualType(oldType.upperBound)
                            is GenericArrayType -> {
                                if (oldType.genericComponentType is TypeVariable<*>) {
                                    oldType.genericComponentType.componentTypeToArray<Any>(0).javaClass
                                } else {
                                    oldType
                                }
                            }
                            else -> oldType
                        }
                        if (oldType !== newType) {
                            needTransform = true
                            actualTypeArguments[i] = newType
                        }
                    }
                    if (!needTransform) {
                        return type
                    }
                    return parameterizedType(type.rawType, this.ownerType, actualTypeArguments)
                }
                is WildcardType -> findActualType(type.upperBound)
                is GenericArrayType -> {
                    if (type.genericComponentType is TypeVariable<*>) {
                        type.genericComponentType.componentTypeToArray<Any>(0).javaClass
                    } else {
                        type
                    }
                }
                else -> type
            }
        }

        private fun tryGetter(): VirtualInvoker? {
            return if (getterMethod === null) null else getterMethod.toVirtualInvoker()
        }

        private fun trySetter(): VirtualInvoker? {
            return if (setterMethod === null) null else setterMethod.toVirtualInvoker()
        }

        private fun tryField(): Field? {
            return ownerType.findField(name, declared = true, deep = true)
        }

        private fun tryFieldAnnotations(): List<Annotation> {
            val f = field
            return if (f === null) emptyList() else f.annotations.asList()
        }

        override fun <T> getValue(bean: Any): T {
            val g = getter
            return if (g !== null) {
                g.invokeVirtual(bean)
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
                old = g.invokeVirtual(bean)
            }
            s.invokeVirtual<Any?>(bean, value)
            return old.asAny()
        }
    }
}