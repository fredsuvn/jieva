package xyz.srclab.common.collection

import xyz.srclab.common.base.As

class IterableOps<T> private constructor(iterable: Iterable<T>) :
    BaseIterableOps<T, Iterable<T>, MutableIterable<T>, IterableOps<T>>(iterable) {

    fun plus(element: T): ListOps<T> {
        return toListOps(plus(operated(), element))
    }

    fun plus(elements: Array<out T>): ListOps<T> {
        return toListOps(plus(operated(), elements))
    }

    fun plus(elements: Iterable<T>): ListOps<T> {
        return toListOps(plus(operated(), elements))
    }

    fun plus(elements: Sequence<T>): ListOps<T> {
        return toListOps(plus(operated(), elements))
    }

    fun minus(element: T): ListOps<T> {
        return toListOps(minus(operated(), element))
    }

    fun minus(elements: Array<out T>): ListOps<T> {
        return toListOps(minus(operated(), elements))
    }

    fun minus(elements: Iterable<T>): ListOps<T> {
        return toListOps(minus(operated(), elements))
    }

    fun minus(elements: Sequence<T>): ListOps<T> {
        return toListOps(minus(operated(), elements))
    }

    fun toListOps(): ListOps<T> {
        return toListOps(operated().toList())
    }

    fun toSetOps(): SetOps<T> {
        return toSetOps(operated().toSet())
    }

    fun finalIterable(): Iterable<T> {
        return operated()
    }

    fun finalMutableIterable(): MutableIterable<T> {
        return mutableOperated()
    }

    override fun toSelfOps(): IterableOps<T> {
        return this
    }

    override fun <T> toIterableOps(iterable: Iterable<T>): IterableOps<T> {
        this.operated = As.any(iterable)
        return As.any(this)
    }

    override fun <T> toListOps(list: List<T>): ListOps<T> {
        return ListOps.opsFor(list)
    }

    override fun <T> toSetOps(set: Set<T>): SetOps<T> {
        return SetOps.opsFor(set)
    }

    override fun <K, V> toMapOps(map: Map<K, V>): MapOps<K, V> {
        return MapOps.opsFor(map)
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(iterable: Iterable<T>): IterableOps<T> {
            return IterableOps(iterable)
        }
    }
}