package xyz.srclab.common.collection

import xyz.srclab.common.base.As
import xyz.srclab.common.base.Sort
import java.util.*
import kotlin.random.Random

class ListOps<T> private constructor(list: List<T>) :
    CollectionOps<T, List<T>, MutableList<T>, ListOps<T>>(list) {

    override fun findLast(predicate: (T) -> Boolean): T? {
        return findLast(operated(), predicate)
    }

    override fun first(): T {
        return first(operated())
    }

    override fun firstOrNull(): T? {
        return firstOrNull(operated())
    }

    override fun last(): T {
        return last(operated())
    }

    override fun last(predicate: (T) -> Boolean): T {
        return last(operated(), predicate)
    }

    override fun lastOrNull(): T? {
        return lastOrNull(operated())
    }

    override fun lastOrNull(predicate: (T) -> Boolean): T? {
        return lastOrNull(operated(), predicate)
    }

    override fun single(): T {
        return single(operated())
    }

    override fun singleOrNull(): T? {
        return singleOrNull(operated())
    }

    override fun elementAt(index: Int): T {
        return elementAt(operated(), index)
    }

    override fun elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
        return elementAtOrElse(operated(), index, defaultValue)
    }

    override fun elementAtOrNull(index: Int): T? {
        return elementAtOrNull(operated(), index)
    }

    override fun indexOf(element: T): Int {
        return indexOf(operated(), element)
    }

    override fun indexOf(predicate: (T) -> Boolean): Int {
        return indexOf(operated(), predicate)
    }

    override fun lastIndexOf(element: T): Int {
        return lastIndexOf(operated(), element)
    }

    override fun lastIndexOf(predicate: (T) -> Boolean): Int {
        return lastIndexOf(operated(), predicate)
    }

    fun dropLast(n: Int): ListOps<T> {
        return toListOps(dropLast(operated(), n))
    }

    fun dropLastWhile(predicate: (T) -> Boolean): ListOps<T> {
        return toListOps(dropLastWhile(operated(), predicate))
    }

    fun takeLast(n: Int): ListOps<T> {
        return toListOps(takeLast(operated(), n))
    }

    fun takeLastWhile(predicate: (T) -> Boolean): ListOps<T> {
        return toListOps(takeLastWhile(operated(), predicate))
    }

    fun slice(indices: IntArray): ListOps<T> {
        return toListOps(slice(operated(), indices))
    }

    fun slice(indices: Iterable<Int>): ListOps<T> {
        return toListOps(slice(operated(), indices))
    }

    fun slice(indices: IntRange): ListOps<T> {
        return toListOps(slice(operated(), indices))
    }

    fun binarySearch(element: T): Int {
        return binarySearch(operated(), element)
    }

    fun binarySearch(element: T, comparator: Comparator<in T>): Int {
        return binarySearch(operated(), element, comparator)
    }

    fun reduceRight(operation: (T, T) -> T): T {
        return reduceRight(operated(), operation)
    }

    fun reduceRightIndexed(operation: (Int, T, T) -> T): T {
        return reduceRightIndexed(operated(), operation)
    }

    fun reduceRightOrNull(operation: (T, T) -> T): T? {
        return reduceRightOrNull(operated(), operation)
    }

    fun reduceRightIndexedOrNull(operation: (Int, T, T) -> T): T? {
        return reduceRightIndexedOrNull(operated(), operation)
    }

    fun <R> reduceRight(initial: R, operation: (T, R) -> R): R {
        return reduceRight(operated(), initial, operation)
    }

    fun <R> reduceRightIndexed(initial: R, operation: (Int, T, R) -> R): R {
        return reduceRightIndexed(operated(), initial, operation)
    }

    fun reverse(): ListOps<T> {
        reverse(mutableOperated())
        return this
    }

    fun sort(): ListOps<T> {
        sort(mutableOperated())
        return this
    }

    fun sort(comparator: Comparator<in T>): ListOps<T> {
        sort(mutableOperated(), comparator)
        return this
    }

    fun shuffle(): ListOps<T> {
        shuffle(mutableOperated())
        return this
    }

    fun shuffle(random: Random): ListOps<T> {
        shuffle(mutableOperated(), random)
        return this
    }

    override fun removeAll(predicate: (T) -> Boolean): Boolean {
        return removeAll(mutableOperated(), predicate)
    }

    override fun removeFirst(): T {
        return removeFirst(mutableOperated())
    }

    override fun removeFirstOrNull(): T? {
        return removeFirstOrNull(mutableOperated())
    }

    override fun removeLast(): T {
        return removeLast(mutableOperated())
    }

    override fun removeLastOrNull(): T? {
        return removeLastOrNull(mutableOperated())
    }

    override fun retainAll(predicate: (T) -> Boolean): Boolean {
        return retainAll(mutableOperated(), predicate)
    }

    fun plus(element: T): ListOps<T> {
        return toListOps(plus(operated(), element))
    }

    fun plus(elements: Array<out T>): ListOps<T> {
        return toListOps(plus(operated(), elements))
    }

    fun plus(elements: Iterable<T>): ListOps<T> {
        return toListOps(plus(operated(), elements))
    }

    fun plus(elements: Sequence<T>): ListOps<T> {
        return toListOps(plus(operated(), elements))
    }

    fun minus(element: T): ListOps<T> {
        return toListOps(minus(operated(), element))
    }

    fun minus(elements: Array<out T>): ListOps<T> {
        return toListOps(minus(operated(), elements))
    }

    fun minus(elements: Iterable<T>): ListOps<T> {
        return toListOps(minus(operated(), elements))
    }

    fun minus(elements: Sequence<T>): ListOps<T> {
        return toListOps(minus(operated(), elements))
    }

    fun finalList(): List<T> {
        return operated()
    }

    fun finalMutableList(): MutableList<T> {
        return mutableOperated()
    }

    override fun <T> toIterableOps(iterable: Iterable<T>): IterableOps<T> {
        return IterableOps.opsFor(iterable)
    }

    override fun <T> toListOps(list: List<T>): ListOps<T> {
        this.operated = As.any(list)
        return As.any(this)
    }

    override fun <T> toSetOps(set: Set<T>): SetOps<T> {
        return SetOps.opsFor(set)
    }

    override fun <K, V> toMapOps(map: Map<K, V>): MapOps<K, V> {
        return MapOps.opsFor(map)
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(list: List<T>): ListOps<T> {
            return ListOps(list)
        }

        @JvmStatic
        inline fun <T> findLast(list: List<T>, predicate: (T) -> Boolean): T? {
            return list.findLast(predicate)
        }

        @JvmStatic
        fun <T> first(list: List<T>): T {
            return list.first()
        }

        @JvmStatic
        fun <T> firstOrNull(list: List<T>): T? {
            return list.firstOrNull()
        }

        @JvmStatic
        fun <T> last(list: List<T>): T {
            return list.last()
        }

        @JvmStatic
        inline fun <T> last(list: List<T>, predicate: (T) -> Boolean): T {
            return list.last(predicate)
        }

        @JvmStatic
        fun <T> lastOrNull(list: List<T>): T? {
            return list.lastOrNull()
        }

        @JvmStatic
        inline fun <T> lastOrNull(list: List<T>, predicate: (T) -> Boolean): T? {
            return list.lastOrNull(predicate)
        }

        @JvmStatic
        fun <T> single(list: List<T>): T {
            return list.single()
        }

        @JvmStatic
        fun <T> singleOrNull(list: List<T>): T? {
            return list.singleOrNull()
        }

        @JvmStatic
        fun <T> elementAt(list: List<T>, index: Int): T {
            return list.elementAt(index)
        }

        @JvmStatic
        inline fun <T> elementAtOrElse(list: List<T>, index: Int, defaultValue: (Int) -> T): T {
            return list.elementAtOrElse(index, defaultValue)
        }

        @JvmStatic
        fun <T> elementAtOrNull(list: List<T>, index: Int): T? {
            return list.elementAtOrNull(index)
        }

        @JvmStatic
        fun <T> indexOf(list: List<T>, element: T): Int {
            return list.indexOf(element)
        }

        @JvmStatic
        inline fun <T> indexOf(list: List<T>, predicate: (T) -> Boolean): Int {
            return list.indexOfFirst(predicate)
        }

        @JvmStatic
        fun <T> lastIndexOf(list: List<T>, element: T): Int {
            return list.lastIndexOf(element)
        }

        @JvmStatic
        inline fun <T> lastIndexOf(list: List<T>, predicate: (T) -> Boolean): Int {
            return list.indexOfLast(predicate)
        }

        @JvmStatic
        fun <T> dropLast(list: List<T>, n: Int): List<T> {
            return list.dropLast(n)
        }

        @JvmStatic
        inline fun <T> dropLastWhile(list: List<T>, predicate: (T) -> Boolean): List<T> {
            return list.dropLastWhile(predicate)
        }

        @JvmStatic
        fun <T> takeLast(list: List<T>, n: Int): List<T> {
            return list.takeLast(n)
        }

        @JvmStatic
        inline fun <T> takeLastWhile(list: List<T>, predicate: (T) -> Boolean): List<T> {
            return list.takeLastWhile(predicate)
        }

        @JvmStatic
        fun <T> slice(list: List<T>, indices: IntArray): List<T> {
            return slice(list, indices.asIterable())
        }

        @JvmStatic
        fun <T> slice(list: List<T>, indices: Iterable<Int>): List<T> {
            return list.slice(indices)
        }

        @JvmStatic
        fun <T> slice(list: List<T>, indices: IntRange): List<T> {
            return list.slice(indices)
        }

        fun <T> binarySearch(list: List<T>, element: T): Int {
            return list.binarySearch(element, Sort.selfComparableComparator())
        }

        fun <T> binarySearch(list: List<T>, element: T, comparator: Comparator<in T>): Int {
            return list.binarySearch(element, comparator)
        }

        @JvmStatic
        inline fun <S, T : S> reduceRight(list: List<T>, operation: (T, S) -> S): S {
            return list.reduceRight(operation)
        }

        @JvmStatic
        inline fun <S, T : S> reduceRightIndexed(list: List<T>, operation: (Int, T, S) -> S): S {
            return list.reduceRightIndexed(operation)
        }

        @JvmStatic
        inline fun <S, T : S> reduceRightOrNull(list: List<T>, operation: (T, S) -> S): S? {
            return list.reduceRightOrNull(operation)
        }

        @JvmStatic
        inline fun <S, T : S> reduceRightIndexedOrNull(
            list: List<T>,
            operation: (Int, T, S) -> S
        ): S? {
            return list.reduceRightIndexedOrNull(operation)
        }

        @JvmStatic
        inline fun <T, R> reduceRight(list: List<T>, initial: R, operation: (T, R) -> R): R {
            return list.foldRight(initial, operation)
        }

        @JvmStatic
        inline fun <T, R> reduceRightIndexed(
            list: List<T>,
            initial: R,
            operation: (Int, T, R) -> R
        ): R {
            return list.foldRightIndexed(initial, operation)
        }

        @JvmStatic
        fun <T> reverse(list: MutableList<T>) {
            list.reverse()
        }

        @JvmStatic
        fun <T> sort(list: MutableList<T>) {
            sort(list, Sort.selfComparableComparator())
        }

        @JvmStatic
        fun <T> sort(list: MutableList<T>, comparator: Comparator<in T>) {
            list.sortWith(comparator)
        }

        @JvmStatic
        fun <T> shuffle(list: MutableList<T>) {
            list.shuffle()
        }

        @JvmStatic
        fun <T> shuffle(list: MutableList<T>, random: Random) {
            list.shuffle(random)
        }

        @JvmStatic
        fun <T> removeAll(list: MutableList<T>, predicate: (T) -> Boolean): Boolean {
            return list.removeAll(predicate)
        }

        @JvmStatic
        fun <T> removeFirst(list: MutableList<T>): T {
            return list.removeFirst()
        }

        @JvmStatic
        fun <T> removeFirstOrNull(list: MutableList<T>): T? {
            return list.removeFirstOrNull()
        }

        @JvmStatic
        fun <T> removeLast(list: MutableList<T>): T {
            return list.removeLast()
        }

        @JvmStatic
        fun <T> removeLastOrNull(list: MutableList<T>): T? {
            return list.removeLastOrNull()
        }

        @JvmStatic
        fun <T> retainAll(list: MutableList<T>, predicate: (T) -> Boolean): Boolean {
            return list.retainAll(predicate)
        }
    }
}