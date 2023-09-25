package xyz.srclab.common.bean

import xyz.srclab.annotations.OutParam
import xyz.srclab.common.base.uncapitalize
import xyz.srclab.common.func.InstFunc
import xyz.srclab.common.func.InstFunc.Companion.toInstInvoke
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
     * Resolves [BeanResolveContext.type] from given [context].
     *
     * For each handler, the resolved properties should be put into or remove from [BeanResolveContext.properties].
     */
    fun resolve(@OutParam context: BeanResolveContext)

    companion object {

        /**
         * Bean style resolve handler.
         *
         * @see BeanStyleBeanResolveHandler
         */
        @JvmField
        val BEAN_STYLE = BeanStyleBeanResolveHandler

        /**
         * Record style resolve handler.
         *
         * @see RecordStyleBeanResolveHandler
         */
        @JvmField
        val RECORD_STYLE = RecordStyleBeanResolveHandler

        /**
         * Default resolve handlers for [defaultResolver].
         */
        @JvmField
        val DEFAULTS: List<BeanResolveHandler> = listOf(
            BEAN_STYLE
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
            @OutParam getters: MutableMap<String, GetterInfo>,
            @OutParam setters: MutableMap<String, SetterInfo>,
    )

    override fun resolve(context: BeanResolveContext) {

        val getters: MutableMap<String, GetterInfo> = LinkedHashMap()
        val setters: MutableMap<String, SetterInfo> = LinkedHashMap()

        resolveAccessors(context, getters, setters)

        for (getterEntry in getters) {
            val propertyName = getterEntry.key
            val getter = getterEntry.value
            val setter = setters[propertyName]
            if (setter === null) {
                val propertyType = PropertyType(
                    context.beanType,
                    propertyName,
                    getter.type,
                    getter.getter,
                    null,
                    getter.field,
                    getter.getterMethod,
                    null
                )
                context.properties[propertyName] = propertyType
            } else if (getter.type == setter.type) {
                val propertyType = PropertyType(
                    context.beanType,
                    propertyName,
                    getter.type,
                    getter.getter,
                    setter.setter,
                    getter.field,
                    getter.getterMethod,
                    setter.setterMethod
                )
                context.properties[propertyName] = propertyType
                setters.remove(propertyName)
            }
        }

        for (setterEntry in setters) {
            val propertyName = setterEntry.key
            val setter = setterEntry.value
            val propertyType = PropertyType(
                context.beanType,
                propertyName,
                setter.type,
                null,
                setter.setter,
                setter.field,
                null,
                setter.setterMethod
            )
            context.properties[propertyName] = propertyType
        }
    }

    data class GetterInfo(
        val name: String,
        val type: Type,
        val getter: InstFunc?,
        val field: Field?,
        val getterMethod: Method?,
    )

    data class SetterInfo(
        val name: String,
        val type: Type,
        val setter: InstFunc?,
        val field: Field?,
        val setterMethod: Method?,
    )
}


/**
 * Bean style of [AbstractBeanResolveHandler]:
 *
 * * getter: getXxx()
 * * setter: setXxx(Xxx)
 *
 * Note this handler doesn't add property if property's name has been existed in builder's properties.
 */
object BeanStyleBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        context: BeanResolveContext,
        getters: MutableMap<String, GetterInfo>,
        setters: MutableMap<String, SetterInfo>,
    ) {
        val beanClass = context.type.rawClass
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
                val propertyName = name.substring(3).uncapitalize()
                if (context.properties.containsKey(propertyName)) {
                    continue
                }
                val type = method.genericReturnType.eraseTypeParameters(context.typeArguments)
                val field = beanClass.searchFieldOrNull(propertyName, true)
                getters[propertyName] = GetterInfo(propertyName, type, method.toInstInvoke(), field, method)
                continue
            }
            if (name.startsWith("set") && method.parameterCount == 1) {
                val propertyName = name.substring(3).uncapitalize()
                if (context.properties.containsKey(propertyName)) {
                    continue
                }
                val type = method.genericParameterTypes[0].eraseTypeParameters(context.typeArguments)
                val field = beanClass.searchFieldOrNull(propertyName, true)
                setters[propertyName] = SetterInfo(propertyName, type, method.toInstInvoke(), field, method)
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
 *
 * Note this handler doesn't add property if property's name has been existed in builder's properties.
 */
object RecordStyleBeanResolveHandler : AbstractBeanResolveHandler() {

    override fun resolveAccessors(
        context: BeanResolveContext,
        getters: MutableMap<String, GetterInfo>,
        setters: MutableMap<String, SetterInfo>,
    ) {
        val beanClass = context.type.rawClass
        val methods = context.methods
        for (method in methods) {
            if (method.isBridge || method.isSynthetic || method.declaringClass == Any::class.java) {
                continue
            }
            val propertyName = method.name
            if (context.properties.containsKey(propertyName)) {
                continue
            }
            if (method.parameterCount == 0) {
                val type = method.genericReturnType.eraseTypeParameters(context.typeArguments)
                val field = beanClass.searchFieldOrNull(propertyName, true)
                getters[propertyName] = GetterInfo(propertyName, type, method.toInstInvoke(), field, method)
                continue
            }
            if (method.parameterCount == 1) {
                val type = method.genericParameterTypes[0].eraseTypeParameters(context.typeArguments)
                val field = beanClass.searchFieldOrNull(propertyName, true)
                setters[propertyName] = SetterInfo(propertyName, type, method.toInstInvoke(), field, method)
                continue
            }
        }
    }
}