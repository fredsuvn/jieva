package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.NamingCase
import xyz.srclab.common.base.asAny
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.invoke.Invoker.Companion.toInvoker
import xyz.srclab.common.reflect.eraseTypeVariables
import xyz.srclab.common.reflect.searchFieldOrNull
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

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
            if (method.isBridge || method.isSynthetic) {
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
            if (method.isBridge || method.isSynthetic || method.declaringClass == Any::class.java) {
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