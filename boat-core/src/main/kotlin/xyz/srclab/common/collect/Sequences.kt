@file:JvmName("Sequences")

package xyz.srclab.common.collect

import xyz.srclab.common.lang.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.streams.asStream
import kotlin.toBigDecimal
import kotlin.collections.toList as toListKt
import kotlin.collections.toTypedArray as toTypedArrayKt
import kotlin.sequences.all as allKt
import kotlin.sequences.any as anyKt
import kotlin.sequences.associate as associateKt
import kotlin.sequences.associateBy as associateByKt
import kotlin.sequences.associateByTo as associateByToKt
import kotlin.sequences.associateTo as associateToKt
import kotlin.sequences.associateWith as associateWithKt
import kotlin.sequences.associateWithTo as associateWithToKt
import kotlin.sequences.chunked as chunkedKt
import kotlin.sequences.contains as containsKt
import kotlin.sequences.count as countKt
import kotlin.sequences.distinct as distinctKt
import kotlin.sequences.distinctBy as distinctByKt
import kotlin.sequences.drop as dropKt
import kotlin.sequences.dropWhile as dropWhileKt
import kotlin.sequences.elementAt as elementAtKt
import kotlin.sequences.elementAtOrElse as elementAtOrElseKt
import kotlin.sequences.elementAtOrNull as elementAtOrNullKt
import kotlin.sequences.filter as filterKt
import kotlin.sequences.filterIndexed as filterIndexedKt
import kotlin.sequences.filterIndexedTo as filterIndexedToKt
import kotlin.sequences.filterNotNull as filterNotNullKt
import kotlin.sequences.filterTo as filterToKt
import kotlin.sequences.find as findKt
import kotlin.sequences.first as firstKt
import kotlin.sequences.firstOrNull as firstOrNullKt
import kotlin.sequences.flatMap as flatMapKt
import kotlin.sequences.flatMapIndexed as flatMapIndexedKt
import kotlin.sequences.flatMapIndexedTo as flatMapIndexedToKt
import kotlin.sequences.flatMapTo as flatMapToKt
import kotlin.sequences.fold as foldKt
import kotlin.sequences.foldIndexed as foldIndexedKt
import kotlin.sequences.forEachIndexed as forEachIndexedKt
import kotlin.sequences.groupBy as groupByKt
import kotlin.sequences.groupByTo as groupByToKt
import kotlin.sequences.indexOf as indexOfKt
import kotlin.sequences.indexOfFirst as indexOfFirstKt
import kotlin.sequences.indexOfLast as indexOfLastKt
import kotlin.sequences.last as lastKt
import kotlin.sequences.lastIndexOf as lastIndexOfKt
import kotlin.sequences.lastOrNull as lastOrNullKt
import kotlin.sequences.map as mapKt
import kotlin.sequences.mapIndexed as mapIndexedKt
import kotlin.sequences.mapIndexedNotNull as mapIndexedNotNullKt
import kotlin.sequences.mapIndexedNotNullTo as mapIndexedNotNullToKt
import kotlin.sequences.mapIndexedTo as mapIndexedToKt
import kotlin.sequences.mapNotNull as mapNotNullKt
import kotlin.sequences.mapNotNullTo as mapNotNullToKt
import kotlin.sequences.mapTo as mapToKt
import kotlin.sequences.maxWithOrNull as maxWithOrNullKt
import kotlin.sequences.minWithOrNull as minWithOrNullKt
import kotlin.sequences.minus as minusKt
import kotlin.sequences.none as noneKt
import kotlin.sequences.plus as plusKt
import kotlin.sequences.reduce as reduceKt
import kotlin.sequences.reduceIndexed as reduceIndexedKt
import kotlin.sequences.reduceIndexedOrNull as reduceIndexedOrNullKt
import kotlin.sequences.reduceOrNull as reduceOrNullKt
import kotlin.sequences.shuffled as shuffledKt
import kotlin.sequences.sortedWith as sortedWithKt
import kotlin.sequences.sumOf as sumOfKt
import kotlin.sequences.take as takeKt
import kotlin.sequences.takeWhile as takeWhileKt
import kotlin.sequences.toCollection as toCollectionKt
import kotlin.sequences.toHashSet as toHashSetKt
import kotlin.sequences.toList as toListKt
import kotlin.sequences.toMutableList as toMutableListKt
import kotlin.sequences.toMutableSet as toMutableSetKt
import kotlin.sequences.toSet as toSetKt
import kotlin.sequences.toSortedSet as toSortedSetKt
import kotlin.sequences.windowed as windowedKt
import kotlin.sequences.zip as zipKt
import kotlin.sequences.zipWithNext as zipWithNextKt

