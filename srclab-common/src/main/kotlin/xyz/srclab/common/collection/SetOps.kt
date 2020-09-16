package xyz.srclab.common.collection

class SetOps<T> private constructor(set: Set<T>) {

    private constructor(sequence: Sequence<T>)

    companion object {

        @JvmStatic
        fun <T> opsFor(set: Set<T>): SetOps<T> {
            return SetOps(set)
        }

        @JvmStatic
        fun <T> opsFor(sequence: Sequence<T>): SetOps<T> {
            return SetOps(sequence)
        }
    }
}