@file:JvmName("BCollect")

package xyz.srclab.common.collect

import xyz.srclab.common.base.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.function.IntFunction
import java.util.function.Predicate
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.random.Random
import kotlin.toBigDecimal
import kotlin.toBigInteger
import kotlin.collections.all as allKt
import kotlin.collections.any as anyKt
import kotlin.collections.asSequence as asSequenceKt
import kotlin.collections.associate as associateKt
import kotlin.collections.associateBy as associateByKt
import kotlin.collections.associateByTo as associateByToKt
import kotlin.collections.associateTo as associateToKt
import kotlin.collections.associateWith as associateWithKt
import kotlin.collections.associateWithTo as associateWithToKt
import kotlin.collections.chunked as chunkedKt
import kotlin.collections.contains as containsKt
import kotlin.collections.count as countKt
import kotlin.collections.distinct as distinctKt
import kotlin.collections.distinctBy as distinctByKt
import kotlin.collections.drop as dropKt
import kotlin.collections.dropWhile as dropWhileKt
import kotlin.collections.elementAt as elementAtKt
import kotlin.collections.elementAtOrElse as elementAtOrElseKt
import kotlin.collections.elementAtOrNull as elementAtOrNullKt
import kotlin.collections.filter as filterKt
import kotlin.collections.filterIndexed as filterIndexedKt
import kotlin.collections.filterIndexedTo as filterIndexedToKt
import kotlin.collections.filterNotNull as filterNotNullKt
import kotlin.collections.filterTo as filterToKt
import kotlin.collections.find as findKt
import kotlin.collections.first as firstKt
import kotlin.collections.firstOrNull as firstOrNullKt
import kotlin.collections.flatMap as flatMapKt
import kotlin.collections.flatMapIndexed as flatMapIndexedKt
import kotlin.collections.flatMapIndexedTo as flatMapIndexedToKt
import kotlin.collections.flatMapTo as flatMapToKt
import kotlin.collections.fold as foldKt
import kotlin.collections.foldIndexed as foldIndexedKt
import kotlin.collections.forEachIndexed as forEachIndexedKt
import kotlin.collections.groupBy as groupByKt
import kotlin.collections.groupByTo as groupByToKt
import kotlin.collections.indexOf as indexOfKt
import kotlin.collections.indexOfFirst as indexOfFirstKt
import kotlin.collections.indexOfLast as indexOfLastKt
import kotlin.collections.intersect as intersectKt
import kotlin.collections.last as lastKt
import kotlin.collections.lastIndexOf as lastIndexOfKt
import kotlin.collections.lastOrNull as lastOrNullKt
import kotlin.collections.map as mapKt
import kotlin.collections.mapIndexed as mapIndexedKt
import kotlin.collections.mapIndexedNotNull as mapIndexedNotNullKt
import kotlin.collections.mapIndexedNotNullTo as mapIndexedNotNullToKt
import kotlin.collections.mapIndexedTo as mapIndexedToKt
import kotlin.collections.mapNotNull as mapNotNullKt
import kotlin.collections.mapNotNullTo as mapNotNullToKt
import kotlin.collections.mapTo as mapToKt
import kotlin.collections.maxWithOrNull as maxWithOrNullKt
import kotlin.collections.minWithOrNull as minWithOrNullKt
import kotlin.collections.minus as minusKt
import kotlin.collections.none as noneKt
import kotlin.collections.plus as plusKt
import kotlin.collections.random as randomKt
import kotlin.collections.randomOrNull as randomOrNullKt
import kotlin.collections.reduce as reduceKt
import kotlin.collections.reduceIndexed as reduceIndexedKt
import kotlin.collections.reduceIndexedOrNull as reduceIndexedOrNullKt
import kotlin.collections.reduceOrNull as reduceOrNullKt
import kotlin.collections.removeAll as removeAllKt
import kotlin.collections.retainAll as retainAllKt
import kotlin.collections.reversed as reversedKt
import kotlin.collections.shuffled as shuffledKt
import kotlin.collections.sortedWith as sortedWithKt
import kotlin.collections.subtract as subtractKt
import kotlin.collections.sumOf as sumOfKt
import kotlin.collections.take as takeKt
import kotlin.collections.takeWhile as takeWhileKt
import kotlin.collections.toCollection as toCollectionKt
import kotlin.collections.toHashSet as toHashSetKt
import kotlin.collections.toList as toListKt
import kotlin.collections.toMutableList as toMutableListKt
import kotlin.collections.toMutableSet as toMutableSetKt
import kotlin.collections.toSet as toSetKt
import kotlin.collections.toSortedSet as toSortedSetKt
import kotlin.collections.toTypedArray as toTypedArrayKt
import kotlin.collections.union as unionKt
import kotlin.collections.windowed as windowedKt
import kotlin.collections.zip as zipKt
import kotlin.collections.zipWithNext as zipWithNextKt
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt
import kotlin.collections.addAll as addAllKt