fun <T> Sequence<T>.contains(element: T): Boolean {
    return this.containsKt(element)
}

fun <T> Sequence<T>.containsAll(elements: Array<out T>): Boolean {
    return containsAll(elements.toListKt())
}

fun <T> Sequence<T>.containsAll(elements: Iterable<T>): Boolean {
    return containsAll(elements.asToList())
}

fun <T> Sequence<T>.containsAll(elements: Collection<T>): Boolean {
    return this.toHashSetKt().containsAll(elements)
}

fun <T> Sequence<T>.count(): Int {
    return this.countKt()
}

inline fun <T> Sequence<T>.count(predicate: (T) -> Boolean): Int {
    return this.countKt(predicate)
}

fun <T> Sequence<T>.isEmpty(): Boolean {
    for (it in this) {
        return false
    }
    return true
}

fun <T> Sequence<T>.isNotEmpty(): Boolean {
    return !isEmpty()
}

fun <T> Sequence<T>.any(): Boolean {
    return this.anyKt()
}

inline fun <T> Sequence<T>.any(predicate: (T) -> Boolean): Boolean {
    return this.anyKt(predicate)
}

fun <T> Sequence<T>.none(): Boolean {
    return this.noneKt()
}

inline fun <T> Sequence<T>.none(predicate: (T) -> Boolean): Boolean {
    return this.noneKt(predicate)
}

inline fun <T> Sequence<T>.all(predicate: (T) -> Boolean): Boolean {
    return this.allKt(predicate)
}

fun <T> Sequence<T>.first(): T {
    return this.firstKt()
}

inline fun <T> Sequence<T>.first(predicate: (T) -> Boolean): T {
    return this.firstKt(predicate)
}

fun <T> Sequence<T>.firstOrNull(): T? {
    return this.firstOrNullKt()
}

inline fun <T> Sequence<T>.firstOrNull(predicate: (T) -> Boolean): T? {
    return this.firstOrNullKt(predicate)
}

fun <T> Sequence<T>.last(): T {
    return this.lastKt()
}

inline fun <T> Sequence<T>.last(predicate: (T) -> Boolean): T {
    return this.lastKt(predicate)
}

fun <T> Sequence<T>.lastOrNull(): T? {
    return this.lastOrNullKt()
}

inline fun <T> Sequence<T>.lastOrNull(predicate: (T) -> Boolean): T? {
    return this.lastOrNullKt(predicate)
}

fun <T> Sequence<T>.elementAt(index: Int): T {
    return this.elementAtKt(index)
}

fun <T> Sequence<T>.elementAtOrElse(index: Int, defaultValue: (index: Int) -> T): T {
    return this.elementAtOrElseKt(index, defaultValue)
}

fun <T> Sequence<T>.elementAtOrNull(index: Int): T? {
    return this.elementAtOrNullKt(index)
}

inline fun <T> Sequence<T>.find(predicate: (T) -> Boolean): T? {
    return this.findKt(predicate)
}

inline fun <T> Sequence<T>.findLast(predicate: (T) -> Boolean): T? {
    return this.firstKt(predicate)
}

fun <T> Sequence<T>.indexOf(element: T): Int {
    return this.indexOfKt(element)
}

inline fun <T> Sequence<T>.indexOf(predicate: (T) -> Boolean): Int {
    return this.indexOfFirstKt(predicate)
}

fun <T> Sequence<T>.lastIndexOf(element: T): Int {
    return this.lastIndexOfKt(element)
}

inline fun <T> Sequence<T>.lastIndexOf(predicate: (T) -> Boolean): Int {
    return this.indexOfLastKt(predicate)
}

fun <T> Sequence<T>.take(n: Int): Sequence<T> {
    return this.takeKt(n)
}

