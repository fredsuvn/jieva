package xyz.srclab.common.id

/**
 *  Id component holder.
 *
 * @param [E] component type
 * @author sunqian
 */
interface IdComponentHolder<E> {

    fun get(): E

    companion object {

        @JvmStatic
        fun <E> newIdComponent(factory: IdComponentFactory<E>, context: IdContext): IdComponentHolder<E> {
            return IdComponentHolderImpl(factory, context)
        }
    }
}

private class IdComponentHolderImpl<E>(
    private val factory: IdComponentFactory<E>,
    private val context: IdContext,
) : IdComponentHolder<E> {

    private var cache: E? = null

    override fun get(): E {
        var c = cache
        if (c !== null) {
            return c
        }
        c = factory.create(context)
        cache = c
        return c
    }
}