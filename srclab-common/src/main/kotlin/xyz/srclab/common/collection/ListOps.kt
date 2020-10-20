package xyz.srclab.common.collection

import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.castSelfComparableComparator
import java.util.*
import kotlin.random.Random
import kotlin.collections.binarySearch as binarySearchKt
import kotlin.collections.dropLast as dropLastKt
import kotlin.collections.dropLastWhile as dropLastWhileKt
import kotlin.collections.elementAt as elementAtKt
import kotlin.collections.elementAtOrElse as elementAtOrElseKt
import kotlin.collections.elementAtOrNull as elementAtOrNullKt
import kotlin.collections.findLast as findLastKt
import kotlin.collections.first as firstKt
import kotlin.collections.firstOrNull as firstOrNullKt
import kotlin.collections.foldRight as foldRightKt
import kotlin.collections.foldRightIndexed as foldRightIndexedKt
import kotlin.collections.indexOf as indexOfKt
import kotlin.collections.indexOfFirst as indexOfFirstKt
import kotlin.collections.indexOfLast as indexOfLastKt
import kotlin.collections.last as lastKt
import kotlin.collections.lastIndexOf as lastIndexOfKt
import kotlin.collections.lastOrNull as lastOrNullKt
import kotlin.collections.reduceRight as reduceRightKt
import kotlin.collections.reduceRightIndexed as reduceRightIndexedKt
import kotlin.collections.reduceRightIndexedOrNull as reduceRightIndexedOrNullKt
import kotlin.collections.reduceRightOrNull as reduceRightOrNullKt
import kotlin.collections.removeAll as removeAllKt
import kotlin.collections.removeFirst as removeFirstKt
import kotlin.collections.removeFirstOrNull as removeFirstOrNullKt
import kotlin.collections.removeLast as removeLastKt
import kotlin.collections.removeLastOrNull as removeLastOrNullKt
import kotlin.collections.retainAll as retainAllKt
import kotlin.collections.reverse as reverseKt
import kotlin.collections.shuffle as shuffleKt
import kotlin.collections.slice as sliceKt
import kotlin.collections.sortWith as sortWithKt
import kotlin.collections.takeLast as takeLastKt
import kotlin.collections.takeLastWhile as takeLastWhileKt

class ListOps<T>(list: List<T>) : CollectionOps<T, List<T>, MutableList<T>, ListOps<T>>(list) {

    override fun first(): T {
        return finalList().first()
    }

    override fun firstOrNull(): T? {
        return finalList().firstOrNull()
    }

    override fun last(): T {
        return finalList().last()
    }

    override fun last(predicate: (T) -> Boolean): T {
        return finalList().last(predicate)
    }

    override fun lastOrNull(): T? {
        return finalList().lastOrNull()
    }

    override fun lastOrNull(predicate: (T) -> Boolean): T? {
        return finalList().lastOrNull(predicate)
    }

    override fun elementAt(index: Int): T {
        return finalList().elementAt(index)
    }

