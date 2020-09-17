package xyz.srclab.common.collection

class IterableOps<T> private constructor(iterable: Iterable<T>) :
    BaseIterableOps<T, Iterable<T>, MutableIterable<T>, IterableOps<T>>(iterable) {

    override fun <T> toListOps(list: List<T>): ListOps<T> {
        return ListOps.opsFor(list)
    }

    override fun <T> toListOps(sequence: Sequence<T>): ListOps<T> {
        return ListOps.opsFor(sequence)
    }

    override fun <T> toSetOps(set: Set<T>): SetOps<T> {
        return SetOps.opsFor(set)
    }

    override fun <T> toSetOps(sequence: Sequence<T>): SetOps<T> {
        return SetOps.opsFor(sequence)
    }

    override fun <K, V> toMapOps(map: Map<K, V>): MapOps<K, V> {
        TODO("Not yet implemented")
    }

    override fun sequenceToCollection(sequence: Sequence<T>): Iterable<T> {
        TODO("Not yet implemented")
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(iterable: Iterable<T>): IterableOps<T> {
            return IterableOps(iterable)
        }

        @JvmStatic
        fun <T> opsFor(sequence: Sequence<T>): IterableOps<T> {
            return IterableOps(iterable)
        }
    }
}