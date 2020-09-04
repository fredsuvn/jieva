package xyz.srclab.common.collection

/**
 * Operations for [List]
 *
 * @author sunqian
 */
interface ListOps<E> : MutableList<E> {

    fun <R> map(transform: (element: E) -> R): ListOps<R>

    fun <R> mapIndexed(transform: (index: Int, element: E) -> R): ListOps<R>
}