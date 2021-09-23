package xyz.srclab.common.bean

import xyz.srclab.common.base.asAny
import xyz.srclab.common.reflect.instantiate

/**
 * Builder helper for beans.
 *
 * @see EmptyConstructorBeanBuilder
 */
interface BeanBuilder {

    /**
     * Returns a new builder.
     */
    fun <T> newBuilder(type: Class<T>): T

    /**
     * Builds and returns result of [builder].
     */
    fun <T, R> build(builder: T): R

    companion object {
        /**
         * @see EmptyConstructorBeanBuilder
         */
        val DEFAULT = EmptyConstructorBeanBuilder
    }
}

/**
 * [BeanBuilder] which uses empty-parameters-constructor to build new bean like:
 *
 * ```
 * Foo foo = new Foo();
 * ```
 */
object EmptyConstructorBeanBuilder : BeanBuilder {

    override fun <T> newBuilder(type: Class<T>): T {
        return type.instantiate()
    }

    override fun <T, R> build(builder: T): R {
        return builder.asAny()
    }
}