fun <T> Sequence<T>.takeWhile(predicate: (T) -> Boolean): Sequence<T> {
    return this.takeWhileKt(predicate)
}

fun <T, C : MutableCollection<in T>> Sequence<T>.takeTo(n: Int, destination: C): C {
    destination.addAll(this.take(n))
    return destination
}

fun <T, C : MutableCollection<in T>> Sequence<T>.takeWhileTo(
    destination: C,
    predicate: (T) -> Boolean
): C {
    destination.addAll(this.takeWhile(predicate))
    return destination
}

fun <T, C : MutableCollection<in T>> Sequence<T>.takeAllTo(destination: C): C {
    destination.addAll(this)
    return destination
}

fun <T> Sequence<T>.drop(n: Int): Sequence<T> {
    return this.dropKt(n)
}

fun <T> Sequence<T>.dropWhile(predicate: (T) -> Boolean): Sequence<T> {
    return this.dropWhileKt(predicate)
}

fun <T, C : MutableCollection<in T>> Sequence<T>.dropTo(n: Int, destination: C): C {
    destination.addAll(this.drop(n))
    return destination
}

fun <T, C : MutableCollection<in T>> Sequence<T>.dropWhileTo(
    destination: C,
    predicate: (T) -> Boolean
): C {
    destination.addAll(this.dropWhile(predicate))
    return destination
}

inline fun <T> Sequence<T>.forEachIndexed(action: (index: Int, T) -> Unit) {
    return this.forEachIndexedKt(action)
}

fun <T> Sequence<T>.filter(predicate: (T) -> Boolean): Sequence<T> {
    return this.filterKt(predicate)
}

fun <T> Sequence<T>.filterIndexed(predicate: (index: Int, T) -> Boolean): Sequence<T> {
    return this.filterIndexedKt(predicate)
}

fun <T> Sequence<T>.filterNotNull(): Sequence<T> {
    return this.filterNotNullKt()
}

inline fun <T, C : MutableCollection<in T>> Sequence<T>.filterTo(destination: C, predicate: (T) -> Boolean): C {
    return this.filterToKt(destination, predicate)
}

inline fun <T, C : MutableCollection<in T>> Sequence<T>.filterIndexedTo(
    destination: C,
    predicate: (index: Int, T) -> Boolean
): C {
    return this.filterIndexedToKt(destination, predicate)
}

fun <C : MutableCollection<in T>, T> Sequence<T>.filterNotNullTo(destination: C): C {
    return this.filterTo(destination, { it !== null })
}

fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
    return this.mapKt(transform)
}

fun <T, R> Sequence<T>.mapIndexed(transform: (index: Int, T) -> R): Sequence<R> {
    return this.mapIndexedKt(transform)
}

fun <T, R : Any> Sequence<T>.mapNotNull(transform: (T) -> R?): Sequence<R> {
    return this.mapNotNullKt(transform)
}

fun <T, R : Any> Sequence<T>.mapIndexedNotNull(transform: (index: Int, T) -> R?): Sequence<R> {
    return this.mapIndexedNotNullKt(transform)
}

inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.mapTo(destination: C, transform: (T) -> R): C {
    return this.mapToKt(destination, transform)
}

inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.mapIndexedTo(
    destination: C,
    transform: (index: Int, T) -> R
): C {
    return this.mapIndexedToKt(destination, transform)
}

inline fun <T, R : Any, C : MutableCollection<in R>> Sequence<T>.mapNotNullTo(
    destination: C,
    transform: (T) -> R?
): C {
    return this.mapNotNullToKt(destination, transform)
}

inline fun <T, R : Any, C : MutableCollection<in R>> Sequence<T>.mapIndexedNotNullTo(
    destination: C,
    transform: (index: Int, T) -> R?
): C {
    return this.mapIndexedNotNullToKt(destination, transform)
}

fun <T, R> Sequence<T>.flatMap(transform: (T) -> Iterable<R>): Sequence<R> {
    return this.flatMapKt(transform)
}

fun <T, R> Sequence<T>.flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): Sequence<R> {
    return this.flatMapIndexedKt(transform)
}

inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.flatMapTo(
    destination: C,
    transform: (T) -> Iterable<R>
): C {
    return this.flatMapToKt(destination, transform)
}

inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.flatMapIndexedTo(
    destination: C,
    transform: (index: Int, T) -> Iterable<R>
): C {
    return this.flatMapIndexedToKt(destination, transform)
}

inline fun <T, K, V> Sequence<T>.associate(
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return this.associateByKt(keySelector, valueTransform)
}

inline fun <T, K, V> Sequence<T>.associate(transform: (T) -> Pair<K, V>): Map<K, V> {
    return this.associateKt(transform)
}

inline fun <T, K> Sequence<T>.associateKeys(keySelector: (T) -> K): Map<K, T> {
    return this.associateByKt(keySelector)
}

fun <T, K> Sequence<T>.associateKeys(keys: Iterable<K>): Map<K, T> {
    return associateKeysTo(LinkedHashMap(), keys)
}

inline fun <T, V> Sequence<T>.associateValues(valueSelector: (T) -> V): Map<T, V> {
    return this.associateWithKt(valueSelector)
}

fun <T, V> Sequence<T>.associateValues(values: Iterable<V>): Map<T, V> {
    return associateValuesTo(LinkedHashMap(), values)
}

inline fun <T, K, V> Sequence<T>.associateWithNext(
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return associateWithNextTo(LinkedHashMap(), keySelector, valueTransform)
}

inline fun <T, K, V> Sequence<T>.associateWithNext(transform: (T, T) -> Pair<K, V>): Map<K, V> {
    return associateWithNextTo(LinkedHashMap(), transform)
}

inline fun <T, K, V> Sequence<T>.associatePairs(
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return associatePairsTo(LinkedHashMap(), keySelector, valueTransform)
}

inline fun <T, K, V> Sequence<T>.associatePairs(transform: (T, T) -> Pair<K, V>): Map<K, V> {
    return associatePairsTo(LinkedHashMap(), transform)
}

inline fun <T, K, V> Sequence<T>.associatePairs(
    complement: T,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return associatePairsTo(LinkedHashMap(), complement, keySelector, valueTransform)
}

