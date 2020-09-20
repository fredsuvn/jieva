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

    fun add(elements: Array<out T>): Boolean {
        return addAll(mutableOperated(), elements)
    }

    fun addAll(elements: Array<out T>): Boolean {
        return addAll(mutableOperated(), elements)
    }

    fun addAll(elements: Iterable<T>): Boolean {
        return addAll(mutableOperated(), elements)
    }

    fun removeAll(elements: Array<out T>): Boolean {
        return removeAll(mutableOperated(), elements)
    }

    fun removeAll(elements: Iterable<T>): Boolean {
        return removeAll(mutableOperated(), elements)
    }

    fun removeAll(elements: Collection<T>): Boolean {
        return removeAll(mutableOperated(), elements)
    }

    fun retainAll(elements: Array<out T>): Boolean {
        return retainAll(mutableOperated(), elements)
    }

    fun retainAll(elements: Iterable<T>): Boolean {
        return retainAll(mutableOperated(), elements)
    }

    fun retainAll(elements: Collection<T>): Boolean {
        return retainAll(mutableOperated(), elements)
    }

    companion object {

        @JvmStatic
        fun <T> count(collection: Collection<T>): Int {
            return collection.count()
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