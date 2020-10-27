package xyz.srclab.common.collection

import xyz.srclab.common.base.asAny
import java.util.*
import kotlin.collections.addAll as addAllKt
import kotlin.collections.count as countKt
import kotlin.collections.plus as plusKt
import kotlin.collections.removeAll as removeAllKt
import kotlin.collections.retainAll as retainAllKt
import kotlin.collections.toMutableList as toMutableListKt
import kotlin.collections.toSet as toSetKt

/**
 * @author sunqian
 */
abstract class CollectionOps<T, C : Collection<T>, MC : MutableCollection<T>, THIS : CollectionOps<T, C, MC, THIS>>
protected constructor(collection: C) : BaseIterableOps<T, C, MC, THIS>(collection) {

    override fun containsAll(elements: Array<out T>): Boolean {
        return finalCollection().containsAll(elements)
    }

    override fun containsAll(elements: Iterable<T>): Boolean {
        return finalCollection().containsAll(elements)
    }

    override fun count(): Int {
        return finalCollection().count()
    }

    override fun toMutableList(): MutableList<T> {
        return finalCollection().toMutableList()
    }

    @JvmOverloads
    override fun asToList(supplier: () -> MutableList<T> = { ArrayList(count()) }): List<T> {
        return finalCollection().asToList()
    }

    @JvmOverloads
    override fun asToMutableList(supplier: () -> MutableList<T> = { ArrayList(count()) }): MutableList<T> {
        return finalCollection().asToMutableList()
    }

    open fun addAll(elements: Array<out T>): THIS {
        finalMutableCollection().addAll(elements)
        return this.asAny()
    }

    open fun addAll(elements: Iterable<T>): THIS {
        finalMutableCollection().addAll(elements)
        return this.asAny()
    }

    open fun addAll(elements: Sequence<T>): THIS {
        finalMutableCollection().addAll(elements)
        return this.asAny()
    }

    open fun removeAll(elements: Array<out T>): THIS {
        finalMutableCollection().removeAll(elements)
        return this.asAny()
    }

    open fun removeAll(elements: Iterable<T>): THIS {
        finalMutableCollection().removeAll(elements)
        return this.asAny()
    }

    open fun removeAll(elements: Sequence<T>): THIS {
        finalMutableCollection().removeAll(elements)
        return this.asAny()
    }

    open fun retainAll(elements: Array<out T>): THIS {
        finalMutableCollection().retainAll(elements)
        return this.asAny()
    }

    open fun retainAll(elements: Iterable<T>): THIS {
        finalMutableCollection().retainAll(elements)
        return this.asAny()
    }

    open fun retainAll(elements: Sequence<T>): THIS {
        finalMutableCollection().retainAll(elements)
        return this.asAny()
    }

    open fun clear(): THIS {
        finalMutableCollection().clear()
        return this.asAny()
    }

    open fun finalCollection(): Collection<T> {
        return iterable
    }

    open fun finalMutableCollection(): MutableCollection<T> {
        return iterable.asAny()
    }

    companion object {

        @JvmStatic
        fun <T> Collection<T>.containsAll(elements: Array<out T>): Boolean {
            return this.containsAll(elements.toSetKt())
        }

        @JvmStatic
        fun <T> Collection<T>.containsAll(elements: Iterable<T>): Boolean {
            return this.containsAll(elements.asToSet())
        }

        @JvmStatic
        fun <T> Collection<T>.count(): Int {
            return this.countKt()
        }

        @JvmStatic
        fun <T> Collection<T>.toMutableList(): MutableList<T> {
            return this.toMutableListKt()
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.asToList(supplier: () -> MutableList<T> = { ArrayList(count()) }): List<T> {
            return if (this is List<T>) this else toCollection(supplier())
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.asToMutableList(supplier: () -> MutableList<T> = { ArrayList(count()) }): MutableList<T> {
            return if (this is MutableList<T>) this else toCollection(supplier())
        }

        @JvmStatic
        fun <T> Collection<T>.plus(element: T): List<T> {
            return this.plusKt(element)
        }

        @JvmStatic
        fun <T> Collection<T>.plus(elements: Array<out T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Collection<T>.plus(elements: Iterable<T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Collection<T>.plus(elements: Sequence<T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.addAll(elements: Array<out T>): Boolean {
            return this.addAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.addAll(elements: Iterable<T>): Boolean {
            return this.addAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.addAll(elements: Sequence<T>): Boolean {
            return this.addAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.removeAll(elements: Array<out T>): Boolean {
            return this.removeAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.removeAll(elements: Iterable<T>): Boolean {
            return this.removeAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.removeAll(elements: Sequence<T>): Boolean {
            return this.removeAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.retainAll(elements: Array<out T>): Boolean {
            return this.retainAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.retainAll(elements: Iterable<T>): Boolean {
            return this.retainAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.retainAll(elements: Sequence<T>): Boolean {
            return this.retainAllKt(elements)
        }
    }
}