inline fun <T, K, V> Sequence<T>.associatePairs(
    complement: T,
    transform: (T, T) -> Pair<K, V>
): Map<K, V> {
    return associatePairsTo(LinkedHashMap(), complement, transform)
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associateTo(
    destination: M,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    return this.associateByToKt(destination, keySelector, valueTransform)
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associateTo(
    destination: M,
    transform: (T) -> Pair<K, V>
): M {
    return this.associateToKt(destination, transform)
}

inline fun <T, K, M : MutableMap<in K, in T>> Sequence<T>.associateKeysTo(
    destination: M,
    keySelector: (T) -> K
): M {
    return this.associateByToKt(destination, keySelector)
}

fun <T, K, M : MutableMap<in K, in T>> Sequence<T>.associateKeysTo(
    destination: M,
    keys: Iterable<K>
): M {
    val values = this.iterator()
    for (key in keys) {
        if (!values.hasNext()) {
            break
        }
        destination[key] = values.next()
    }
    return destination
}

inline fun <T, V, M : MutableMap<in T, in V>> Sequence<T>.associateValuesTo(
    destination: M,
    valueSelector: (T) -> V
): M {
    return this.associateWithToKt(destination, valueSelector)
}

fun <T, V, M : MutableMap<in T, in V>> Sequence<T>.associateValuesTo(
    destination: M,
    values: Iterable<V>
): M {
    val valueIterator = values.iterator()
    for (key in this) {
        if (!valueIterator.hasNext()) {
            break
        }
        destination[key] = valueIterator.next()
    }
    return destination
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associateWithNextTo(
    destination: M,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    val iterator = iterator()
    if (!iterator.hasNext()) return destination
    var current = iterator.next()
    while (iterator.hasNext()) {
        val next = iterator.next()
        val k = keySelector(current)
        val v = valueTransform(next)
        destination.put(k, v)
        current = next
    }
    return destination
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associateWithNextTo(
    destination: M,
    transform: (T, T) -> Pair<K, V>
): M {
    val iterator = iterator()
    if (!iterator.hasNext()) return destination
    var current = iterator.next()
    while (iterator.hasNext()) {
        val next = iterator.next()
        val pair = transform(current, next)
        destination.put(pair.first, pair.second)
        current = next
    }
    return destination
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associatePairsTo(
    destination: M,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val ik = iterator.next()
        if (iterator.hasNext()) {
            val iv = iterator.next()
            val k = keySelector(ik)
            val v = valueTransform(iv)
            destination.put(k, v)
        } else {
            break
        }
    }
    return destination
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associatePairsTo(
    destination: M,
    transform: (T, T) -> Pair<K, V>
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val ik = iterator.next()
        if (iterator.hasNext()) {
            val iv = iterator.next()
            val pair = transform(ik, iv)
            destination.put(pair.first, pair.second)
        } else {
            break
        }
    }
    return destination
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associatePairsTo(
    destination: M,
    complement: T,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val ik = iterator.next()
        if (iterator.hasNext()) {
            val iv = iterator.next()
            val k = keySelector(ik)
            val v = valueTransform(iv)
            destination.put(k, v)
        } else {
            val k = keySelector(ik)
            val v = valueTransform(complement)
            destination.put(k, v)
            break
        }
    }
    return destination
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associatePairsTo(
    destination: M,
    complement: T,
    transform: (T, T) -> Pair<K, V>
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val ik = iterator.next()
        if (iterator.hasNext()) {
            val iv = iterator.next()
            val pair = transform(ik, iv)
            destination.put(pair.first, pair.second)
        } else {
            val pair = transform(ik, complement)
            destination.put(pair.first, pair.second)
            break
        }
    }
    return destination
}

inline fun <T, K> Sequence<T>.groupBy(keySelector: (T) -> K): Map<K, List<T>> {
    return this.groupByKt(keySelector)
}

inline fun <T, K, V> Sequence<T>.groupBy(
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, List<V>> {
    return this.groupByKt(keySelector, valueTransform)
}

inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Sequence<T>.groupByTo(
    destination: M,
    keySelector: (T) -> K
): M {
    return this.groupByToKt(destination, keySelector)
}

inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Sequence<T>.groupByTo(
    destination: M,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    return this.groupByToKt(destination, keySelector, valueTransform)
}

inline fun <S, T : S> Sequence<T>.reduce(operation: (S, T) -> S): S {
    return this.reduceKt(operation)
}

inline fun <S, T : S> Sequence<T>.reduceIndexed(operation: (index: Int, S, T) -> S): S {
    return this.reduceIndexedKt(operation)
}

inline fun <S, T : S> Sequence<T>.reduceOrNull(operation: (S, T) -> S): S? {
    return this.reduceOrNullKt(operation)
}

inline fun <S, T : S> Sequence<T>.reduceIndexedOrNull(operation: (index: Int, S, T) -> S): S? {
    return this.reduceIndexedOrNullKt(operation)
}

inline fun <T, R> Sequence<T>.reduce(initial: R, operation: (R, T) -> R): R {
    return this.foldKt(initial, operation)
}

inline fun <T, R> Sequence<T>.reduceIndexed(initial: R, operation: (index: Int, R, T) -> R): R {
    return this.foldIndexedKt(initial, operation)
}

fun <T, R, V> Sequence<T>.zip(other: Sequence<R>, transform: (T, R) -> V): Sequence<V> {
    return this.zipKt(other, transform)
}

fun <T, R> Sequence<T>.zipWithNext(transform: (T, T) -> R): Sequence<R> {
    return this.zipWithNextKt(transform)
}

fun <T> Sequence<T>.chunked(size: Int): Sequence<List<T>> {
    return this.chunkedKt(size)
}

fun <T, R> Sequence<T>.chunked(size: Int, transform: (List<T>) -> R): Sequence<R> {
    return this.chunkedKt(size, transform)
}

fun <T> Sequence<T>.windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): Sequence<List<T>> {
    return this.windowedKt(size, step, partialWindows)
}

fun <T, R> Sequence<T>.windowed(
    size: Int,
    step: Int = 1,
    partialWindows: Boolean = false,
    transform: (List<T>) -> R
): Sequence<R> {
    return this.windowedKt(size, step, partialWindows, transform)
}

fun <T> Sequence<T>.distinct(): Sequence<T> {
    return this.distinctKt()
}

fun <T, K> Sequence<T>.distinct(selector: (T) -> K): Sequence<T> {
    return this.distinctByKt(selector)
}

@JvmOverloads
fun <T> Sequence<T>.sorted(comparator: Comparator<in T> = comparableComparator()): Sequence<T> {
    return this.sortedWithKt(comparator)
}

fun <T> Sequence<T>.shuffled(): Sequence<T> {
    return this.shuffledKt()
}

fun <T> Sequence<T>.shuffled(random: Random): Sequence<T> {
    return this.shuffledKt(random)
}

@JvmOverloads
fun <T> Sequence<T>.max(comparator: Comparator<in T> = comparableComparator()): T {
    return maxOrNull(comparator) ?: throw NoSuchElementException()
}

@JvmOverloads
fun <T> Sequence<T>.maxOrNull(comparator: Comparator<in T> = comparableComparator()): T? {
    return this.maxWithOrNullKt(comparator)
}

@JvmOverloads
fun <T> Sequence<T>.min(comparator: Comparator<in T> = comparableComparator()): T {
    return minOrNull(comparator) ?: throw NoSuchElementException()
}

@JvmOverloads
fun <T> Sequence<T>.minOrNull(comparator: Comparator<in T> = comparableComparator()): T? {
    return this.minWithOrNullKt(comparator)
}

@JvmOverloads
inline fun <T> Sequence<T>.sumInt(selector: (T) -> Int = { it.toInt() }): Int {
    return this.sumOfKt(selector)
}

@JvmOverloads
inline fun <T> Sequence<T>.sumLong(selector: (T) -> Long = { it.toLong() }): Long {
    return this.sumOfKt(selector)
}

@JvmOverloads
fun <T> Sequence<T>.sumDouble(selector: (T) -> Double = { it.toDouble() }): Double {
    return this.sumOfKt(selector)
}

@JvmOverloads
fun <T> Sequence<T>.sumBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
    return this.sumOfKt(selector)
}

@JvmOverloads
fun <T> Sequence<T>.sumBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
    return this.sumOfKt(selector)
}

@JvmOverloads
inline fun <T> Sequence<T>.averageInt(selector: (T) -> Int = { it.toInt() }): Int {
    var sum = 0
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) 0 else sum / count
}

@JvmOverloads
inline fun <T> Sequence<T>.averageLong(selector: (T) -> Long = { it.toLong() }): Long {
    var sum = 0L
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) 0 else sum / count
}

