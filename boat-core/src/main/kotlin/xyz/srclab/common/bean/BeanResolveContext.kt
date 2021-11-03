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
class BeanResolveContext(val type: Type) {

    val typeArguments: Map<TypeVariable<*>, Type> = type.getTypeArguments()
    val methods: List<Method> = type.rawClass.methods()
}