package xyz.srclab.common.bean

import xyz.srclab.common.collect.toUnmodifiable
import xyz.srclab.common.reflect.methods
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.common.reflect.typeArguments
import java.lang.reflect.Method
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

/**
 * Context of bean resolving.
 */
interface BeanResolveContext {

    val isComplete: Boolean
    val preparedBeanType: BeanType
    val type: Type
    val typeArguments: Map<TypeVariable<*>, Type>
    val properties: MutableMap<String, PropertyType>
    val methods: List<Method>

    /**
     * Force complete current resolver, prevent left resolver handler.
     */
    fun complete()

    fun build(): BeanType {
        return preparedBeanType
    }

    companion object {
        @JvmStatic
        fun newBeanResolveContext(
            type: Type
        ): BeanResolveContext {
            val properties: MutableMap<String, PropertyType> = HashMap()
            val beanTypeProperties = properties.toUnmodifiable()
            val preparedBeanType = BeanType.newBeanType(type, beanTypeProperties)
            return object : BeanResolveContext {

                private var complete = false

                override val isComplete: Boolean get() = complete
                override val preparedBeanType: BeanType = preparedBeanType
                override val type: Type = type
                override val typeArguments: Map<TypeVariable<*>, Type> = type.typeArguments
                override val properties: MutableMap<String, PropertyType> = properties
                override val methods: List<Method> = type.rawClass.methods()

                override fun complete() {
                    complete = true
                }
            }
        }
    }
}