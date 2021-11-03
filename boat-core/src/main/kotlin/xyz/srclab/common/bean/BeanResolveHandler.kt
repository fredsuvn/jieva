package xyz.srclab.common.bean

import xyz.srclab.annotations.Written
import xyz.srclab.common.base.NamingCase
import xyz.srclab.common.base.JumpStatement
import xyz.srclab.common.invoke.InstInvoker
import xyz.srclab.common.invoke.Invoker.Companion.toInvoker
import xyz.srclab.common.reflect.eraseTypeParameters
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.common.reflect.searchFieldOrNull
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Handler to resolve specified bean type.
 * default, a [BeanResolver] contains a chain of [BeanResolveHandler]s.
 *
 * @see AbstractBeanResolveHandler
 * @see BeanStyleBeanResolveHandler
 * @see RecordStyleBeanResolveHandler
 */
interface BeanResolveHandler {

    /**
     * Resolves into given [builder].
     */
    fun resolve(context: BeanResolveContext, @Written builder: BeanTypeBuilder):JumpStatement

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
        context: BeanResolveContext,
        @Written getters: MutableMap<String, GetterInfo>,
        @Written setters: MutableMap<String, SetterInfo>,
    )

    override fun resolve(context: BeanResolveContext, @Written builder2: BeanTypeBuilder) {

        val getters: MutableMap<String, GetterInfo> = LinkedHashMap()
        val setters: MutableMap<String, SetterInfo> = LinkedHashMap()

        resolveAccessors(context, getters, setters)

        val properties:MutableMap<String, PropertyType> = LinkedHashMap()
        for (getterEntry in getters) {
            val propertyName = getterEntry.key
            val getter = getterEntry.value
            val setter = setters[propertyName]
            if (setter === null) {
                properties[propertyName] = PropertyType.newPropertyType(
                    builder.preparedBeanType,
                    propertyName,
                    getter.type,
                    getter.getter,
                    null,
                    getter.field,
                    getter.getterMethod,
                    null
                )
            } else if (getter.type == setter.type) {
                properties[propertyName] = PropertyType.newPropertyType(
                    builder.preparedBeanType,
                    propertyName,
                    getter.type,
                    getter.getter,
                    setter.setter,
                    getter.field,
                    getter.getterMethod,
                    setter.setterMethod
                )
                setters.remove(propertyName)
            }
        }

        for (setterEntry in setters) {
            val propertyName = setterEntry.key
            val setter = setterEntry.value
            properties[propertyName] = PropertyType.newPropertyType(
                builder.preparedBeanType,
                propertyName,
                setter.type,
                null,
                setter.setter,
                setter.field,
                null,
                setter.setterMethod
            )
        }
    }

    data class GetterInfo(
        val name: String,
        val type: Type,
        val getter: InstInvoker?,
        val field: Field?,
        val getterMethod: Method?,
    )

    data class SetterInfo(
        val name: String,
        val type: Type,
        val setter: InstInvoker?,
        val field: Field?,
        val setterMethod: Method?,
    )
}


/**
 * Bean style of [AbstractBeanResolveHandler]:
 *
 * * getter: getXxx()
 * * setter: setXxx(Xxx)
 */
object BeanStyleBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        builder: BeanResolveContext,
        getters: MutableMap<String, GetterInfo>,
        setters: MutableMap<String, SetterInfo>,
    ) {
        val beanClass = builder.preparedBeanType.type.rawClass
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
                val field = beanClass.searchFieldOrNull(propertyName, true)
                getters[propertyName] = GetterInfo(propertyName, type, method.toInvoker(), field, method)
                continue
            }
            if (name.startsWith("set") && method.parameterCount == 1) {
                val propertyName =
                    NamingCase.UPPER_CAMEL.convertTo(name.substring(3, name.length), NamingCase.LOWER_CAMEL)
                val type = method.genericParameterTypes[0].eraseTypeParameters(builder.typeArguments)
                val field = beanClass.searchFieldOrNull(propertyName, true)
                setters[propertyName] = SetterInfo(propertyName, type, method.toInvoker(), field, method)
                continue
            }
        }
    }
}

/**
 * Record style of [AbstractBeanResolveHandler]:
 *
 * * getter: xxx()
 * * setter: xxx(xxx)
 */
object RecordStyleBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        builder: BeanResolveContext,
        getters: MutableMap<String, GetterInfo>,
        setters: MutableMap<String, SetterInfo>,
    ) {
        val beanClass = builder.preparedBeanType.type.rawClass
        val methods = builder.methods
        for (method in methods) {
            if (method.isBridge || method.isSynthetic || method.declaringClass == Any::class.java) {
                continue
            }
            val name = method.name
            if (method.parameterCount == 0) {
                val type = method.genericReturnType.eraseTypeParameters(builder.typeArguments)
                val field = beanClass.searchFieldOrNull(name, true)
                getters[name] = GetterInfo(name, type, method.toInvoker(), field, method)
                continue
            }
            if (method.parameterCount == 1) {
                val type = method.genericParameterTypes[0].eraseTypeParameters(builder.typeArguments)
                val field = beanClass.searchFieldOrNull(name, true)
                setters[name] = SetterInfo(name, type, method.toInvoker(), field, method)
                continue
            }
        }
    }
}