package xyz.srclab.common.bean

import xyz.srclab.common.base.asAny
import xyz.srclab.common.reflect.instantiate

/**
 * Bean creator.
 *
 * @see EmptyConstructorBeanCreator
 */
interface BeanCreator {

    /**
     * Returns a new bean builder.
     */
    fun <T> newBuilder(type: Class<T>): T

    /**
     * Builds and returns result of [builder].
     */
    fun <T, R> build(builder: T): R

    companion object {
        /**
         * @see EmptyConstructorBeanCreator
         */
        val DEFAULT = EmptyConstructorBeanCreator
    }
}

/**
 * [BeanCreator] which uses empty-parameters-constructor to build new bean like:
 *
 * ```
 * Foo foo = new Foo();
 * ```
 */
object EmptyConstructorBeanCreator : BeanCreator {

    override fun <T> newBuilder(type: Class<T>): T {
        return type.instantiate()
    }

    override fun <T, R> build(builder: T): R {
        return builder.asAny()
    }
}