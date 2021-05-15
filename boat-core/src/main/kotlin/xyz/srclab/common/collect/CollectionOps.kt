package xyz.srclab.common.collect

import xyz.srclab.common.lang.asAny

/**
 * @author sunqian
 */
abstract class CollectionOps<T, C : Collection<T>, MC : MutableCollection<T>, THIS : CollectionOps<T, C, MC, THIS>>
protected constructor(collection: C) : BaseIterableOps<T, C, MC, THIS>(collection) {

    open fun finalCollection(): Collection<T> {
        return operated
    }

    open fun finalMutableCollection(): MutableCollection<T> {
        return operated.asAny()
    }

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
}