package xyz.srclab.common.collect

import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.castSelfComparableComparator
import java.util.*
import kotlin.random.Random

class ListOps<T>(list: List<T>) : CollectionOps<T, List<T>, MutableList<T>, ListOps<T>>(list) {

    fun finalList(): List<T> {
        return operated
    }

    fun finalMutableList(): MutableList<T> {
        return operated.asAny()
    }

    override fun <T> List<T>.toListOps(): ListOps<T> {
        operated = this.asAny()
        return this@ListOps.asAny()
    }

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

    override fun plus(element: T): ListOps<T> {
        return finalList().plus(element).toListOps()
    }

    override fun plus(elements: Array<out T>): ListOps<T> {
        return finalList().plus(elements).toListOps()
    }

    override fun plus(elements: Iterable<T>): ListOps<T> {
        return finalList().plus(elements).toListOps()
    }

    override fun plus(elements: Sequence<T>): ListOps<T> {
        return finalList().plus(elements).toListOps()
    }

    override fun minus(element: T): ListOps<T> {
        return finalList().minus(element).toListOps()
    }

    override fun minus(elements: Array<out T>): ListOps<T> {
        return finalList().minus(elements).toListOps()
    }

    override fun minus(elements: Iterable<T>): ListOps<T> {
        return finalList().minus(elements).toListOps()
    }

    override fun minus(elements: Sequence<T>): ListOps<T> {
        return finalList().minus(elements).toListOps()
    }

    @JvmOverloads
    fun subList(fromIndex: Int, toIndex: Int = count()): ListOps<T> {
        return finalList().subList(fromIndex, toIndex).toListOps()
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

    @JvmOverloads
    fun binarySearch(element: T, comparator: Comparator<in T> = castSelfComparableComparator()): Int {
        return finalList().binarySearch(element, comparator)
    }

    fun reverse(): ListOps<T> {
        finalMutableList().reverse()
        return this.asAny()
    }

    @JvmOverloads
    fun sort(comparator: Comparator<in T> = castSelfComparableComparator()): ListOps<T> {
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

    companion object {

        @JvmStatic
        fun <T> opsFor(iterable: Iterable<T>): ListOps<T> {
            return ListOps(iterable.asToList())
        }

        @JvmStatic
        fun <T> opsFor(array: Array<T>): ListOps<T> {
            return opsFor(array.asList())
        }
    }
}