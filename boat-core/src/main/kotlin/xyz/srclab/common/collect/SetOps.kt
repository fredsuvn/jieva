package xyz.srclab.common.collect

import xyz.srclab.common.lang.asAny

class SetOps<T>(set: Set<T>) : CollectionOps<T, Set<T>, MutableSet<T>, SetOps<T>>(set) {

    fun finalSet(): Set<T> {
        return operated
    }

    fun finalMutableSet(): MutableSet<T> {
        return operated.asAny()
    }

    override fun <T> Set<T>.toSetOps(): SetOps<T> {
        operated = this.asAny()
        return this@SetOps.asAny()
    }

    override fun plus(element: T): SetOps<T> {
        return finalSet().plus(element).toSetOps()
    }

    override fun plus(elements: Array<out T>): SetOps<T> {
        return finalSet().plus(elements).toSetOps()
    }

    override fun plus(elements: Iterable<T>): SetOps<T> {
        return finalSet().plus(elements).toSetOps()
    }

    override fun plus(elements: Sequence<T>): SetOps<T> {
        return finalSet().plus(elements).toSetOps()
    }

    override fun minus(element: T): SetOps<T> {
        return finalSet().minus(element).toSetOps()
    }

    override fun minus(elements: Array<out T>): SetOps<T> {
        return finalSet().minus(elements).toSetOps()
    }

    override fun minus(elements: Iterable<T>): SetOps<T> {
        return finalSet().minus(elements).toSetOps()
    }

    override fun minus(elements: Sequence<T>): SetOps<T> {
        return finalSet().minus(elements).toSetOps()
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(iterable: Iterable<T>): SetOps<T> {
            return SetOps(iterable.asToSet())
        }
    }
}