fun <T, C : MutableCollection<T>> newCollection(collection: C, vararg elements: T): C {
    collection.addAll(elements)
    return collection
}

fun <T> Iterable<T>.contains(element: T): Boolean {
    return this.containsKt(element)
}

fun <T> Iterable<T>.count(): Int {
    return this.countKt()
}

fun <T> Iterable<T>.count(predicate: Predicate<T>): Int {
    return this.countKt(predicate.toFunction())
}

fun <T> Iterable<T>.isEmpty(): Boolean {
    if (this is Collection) {
        return this.isEmpty()
    }
    for (it in this) {
        return false
    }
    return true
}

fun <T> Iterable<T>.isNotEmpty(): Boolean {
    return !isEmpty()
}

fun <T> Iterable<T>.any(): Boolean {
    return this.anyKt()
}

fun <T> Iterable<T>.any(predicate: Predicate<T>): Boolean {
    return this.anyKt(predicate.toFunction())
}

fun <T> Iterable<T>.none(): Boolean {
    return this.noneKt()
}

fun <T> Iterable<T>.none(predicate: Predicate<T>): Boolean {
    return this.noneKt(predicate.toFunction())
}

fun <T> Iterable<T>.all(predicate: Predicate<T>): Boolean {
    return this.allKt(predicate.toFunction())
}

fun <T> Iterable<T>.first(): T {
    return this.firstKt()
}

fun <T> Iterable<T>.first(predicate: Predicate<T>): T {
    return this.firstKt(predicate.toFunction())
}

fun <T> Iterable<T>.firstOrNull(): T? {
    return this.firstOrNullKt()
}

fun <T> Iterable<T>.firstOrNull(predicate: Predicate<T>): T? {
    return this.firstOrNullKt(predicate.toFunction())
}

fun <T> Iterable<T>.last(): T {
    return this.lastKt()
}

fun <T> Iterable<T>.last(predicate: Predicate<T>): T {
    return this.lastKt(predicate.toFunction())
}

fun <T> Iterable<T>.lastOrNull(): T? {
    return this.lastOrNullKt()
}

fun <T> Iterable<T>.lastOrNull(predicate: Predicate<T>): T? {
    return this.lastOrNullKt(predicate.toFunction())
}

fun <T> Iterable<T>.random(): T {
    return asToList().randomKt()
}

fun <T> Iterable<T>.random(random: Random): T {
    return asToList().randomKt(random)
}

fun <T> Iterable<T>.randomOrNull(): T? {
    return asToList().randomOrNullKt()
}

fun <T> Iterable<T>.randomOrNull(random: Random): T? {
    return asToList().randomOrNullKt(random)
}

fun <T> Iterable<T>.get(index: Int): T {
    return this.elementAtKt(index)
}

fun <T> Iterable<T>.getOrNull(index: Int): T? {
    return this.elementAtOrNullKt(index)
}

fun <T> Iterable<T>.getOrDefault(index: Int, defaultValue: T): T {
    return this.elementAtOrElseKt(index){defaultValue}
}

fun <T> Iterable<T>.getOrElse(index: Int, defaultValue: IntFunction<T>): T {
    return this.elementAtOrElseKt(index, defaultValue.toFunction())
}

fun <T> Iterable<T>.find(predicate: Predicate<T>): T? {
    return this.findKt(predicate.toFunction())
}

fun <T> Iterable<T>.findLast(predicate: Predicate<T>): T? {
    return this.firstKt(predicate.toFunction())
}

fun <T> Iterable<T>.indexOf(element: T): Int {
    return this.indexOfKt(element)
}

fun <T> Iterable<T>.indexOf(predicate: Predicate<T>): Int {
    return this.indexOfFirstKt(predicate.toFunction())
}

