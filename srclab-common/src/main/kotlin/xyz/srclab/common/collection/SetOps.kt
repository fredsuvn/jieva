package xyz.srclab.common.collection

import xyz.srclab.common.base.As

class SetOps<T> private constructor(set: Set<T>) :
    CollectionOps<T, Set<T>, MutableSet<T>, SetOps<T>>(set) {

    fun plus(element: T): SetOps<T> {
        return toSetOps(plus(operated(), element))
    }

    fun plus(elements: Array<out T>): SetOps<T> {
        return toSetOps(plus(operated(), elements))
    }

    fun plus(elements: Iterable<T>): SetOps<T> {
        return toSetOps(plus(operated(), elements))
    }

    fun plus(elements: Sequence<T>): SetOps<T> {
        return toSetOps(plus(operated(), elements))
    }

    fun minus(element: T): SetOps<T> {
        return toSetOps(minus(operated(), element))
    }

    fun minus(elements: Array<out T>): SetOps<T> {
        return toSetOps(minus(operated(), elements))
    }

    fun minus(elements: Iterable<T>): SetOps<T> {
        return toSetOps(minus(operated(), elements))
    }

    fun minus(elements: Sequence<T>): SetOps<T> {
        return toSetOps(minus(operated(), elements))
    }

    fun finalSet(): Set<T> {
        return operated()
    }

    fun finalMutableSet(): MutableSet<T> {
        return mutableOperated()
    }

    override fun toSelfOps(): SetOps<T> {
        return this
    }

    override fun <T> toIterableOps(iterable: Iterable<T>): IterableOps<T> {
        return IterableOps.opsFor(iterable)
    }

    override fun <T> toListOps(list: List<T>): ListOps<T> {
        return ListOps.opsFor(list)
    }

    override fun <T> toSetOps(set: Set<T>): SetOps<T> {
        this.operated = As.any(set)
        return As.any(this)
    }

    override fun <K, V> toMapOps(map: Map<K, V>): MapOps<K, V> {
        return MapOps.opsFor(map)
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(set: Set<T>): SetOps<T> {
            return SetOps(set)
        }

        @JvmStatic
        fun <T> plus(set: Set<T>, element: T): Set<T> {
            return set.plus(element)
        }

        @JvmStatic
        fun <T> plus(set: Set<T>, elements: Array<out T>): Set<T> {
            return set.plus(elements)
        }

        @JvmStatic
        fun <T> plus(set: Set<T>, elements: Iterable<T>): Set<T> {
            return set.plus(elements)
        }

        @JvmStatic
        fun <T> plus(set: Set<T>, elements: Sequence<T>): Set<T> {
            return set.plus(elements)
        }

        @JvmStatic
        fun <T> minus(set: Set<T>, element: T): Set<T> {
            return set.minus(element)
        }

        @JvmStatic
        fun <T> minus(set: Set<T>, elements: Array<out T>): Set<T> {
            return set.minus(elements)
        }

        @JvmStatic
        fun <T> minus(set: Set<T>, elements: Iterable<T>): Set<T> {
            return set.minus(elements)
        }

        @JvmStatic
        fun <T> minus(set: Set<T>, elements: Sequence<T>): Set<T> {
            return set.minus(elements)
        }
    }
}