@JvmOverloads
inline fun <T> Sequence<T>.averageDouble(selector: (T) -> Double = { it.toDouble() }): Double {
    var sum = 0.0
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) 0.0 else sum / count
}

@JvmOverloads
inline fun <T> Sequence<T>.averageBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
    var sum = BigInteger.ZERO
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) BigInteger.ZERO else sum / count.toBigInteger()
}

@JvmOverloads
inline fun <T> Sequence<T>.averageBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
    var sum = BigDecimal.ZERO
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) BigDecimal.ZERO else sum / count.toBigDecimal()
}

fun <T, C : MutableCollection<in T>> Sequence<T>.toCollection(destination: C): C {
    return this.toCollectionKt(destination)
}

fun <T> Sequence<T>.toSet(): Set<T> {
    return this.toSetKt()
}

fun <T> Sequence<T>.toImmutableSet(): ImmutableSet<T> {
    return toSet().toImmutableSet()
}

fun <T> Sequence<T>.toMutableSet(): MutableSet<T> {
    return this.toMutableSetKt()
}

fun <T> Sequence<T>.toHashSet(): HashSet<T> {
    return this.toHashSetKt()
}

fun <T> Sequence<T>.toLinkedHashSet(): LinkedHashSet<T> {
    return this.takeAllTo(LinkedHashSet())
}

@JvmOverloads
fun <T> Sequence<T>.toSortedSet(comparator: Comparator<in T> = comparableComparator()): SortedSet<T> {
    return this.toSortedSetKt(comparator)
}

fun <T> Sequence<T>.toList(): List<T> {
    return this.toListKt()
}

fun <T> Sequence<T>.toImmutableList(): ImmutableList<T> {
    return toList().toImmutableList()
}

fun <T> Sequence<T>.toMutableList(): MutableList<T> {
    return this.toMutableListKt()
}

