package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.invoke.Invoker.Companion.toInvoker
import xyz.srclab.common.lang.NamingCase
import xyz.srclab.common.lang.Next
import xyz.srclab.common.reflect.eraseTypeParameters
import java.lang.reflect.Type

/**
 * Handler to resolve specified bean type.
 * default, a [BeanResolver] contains a chain of [BeanResolveHandler]s.
 *
 * @see AbstractBeanResolveHandler
 * @see BeanStyleBeanResolveHandler
 * @see NamingStyleBeanResolveHandler
 */
interface BeanResolveHandler {

    /**
     * Resolves and returns whether continue to resolve by next handler.
     */
    fun resolve(@Written builder: BeanTypeBuilder): Next

    companion object {

        @JvmField
        val DEFAULTS: List<BeanResolveHandler> = listOf(
            BeanStyleBeanResolveHandler
        )
    }
}

/**
 * Convenient [BeanResolveHandler], just override [resolveAccessors] method.
 */
abstract class AbstractBeanResolveHandler : BeanResolveHandler {

    /**
     * Overrides this method to provide getters and setters of target bean type.
     */
    protected abstract fun resolveAccessors(
        @Written builder: BeanTypeBuilder,
        @Written getters: MutableMap<String, PropertyInvoker>,
        @Written setters: MutableMap<String, PropertyInvoker>,
    )

    override fun resolve(builder: BeanTypeBuilder): Next {
        val getters: MutableMap<String, PropertyInvoker> = LinkedHashMap()
        val setters: MutableMap<String, PropertyInvoker> = LinkedHashMap()
        resolveAccessors(builder, getters, setters)
        val properties = builder.properties
        for (getterEntry in getters) {

            val propertyName = getterEntry.key
            val getter = getterEntry.value
            val setter = setters[propertyName]

            if (setter === null) {
                properties[propertyName] =
                    createProperty(propertyName, getter.type, getter.invoker, null)
                continue
            }

            //remove setter because it will be deal with here next
            setters.remove(propertyName)

            if (getter.type == setter.type) {
                properties[propertyName] =
                    createProperty(propertyName, getter.type, getter.invoker, setter.invoker)
                continue
            }
        }
        for (setterEntry in setters) {
            val propertyName = setterEntry.key
            val setter = setterEntry.value
            properties[propertyName] =
                createProperty(propertyName, setter.type, null, setter.invoker)
        }
        return Next.CONTINUE
    }

    private fun createProperty(
        name: String,
        type: Type,
        getterInvoker: Invoker?,
        setterInvoker: Invoker?
    ): PropertyTypeBuilder {
        return PropertyTypeBuilder.newPropertyTypeBuilder(name, type, getterInvoker, setterInvoker)
    }

    class PropertyInvoker(val type: Type, val invoker: Invoker)
}


/**
 * Bean style of [AbstractBeanResolveHandler]:
 * * getter: getXxx()
 * * setter: setXxx(Xxx)
 */
object BeanStyleBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        builder: BeanTypeBuilder,
        getters: MutableMap<String, PropertyInvoker>,
        setters: MutableMap<String, PropertyInvoker>,
    ) {
        val methods = builder.methods
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
                val type = method.genericReturnType.eraseTypeParameters(builder.typeArguments)
                getters[propertyName] = PropertyInvoker(type, method.toInvoker())
                continue
            }
            if (name.startsWith("set") && method.parameterCount == 1) {
                val propertyName =
                    NamingCase.UPPER_CAMEL.convertTo(name.substring(3, name.length), NamingCase.LOWER_CAMEL)
                val type = method.genericParameterTypes[0].eraseTypeParameters(builder.typeArguments)
                setters[propertyName] = PropertyInvoker(type, method.toInvoker())
                continue
            }
        }
    }
}

/**
 * Naming style of [AbstractBeanResolveHandler]:
 * * getter: xxx()
 * * setter: xxx(xxx)
 */
object NamingStyleBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        builder: BeanTypeBuilder,
        getters: MutableMap<String, PropertyInvoker>,
        setters: MutableMap<String, PropertyInvoker>,
    ) {
        val methods = builder.methods
        for (method in methods) {
            if (method.isBridge || method.isSynthetic || method.declaringClass == Any::class.java) {
                continue
            }
            val name = method.name
            if (method.parameterCount == 0) {
                val type = method.genericReturnType.eraseTypeParameters(builder.typeArguments)
                getters[name] = PropertyInvoker(type, method.toInvoker())
                continue
            }
            if (method.parameterCount == 1) {
                val type = method.genericParameterTypes[0].eraseTypeParameters(builder.typeArguments)
                setters[name] = PropertyInvoker(type, method.toInvoker())
                continue
            }
        }
    }
}