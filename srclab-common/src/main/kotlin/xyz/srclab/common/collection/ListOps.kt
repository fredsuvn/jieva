package xyz.srclab.common.collection

class ListOps<T> private constructor(list: List<T>) {

    private constructor(sequence: Sequence<T>)

    companion object {

        @JvmStatic
        fun <T> opsFor(list: List<T>): ListOps<T> {
            return ListOps(list)
        }

        @JvmStatic
        fun <T> opsFor(sequence: Sequence<T>): ListOps<T> {
            return ListOps(sequence)
        }
    }
}