fun <T> Iterable<T>.lastIndexOf(element: T): Int {
    return this.lastIndexOfKt(element)
}

fun <T> Iterable<T>.lastIndexOf(predicate: Predicate<T>): Int {
    return this.indexOfLastKt(predicate.toFunction())
}

fun <T> Iterable<T>.take(n: Int): List<T> {
    return this.takeKt(n)
}

fun <T> Iterable<T>.take(predicate: Predicate<T>): List<T> {
    return this.takeWhileKt(predicate.toFunction())
}

fun <T> Iterable<T>.drop(n: Int): List<T> {
    return this.dropKt(n)
}

fun <T> Iterable<T>.drop(predicate: Predicate<T>): List<T> {
    return this.dropWhileKt(predicate.toFunction())
}

fun <T> Iterable<T>.filter(predicate: Predicate<T>): List<T> {
    return this.filterKt(predicate.toFunction())
}

inline fun <T> Iterable<T>.filterIndexed(predicate: (index: Int, T) -> Boolean): List<T> {
    return this.filterIndexedKt(predicate)
}

fun <T> Iterable<T>.filterNotNull(): List<T> {
    return this.filterNotNullKt()
}

fun <T, C : MutableCollection<in T>> Iterable<T>.filterTo(destination: C, predicate: Predicate<T>): C {
    return this.filterToKt(destination, predicate.toFunction())
}

inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterIndexedTo(
    destination: C,
    predicate: (index: Int, T) -> Boolean
): C {
    return this.filterIndexedToKt(destination, predicate)
}

fun <C : MutableCollection<in T>, T> Iterable<T>.filterNotNullTo(destination: C): C {
    return this.filterTo(destination) { it !== null }
}

inline fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R> {
    return this.mapKt(transform)
}

inline fun <T, R> Iterable<T>.mapIndexed(transform: (index: Int, T) -> R): List<R> {
    return this.mapIndexedKt(transform)
}

inline fun <T, R : Any> Iterable<T>.mapNotNull(transform: (T) -> R?): List<R> {
    return this.mapNotNullKt(transform)
}

