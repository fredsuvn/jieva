package xyz.srclab.common.id

/**
 *  Id component factory.
 *
 * @param [E] component type
 * @author sunqian
 *
 * @see ConstantComponentFactory
 * @see TimeCountComponentFactory
 */
interface IdComponentFactory<E> {

    fun create(context: IdContext): E
}