    override fun elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
        return finalList().elementAtOrElse(index, defaultValue)
    }

    override fun elementAtOrNull(index: Int): T? {
        return finalList().elementAtOrNull(index)
    }

    override fun findLast(predicate: (T) -> Boolean): T? {
        return finalList().findLast(predicate)
    }

    override fun indexOf(element: T): Int {
        return finalList().indexOf(element)
    }

    override fun indexOf(predicate: (T) -> Boolean): Int {
        return finalList().indexOf(predicate)
    }

    override fun lastIndexOf(element: T): Int {
        return finalList().lastIndexOf(element)
    }

    override fun lastIndexOf(predicate: (T) -> Boolean): Int {
        return finalList().lastIndexOf(predicate)
    }

    fun takeLast(n: Int): ListOps<T> {
        return finalList().takeLast(n).toListOps()
    }

    fun takeLastWhile(predicate: (T) -> Boolean): ListOps<T> {
        return finalList().takeLastWhile(predicate).toListOps()
    }

    fun dropLast(n: Int): ListOps<T> {
        return finalList().dropLast(n).toListOps()
    }

    fun dropLastWhile(predicate: (T) -> Boolean): ListOps<T> {
        return finalList().dropLastWhile(predicate).toListOps()
    }

    fun reduceRight(operation: (T, T) -> T): T {
        return finalList().reduceRight(operation)
    }

    fun reduceRightIndexed(operation: (Int, T, T) -> T): T {
        return finalList().reduceRightIndexed(operation)
    }

    fun reduceRightOrNull(operation: (T, T) -> T): T? {
        return finalList().reduceRightOrNull(operation)
    }

    fun reduceRightIndexedOrNull(operation: (Int, T, T) -> T): T? {
        return finalList().reduceRightIndexedOrNull(operation)
    }

    fun <R> reduceRight(initial: R, operation: (T, R) -> R): R {
        return finalList().reduceRight(initial, operation)
    }

    fun <R> reduceRightIndexed(initial: R, operation: (Int, T, R) -> R): R {
        return finalList().reduceRightIndexed(initial, operation)
    }

    fun slice(indices: IntArray): ListOps<T> {
        return finalList().slice(indices.asIterable()).toListOps()
    }

    fun slice(indices: Iterable<Int>): ListOps<T> {
        return finalList().slice(indices).toListOps()
    }

    fun slice(indices: IntRange): ListOps<T> {
        return finalList().slice(indices).toListOps()
    }

    fun binarySearch(element: T): Int {
        return finalList().binarySearch(element, castSelfComparableComparator())
    }

    fun binarySearch(element: T, comparator: Comparator<in T>): Int {
        return finalList().binarySearch(element, comparator)
    }

    fun reverse(): ListOps<T> {
        finalMutableList().reverse()
        return this.asAny()
    }

    fun sort(): ListOps<T> {
        finalMutableList().sort(castSelfComparableComparator())
        return this.asAny()
    }

    fun sort(comparator: Comparator<in T>): ListOps<T> {
        finalMutableList().sort(comparator)
        return this.asAny()
    }

    fun shuffle(): ListOps<T> {
        finalMutableList().shuffle()
        return this.asAny()
    }

    fun shuffle(random: Random): ListOps<T> {
        finalMutableList().shuffle(random)
        return this.asAny()
    }

    fun removeAll(predicate: (T) -> Boolean): ListOps<T> {
        finalMutableList().removeAll(predicate)
        return this.asAny()
    }

    fun removeFirst(): ListOps<T> {
        finalMutableList().removeFirst()
        return this.asAny()
    }

    fun removeFirstOrNull(): ListOps<T> {
        finalMutableList().removeFirstOrNull()
        return this.asAny()
    }

    fun removeLast(): ListOps<T> {
        finalMutableList().removeLast()
        return this.asAny()
    }

    fun removeLastOrNull(): ListOps<T> {
        finalMutableList().removeLastOrNull()
        return this.asAny()
    }

    override fun retainAll(predicate: (T) -> Boolean): ListOps<T> {
        finalMutableList().retainAll(predicate)
        return this.asAny()
    }

    fun plus(element: T): ListOps<T> {
        return finalList().plus(element).toListOps()
    }

    fun plus(elements: Array<out T>): ListOps<T> {
        return finalList().plus(elements).toListOps()
    }

    fun plus(elements: Iterable<T>): ListOps<T> {
        return finalList().plus(elements).toListOps()
    }

    fun plus(elements: Sequence<T>): ListOps<T> {
        return finalList().plus(elements).toListOps()
    }

    fun minus(element: T): ListOps<T> {
        return finalList().minus(element).toListOps()
    }

    fun minus(elements: Array<out T>): ListOps<T> {
        return finalList().minus(elements).toListOps()
    }

    fun minus(elements: Iterable<T>): ListOps<T> {
        return finalList().minus(elements).toListOps()
    }

    fun minus(elements: Sequence<T>): ListOps<T> {
        return finalList().minus(elements).toListOps()
    }

    fun finalList(): List<T> {
        return iterable
    }

    fun finalMutableList(): MutableList<T> {
        return iterable.asAny()
    }

    override fun List<T>.asThis(): ListOps<T> {
        iterable = this
        return this@ListOps
    }

    override fun <T> Iterable<T>.toIterableOps(): IterableOps<T> {
        return IterableOps.opsFor(this)
    }

    override fun <T> List<T>.toListOps(): ListOps<T> {
        iterable = this.asAny()
        return this@ListOps.asAny()
    }

    override fun <T> Set<T>.toSetOps(): SetOps<T> {
        return SetOps.opsFor(this)
    }

    override fun <K, V> Map<K, V>.toMapOps(): MapOps<K, V> {
        return MapOps.opsFor(this)
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(list: List<T>): ListOps<T> {
            return ListOps(list)
        }

        @JvmStatic
        fun <T> List<T>.first(): T {
            return this.firstKt()
        }

        @JvmStatic
        fun <T> List<T>.firstOrNull(): T? {
            return this.firstOrNullKt()
        }

        @JvmStatic
        fun <T> List<T>.last(): T {
            return this.lastKt()
        }

        @JvmStatic
        inline fun <T> List<T>.last(predicate: (T) -> Boolean): T {
            return this.lastKt(predicate)
        }

        @JvmStatic
        fun <T> List<T>.lastOrNull(): T? {
            return this.lastOrNullKt()
        }

        @JvmStatic
        inline fun <T> List<T>.lastOrNull(predicate: (T) -> Boolean): T? {
            return this.lastOrNullKt(predicate)
        }

        @JvmStatic
        fun <T> List<T>.elementAt(index: Int): T {
            return this.elementAtKt(index)
        }

        @JvmStatic
        inline fun <T> List<T>.elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
            return this.elementAtOrElseKt(index, defaultValue)
        }

        @JvmStatic
        fun <T> List<T>.elementAtOrNull(index: Int): T? {
            return this.elementAtOrNullKt(index)
        }

        @JvmStatic
        inline fun <T> List<T>.findLast(predicate: (T) -> Boolean): T? {
            return this.findLastKt(predicate)
        }

        @JvmStatic
        fun <T> List<T>.indexOf(element: T): Int {
            return this.indexOfKt(element)
        }

        @JvmStatic
        inline fun <T> List<T>.indexOf(predicate: (T) -> Boolean): Int {
            return this.indexOfFirstKt(predicate)
        }

        @JvmStatic
        fun <T> List<T>.lastIndexOf(element: T): Int {
            return this.lastIndexOfKt(element)
        }

        @JvmStatic
        inline fun <T> List<T>.lastIndexOf(predicate: (T) -> Boolean): Int {
            return this.indexOfLastKt(predicate)
        }

        @JvmStatic
        fun <T> List<T>.takeLast(n: Int): List<T> {
            return this.takeLastKt(n)
        }

        @JvmStatic
        inline fun <T> List<T>.takeLastWhile(predicate: (T) -> Boolean): List<T> {
            return this.takeLastWhileKt(predicate)
        }

        @JvmStatic
        fun <T> List<T>.dropLast(n: Int): List<T> {
            return this.dropLastKt(n)
        }

        @JvmStatic
        inline fun <T> List<T>.dropLastWhile(predicate: (T) -> Boolean): List<T> {
            return this.dropLastWhileKt(predicate)
        }

        @JvmStatic
        inline fun <S, T : S> List<T>.reduceRight(operation: (T, S) -> S): S {
            return this.reduceRightKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> List<T>.reduceRightIndexed(operation: (Int, T, S) -> S): S {
            return this.reduceRightIndexedKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> List<T>.reduceRightOrNull(operation: (T, S) -> S): S? {
            return this.reduceRightOrNullKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> List<T>.reduceRightIndexedOrNull(operation: (Int, T, S) -> S): S? {
            return this.reduceRightIndexedOrNullKt(operation)
        }

        @JvmStatic
        inline fun <T, R> List<T>.reduceRight(initial: R, operation: (T, R) -> R): R {
            return this.foldRightKt(initial, operation)
        }

        @JvmStatic
        inline fun <T, R> List<T>.reduceRightIndexed(initial: R, operation: (Int, T, R) -> R): R {
            return this.foldRightIndexedKt(initial, operation)
        }

        @JvmStatic
        fun <T> List<T>.slice(indices: IntArray): List<T> {
            return this.sliceKt(indices.asIterable())
        }

        @JvmStatic
        fun <T> List<T>.slice(indices: Iterable<Int>): List<T> {
            return this.sliceKt(indices)
        }

        @JvmStatic
        fun <T> List<T>.slice(indices: IntRange): List<T> {
            return this.sliceKt(indices)
        }

        @JvmStatic
        fun <T> List<T>.binarySearch(element: T): Int {
            return this.binarySearchKt(element, castSelfComparableComparator())
        }

        @JvmStatic
        fun <T> List<T>.binarySearch(element: T, comparator: Comparator<in T>): Int {
            return this.binarySearchKt(element, comparator)
        }

        @JvmStatic
        fun <T> MutableList<T>.reverse() {
            this.reverseKt()
        }

        @JvmStatic
        fun <T> MutableList<T>.sort() {
            this.sort(castSelfComparableComparator())
        }

        @JvmStatic
        fun <T> MutableList<T>.sort(comparator: Comparator<in T>) {
            this.sortWithKt(comparator)
        }

        @JvmStatic
        fun <T> MutableList<T>.shuffle() {
            this.shuffleKt()
        }

        @JvmStatic
        fun <T> MutableList<T>.shuffle(random: Random) {
            this.shuffleKt(random)
        }

        @JvmStatic
        fun <T> MutableList<T>.removeAll(predicate: (T) -> Boolean): Boolean {
            return this.removeAllKt(predicate)
        }

        @JvmStatic
        fun <T> MutableList<T>.removeFirst(): T {
            return this.removeFirstKt()
        }

        @JvmStatic
        fun <T> MutableList<T>.removeFirstOrNull(): T? {
            return this.removeFirstOrNullKt()
        }

        @JvmStatic
        fun <T> MutableList<T>.removeLast(): T {
            return this.removeLastKt()
        }

        @JvmStatic
        fun <T> MutableList<T>.removeLastOrNull(): T? {
            return this.removeLastOrNullKt()
        }

        @JvmStatic
        fun <T> MutableList<T>.retainAll(predicate: (T) -> Boolean): Boolean {
            return this.retainAllKt(predicate)
        }
    }
}