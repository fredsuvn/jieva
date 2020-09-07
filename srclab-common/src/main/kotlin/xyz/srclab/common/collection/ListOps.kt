package xyz.srclab.common.collection

/**
 * Operations for [List]
 *
 * @author sunqian
 */
interface ListOps<E> : MutableList<E> {


    fun chunked(size: Int): List<List<T>> { return operated().chunked(size) }

    fun <R> chunked(size: Int, transform: (List<T>) -> R): List<R> { return operated().chunked(size, transform) }

    fun List<T>.dropLast(n: Int): List<T> { /* compiled code */
    }

    fun List<T>.dropLastWhile(predicate: (T) -> Boolean): List<T> { /* compiled code */
    }

    fun List<T>.elementAt(index: Int): T { /* compiled code */
    }

    fun List<T>.elementAtOrElse(index: Int, defaultValue: (Int) -> T): T { /* compiled code */
    }

    fun List<T>.elementAtOrNull(index: Int): T? { /* compiled code */
    }
    fun List<T>.findLast(predicate: (T) -> Boolean): T? { /* compiled code */ }
    fun List<T>.first(): T { /* compiled code */ }
    fun List<T>.firstOrNull(): T? { /* compiled code */ }

    fun <R> flatMap(transform: (T) -> Iterable<R>): List<R> { /* compiled code */ }

    fun <R> flatMapIndexed(transform: (Int, T) -> Iterable<R>): List<R> { /* compiled code */ }

    fun <R, C : MutableCollection<in R>> flatMapIndexedTo(destination: C, transform: (Int, T) -> Iterable<R>): C { /* compiled code */ }

    fun <R, C : MutableCollection<in R>> flatMapTo(destination: C, transform: (T) -> Iterable<R>): C { /* compiled code */ }

    fun <R> fold(initial: R, operation: (R, T) -> R): R { /* compiled code */ }

    fun <R> foldIndexed(initial: R, operation: (Int, R, T) -> R): R { /* compiled code */ }

    fun <R> List<T>.foldRight(initial: R, operation: (T, R) -> R): R { /* compiled code */ }

    fun <R> List<T>.foldRightIndexed(initial: R, operation: (Int, T, R) -> R): R { /* compiled code */ }

    fun List<T>.getOrElse(index: Int, defaultValue: (Int) -> T): T { /* compiled code */ }

    fun List<T>.getOrNull(index: Int): T? { /* compiled code */ }
    fun < T> List<T>.indexOf(element: T): Int { /* compiled code */ }
    fun List<T>.indexOfFirst(predicate: (T) -> Boolean): Int { /* compiled code */ }
    fun List<T>.indexOfLast(predicate: (T) -> Boolean): Int { /* compiled code */ }
    fun List<T>.last(): T { /* compiled code */ }

    fun List<T>.last(predicate: (T) -> Boolean): T { /* compiled code */ }
    fun < T> List<T>.lastIndexOf(element: T): Int { /* compiled code */ }
    fun List<T>.lastOrNull(): T? { /* compiled code */ }

    fun List<T>.lastOrNull(predicate: (T) -> Boolean): T? { /* compiled code */ }

    fun <T, R> map(transform: (T) -> R): List<R> { /* compiled code */ }

    fun <T, R> mapIndexed(transform: (Int, T) -> R): List<R> { /* compiled code */ }

    fun <T, R : Any> mapIndexedNotNull(transform: (Int, T) -> R?): List<R> { /* compiled code */ }

    fun <T, R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(destination: C, transform: (Int, T) -> R?): C { /* compiled code */ }

    fun <T, R, C : MutableCollection<in R>> mapIndexedTo(destination: C, transform: (Int, T) -> R): C { /* compiled code */ }

    fun <T, R : Any> mapNotNull(transform: (T) -> R?): List<R> { /* compiled code */ }

    fun <T, R : Any, C : MutableCollection<in R>> mapNotNullTo(destination: C, transform: (T) -> R?): C { /* compiled code */ }

    fun <T, R, C : MutableCollection<in R>> mapTo(destination: C, transform: (T) -> R): C { /* compiled code */ }

    fun minus(element: T): List<T> { /* compiled code */ }

    fun minus(elements: Array<out T>): List<T> { /* compiled code */ }

    fun minus(elements: Iterable<T>): List<T> { /* compiled code */ }

    fun minus(elements: Sequence<T>): List<T> { /* compiled code */ }

    fun minusElement(element: T): List<T> { /* compiled code */ }

    fun <S, T : S> List<T>.reduceRight(operation: (T, S) -> S): S { /* compiled code */ }

    fun <S, T : S> List<T>.reduceRightIndexed(operation: (Int, T, S) -> S): S { /* compiled code */ }

    fun <S, T : S> List<T>.reduceRightIndexedOrNull(operation: (Int, T, S) -> S): S? { /* compiled code */ }

    fun <S, T : S> List<T>.reduceRightOrNull(operation: (T, S) -> S): S? { /* compiled code */ }

    fun MutableList<T>.shuffle(random: kotlin.random.Random): Unit { /* compiled code */ }

    fun List<T>.slice(indices: Iterable<Int>): List<T> { /* compiled code */ }

    fun List<T>.slice(indices: IntRange): List<T> { /* compiled code */ }

    fun <T, R : Comparable<R>> MutableList<T>.sortBy(crossinline selector: (T) -> R?): Unit { /* compiled code */ }

    fun <T, R : Comparable<R>> MutableList<T>.sortByDescending(crossinline selector: (T) -> R?): Unit { /* compiled code */ }

    fun <T : Comparable<T>> MutableList<T>.sortDescending(): Unit { /* compiled code */ }

    fun <T : Comparable<T>> sorted(): List<T> { /* compiled code */ }
    fun List<T>.takeLast(n: Int): List<T> { /* compiled code */ }

    fun List<T>.takeLastWhile(predicate: (T) -> Boolean): List<T> { /* compiled code */ }
    companion object {

        @JvmStatic
        fun <E> of(list: List<E>): ListOps<E> {
            TODO()
        }

        @JvmStatic
        fun <E> of(iterable: Iterable<E>): ListOps<E> {
            TODO()
        }

        @JvmStatic
        fun <E> of(collection: Collection<E>): ListOps<E> {
            TODO()
        }
    }
}