inline fun <T, R : Any> Iterable<T>.mapIndexedNotNull(transform: (index: Int, T) -> R?): List<R> {
    return this.mapIndexedNotNullKt(transform)
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapTo(destination: C, transform: (T) -> R): C {
    return this.mapToKt(destination, transform)
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapIndexedTo(
    destination: C,
    transform: (index: Int, T) -> R
): C {
    return this.mapIndexedToKt(destination, transform)
}

inline fun <T, R : Any, C : MutableCollection<in R>> Iterable<T>.mapNotNullTo(
    destination: C,
    transform: (T) -> R?
): C {
    return this.mapNotNullToKt(destination, transform)
}

inline fun <T, R : Any, C : MutableCollection<in R>> Iterable<T>.mapIndexedNotNullTo(
    destination: C,
    transform: (index: Int, T) -> R?
): C {
    return this.mapIndexedNotNullToKt(destination, transform)
}

inline fun <T, R> Iterable<T>.flatMap(transform: (T) -> Iterable<R>): List<R> {
    return this.flatMapKt(transform)
}

inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): List<R> {
    return this.flatMapIndexedKt(transform)
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapTo(
    destination: C,
    transform: (T) -> Iterable<R>
): C {
    return this.flatMapToKt(destination, transform)
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapIndexedTo(
    destination: C,
    transform: (index: Int, T) -> Iterable<R>
): C {
    return this.flatMapIndexedToKt(destination, transform)
}

inline fun <T, K, V> Iterable<T>.associate(
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return this.associateByKt(keySelector, valueTransform)
}

inline fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>): Map<K, V> {
    return this.associateKt(transform)
}

inline fun <T, K> Iterable<T>.associateKeys(keySelector: (T) -> K): Map<K, T> {
    return this.associateByKt(keySelector)
}

fun <T, K> Iterable<T>.associateKeys(keys: Iterable<K>): Map<K, T> {
    return associateKeysTo(LinkedHashMap(), keys)
}

inline fun <T, V> Iterable<T>.associateValues(valueSelector: (T) -> V): Map<T, V> {
    return this.associateWithKt(valueSelector)
}

fun <T, V> Iterable<T>.associateValues(values: Iterable<V>): Map<T, V> {
    return associateValuesTo(LinkedHashMap(), values)
}

inline fun <T, K, V> Iterable<T>.associateWithNext(
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return associateWithNextTo(LinkedHashMap(), keySelector, valueTransform)
}

inline fun <T, K, V> Iterable<T>.associateWithNext(transform: (T, T) -> Pair<K, V>): Map<K, V> {
    return associateWithNextTo(LinkedHashMap(), transform)
}

inline fun <T, K, V> Iterable<T>.associatePairs(
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return associatePairsTo(LinkedHashMap(), keySelector, valueTransform)
}

inline fun <T, K, V> Iterable<T>.associatePairs(transform: (T, T) -> Pair<K, V>): Map<K, V> {
    return associatePairsTo(LinkedHashMap(), transform)
}

inline fun <T, K, V> Iterable<T>.associatePairs(
    complement: T,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return associatePairsTo(LinkedHashMap(), complement, keySelector, valueTransform)
}

inline fun <T, K, V> Iterable<T>.associatePairs(
    complement: T,
    transform: (T, T) -> Pair<K, V>
): Map<K, V> {
    return associatePairsTo(LinkedHashMap(), complement, transform)
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateTo(
    destination: M,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    return this.associateByToKt(destination, keySelector, valueTransform)
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateTo(
    destination: M,
    transform: (T) -> Pair<K, V>
): M {
    return this.associateToKt(destination, transform)
}

inline fun <T, K, M : MutableMap<in K, in T>> Iterable<T>.associateKeysTo(
    destination: M,
    keySelector: (T) -> K
): M {
    return this.associateByToKt(destination, keySelector)
}

fun <T, K, M : MutableMap<in K, in T>> Iterable<T>.associateKeysTo(
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

inline fun <T, V, M : MutableMap<in T, in V>> Iterable<T>.associateValuesTo(
    destination: M,
    valueSelector: (T) -> V
): M {
    return this.associateWithToKt(destination, valueSelector)
}

fun <T, V, M : MutableMap<in T, in V>> Iterable<T>.associateValuesTo(
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

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateWithNextTo(
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

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateWithNextTo(
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

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associatePairsTo(
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

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associatePairsTo(
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

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associatePairsTo(
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

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associatePairsTo(
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

inline fun <T, K> Iterable<T>.groupBy(keySelector: (T) -> K): Map<K, List<T>> {
    return this.groupByKt(keySelector)
}

inline fun <T, K, V> Iterable<T>.groupBy(
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, List<V>> {
    return this.groupByKt(keySelector, valueTransform)
}

inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Iterable<T>.groupByTo(
    destination: M,
    keySelector: (T) -> K
): M {
    return this.groupByToKt(destination, keySelector)
}

inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Iterable<T>.groupByTo(
    destination: M,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    return this.groupByToKt(destination, keySelector, valueTransform)
}

inline fun <S, T : S> Iterable<T>.reduce(operation: (S, T) -> S): S {
    return this.reduceKt(operation)
}

inline fun <S, T : S> Iterable<T>.reduceIndexed(operation: (index: Int, S, T) -> S): S {
    return this.reduceIndexedKt(operation)
}

inline fun <S, T : S> Iterable<T>.reduceOrNull(operation: (S, T) -> S): S? {
    return this.reduceOrNullKt(operation)
}

inline fun <S, T : S> Iterable<T>.reduceIndexedOrNull(operation: (index: Int, S, T) -> S): S? {
    return this.reduceIndexedOrNullKt(operation)
}

inline fun <T, R> Iterable<T>.reduce(initial: R, operation: (R, T) -> R): R {
    return this.foldKt(initial, operation)
}

inline fun <T, R> Iterable<T>.reduceIndexed(initial: R, operation: (index: Int, R, T) -> R): R {
    return this.foldIndexedKt(initial, operation)
}

inline fun <T, R, V> Iterable<T>.zip(other: Array<out R>, transform: (T, R) -> V): List<V> {
    return this.zipKt(other, transform)
}

inline fun <T, R, V> Iterable<T>.zip(other: Iterable<R>, transform: (T, R) -> V): List<V> {
    return this.zipKt(other, transform)
}

inline fun <T, R> Iterable<T>.zipWithNext(transform: (T, T) -> R): List<R> {
    return this.zipWithNextKt(transform)
}

fun <T> Iterable<T>.chunked(size: Int): List<List<T>> {
    return this.chunkedKt(size)
}

fun <T, R> Iterable<T>.chunked(size: Int, transform: (List<T>) -> R): List<R> {
    return this.chunkedKt(size, transform)
}

fun <T> Iterable<T>.windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<T>> {
    return this.windowedKt(size, step, partialWindows)
}

fun <T, R> Iterable<T>.windowed(
    size: Int,
    step: Int = 1,
    partialWindows: Boolean = false,
    transform: (List<T>) -> R
): List<R> {
    return this.windowedKt(size, step, partialWindows, transform)
}

fun <T> Iterable<T>.intersect(other: Iterable<T>): Set<T> {
    return this.intersectKt(other)
}

fun <T> Iterable<T>.union(other: Iterable<T>): Set<T> {
    return this.unionKt(other)
}

fun <T> Iterable<T>.subtract(other: Iterable<T>): Set<T> {
    return this.subtractKt(other)
}

fun <T> Iterable<T>.distinct(): List<T> {
    return this.distinctKt()
}

inline fun <T, K> Iterable<T>.distinct(selector: (T) -> K): List<T> {
    return this.distinctByKt(selector)
}

@JvmOverloads
fun <T> Iterable<T>.sorted(comparator: Comparator<in T> = castComparableComparator()): List<T> {
    return this.sortedWithKt(comparator)
}

fun <T> Iterable<T>.reversed(): List<T> {
    return this.reversedKt()
}

fun <T> Iterable<T>.shuffled(): List<T> {
    return this.shuffledKt()
}

fun <T> Iterable<T>.shuffled(random: Random): List<T> {
    return this.shuffledKt(random)
}

inline fun <T> Iterable<T>.forEach(action: (index: Int, T) -> Unit) {
    return this.forEachIndexedKt(action)
}

@JvmOverloads
fun <T> Iterable<T>.max(comparator: Comparator<in T> = castComparableComparator()): T {
    return maxOrNull(comparator) ?: throw NoSuchElementException()
}

@JvmOverloads
fun <T> Iterable<T>.maxOrNull(comparator: Comparator<in T> = castComparableComparator()): T? {
    return this.maxWithOrNullKt(comparator)
}

@JvmOverloads
fun <T> Iterable<T>.min(comparator: Comparator<in T> = castComparableComparator()): T {
    return minOrNull(comparator) ?: throw NoSuchElementException()
}

@JvmOverloads
fun <T> Iterable<T>.minOrNull(comparator: Comparator<in T> = castComparableComparator()): T? {
    return this.minWithOrNullKt(comparator)
}

@JvmOverloads
inline fun <T> Iterable<T>.sumInt(selector: (T) -> Int = { it.toInt() }): Int {
    return this.sumOfKt(selector)
}

@JvmOverloads
inline fun <T> Iterable<T>.sumLong(selector: (T) -> Long = { it.toLong() }): Long {
    return this.sumOfKt(selector)
}

@JvmOverloads
fun <T> Iterable<T>.sumDouble(selector: (T) -> Double = { it.toDouble() }): Double {
    return this.sumOfKt(selector)
}

@JvmOverloads
fun <T> Iterable<T>.sumBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
    return this.sumOfKt(selector)
}

@JvmOverloads
fun <T> Iterable<T>.sumBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
    return this.sumOfKt(selector)
}

@JvmOverloads
inline fun <T> Iterable<T>.averageInt(selector: (T) -> Int = { it.toInt() }): Int {
    var sum = 0
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) 0 else sum / count
}

@JvmOverloads
inline fun <T> Iterable<T>.averageLong(selector: (T) -> Long = { it.toLong() }): Long {
    var sum = 0L
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) 0 else sum / count
}

@JvmOverloads
inline fun <T> Iterable<T>.averageDouble(selector: (T) -> Double = { it.toDouble() }): Double {
    var sum = 0.0
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) 0.0 else sum / count
}

@JvmOverloads
inline fun <T> Iterable<T>.averageBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
    var sum = BigInteger.ZERO
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) BigInteger.ZERO else sum / count.toBigInteger()
}

@JvmOverloads
inline fun <T> Iterable<T>.averageBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
    var sum = BigDecimal.ZERO
    var count = 0
    for (t in this) {
        sum += selector(t)
        count++
    }
    return if (count == 0) BigDecimal.ZERO else sum / count.toBigDecimal()
}

fun <T, C : MutableCollection<in T>> Iterable<T>.toCollection(destination: C): C {
    return this.toCollectionKt(destination)
}

fun <T> Iterable<T>.toCollection(): Collection<T> {
    return toSet()
}

fun <T> Iterable<T>.toMutableCollection(): MutableCollection<T> {
    return toMutableSet()
}

fun <T> Iterable<T>.toSet(): Set<T> {
    return this.toSetKt()
}

fun <T> Iterable<T>.toMutableSet(): MutableSet<T> {
    return this.toMutableSetKt()
}

fun <T> Iterable<T>.toHashSet(): HashSet<T> {
    return this.toHashSetKt()
}

fun <T> Iterable<T>.toLinkedHashSet(): LinkedHashSet<T> {
    return if (this is Collection<T>) toCollection(LinkedHashSet(size)) else toCollection(LinkedHashSet())
}

@JvmOverloads
fun <T> Iterable<T>.toSortedSet(comparator: Comparator<in T>? = null): SortedSet<T> {
    return if (comparator === null) this.toSortedSetKt(castComparableComparator()) else this.toSortedSetKt(comparator)
}

@JvmOverloads
fun <T> Iterable<T>.toTreeSet(comparator: Comparator<in T>? = null): TreeSet<T> {
    return toCollection(if (comparator === null) TreeSet() else TreeSet(comparator))
}

fun <T> Iterable<T>.toList(): List<T> {
    return this.toListKt()
}

fun <T> Iterable<T>.toMutableList(): MutableList<T> {
    return this.toMutableListKt()
}

fun <T> Iterable<T>.toArrayList(): ArrayList<T> {
    return if (this is Collection<T>) toCollection(ArrayList(size)) else toCollection(ArrayList())
}

fun <T> Iterable<T>.toLinkedList(): LinkedList<T> {
    return this.toCollection(LinkedList())
}

fun <T> Iterable<T>.asToCollection(): Collection<T> {
    return if (this is Collection<T>) this else toCollection()
}

fun <T> Iterable<T>.asToMutableCollection(): MutableCollection<T> {
    return if (this is MutableCollection<T>) this else toMutableCollection()
}

fun <T> Iterable<T>.asToSet(): Set<T> {
    return if (this is Set<T>) this else toSet()
}

fun <T> Iterable<T>.asToMutableSet(): MutableSet<T> {
    return if (this is MutableSet<T>) this else toMutableSet()
}

fun <T> Iterable<T>.asToHashSet(): HashSet<T> {
    return if (this is HashSet<T>) this else toHashSet()
}

fun <T> Iterable<T>.asToLinkedHashSet(): LinkedHashSet<T> {
    return if (this is LinkedHashSet<T>) this else toLinkedHashSet()
}

@JvmOverloads
fun <T> Iterable<T>.asToSortedSet(comparator: Comparator<in T>? = null): SortedSet<T> {
    return if (this is SortedSet<T>) this else toSortedSet(comparator)
}

@JvmOverloads
fun <T> Iterable<T>.asToTreeSet(comparator: Comparator<in T>? = null): TreeSet<T> {
    return if (this is TreeSet<T>) this else toTreeSet(comparator)
}

fun <T> Iterable<T>.asToList(): List<T> {
    return if (this is List<T>) this else toList()
}

fun <T> Iterable<T>.asToMutableList(): MutableList<T> {
    return if (this is MutableList<T>) this else toMutableList()
}

fun <T> Iterable<T>.asToArrayList(): ArrayList<T> {
    return if (this is ArrayList<T>) this else toArrayList()
}

fun <T> Iterable<T>.asToLinkedList(): LinkedList<T> {
    return if (this is LinkedList<T>) this else toLinkedList()
}

@JvmOverloads
fun <T> Iterable<T>.toStream(parallel: Boolean = false): Stream<T> {
    return StreamSupport.stream(this.spliterator(), parallel)
}

fun <T> Iterable<T>.toSequence(): Sequence<T> {
    return this.asSequenceKt()
}

inline fun <reified T> Iterable<T>.toTypedArray(): Array<T> {
    return asToCollection().toTypedArrayKt()
}

fun <T> Iterable<T>.toArray(): Array<Any?> {
    return asToCollection().toTypedArrayKt()
}

fun <T> Iterable<T>.toArray(array: (size: Int) -> Array<T>): Array<T> {
    val collection = asToCollection()
    val result = array(collection.size)
    return JavaCollects.toArray(collection, result)
}

@JvmOverloads
fun <T, R> Iterable<T>.toArray(componentType: Type, selector: ((T) -> R)? = null): Array<R> {
    val collection = asToCollection()
    val result = componentType.newArray<R>(collection.size)
    if (selector !== null) {
        collection.forEach { i, t -> result[i] = selector(t) }
    } else {
        collection.forEach { i, t -> result[i] = t.asTyped() }
    }
    return result
}

@JvmOverloads
fun <T> Iterable<T>.toBooleanArray(selector: Predicate<T> = Predicate{ it.toBoolean() }): BooleanArray {
    val collection = asToCollection()
    val result = BooleanArray(collection.size)
    collection.forEach { i, t -> result[i] = selector.test(t) }
    return result
}

@JvmOverloads
inline fun <T> Iterable<T>.toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
    val collection = asToCollection()
    val result = ByteArray(collection.size)
    collection.forEach { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Iterable<T>.toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
    val collection = asToCollection()
    val result = ShortArray(collection.size)
    collection.forEach { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Iterable<T>.toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
    val collection = asToCollection()
    val result = CharArray(collection.size)
    collection.forEach { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Iterable<T>.toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
    val collection = asToCollection()
    val result = IntArray(collection.size)
    collection.forEach { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Iterable<T>.toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
    val collection = asToCollection()
    val result = LongArray(collection.size)
    collection.forEach { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Iterable<T>.toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
    val collection = asToCollection()
    val result = FloatArray(collection.size)
    collection.forEach { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
inline fun <T> Iterable<T>.toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
    val collection = asToCollection()
    val result = DoubleArray(collection.size)
    collection.forEach { i, t -> result[i] = selector(t) }
    return result
}

@JvmOverloads
fun <T, R, A> Iterable<T>.toAnyArray(componentType: Type, selector: ((T) -> R)? = null): A {
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
    }.asTyped()
}

fun <T> Iterable<T>.plus(element: T): List<T> {
    return this.plusKt(element)
}

fun <T> Iterable<T>.plus(elements: Array<out T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Iterable<T>.plus(elements: Iterable<T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Iterable<T>.plus(elements: Sequence<T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Iterable<T>.minus(element: T): List<T> {
    return this.minusKt(element)
}

fun <T> Iterable<T>.minus(elements: Array<out T>): List<T> {
    return this.minusKt(elements)
}

fun <T> Iterable<T>.minus(elements: Iterable<T>): List<T> {
    return this.minusKt(elements)
}

fun <T> Iterable<T>.minus(elements: Sequence<T>): List<T> {
    return this.minusKt(elements)
}

fun <T> Iterable<T>.plusBefore(index: Int, element: T): List<T> {
    return plusBefore(index, listOf(element))
}

fun <T> Iterable<T>.plusBefore(index: Int, elements: Array<out T>): List<T> {
    return plusBefore(index, elements.toListKt())
}

fun <T> Iterable<T>.plusBefore(index: Int, elements: Iterable<T>): List<T> {
    if (index == 0) {
        return elements.plusKt(this)
    }
    val list = asToList()
    val front = list.subList(0, index)
    val back = list.subList(index, list.size)
    return concatList(front, elements, back)
    //return front.plusKt(elements).plusKt(back)
}

fun <T> Iterable<T>.plusBefore(index: Int, elements: Sequence<T>): List<T> {
    return plusBefore(index, elements.toList())
}

fun <T> Iterable<T>.plusAfter(index: Int, element: T): List<T> {
    return plusAfter(index, listOf(element))
}

fun <T> Iterable<T>.plusAfter(index: Int, elements: Array<out T>): List<T> {
    return plusAfter(index, elements.toListKt())
}

fun <T> Iterable<T>.plusAfter(index: Int, elements: Iterable<T>): List<T> {
    val list = asToList()
    if (index == list.size - 1) {
        return list.plusKt(this)
    }
    val front = list.subList(0, index + 1)
    val back = list.subList(index + 1, list.size)
    return concatList(front, elements, back)
    //return front.plusKt(elements).plusKt(back)
}

fun <T> Iterable<T>.plusAfter(index: Int, elements: Sequence<T>): List<T> {
    return plusAfter(index, elements.toList())
}

@JvmOverloads
fun <T> Iterable<T>.minusAt(index: Int, count: Int = 1): List<T> {
    val list = asToList()
    if (index == 0) {
        return if (count < list.size) list.subList(count, list.size) else emptyList()
    }
    val front = list.subList(0, index)
    if (count >= list.size - index) {
        return front
    }
    return front.plusKt(list.subList(index + count, list.size))
}

fun <T> MutableIterable<T>.remove(element: T): Boolean {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (next == element) {
            iterator.remove()
            return true
        }
    }
    return false
}

fun <T> MutableIterable<T>.removeFirst(n: Int): Boolean {
    val iterator = this.iterator()
    var success = true
    for (i in 1..n) {
        if (iterator.hasNext()) {
            iterator.next()
            iterator.remove()
        } else {
            success = false
            break
        }
    }
    return success
}

fun <T> MutableIterable<T>.removeAll(predicate: Predicate<T>): Boolean {
    return this.removeAllKt(predicate.toFunction())
}

fun <T> MutableCollection<T>.removeAll(elements: Array<out T>): Boolean {
    return this.removeAllKt(elements)
}

fun <T> MutableCollection<T>.removeAll(elements: Iterable<T>): Boolean {
    return this.removeAllKt(elements)
}

fun <T> MutableIterable<T>.retainAll(predicate: Predicate<T>): Boolean {
    return this.retainAllKt(predicate.toFunction())
}

fun <T> MutableCollection<T>.retainAll(elements: Array<out T>): Boolean {
    return this.retainAllKt(elements)
}

fun <T> MutableCollection<T>.retainAll(elements: Iterable<T>): Boolean {
    return this.retainAllKt(elements)
}

fun <T> Collection<T>.plus(element: T): List<T> {
    return this.plusKt(element)
}

fun <T> Collection<T>.plus(elements: Array<out T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Collection<T>.plus(elements: Iterable<T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Collection<T>.plus(elements: Sequence<T>): List<T> {
    return this.plusKt(elements)
}

fun <T> MutableCollection<T>.addAll(elements: Array<out T>): Boolean {
    return this.addAllKt(elements)
}

fun <T> MutableCollection<T>.addAll(elements: Iterable<T>): Boolean {
    return this.addAllKt(elements)
}

fun <T> MutableCollection<T>.addAll(elements: Sequence<T>): Boolean {
    return this.addAllKt(elements)
}



//Join to String:

@JvmOverloads
fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    transform: ((T) -> CharSequence)? = null
): String {
    return this.joinToStringKt(separator = separator, transform = transform)
}

fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): String {
    return this.joinToStringKt(
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform,
    )
}

fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): String {
    return this.joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
}

@JvmOverloads
fun <T, A : Appendable> Iterable<T>.joinTo(
    destination: A,
    separator: CharSequence = ", ",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(buffer = destination, separator = separator, transform = transform)
}

fun <T, A : Appendable> Iterable<T>.joinTo(
    destination: A,
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(
        buffer = destination,
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform
    )
}

fun <T, A : Appendable> Iterable<T>.joinTo(
    destination: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(destination, separator, prefix, suffix, limit, truncated, transform)
}

//Iterator

fun <T> Iterator<T>.asIterable(): Iterable<T> {
    return object : Iterable<T> {
        override fun iterator(): Iterator<T> = this@asIterable
    }
}

//Enumeration

/**
 * For java.
 */
fun <T> Enumeration<T>.asIterator(): Iterator<T> {
    return this.iterator()
}

fun <T> Enumeration<T>.asIterable(): Iterable<T> {
    return asIterator().asIterable()
}

fun <T> Iterator<T>.asEnumeration(): Enumeration<T> {
    return object : Enumeration<T> {
        override fun hasMoreElements(): Boolean = this@asEnumeration.hasNext()
        override fun nextElement(): T = this@asEnumeration.next()
    }
}

fun <T> Iterable<T>.asEnumeration(): Enumeration<T> {
    return this.iterator().asEnumeration()
}