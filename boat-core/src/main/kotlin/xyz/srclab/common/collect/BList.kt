@file:JvmName("BList")

package xyz.srclab.common.collect

import xyz.srclab.common.base.castComparableComparator
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

fun <T> newList(vararg elements: T): ArrayList<T> {
    val list = ArrayList<T>(elements.size)
    list.addAll(elements)
    return list
}

fun <T> List<T>.first(): T {
    return this.firstKt()
}

fun <T> List<T>.firstOrNull(): T? {
    return this.firstOrNullKt()
}

fun <T> List<T>.last(): T {
    return this.lastKt()
}

inline fun <T> List<T>.last(predicate: (T) -> Boolean): T {
    return this.lastKt(predicate)
}

fun <T> List<T>.lastOrNull(): T? {
    return this.lastOrNullKt()
}

inline fun <T> List<T>.lastOrNull(predicate: (T) -> Boolean): T? {
    return this.lastOrNullKt(predicate)
}

fun <T> List<T>.elementAt(index: Int): T {
    return this.elementAtKt(index)
}

inline fun <T> List<T>.elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
    return this.elementAtOrElseKt(index, defaultValue)
}

fun <T> List<T>.elementAtOrNull(index: Int): T? {
    return this.elementAtOrNullKt(index)
}

inline fun <T> List<T>.findLast(predicate: (T) -> Boolean): T? {
    return this.findLastKt(predicate)
}

fun <T> List<T>.indexOf(element: T): Int {
    return this.indexOfKt(element)
}

inline fun <T> List<T>.indexOf(predicate: (T) -> Boolean): Int {
    return this.indexOfFirstKt(predicate)
}

fun <T> List<T>.lastIndexOf(element: T): Int {
    return this.lastIndexOfKt(element)
}

inline fun <T> List<T>.lastIndexOf(predicate: (T) -> Boolean): Int {
    return this.indexOfLastKt(predicate)
}

fun <T> List<T>.takeLast(n: Int): List<T> {
    return this.takeLastKt(n)
}

inline fun <T> List<T>.takeLast(predicate: (T) -> Boolean): List<T> {
    return this.takeLastWhileKt(predicate)
}

fun <T> List<T>.dropLast(n: Int): List<T> {
    return this.dropLastKt(n)
}

inline fun <T> List<T>.dropLast(predicate: (T) -> Boolean): List<T> {
    return this.dropLastWhileKt(predicate)
}

inline fun <S, T : S> List<T>.reduceRight(operation: (T, S) -> S): S {
    return this.reduceRightKt(operation)
}

inline fun <S, T : S> List<T>.reduceRightIndexed(operation: (Int, T, S) -> S): S {
    return this.reduceRightIndexedKt(operation)
}

inline fun <S, T : S> List<T>.reduceRightOrNull(operation: (T, S) -> S): S? {
    return this.reduceRightOrNullKt(operation)
}

inline fun <S, T : S> List<T>.reduceRightIndexedOrNull(operation: (Int, T, S) -> S): S? {
    return this.reduceRightIndexedOrNullKt(operation)
}

inline fun <T, R> List<T>.reduceRight(initial: R, operation: (T, R) -> R): R {
    return this.foldRightKt(initial, operation)
}

inline fun <T, R> List<T>.reduceRightIndexed(initial: R, operation: (Int, T, R) -> R): R {
    return this.foldRightIndexedKt(initial, operation)
}

fun <T> List<T>.slice(indices: IntArray): List<T> {
    return this.sliceKt(indices.asIterable())
}

fun <T> List<T>.slice(indices: Iterable<Int>): List<T> {
    return this.sliceKt(indices)
}

fun <T> List<T>.slice(indices: IntRange): List<T> {
    return this.sliceKt(indices)
}

@JvmOverloads
fun <T> List<T>.binarySearch(element: T, comparator: Comparator<in T> = castComparableComparator()): Int {
    return this.binarySearchKt(element, comparator)
}

fun <T> MutableList<T>.reverse() {
    this.reverseKt()
}

@JvmOverloads
fun <T> MutableList<T>.sort(comparator: Comparator<in T> = castComparableComparator()) {
    this.sortWithKt(comparator)
}

fun <T> MutableList<T>.shuffle() {
    this.shuffleKt()
}

fun <T> MutableList<T>.shuffle(random: Random) {
    this.shuffleKt(random)
}

fun <T> MutableList<T>.removeAll(predicate: (T) -> Boolean): Boolean {
    return this.removeAllKt(predicate)
}

fun <T> MutableList<T>.removeFirst(): T {
    return this.removeFirstKt()
}

fun <T> MutableList<T>.removeFirstOrNull(): T? {
    return this.removeFirstOrNullKt()
}

fun <T> MutableList<T>.removeLast(): T {
    return this.removeLastKt()
}

fun <T> MutableList<T>.removeLastOrNull(): T? {
    return this.removeLastOrNullKt()
}

fun <T> MutableList<T>.retainAll(predicate: (T) -> Boolean): Boolean {
    return this.retainAllKt(predicate)
}