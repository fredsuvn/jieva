package xyz.srclab.common.collection

/**
 * @author sunqian
 */
abstract class CollectionOps<T, C : Collection<T>, MC : MutableCollection<T>, THIS : CollectionOps<T, C, MC, THIS>>
protected constructor(operated: C) :
    BaseIterableOps<T, C, MC, THIS>(operated) {

    override fun count(): Int {
        return count(operated())
    }

    override fun containsAll(elements: Array<out T>): Boolean {
        return containsAll(operated(), elements)
    }

    override fun containsAll(elements: Iterable<T>): Boolean {
        return containsAll(operated(), elements)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return containsAll(operated(), elements)
    }

    fun add(element: T): THIS {
        add(element)
        return toSelfOps()
    }

    override fun remove(element: T): THIS {
        remove(element)
        return toSelfOps()
    }

    fun addAll(elements: Array<out T>): THIS {
        addAll(mutableOperated(), elements)
        return toSelfOps()
    }

    fun addAll(elements: Iterable<T>): THIS {
        addAll(mutableOperated(), elements)
        return toSelfOps()
    }

    fun removeAll(elements: Array<out T>): THIS {
        removeAll(mutableOperated(), elements)
        return toSelfOps()
    }

    fun removeAll(elements: Iterable<T>): THIS {
        removeAll(mutableOperated(), elements)
        return toSelfOps()
    }

    fun removeAll(elements: Collection<T>): THIS {
        removeAll(mutableOperated(), elements)
        return toSelfOps()
    }

    fun retainAll(elements: Array<out T>): THIS {
        retainAll(mutableOperated(), elements)
        return toSelfOps()
    }

    fun retainAll(elements: Iterable<T>): THIS {
        retainAll(mutableOperated(), elements)
        return toSelfOps()
    }

    fun retainAll(elements: Collection<T>): THIS {
        retainAll(mutableOperated(), elements)
        return toSelfOps()
    }

    companion object {

        @JvmStatic
        fun <T> count(collection: Collection<T>): Int {
            return collection.count()
        }

        @JvmStatic
        fun <T> containsAll(collection: Collection<T>, elements: Array<out T>): Boolean {
            return containsAll(collection, elements.toSet())
        }

        @JvmStatic
        fun <T> containsAll(collection: Collection<T>, elements: Iterable<T>): Boolean {
            return containsAll(collection, elements.toSet())
        }

        @JvmStatic
        fun <T> containsAll(collection: Collection<T>, elements: Collection<T>): Boolean {
            return collection.containsAll(elements)
        }

        @JvmStatic
        fun <T> add(collection: MutableCollection<T>, element: T): Boolean {
            return collection.add(element)
        }

        @JvmStatic
        fun <T> remove(collection: MutableCollection<T>, element: T): Boolean {
            return collection.remove(element)
        }

        @JvmStatic
        fun <T> addAll(collection: MutableCollection<T>, elements: Array<out T>): Boolean {
            return collection.addAll(elements)
        }

        @JvmStatic
        fun <T> addAll(collection: MutableCollection<T>, elements: Iterable<T>): Boolean {
            return collection.addAll(elements)
        }

        @JvmStatic
        fun <T> removeAll(collection: MutableCollection<T>, elements: Array<out T>): Boolean {
            return collection.removeAll(elements)
        }

        @JvmStatic
        fun <T> removeAll(collection: MutableCollection<T>, elements: Iterable<T>): Boolean {
            return collection.removeAll(elements)
        }

        @JvmStatic
        fun <T> removeAll(collection: MutableCollection<T>, elements: Collection<T>): Boolean {
            return collection.removeAll(elements)
        }

        @JvmStatic
        fun <T> retainAll(collection: MutableCollection<T>, elements: Array<out T>): Boolean {
            return collection.retainAll(elements)
        }

        @JvmStatic
        fun <T> retainAll(collection: MutableCollection<T>, elements: Iterable<T>): Boolean {
            return collection.retainAll(elements)
        }

        @JvmStatic
        fun <T> retainAll(collection: MutableCollection<T>, elements: Collection<T>): Boolean {
            return collection.retainAll(elements)
        }

        @JvmStatic
        fun <T> plus(collection: Collection<T>, element: T): List<T> {
            return collection.plus(element)
        }

        @JvmStatic
        fun <T> plus(collection: Collection<T>, elements: Array<out T>): List<T> {
            return collection.plus(elements)
        }

        @JvmStatic
        fun <T> plus(collection: Collection<T>, elements: Iterable<T>): List<T> {
            return collection.plus(elements)
        }

        @JvmStatic
        fun <T> plus(collection: Collection<T>, elements: Sequence<T>): List<T> {
            return collection.plus(elements)
        }
    }
}