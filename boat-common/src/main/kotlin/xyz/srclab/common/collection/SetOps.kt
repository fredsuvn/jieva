package xyz.srclab.common.collection

import xyz.srclab.common.base.asAny
import kotlin.collections.minus as minusKt
import kotlin.collections.plus as plusKt

class SetOps<T>(set: Set<T>) : CollectionOps<T, Set<T>, MutableSet<T>, SetOps<T>>(set) {

    fun plus(element: T): SetOps<T> {
        return finalSet().plus(element).toSetOps()
    }

    fun plus(elements: Array<out T>): SetOps<T> {
        return finalSet().plus(elements).toSetOps()
    }

    fun plus(elements: Iterable<T>): SetOps<T> {
        return finalSet().plus(elements).toSetOps()
    }

    fun plus(elements: Sequence<T>): SetOps<T> {
        return finalSet().plus(elements).toSetOps()
    }

    fun minus(element: T): SetOps<T> {
        return finalSet().minus(element).toSetOps()
    }

    fun minus(elements: Array<out T>): SetOps<T> {
        return finalSet().minus(elements).toSetOps()
    }

    fun minus(elements: Iterable<T>): SetOps<T> {
        return finalSet().minus(elements).toSetOps()
    }

    fun minus(elements: Sequence<T>): SetOps<T> {
        return finalSet().minus(elements).toSetOps()
    }

    fun finalSet(): Set<T> {
        return iterable
    }

    fun finalMutableSet(): MutableSet<T> {
        return iterable.asAny()
    }

    override fun Set<T>.asThis(): SetOps<T> {
        iterable = this
        return this@SetOps
    }

    override fun <T> Iterable<T>.toIterableOps(): IterableOps<T> {
        return IterableOps.opsFor(this)
    }

    override fun <T> List<T>.toListOps(): ListOps<T> {
        return ListOps.opsFor(this)
    }

    override fun <T> Set<T>.toSetOps(): SetOps<T> {
        iterable = this.asAny()
        return this@SetOps.asAny()
    }

    override fun <K, V> Map<K, V>.toMapOps(): MapOps<K, V> {
        return MapOps.opsFor(this)
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(set: Set<T>): SetOps<T> {
            return SetOps(set)
        }

        @JvmStatic
        fun <T> Set<T>.plus(element: T): Set<T> {
            return this.plusKt(element)
        }

        @JvmStatic
        fun <T> Set<T>.plus(elements: Array<out T>): Set<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Set<T>.plus(elements: Iterable<T>): Set<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Set<T>.plus(elements: Sequence<T>): Set<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Set<T>.minus(element: T): Set<T> {
            return this.minusKt(element)
        }

        @JvmStatic
        fun <T> Set<T>.minus(elements: Array<out T>): Set<T> {
            return this.minusKt(elements)
        }

        @JvmStatic
        fun <T> Set<T>.minus(elements: Iterable<T>): Set<T> {
            return this.minusKt(elements)
        }

        @JvmStatic
        fun <T> Set<T>.minus(elements: Sequence<T>): Set<T> {
            return this.minusKt(elements)
        }
    }
}