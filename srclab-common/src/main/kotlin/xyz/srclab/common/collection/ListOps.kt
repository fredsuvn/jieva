package xyz.srclab.common.collection

interface ListOps<T> : IterableOps<T> {

    companion object {

        @JvmStatic
        fun <T> opsFor(list: List<T>): ListOps<T> {
            return IterableOps(iterable)
        }

        @JvmStatic
        fun <T> opsFor(sequence: Sequence<T>): ListOps<T> {
            return IterableOps(iterable)
        }
    }
}