fun <T> Sequence<T>.toArrayList(): ArrayList<T> {
    return this.takeAllTo(ArrayList())
}

fun <T> Sequence<T>.toLinkedList(): LinkedList<T> {
    return this.takeAllTo(LinkedList())
}

@JvmOverloads
fun <T> Sequence<T>.toStream(parallel: Boolean = false): Stream<T> {
    return if (parallel) this.asStream().parallel() else this.asStream()
}

inline fun <reified T> Sequence<T>.toTypedArray(): Array<T> {
    val list = this.toListKt()
    return list.toTypedArrayKt()
}

fun <T> Sequence<T>.toArray(): Array<Any?> {
    val list = this.toListKt()
    return JavaCollects.toArray(list).asAny()
}

fun <T> Sequence<T>.toArray(generator: (size: Int) -> Array<T>): Array<T> {
    val list = this.toListKt()
    return JavaCollects.toArray(list, generator(list.size))
}

@JvmOverloads
fun <T, R> Sequence<T>.toArray(componentType: Type, selector: ((T) -> R)? = null): Array<R> {
    val list = this.toListKt()
    val result = componentType.newArray<R>(list.size)
    if (selector !== null) {
        list.forEachIndexed { i, t -> result[i] = selector(t) }
    } else {
        list.forEachIndexed { i, t -> result[i] = t.asAny() }
    }
    return result
}

@JvmOverloads
inline fun <T> Sequence<T>.toBooleanArray(selector: (T) -> Boolean = { it.toBoolean() }): BooleanArray {
    val list = this.toListKt()
    val result = BooleanArray(list.size)
    list.forEachIndexed { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Sequence<T>.toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
    val list = this.toListKt()
    val result = ByteArray(list.size)
    list.forEachIndexed { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Sequence<T>.toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
    val list = this.toListKt()
    val result = ShortArray(list.size)
    list.forEachIndexed { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Sequence<T>.toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
    val list = this.toListKt()
    val result = CharArray(list.size)
    list.forEachIndexed { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Sequence<T>.toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
    val list = this.toListKt()
    val result = IntArray(list.size)
    list.forEachIndexed { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Sequence<T>.toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
    val list = this.toListKt()
    val result = LongArray(list.size)
    list.forEachIndexed { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Sequence<T>.toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
    val list = this.toListKt()
    val result = FloatArray(list.size)
    list.forEachIndexed { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Sequence<T>.toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
    val list = this.toListKt()
    val result = DoubleArray(list.size)
    list.forEachIndexed { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
fun <T, R, A> Sequence<T>.toAnyArray(componentType: Type, selector: ((T) -> R)? = null): A {
    return when (componentType) {
        Boolean::class.javaPrimitiveType -> this.toBooleanArray()
        Byte::class.javaPrimitiveType -> this.toByteArray()
        Short::class.javaPrimitiveType -> this.toShortArray()
        Char::class.javaPrimitiveType -> this.toCharArray()
        Int::class.javaPrimitiveType -> this.toIntArray()
        Long::class.javaPrimitiveType -> this.toLongArray()
        Float::class.javaPrimitiveType -> this.toFloatArray()
        Double::class.javaPrimitiveType -> this.toDoubleArray()
        else -> this.toArray(componentType, selector)
    }.asAny()
}

fun <T> Sequence<T>.plus(element: T): Sequence<T> {
    return this.plusKt(element)
}

fun <T> Sequence<T>.plus(elements: Array<out T>): Sequence<T> {
    return this.plusKt(elements)
}

fun <T> Sequence<T>.plus(elements: Iterable<T>): Sequence<T> {
    return this.plusKt(elements)
}

fun <T> Sequence<T>.plus(elements: Sequence<T>): Sequence<T> {
    return this.plusKt(elements)
}

fun <T> Sequence<T>.minus(element: T): Sequence<T> {
    return this.minusKt(element)
}

fun <T> Sequence<T>.minus(elements: Array<out T>): Sequence<T> {
    return this.minusKt(elements)
}

fun <T> Sequence<T>.minus(elements: Iterable<T>): Sequence<T> {
    return this.minusKt(elements)
}

fun <T> Sequence<T>.minus(elements: Sequence<T>): Sequence<T> {
    return this.minusKt(elements)
}