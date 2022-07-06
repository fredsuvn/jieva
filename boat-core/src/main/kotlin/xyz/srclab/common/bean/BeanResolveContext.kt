package xyz.srclab.common.bean

import xyz.srclab.common.reflect.getTypeArguments
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Context of bean resolving.
 */
open class BeanResolveContext(

    /**
     * Type to be resolved.
     */
    open val type: Type,

    /**
     * Final [BeanType].
     *
     * The properties of this [BeanType] is backed by [properties],
     * any change for [properties] will reflect to this [BeanType] and vise versa.
     */
    open val beanType: BeanType,

    /**
     * Properties of target [BeanType], backs [beanType].
     */
    open val properties: MutableMap<String, PropertyType>
) {

    private var _isBreak = false

    /**
     * Type arguments of [type].
     *
     * @see Type.getTypeArguments
     */
    open val typeArguments: Map<TypeVariable<*>, Type> = type.getTypeArguments()

    /**
     * Methods of raw class of [type].
     */
    open val methods: List<Method> = type.rawClass.methods.asList()

    /**
     * Whether current resolving is broken.
     */
    open val isBreak: Boolean get() = _isBreak

    /**
     * Sets current resolving completed, causes remainder [BeanResolveHandler] will not be called.
     */
    open fun complete() {
        _isBreak = true
    }
}