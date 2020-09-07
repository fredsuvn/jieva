package xyz.srclab.common.collection

/**
 * Operations for [Set]
 *
 * @author sunqian
 */
interface SetOps<E> : MutableSet<E> {


    fun chunked(size: Int): List<List<T>> { return operated().chunked(size) }

    fun <R> chunked(size: Int, transform: (List<T>) -> R): List<R> { return operated().chunked(size, transform) }

    fun <T, R> map(transform: (T) -> R): List<R> { /* compiled code */ }

    fun <T, R> mapIndexed(transform: (Int, T) -> R): List<R> { /* compiled code */ }

    fun <T, R : Any> mapIndexedNotNull(transform: (Int, T) -> R?): List<R> { /* compiled code */ }

    fun <T, R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(destination: C, transform: (Int, T) -> R?): C { /* compiled code */ }

    fun <T, R, C : MutableCollection<in R>> mapIndexedTo(destination: C, transform: (Int, T) -> R): C { /* compiled code */ }

    fun <T, R : Any> mapNotNull(transform: (T) -> R?): List<R> { /* compiled code */ }

    fun <T, R : Any, C : MutableCollection<in R>> mapNotNullTo(destination: C, transform: (T) -> R?): C { /* compiled code */ }

    fun <T, R, C : MutableCollection<in R>> mapTo(destination: C, transform: (T) -> R): C { /* compiled code */ }
    companion object {

        @JvmStatic
        fun <E> of(set: Set<E>): SetOps<E> {
            TODO()
        }

        @JvmStatic
        fun <E> of(iterable: Iterable<E>): SetOps<E> {
            TODO()
        }

        @JvmStatic
        fun <E> of(collection: Collection<E>): SetOps<E> {
            TODO()
        }
    }
}