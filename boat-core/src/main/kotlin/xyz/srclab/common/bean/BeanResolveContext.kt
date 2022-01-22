package xyz.srclab.common.bean

import xyz.srclab.common.reflect.getTypeArguments
import xyz.srclab.common.reflect.methods
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Context of bean resolving.
 */
open class BeanResolveContext(open val type: Type) {

    private var _isBreak = false

    val typeArguments: Map<TypeVariable<*>, Type> = type.getTypeArguments()
    val methods: List<Method> = type.rawClass.methods()
    val isBreak: Boolean get() = _isBreak

    /**
     * Sets current resolving completed, causes remainder [BeanResolveHandler] will not be called.
     */
    open fun complete() {
        _isBreak = true
    }
}