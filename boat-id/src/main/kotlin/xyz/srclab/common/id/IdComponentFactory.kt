package xyz.srclab.common.id

/**
 *  Id component factory.
 *
 * @param [E] component type
 * @author sunqian
 *
 * @see TimeCountComponentFactory
 */
interface IdComponentFactory<E> {

    fun create(context: IdContext): E
}