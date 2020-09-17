package xyz.srclab.common.collection

import xyz.srclab.common.base.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.collections.LinkedHashMap
import kotlin.random.Random

abstract class BaseIterableOps<T, I : Iterable<T>, MI : MutableIterable<T>, THIS : BaseIterableOps<T, I, MI, THIS>>
protected constructor(operated: I) {

    protected var processed: I? = operated
    protected var sequence: Sequence<T>? = null

    private var mode = IMMEDIATE_MODE

    protected abstract fun <T> toListOps(iterable: Iterable<T>): ListOps<T>

    protected abstract fun <T> toListOps(sequence: Sequence<T>): ListOps<T>

    protected abstract fun <T> toSetOps(iterable: Iterable<T>): SetOps<T>

    protected abstract fun <T> toSetOps(sequence: Sequence<T>): SetOps<T>

    protected abstract fun <K, V> toMapOps(map: Map<K, V>): MapOps<K, V>

    protected abstract fun sequenceToCollection(sequence: Sequence<T>): I

    fun lazyMode(): THIS {
        if (mode == LAZY_MODE) {
            return asThis()
        }

        Check.checkState(
            processed != null,
            "Wrong state: both internal processed is null."
        )
        sequence = processed!!.asSequence()
        processed = null
        mode = LAZY_MODE

        return asThis()
    }

    fun immediateMode(): THIS {
        if (mode == IMMEDIATE_MODE) {
            return asThis()
        }

        Check.checkState(
            sequence != null,
            "Wrong state: both internal sequence is null."
        )
        processed = sequenceToCollection(sequence!!)
        sequence = null
        mode = IMMEDIATE_MODE

        return asThis()
    }

    fun find(predicate: (T) -> Boolean): T? {
        return when (mode) {
            IMMEDIATE_MODE -> find(processed(), predicate)
            LAZY_MODE -> SequenceOps.find(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun findLast(predicate: (T) -> Boolean): T? {
        return when (mode) {
            IMMEDIATE_MODE -> findLast(processed(), predicate)
            LAZY_MODE -> SequenceOps.findLast(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun first(): T {
        return when (mode) {
            IMMEDIATE_MODE -> first(processed())
            LAZY_MODE -> SequenceOps.first(sequence())
            else -> throwIllegalMode()
        }
    }

    fun first(predicate: (T) -> Boolean): T {
        return when (mode) {
            IMMEDIATE_MODE -> first(processed(), predicate)
            LAZY_MODE -> SequenceOps.first(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun firstOrNull(): T? {
        return when (mode) {
            IMMEDIATE_MODE -> firstOrNull(processed())
            LAZY_MODE -> SequenceOps.firstOrNull(sequence())
            else -> throwIllegalMode()
        }
    }

    fun firstOrNull(predicate: (T) -> Boolean): T? {
        return when (mode) {
            IMMEDIATE_MODE -> firstOrNull(processed(), predicate)
            LAZY_MODE -> SequenceOps.firstOrNull(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun last(): T {
        return when (mode) {
            IMMEDIATE_MODE -> last(processed())
            LAZY_MODE -> SequenceOps.last(sequence())
            else -> throwIllegalMode()
        }
    }

    fun last(predicate: (T) -> Boolean): T {
        return when (mode) {
            IMMEDIATE_MODE -> last(processed(), predicate)
            LAZY_MODE -> SequenceOps.last(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun lastOrNull(): T? {
        return when (mode) {
            IMMEDIATE_MODE -> lastOrNull(processed())
            LAZY_MODE -> SequenceOps.lastOrNull(sequence())
            else -> throwIllegalMode()
        }
    }

    fun lastOrNull(predicate: (T) -> Boolean): T? {
        return when (mode) {
            IMMEDIATE_MODE -> lastOrNull(processed(), predicate)
            LAZY_MODE -> SequenceOps.lastOrNull(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun all(predicate: (T) -> Boolean): Boolean {
        return when (mode) {
            IMMEDIATE_MODE -> all(processed(), predicate)
            LAZY_MODE -> SequenceOps.all(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun any(): Boolean {
        return when (mode) {
            IMMEDIATE_MODE -> any(processed())
            LAZY_MODE -> SequenceOps.any(sequence())
            else -> throwIllegalMode()
        }
    }

    fun any(predicate: (T) -> Boolean): Boolean {
        return when (mode) {
            IMMEDIATE_MODE -> any(processed(), predicate)
            LAZY_MODE -> SequenceOps.any(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun none(): Boolean {
        return when (mode) {
            IMMEDIATE_MODE -> none(processed())
            LAZY_MODE -> SequenceOps.none(sequence())
            else -> throwIllegalMode()
        }
    }

    fun none(predicate: (T) -> Boolean): Boolean {
        return when (mode) {
            IMMEDIATE_MODE -> none(processed(), predicate)
            LAZY_MODE -> SequenceOps.none(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun single(): T {
        return when (mode) {
            IMMEDIATE_MODE -> single(processed())
            LAZY_MODE -> SequenceOps.single(sequence())
            else -> throwIllegalMode()
        }
    }

    fun single(predicate: (T) -> Boolean): T {
        return when (mode) {
            IMMEDIATE_MODE -> single(processed(), predicate)
            LAZY_MODE -> SequenceOps.single(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun singleOrNull(): T? {
        return when (mode) {
            IMMEDIATE_MODE -> singleOrNull(processed())
            LAZY_MODE -> SequenceOps.singleOrNull(sequence())
            else -> throwIllegalMode()
        }
    }

    fun singleOrNull(predicate: (T) -> Boolean): T? {
        return when (mode) {
            IMMEDIATE_MODE -> singleOrNull(processed(), predicate)
            LAZY_MODE -> SequenceOps.singleOrNull(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun contains(element: T): Boolean {
        return when (mode) {
            IMMEDIATE_MODE -> contains(processed(), element)
            LAZY_MODE -> SequenceOps.contains(sequence(), element)
            else -> throwIllegalMode()
        }
    }

    fun count(): Int {
        return when (mode) {
            IMMEDIATE_MODE -> count(processed())
            LAZY_MODE -> SequenceOps.count(sequence())
            else -> throwIllegalMode()
        }
    }

    fun count(predicate: (T) -> Boolean): Int {
        return when (mode) {
            IMMEDIATE_MODE -> count(processed(), predicate)
            LAZY_MODE -> SequenceOps.count(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun elementAt(index: Int): T {
        return when (mode) {
            IMMEDIATE_MODE -> elementAt(processed(), index)
            LAZY_MODE -> SequenceOps.elementAt(sequence(), index)
            else -> throwIllegalMode()
        }
    }

    fun elementAtOrElse(
        index: Int,
        defaultValue: (index: Int) -> T
    ): T {
        return when (mode) {
            IMMEDIATE_MODE -> elementAtOrElse(processed(), index, defaultValue)
            LAZY_MODE -> SequenceOps.elementAtOrElse(sequence(), index, defaultValue)
            else -> throwIllegalMode()
        }
    }

    fun elementAtOrNull(index: Int): T? {
        return when (mode) {
            IMMEDIATE_MODE -> elementAtOrNull(processed(), index)
            LAZY_MODE -> SequenceOps.elementAtOrNull(sequence(), index)
            else -> throwIllegalMode()
        }
    }

    fun indexOf(element: T): Int {
        return when (mode) {
            IMMEDIATE_MODE -> indexOf(processed(), element)
            LAZY_MODE -> SequenceOps.indexOf(sequence(), element)
            else -> throwIllegalMode()
        }
    }

    fun indexOf(predicate: (T) -> Boolean): Int {
        return when (mode) {
            IMMEDIATE_MODE -> indexOf(processed(), predicate)
            LAZY_MODE -> SequenceOps.indexOf(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun lastIndexOf(element: T): Int {
        return when (mode) {
            IMMEDIATE_MODE -> lastIndexOf(processed(), element)
            LAZY_MODE -> SequenceOps.lastIndexOf(sequence(), element)
            else -> throwIllegalMode()
        }
    }

    fun lastIndexOf(predicate: (T) -> Boolean): Int {
        return when (mode) {
            IMMEDIATE_MODE -> lastIndexOf(processed(), predicate)
            LAZY_MODE -> SequenceOps.lastIndexOf(sequence(), predicate)
            else -> throwIllegalMode()
        }
    }

    fun drop(n: Int): ListOps<T> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(drop(processed(), n))
            LAZY_MODE -> toListOps(SequenceOps.drop(sequence(), n))
            else -> throwIllegalMode()
        }
    }

    fun dropWhile(predicate: (T) -> Boolean): ListOps<T> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(dropWhile(processed(), predicate))
            LAZY_MODE -> toListOps(SequenceOps.dropWhile(sequence(), predicate))
            else -> throwIllegalMode()
        }
    }

    fun take(n: Int): ListOps<T> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(take(processed(), n))
            LAZY_MODE -> toListOps(SequenceOps.take(sequence(), n))
            else -> throwIllegalMode()
        }
    }

    fun takeWhile(predicate: (T) -> Boolean): ListOps<T> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(takeWhile(processed(), predicate))
            LAZY_MODE -> toListOps(SequenceOps.takeWhile(sequence(), predicate))
            else -> throwIllegalMode()
        }
    }

    fun filter(predicate: (T) -> Boolean): ListOps<T> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(filter(processed(), predicate))
            LAZY_MODE -> toListOps(SequenceOps.filter(sequence(), predicate))
            else -> throwIllegalMode()
        }
    }

    fun filterIndexed(predicate: (index: Int, T) -> Boolean): ListOps<T> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(filterIndexed(processed(), predicate))
            LAZY_MODE -> toListOps(SequenceOps.filterIndexed(sequence(), predicate))
            else -> throwIllegalMode()
        }
    }

    fun filterNotNull(): ListOps<T> {
        return filter { it != null }
    }

    fun <C : MutableCollection<in T>> filterTo(
        destination: C,
        predicate: (T) -> Boolean
    ): C {
        return when (mode) {
            IMMEDIATE_MODE -> filterTo(processed(), destination, predicate)
            LAZY_MODE -> SequenceOps.filterTo(sequence(), destination, predicate)
            else -> throwIllegalMode()
        }
    }

    fun <C : MutableCollection<in T>> filterIndexedTo(
        destination: C,
        predicate: (index: Int, T) -> Boolean
    ): C {
        return when (mode) {
            IMMEDIATE_MODE -> filterIndexedTo(processed(), destination, predicate)
            LAZY_MODE -> SequenceOps.filterIndexedTo(sequence(), destination, predicate)
            else -> throwIllegalMode()
        }
    }

    fun <C : MutableCollection<in T>> filterNotNullTo(
        destination: C
    ): C {
        return filterTo(destination) { it != null }
    }

    fun <R> map(transform: (T) -> R): ListOps<R> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(map(processed(), transform))
            LAZY_MODE -> toListOps(SequenceOps.map(sequence(), transform))
            else -> throwIllegalMode()
        }
    }

    fun <R> mapIndexed(transform: (index: Int, T) -> R): ListOps<R> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(mapIndexed(processed(), transform))
            LAZY_MODE -> toListOps(SequenceOps.mapIndexed(sequence(), transform))
            else -> throwIllegalMode()
        }
    }

    fun <R> mapNotNull(transform: (T) -> R): ListOps<R> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(mapNotNull(processed(), transform))
            LAZY_MODE -> toListOps(SequenceOps.mapNotNull(sequence(), transform))
            else -> throwIllegalMode()
        }
    }

    fun <R> mapIndexedNotNull(transform: (index: Int, T) -> R): ListOps<R> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(mapIndexedNotNull(processed(), transform))
            LAZY_MODE -> toListOps(SequenceOps.mapIndexedNotNull(sequence(), transform))
            else -> throwIllegalMode()
        }
    }

    fun <R, C : MutableCollection<in R>> mapTo(
        destination: C,
        transform: (T) -> R
    ): C {
        return when (mode) {
            IMMEDIATE_MODE -> mapTo(processed(), destination, transform)
            LAZY_MODE -> SequenceOps.mapTo(sequence(), destination, transform)
            else -> throwIllegalMode()
        }
    }

    fun <R, C : MutableCollection<in R>> mapIndexedTo(
        destination: C,
        transform: (index: Int, T) -> R
    ): C {
        return when (mode) {
            IMMEDIATE_MODE -> mapIndexedTo(processed(), destination, transform)
            LAZY_MODE -> SequenceOps.mapIndexedTo(sequence(), destination, transform)
            else -> throwIllegalMode()
        }
    }

    fun <R : Any, C : MutableCollection<in R>> mapNotNullTo(
        destination: C,
        transform: (T) -> R?
    ): C {
        return when (mode) {
            IMMEDIATE_MODE -> mapNotNullTo(processed(), destination, transform)
            LAZY_MODE -> SequenceOps.mapNotNullTo(sequence(), destination, transform)
            else -> throwIllegalMode()
        }
    }

    fun <R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(
        destination: C,
        transform: (index: Int, T) -> R?
    ): C {
        return when (mode) {
            IMMEDIATE_MODE -> mapIndexedNotNullTo(processed(), destination, transform)
            LAZY_MODE -> SequenceOps.mapIndexedNotNullTo(sequence(), destination, transform)
            else -> throwIllegalMode()
        }
    }

    fun <R> flatMap(transform: (T) -> Iterable<R>): ListOps<R> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(flatMap(processed(), transform))
            LAZY_MODE -> toListOps(SequenceOps.flatMap(sequence(), transform))
            else -> throwIllegalMode()
        }
    }

    fun <R> flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): ListOps<R> {
        return when (mode) {
            IMMEDIATE_MODE -> toListOps(flatMapIndexed(processed(), transform))
            LAZY_MODE -> toListOps(SequenceOps.flatMapIndexed(sequence(), transform))
            else -> throwIllegalMode()
        }
    }

    fun <R, C : MutableCollection<in R>> flatMapTo(
        destination: C,
        transform: (T) -> Iterable<R>
    ): C {
        return when (mode) {
            IMMEDIATE_MODE -> flatMapTo(processed(), destination, transform)
            LAZY_MODE -> SequenceOps.flatMapTo(sequence(), destination, transform)
            else -> throwIllegalMode()
        }
    }

    fun <R, C : MutableCollection<in R>> flatMapIndexedTo(
        destination: C,
        transform: (index: Int, T) -> Iterable<R>
    ): C {
        return when (mode) {
            IMMEDIATE_MODE -> flatMapIndexedTo(processed(), destination, transform)
            LAZY_MODE -> SequenceOps.flatMapIndexedTo(sequence(), destination, transform)
            else -> throwIllegalMode()
        }
    }

    fun reduce(operation: (T, T) -> T): T {
        return when (mode) {
            IMMEDIATE_MODE -> reduce(processed(), operation)
            LAZY_MODE -> SequenceOps.reduce(sequence(), operation)
            else -> throwIllegalMode()
        }
    }

    fun reduceIndexed(operation: (index: Int, T, T) -> T): T {
        return when (mode) {
            IMMEDIATE_MODE -> reduceIndexed(processed(), operation)
            LAZY_MODE -> SequenceOps.reduceIndexed(sequence(), operation)
            else -> throwIllegalMode()
        }
    }

    fun reduceOrNull(operation: (T, T) -> T): T? {
        return when (mode) {
            IMMEDIATE_MODE -> reduceOrNull(processed(), operation)
            LAZY_MODE -> SequenceOps.reduceOrNull(sequence(), operation)
            else -> throwIllegalMode()
        }
    }

    fun reduceIndexedOrNull(operation: (index: Int, T, T) -> T): T? {
        return when (mode) {
            IMMEDIATE_MODE -> reduceIndexedOrNull(processed(), operation)
            LAZY_MODE -> SequenceOps.reduceIndexedOrNull(sequence(), operation)
            else -> throwIllegalMode()
        }
    }

    fun <R> reduce(
        initial: R,
        operation: (R, T) -> R
    ): R {
        return when (mode) {
            IMMEDIATE_MODE -> reduce(processed(), initial, operation)
            LAZY_MODE -> SequenceOps.reduce(sequence(), initial, operation)
            else -> throwIllegalMode()
        }
    }

    fun <R> reduceIndexed(
        initial: R,
        operation: (index: Int, R, T) -> R
    ): R {
        return when (mode) {
            IMMEDIATE_MODE -> reduceIndexed(processed(), initial, operation)
            LAZY_MODE -> SequenceOps.reduceIndexed(sequence(), initial, operation)
            else -> throwIllegalMode()
        }
    }

    fun <K, V> associate(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return when (mode) {
            IMMEDIATE_MODE -> toMapOps(associate(processed(), keySelector, valueTransform))
            LAZY_MODE -> toMapOps(SequenceOps.associate(sequence(), keySelector, valueTransform))
            else -> throwIllegalMode()
        }
    }

    fun <K, V> associate(transform: (T) -> Pair<K, V>): MapOps<K, V> {
        return when (mode) {
            IMMEDIATE_MODE -> toMapOps(associate(processed(), transform))
            LAZY_MODE -> toMapOps(SequenceOps.associate(sequence(), transform))
            else -> throwIllegalMode()
        }
    }

    fun <K> associateKey(keySelector: (T) -> K): MapOps<K, T> {
        return when (mode) {
            IMMEDIATE_MODE -> toMapOps(associateKey(processed(), keySelector))
            LAZY_MODE -> toMapOps(SequenceOps.associateKey(sequence(), keySelector))
            else -> throwIllegalMode()
        }
    }

    fun <V> associateValue(valueSelector: (T) -> V): MapOps<T, V> {
        return when (mode) {
            IMMEDIATE_MODE -> toMapOps(associateValue(processed(), valueSelector))
            LAZY_MODE -> toMapOps(SequenceOps.associateValue(sequence(), valueSelector))
            else -> throwIllegalMode()
        }
    }

    fun <K, V> associateWithNext(
        keySelector: (T) -> K,
        valueTransform: (T?) -> V
    ): MapOps<K, V> {
        return when (mode) {
            IMMEDIATE_MODE -> toMapOps(associateWithNext(processed(), keySelector, valueTransform))
            LAZY_MODE -> toMapOps(SequenceOps.associateWithNext(sequence(), keySelector, valueTransform))
            else -> throwIllegalMode()
        }
    }

    fun <K, V> associateWithNext(transform: (T, T?) -> Pair<K, V>): MapOps<K, V> {
        return when (mode) {
            IMMEDIATE_MODE -> toMapOps(associateWithNext(processed(), transform))
            LAZY_MODE -> toMapOps(SequenceOps.associateWithNext(sequence(), transform))
            else -> throwIllegalMode()
        }
    }

    fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return when (mode) {
            IMMEDIATE_MODE -> associateTo(processed(), destination, keySelector, valueTransform)
            LAZY_MODE -> SequenceOps.associateTo(sequence(), destination, keySelector, valueTransform)
            else -> throwIllegalMode()
        }
    }

    fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        transform: (T) -> Pair<K, V>
    ): M {
        return when (mode) {
            IMMEDIATE_MODE -> associateTo(processed(), destination, transform)
            LAZY_MODE -> SequenceOps.associateTo(sequence(), destination, transform)
            else -> throwIllegalMode()
        }
    }

    fun <K, M : MutableMap<in K, in T>> associateKeyTo(
        destination: M,
        keySelector: (T) -> K
    ): M {
        return when (mode) {
            IMMEDIATE_MODE -> associateKeyTo(processed(), destination, keySelector)
            LAZY_MODE -> SequenceOps.associateKeyTo(sequence(), destination, keySelector)
            else -> throwIllegalMode()
        }
    }

    fun <V, M : MutableMap<in T, in V>> associateValueTo(
        destination: M,
        valueSelector: (T) -> V
    ): M {
        return when (mode) {
            IMMEDIATE_MODE -> associateValueTo(processed(), destination, valueSelector)
            LAZY_MODE -> SequenceOps.associateValueTo(sequence(), destination, valueSelector)
            else -> throwIllegalMode()
        }
    }

    fun <K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T?) -> V
    ): M {
        val iterator = processed().iterator()
        while (iterator.hasNext()) {
            val tk = iterator.next()
            val k = keySelector(tk)
            if (iterator.hasNext()) {
                val tv = iterator.next()
                val v = valueTransform(tv)
                destination.put(k, v)
            } else {
                val v = valueTransform(null)
                destination.put(k, v)
                break
            }
        }
        return destination
    }

    fun <K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        destination: M,
        transform: (T, T?) -> Pair<K, V>
    ): M {
        val iterator = processed().iterator()
        while (iterator.hasNext()) {
            val tk = iterator.next()
            if (iterator.hasNext()) {
                val tv = iterator.next()
                val pair = transform(tk, tv)
                destination.put(pair.first, pair.second)
            } else {
                val pair = transform(tk, null)
                destination.put(pair.first, pair.second)
                break
            }
        }
        return destination
    }

    fun <K> groupBy(keySelector: (T) -> K): Map<K, List<T>> {
        return processed().groupBy(keySelector)
    }

    fun <K, V> groupBy(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): Map<K, List<V>> {
        return processed().groupBy(keySelector, valueTransform)
    }

    fun <K, M : MutableMap<in K, MutableList<T>>> groupByTo(
        destination: M,
        keySelector: (T) -> K
    ): M {
        return processed().groupByTo(destination, keySelector)
    }

    fun <K, V, M : MutableMap<in K, MutableList<V>>> groupByTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return processed().groupByTo(destination, keySelector, valueTransform)
    }

    fun chunked(size: Int): List<List<T>> {
        return processed().chunked(size)
    }

    fun <R> chunked(
        size: Int,
        transform: (List<T>) -> R
    ): List<R> {
        return processed().chunked(size, transform)
    }

    fun windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false
    ): List<List<T>> {
        return processed().windowed(size, step, partialWindows)
    }

    fun <R> windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): List<R> {
        return processed().windowed(size, step, partialWindows, transform)
    }

    fun <R, V> zip(
        other: Array<out R>,
        transform: (T, R) -> V
    ): List<V> {
        return processed().zip(other, transform)
    }

    fun <R, V> zip(
        other: Iterable<R>,
        transform: (T, R) -> V
    ): List<V> {
        return processed().zip(other, transform)
    }

    fun <R> zipWithNext(transform: (T, T) -> R): List<R> {
        return processed().zipWithNext(transform)
    }

    fun max(): T {
        return max(Sort.selfComparableComparator())
    }

    fun maxOrNull(): T? {
        return maxOrNull(Sort.selfComparableComparator())
    }

    fun max(comparator: Comparator<in T>): T {
        return Require.notNull(maxOrNull(comparator))
    }

    fun maxOrNull(comparator: Comparator<in T>): T? {
        return processed().maxWithOrNull(comparator)
    }

    fun min(): T {
        return min(Sort.selfComparableComparator())
    }

    fun minOrNull(): T? {
        return minOrNull(Sort.selfComparableComparator())
    }

    fun min(comparator: Comparator<in T>): T {
        return Require.notNull(minOrNull(comparator))
    }

    fun minOrNull(comparator: Comparator<in T>): T? {
        return processed().minWithOrNull(comparator)
    }

    fun sumInt(): Int {
        return sumInt { To.toInt(it) }
    }

    fun sumLong(): Long {
        return sumLong { To.toLong(it) }
    }

    fun sumDouble(): Double {
        return sumDouble { To.toDouble(it) }
    }

    fun sumBigInteger(): BigInteger {
        return sumBigInteger { To.toBigInteger(it) }
    }

    fun sumBigDecimal(): BigDecimal {
        return sumBigDecimal { To.toBigDecimal(it) }
    }

    fun sumInt(selector: (T) -> Int): Int {
        return processed().sumOf(selector)
    }

    fun sumLong(selector: (T) -> Long): Long {
        return processed().sumOf(selector)
    }

    fun sumDouble(selector: (T) -> Double): Double {
        return processed().sumOf(selector)
    }

    fun sumBigInteger(selector: (T) -> BigInteger): BigInteger {
        return processed().sumOf(selector)
    }

    fun sumBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
        return processed().sumOf(selector)
    }

    fun intersect(other: Iterable<T>): Set<T> {
        return processed().intersect(other)
    }

    fun union(other: Iterable<T>): Set<T> {
        return processed().union(other)
    }

    fun subtract(other: Iterable<T>): Set<T> {
        return processed().subtract(other)
    }

    fun reversed(): List<T> {
        return processed().reversed()
    }

    fun sorted(): List<T> {
        return sorted(Sort.selfComparableComparator())
    }

    fun sorted(comparator: Comparator<in T>): List<T> {
        return processed().sortedWith(comparator)
    }

    fun shuffled(): List<T> {
        return processed().shuffled()
    }

    fun shuffled(random: Random): List<T> {
        return processed().shuffled(random)
    }

    fun distinct(): List<T> {
        return processed().distinct()
    }

    fun <K> distinct(selector: (T) -> K): List<T> {
        return processed().distinctBy(selector)
    }

    fun forEachIndexed(action: (index: Int, T) -> Unit) {
        return processed().forEachIndexed(action)
    }

    fun removeAll(predicate: (T) -> Boolean): Boolean {
        return mutableProcessed().removeAll(predicate)
    }

    fun removeFirst(): T {
        val iterator = mutableProcessed().iterator()
        Check.checkElement(iterator.hasNext(), "Iterable is empty.")
        val first = iterator.next()
        iterator.remove()
        return first
    }

    fun removeFirstOrNull(): T? {
        val iterator = mutableProcessed().iterator()
        if (!iterator.hasNext()) {
            return null
        }
        val first = iterator.next()
        iterator.remove()
        return first
    }

    fun removeLast(): T {
        val iterator = mutableProcessed().iterator()
        Check.checkElement(iterator.hasNext(), "Iterable is empty.")
        var last = iterator.next()
        while (iterator.hasNext()) {
            last = iterator.next()
        }
        iterator.remove()
        return last
    }

    fun removeLastOrNull(): T? {
        val iterator = mutableProcessed().iterator()
        if (!iterator.hasNext()) {
            return null
        }
        var last = iterator.next()
        while (iterator.hasNext()) {
            last = iterator.next()
        }
        iterator.remove()
        return last
    }

    fun retainAll(predicate: (T) -> Boolean): Boolean {
        return mutableProcessed().retainAll(predicate)
    }

    fun plus(element: T): List<T> {
        return processed().plus(element)
    }

    fun plus(elements: Array<out T>): List<T> {
        return processed().plus(elements)
    }

    fun plus(elements: Iterable<T>): List<T> {
        return processed().plus(elements)
    }

    fun minus(element: T): List<T> {
        return processed().minus(element)
    }

    fun minus(elements: Array<out T>): List<T> {
        return processed().minus(elements)
    }

    fun minus(elements: Iterable<T>): List<T> {
        return processed().minus(elements)
    }

    fun <C : MutableCollection<in T>> toCollection(destination: C): C {
        return processed().toCollection(destination)
    }

    fun toSet(): Set<T> {
        return processed().toSet()
    }

    fun toMutableSet(): MutableSet<T> {
        return processed().toMutableSet()
    }

    fun toHashSet(): HashSet<T> {
        return processed().toHashSet()
    }

    fun toSortedSet(): SortedSet<T> {
        return toSortedSet(Sort.selfComparableComparator())
    }

    fun toSortedSet(comparator: Comparator<in T>): SortedSet<T> {
        return processed().toSortedSet(comparator)
    }

    fun toList(): List<T> {
        return processed().toList()
    }

    fun toMutableList(): MutableList<T> {
        return processed().toMutableList()
    }

    fun toStream(): Stream<T> {
        return toStream(false)
    }

    fun toStream(parallel: Boolean): Stream<T> {
        return StreamSupport.stream(processed().spliterator(), parallel)
    }

    fun toArray(): Array<Any?> {
        val list = toList()
        val result = arrayOfNulls<Any?>(list.size)
        list.forEachIndexed { i, t -> result[i] = t }
        return result
    }

    fun toArray(generator: (size: Int) -> Array<T>): Array<T> {
        val list = toList()
        val result = generator(list.size)
        list.forEachIndexed { i, t -> result[i] = t }
        return result
    }

    fun toBooleanArray(): BooleanArray {
        return toBooleanArray { To.toBoolean(it) }
    }

    fun toBooleanArray(selector: (T) -> Boolean): BooleanArray {
        val list = toList()
        val result = BooleanArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    fun toByteArray(): ByteArray {
        return toByteArray { To.toByte(it) }
    }

    fun toByteArray(selector: (T) -> Byte): ByteArray {
        val list = toList()
        val result = ByteArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    fun toShortArray(): ShortArray {
        return toShortArray { To.toShort(it) }
    }

    fun toShortArray(selector: (T) -> Short): ShortArray {
        val list = toList()
        val result = ShortArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    fun toCharArray(): CharArray {
        return toCharArray { To.toChar(it) }
    }

    fun toCharArray(selector: (T) -> Char): CharArray {
        val list = toList()
        val result = CharArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    fun toIntArray(): IntArray {
        return toIntArray { To.toInt(it) }
    }

    fun toIntArray(selector: (T) -> Int): IntArray {
        val list = toList()
        val result = IntArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    fun toLongArray(): LongArray {
        return toLongArray { To.toLong(it) }
    }

    fun toLongArray(selector: (T) -> Long): LongArray {
        val list = toList()
        val result = LongArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    fun toFloatArray(): FloatArray {
        return toFloatArray { To.toFloat(it) }
    }

    fun toFloatArray(selector: (T) -> Float): FloatArray {
        val list = toList()
        val result = FloatArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    fun toDoubleArray(): DoubleArray {
        return toDoubleArray { To.toDouble(it) }
    }

    fun toDoubleArray(selector: (T) -> Double): DoubleArray {
        val list = toList()
        val result = DoubleArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    protected fun throwIllegalMode(): Nothing {
        throw IllegalStateException("Wrong mode: $mode.")
    }

    protected fun processed(): I {
        return As.notNull(processed)
    }

    protected fun mutableProcessed(): MI {
        return As.notNull(processed)
    }

    protected fun sequence(): Sequence<T> {
        return As.notNull(sequence)
    }

    protected fun asThis(): THIS {
        return As.any(this)
    }

    companion object {

        private const val IMMEDIATE_MODE = 0
        private const val LAZY_MODE = 1

        @JvmStatic
        inline fun <T> find(iterable: Iterable<T>, predicate: (T) -> Boolean): T? {
            return iterable.find(predicate)
        }

        @JvmStatic
        inline fun <T> findLast(iterable: Iterable<T>, predicate: (T) -> Boolean): T? {
            return iterable.findLast(predicate)
        }

        @JvmStatic
        fun <T> first(iterable: Iterable<T>): T {
            return iterable.first()
        }

        @JvmStatic
        inline fun <T> first(iterable: Iterable<T>, predicate: (T) -> Boolean): T {
            return iterable.first(predicate)
        }

        @JvmStatic
        fun <T> firstOrNull(iterable: Iterable<T>): T? {
            return iterable.firstOrNull()
        }

        @JvmStatic
        inline fun <T> firstOrNull(iterable: Iterable<T>, predicate: (T) -> Boolean): T? {
            return iterable.firstOrNull(predicate)
        }

        @JvmStatic
        fun <T> last(iterable: Iterable<T>): T {
            return iterable.last()
        }

        @JvmStatic
        inline fun <T> last(iterable: Iterable<T>, predicate: (T) -> Boolean): T {
            return iterable.last(predicate)
        }

        @JvmStatic
        fun <T> lastOrNull(iterable: Iterable<T>): T? {
            return iterable.lastOrNull()
        }

        @JvmStatic
        inline fun <T> lastOrNull(iterable: Iterable<T>, predicate: (T) -> Boolean): T? {
            return iterable.lastOrNull(predicate)
        }

        @JvmStatic
        inline fun <T> all(iterable: Iterable<T>, predicate: (T) -> Boolean): Boolean {
            return iterable.all(predicate)
        }

        @JvmStatic
        fun <T> any(iterable: Iterable<T>): Boolean {
            return iterable.any()
        }

        @JvmStatic
        inline fun <T> any(iterable: Iterable<T>, predicate: (T) -> Boolean): Boolean {
            return iterable.any(predicate)
        }

        @JvmStatic
        fun <T> none(iterable: Iterable<T>): Boolean {
            return iterable.none()
        }

        @JvmStatic
        inline fun <T> none(iterable: Iterable<T>, predicate: (T) -> Boolean): Boolean {
            return iterable.none(predicate)
        }

        @JvmStatic
        fun <T> single(iterable: Iterable<T>): T {
            return iterable.single()
        }

        @JvmStatic
        inline fun <T> single(iterable: Iterable<T>, predicate: (T) -> Boolean): T {
            return iterable.single(predicate)
        }

        @JvmStatic
        fun <T> singleOrNull(iterable: Iterable<T>): T? {
            return iterable.singleOrNull()
        }

        @JvmStatic
        inline fun <T> singleOrNull(iterable: Iterable<T>, predicate: (T) -> Boolean): T? {
            return iterable.singleOrNull(predicate)
        }

        @JvmStatic
        fun <T> contains(iterable: Iterable<T>, element: T): Boolean {
            return iterable.contains(element)
        }

        @JvmStatic
        fun <T> count(iterable: Iterable<T>): Int {
            return iterable.count()
        }

        @JvmStatic
        inline fun <T> count(iterable: Iterable<T>, predicate: (T) -> Boolean): Int {
            return iterable.count(predicate)
        }

        @JvmStatic
        fun <T> elementAt(iterable: Iterable<T>, index: Int): T {
            return iterable.elementAt(index)
        }

        @JvmStatic
        fun <T> elementAtOrElse(
            iterable: Iterable<T>,
            index: Int,
            defaultValue: (index: Int) -> T
        ): T {
            return iterable.elementAtOrElse(index, defaultValue)
        }

        @JvmStatic
        fun <T> elementAtOrNull(iterable: Iterable<T>, index: Int): T? {
            return iterable.elementAtOrNull(index)
        }

        @JvmStatic
        fun <T> indexOf(iterable: Iterable<T>, element: T): Int {
            return iterable.indexOf(element)
        }

        @JvmStatic
        inline fun <T> indexOf(iterable: Iterable<T>, predicate: (T) -> Boolean): Int {
            return iterable.indexOfFirst(predicate)
        }

        @JvmStatic
        fun <T> lastIndexOf(iterable: Iterable<T>, element: T): Int {
            return iterable.lastIndexOf(element)
        }

        @JvmStatic
        inline fun <T> lastIndexOf(iterable: Iterable<T>, predicate: (T) -> Boolean): Int {
            return iterable.indexOfLast(predicate)
        }

        @JvmStatic
        fun <T> drop(iterable: Iterable<T>, n: Int): List<T> {
            return iterable.drop(n)
        }

        @JvmStatic
        inline fun <T> dropWhile(iterable: Iterable<T>, predicate: (T) -> Boolean): List<T> {
            return iterable.dropWhile(predicate)
        }

        @JvmStatic
        fun <T> take(iterable: Iterable<T>, n: Int): List<T> {
            return iterable.take(n)
        }

        @JvmStatic
        inline fun <T> takeWhile(iterable: Iterable<T>, predicate: (T) -> Boolean): List<T> {
            return iterable.takeWhile(predicate)
        }

        @JvmStatic
        inline fun <T> filter(iterable: Iterable<T>, predicate: (T) -> Boolean): List<T> {
            return iterable.filter(predicate)
        }

        @JvmStatic
        inline fun <T> filterIndexed(iterable: Iterable<T>, predicate: (index: Int, T) -> Boolean): List<T> {
            return iterable.filterIndexed(predicate)
        }

        @JvmStatic
        fun <T : Any> filterNotNull(iterable: Iterable<T?>): List<T> {
            return iterable.filterNotNull()
        }

        @JvmStatic
        inline fun <T, C : MutableCollection<in T>> filterTo(
            iterable: Iterable<T>,
            destination: C,
            predicate: (T) -> Boolean
        ): C {
            return iterable.filterTo(destination, predicate)
        }

        @JvmStatic
        inline fun <T, C : MutableCollection<in T>> filterIndexedTo(
            iterable: Iterable<T>,
            destination: C,
            predicate: (index: Int, T) -> Boolean
        ): C {
            return iterable.filterIndexedTo(destination, predicate)
        }

        @JvmStatic
        fun <C : MutableCollection<in T>, T : Any> filterNotNullTo(
            iterable: Iterable<T?>,
            destination: C
        ): C {
            return iterable.filterNotNullTo(destination)
        }

        @JvmStatic
        inline fun <T, R> map(iterable: Iterable<T>, transform: (T) -> R): List<R> {
            return iterable.map(transform)
        }

        @JvmStatic
        inline fun <T, R> mapIndexed(iterable: Iterable<T>, transform: (index: Int, T) -> R): List<R> {
            return iterable.mapIndexed(transform)
        }

        @JvmStatic
        inline fun <T, R> mapNotNull(iterable: Iterable<T>, transform: (T) -> R): List<R> {
            return iterable.mapNotNull(transform)
        }

        @JvmStatic
        inline fun <T, R> mapIndexedNotNull(iterable: Iterable<T>, transform: (index: Int, T) -> R): List<R> {
            return iterable.mapIndexedNotNull(transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> mapTo(
            iterable: Iterable<T>,
            destination: C,
            transform: (T) -> R
        ): C {
            return iterable.mapTo(destination, transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> mapIndexedTo(
            iterable: Iterable<T>,
            destination: C,
            transform: (index: Int, T) -> R
        ): C {
            return iterable.mapIndexedTo(destination, transform)
        }

        @JvmStatic
        inline fun <T, R : Any, C : MutableCollection<in R>> mapNotNullTo(
            iterable: Iterable<T>,
            destination: C,
            transform: (T) -> R?
        ): C {
            return iterable.mapNotNullTo(destination, transform)
        }

        @JvmStatic
        inline fun <T, R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(
            iterable: Iterable<T>,
            destination: C,
            transform: (index: Int, T) -> R?
        ): C {
            return iterable.mapIndexedNotNullTo(destination, transform)
        }

        @JvmStatic
        inline fun <T, R> flatMap(iterable: Iterable<T>, transform: (T) -> Iterable<R>): List<R> {
            return iterable.flatMap(transform)
        }

        @JvmStatic
        inline fun <T, R> flatMapIndexed(iterable: Iterable<T>, transform: (index: Int, T) -> Iterable<R>): List<R> {
            return iterable.flatMapIndexed(transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> flatMapTo(
            iterable: Iterable<T>,
            destination: C,
            transform: (T) -> Iterable<R>
        ): C {
            return iterable.flatMapTo(destination, transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> flatMapIndexedTo(
            iterable: Iterable<T>,
            destination: C,
            transform: (index: Int, T) -> Iterable<R>
        ): C {
            return iterable.flatMapIndexedTo(destination, transform)
        }

        @JvmStatic
        inline fun <S, T : S> reduce(iterable: Iterable<T>, operation: (S, T) -> S): S {
            return iterable.reduce(operation)
        }

        @JvmStatic
        inline fun <S, T : S> reduceIndexed(iterable: Iterable<T>, operation: (index: Int, S, T) -> S): S {
            return iterable.reduceIndexed(operation)
        }

        @JvmStatic
        inline fun <S, T : S> reduceOrNull(iterable: Iterable<T>, operation: (S, T) -> S): S? {
            return iterable.reduceOrNull(operation)
        }

        @JvmStatic
        inline fun <S, T : S> reduceIndexedOrNull(iterable: Iterable<T>, operation: (index: Int, S, T) -> S): S? {
            return iterable.reduceIndexedOrNull(operation)
        }

        @JvmStatic
        inline fun <T, R> reduce(
            iterable: Iterable<T>,
            initial: R,
            operation: (R, T) -> R
        ): R {
            return iterable.fold(initial, operation)
        }

        @JvmStatic
        inline fun <T, R> reduceIndexed(
            iterable: Iterable<T>,
            initial: R,
            operation: (index: Int, R, T) -> R
        ): R {
            return iterable.foldIndexed(initial, operation)
        }

        @JvmStatic
        inline fun <T, K, V> associate(
            iterable: Iterable<T>,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return iterable.associateBy(keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> associate(iterable: Iterable<T>, transform: (T) -> Pair<K, V>): Map<K, V> {
            return iterable.associate(transform)
        }

        @JvmStatic
        inline fun <T, K> associateKey(iterable: Iterable<T>, keySelector: (T) -> K): Map<K, T> {
            return iterable.associateBy(keySelector)
        }

        @JvmStatic
        inline fun <T, V> associateValue(iterable: Iterable<T>, valueSelector: (T) -> V): Map<T, V> {
            return iterable.associateWith(valueSelector)
        }

        @JvmStatic
        inline fun <T, K, V> associateWithNext(
            iterable: Iterable<T>,
            keySelector: (T) -> K,
            valueTransform: (T?) -> V
        ): Map<K, V> {
            return associateWithNextTo(iterable, LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> associateWithNext(iterable: Iterable<T>, transform: (T, T?) -> Pair<K, V>): Map<K, V> {
            return associateWithNextTo(iterable, LinkedHashMap(), transform)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> associateTo(
            iterable: Iterable<T>,
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): M {
            return iterable.associateByTo(destination, keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> associateTo(
            iterable: Iterable<T>,
            destination: M,
            transform: (T) -> Pair<K, V>
        ): M {
            return iterable.associateTo(destination, transform)
        }

        @JvmStatic
        inline fun <T, K, M : MutableMap<in K, in T>> associateKeyTo(
            iterable: Iterable<T>,
            destination: M,
            keySelector: (T) -> K
        ): M {
            return iterable.associateByTo(destination, keySelector)
        }

        @JvmStatic
        inline fun <T, V, M : MutableMap<in T, in V>> associateValueTo(
            iterable: Iterable<T>,
            destination: M,
            valueSelector: (T) -> V
        ): M {
            return iterable.associateWithTo(destination, valueSelector)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> associateWithNextTo(
            iterable: Iterable<T>,
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T?) -> V
        ): M {
            val iterator = iterable.iterator()
            while (iterator.hasNext()) {
                val tk = iterator.next()
                val k = keySelector(tk)
                if (iterator.hasNext()) {
                    val tv = iterator.next()
                    val v = valueTransform(tv)
                    destination.put(k, v)
                } else {
                    val v = valueTransform(null)
                    destination.put(k, v)
                    break
                }
            }
            return destination
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> associateWithNextTo(
            iterable: Iterable<T>,
            destination: M,
            transform: (T, T?) -> Pair<K, V>
        ): M {
            val iterator = iterable.iterator()
            while (iterator.hasNext()) {
                val tk = iterator.next()
                if (iterator.hasNext()) {
                    val tv = iterator.next()
                    val pair = transform(tk, tv)
                    destination.put(pair.first, pair.second)
                } else {
                    val pair = transform(tk, null)
                    destination.put(pair.first, pair.second)
                    break
                }
            }
            return destination
        }

        @JvmStatic
        inline fun <T, K> groupBy(iterable: Iterable<T>, keySelector: (T) -> K): Map<K, List<T>> {
            return iterable.groupBy(keySelector)
        }

        @JvmStatic
        inline fun <T, K, V> groupBy(
            iterable: Iterable<T>,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, List<V>> {
            return iterable.groupBy(keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, M : MutableMap<in K, MutableList<T>>> groupByTo(
            iterable: Iterable<T>,
            destination: M,
            keySelector: (T) -> K
        ): M {
            return iterable.groupByTo(destination, keySelector)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> groupByTo(
            iterable: Iterable<T>,
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): M {
            return iterable.groupByTo(destination, keySelector, valueTransform)
        }

        @JvmStatic
        fun <T> chunked(iterable: Iterable<T>, size: Int): List<List<T>> {
            return iterable.chunked(size)
        }

        @JvmStatic
        fun <T, R> chunked(
            iterable: Iterable<T>,
            size: Int,
            transform: (List<T>) -> R
        ): List<R> {
            return iterable.chunked(size, transform)
        }

        @JvmStatic
        fun <T> windowed(
            iterable: Iterable<T>,
            size: Int,
            step: Int = 1,
            partialWindows: Boolean = false
        ): List<List<T>> {
            return iterable.windowed(size, step, partialWindows)
        }

        @JvmStatic
        fun <T, R> windowed(
            iterable: Iterable<T>,
            size: Int,
            step: Int = 1,
            partialWindows: Boolean = false,
            transform: (List<T>) -> R
        ): List<R> {
            return iterable.windowed(size, step, partialWindows, transform)
        }

        @JvmStatic
        inline fun <T, R, V> zip(
            iterable: Iterable<T>,
            other: Array<out R>,
            transform: (T, R) -> V
        ): List<V> {
            return iterable.zip(other, transform)
        }

        @JvmStatic
        inline fun <T, R, V> zip(
            iterable: Iterable<T>,
            other: Iterable<R>,
            transform: (T, R) -> V
        ): List<V> {
            return iterable.zip(other, transform)
        }

        @JvmStatic
        inline fun <T, R> zipWithNext(iterable: Iterable<T>, transform: (T, T) -> R): List<R> {
            return iterable.zipWithNext(transform)
        }

        @JvmStatic
        fun <T : Comparable<T>> max(iterable: Iterable<T>): T {
            return Require.notNull(maxOrNull(iterable))
        }

        @JvmStatic
        fun <T> max(iterable: Iterable<T>, comparator: Comparator<in T>): T {
            return Require.notNull(maxOrNull(iterable, comparator))
        }

        @JvmStatic
        fun <T : Comparable<T>> maxOrNull(iterable: Iterable<T>): T? {
            return iterable.maxOrNull()
        }

        @JvmStatic
        fun <T> maxOrNull(iterable: Iterable<T>, comparator: Comparator<in T>): T? {
            return iterable.maxWithOrNull(comparator)
        }

        @JvmStatic
        fun <T : Comparable<T>> min(iterable: Iterable<T>): T {
            return Require.notNull(minOrNull(iterable))
        }

        @JvmStatic
        fun <T> min(iterable: Iterable<T>, comparator: Comparator<in T>): T {
            return Require.notNull(minOrNull(iterable, comparator))
        }

        @JvmStatic
        fun <T : Comparable<T>> minOrNull(iterable: Iterable<T>): T? {
            return iterable.minOrNull()
        }

        @JvmStatic
        fun <T> minOrNull(iterable: Iterable<T>, comparator: Comparator<in T>): T? {
            return iterable.minWithOrNull(comparator)
        }

        @JvmStatic
        fun <T> sumInt(iterable: Iterable<T>): Int {
            return sumInt(iterable) { To.toInt(it) }
        }

        @JvmStatic
        inline fun <T> sumInt(iterable: Iterable<T>, selector: (T) -> Int): Int {
            return iterable.sumOf(selector)
        }

        @JvmStatic
        fun <T> sumLong(iterable: Iterable<T>): Long {
            return sumLong(iterable) { To.toLong(it) }
        }

        @JvmStatic
        inline fun <T> sumLong(iterable: Iterable<T>, selector: (T) -> Long): Long {
            return iterable.sumOf(selector)
        }

        @JvmStatic
        fun <T> sumDouble(iterable: Iterable<T>): Double {
            return sumDouble(iterable) { To.toDouble(it) }
        }

        @JvmStatic
        fun <T> sumDouble(iterable: Iterable<T>, selector: (T) -> Double): Double {
            return iterable.sumOf(selector)
        }

        @JvmStatic
        fun <T> sumBigInteger(iterable: Iterable<T>): BigInteger {
            return sumBigInteger(iterable) { To.toBigInteger(it) }
        }

        @JvmStatic
        fun <T> sumBigInteger(iterable: Iterable<T>, selector: (T) -> BigInteger): BigInteger {
            return iterable.sumOf(selector)
        }

        @JvmStatic
        fun <T> sumBigDecimal(iterable: Iterable<T>): BigDecimal {
            return sumBigDecimal(iterable) { To.toBigDecimal(it) }
        }

        @JvmStatic
        fun <T> sumBigDecimal(iterable: Iterable<T>, selector: (T) -> BigDecimal): BigDecimal {
            return iterable.sumOf(selector)
        }

        @JvmStatic
        fun <T> intersect(iterable: Iterable<T>, other: Iterable<T>): Set<T> {
            return iterable.intersect(other)
        }

        @JvmStatic
        fun <T> union(iterable: Iterable<T>, other: Iterable<T>): Set<T> {
            return iterable.union(other)
        }

        @JvmStatic
        fun <T> subtract(iterable: Iterable<T>, other: Iterable<T>): Set<T> {
            return iterable.subtract(other)
        }

        @JvmStatic
        fun <T> reversed(iterable: Iterable<T>): List<T> {
            return iterable.reversed()
        }

        @JvmStatic
        fun <T : Comparable<T>> sorted(iterable: Iterable<T>): List<T> {
            return iterable.sorted()
        }

        @JvmStatic
        fun <T> sorted(iterable: Iterable<T>, comparator: Comparator<in T>): List<T> {
            return iterable.sortedWith(comparator)
        }

        @JvmStatic
        fun <T> shuffled(iterable: Iterable<T>): List<T> {
            return iterable.shuffled()
        }

        @JvmStatic
        fun <T> shuffled(iterable: Iterable<T>, random: Random): List<T> {
            return iterable.shuffled(random)
        }

        @JvmStatic
        fun <T> distinct(iterable: Iterable<T>): List<T> {
            return iterable.distinct()
        }

        @JvmStatic
        inline fun <T, K> distinct(iterable: Iterable<T>, selector: (T) -> K): List<T> {
            return iterable.distinctBy(selector)
        }

        @JvmStatic
        inline fun <T> forEachIndexed(iterable: Iterable<T>, action: (index: Int, T) -> Unit) {
            return iterable.forEachIndexed(action)
        }

        @JvmStatic
        fun <T> removeAll(iterable: MutableIterable<T>): Boolean {
            val iterator = iterable.iterator()
            if (!iterator.hasNext()) {
                return false
            }
            do {
                iterator.next()
                iterator.remove()
            } while (iterator.hasNext())
            return true
        }

        @JvmStatic
        fun <T> removeAll(iterable: MutableIterable<T>, predicate: (T) -> Boolean): Boolean {
            return iterable.removeAll(predicate)
        }

        @JvmStatic
        fun <T> removeFirst(iterable: MutableIterable<T>): T {
            val iterator = iterable.iterator()
            Check.checkElement(iterator.hasNext(), "Iterable is empty.")
            val first = iterator.next()
            iterator.remove()
            return first
        }

        @JvmStatic
        fun <T> removeFirstOrNull(iterable: MutableIterable<T>): T? {
            val iterator = iterable.iterator()
            if (!iterator.hasNext()) {
                return null
            }
            val first = iterator.next()
            iterator.remove()
            return first
        }

        @JvmStatic
        fun <T> removeLast(iterable: MutableIterable<T>): T {
            val iterator = iterable.iterator()
            Check.checkElement(iterator.hasNext(), "Iterable is empty.")
            var last = iterator.next()
            while (iterator.hasNext()) {
                last = iterator.next()
            }
            iterator.remove()
            return last
        }

        @JvmStatic
        fun <T> removeLastOrNull(iterable: MutableIterable<T>): T? {
            val iterator = iterable.iterator()
            if (!iterator.hasNext()) {
                return null
            }
            var last = iterator.next()
            while (iterator.hasNext()) {
                last = iterator.next()
            }
            iterator.remove()
            return last
        }

        @JvmStatic
        fun <T> retainAll(iterable: MutableIterable<T>, predicate: (T) -> Boolean): Boolean {
            return iterable.retainAll(predicate)
        }

        @JvmStatic
        fun <T> plus(iterable: Iterable<T>, element: T): List<T> {
            return iterable.plus(element)
        }

        @JvmStatic
        fun <T> plus(iterable: Iterable<T>, elements: Array<out T>): List<T> {
            return iterable.plus(elements)
        }

        @JvmStatic
        fun <T> plus(iterable: Iterable<T>, elements: Iterable<T>): List<T> {
            return iterable.plus(elements)
        }

        @JvmStatic
        fun <T> minus(iterable: Iterable<T>, element: T): List<T> {
            return iterable.minus(element)
        }

        @JvmStatic
        fun <T> minus(iterable: Iterable<T>, elements: Array<out T>): List<T> {
            return iterable.minus(elements)
        }

        @JvmStatic
        fun <T> minus(iterable: Iterable<T>, elements: Iterable<T>): List<T> {
            return iterable.minus(elements)
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> toCollection(iterable: Iterable<T>, destination: C): C {
            return iterable.toCollection(destination)
        }

        @JvmStatic
        fun <T> toSet(iterable: Iterable<T>): Set<T> {
            return iterable.toSet()
        }

        @JvmStatic
        fun <T> toMutableSet(iterable: Iterable<T>): MutableSet<T> {
            return iterable.toMutableSet()
        }

        @JvmStatic
        fun <T> toHashSet(iterable: Iterable<T>): HashSet<T> {
            return iterable.toHashSet()
        }

        @JvmStatic
        fun <T> toSortedSet(iterable: Iterable<T>): SortedSet<T> {
            return toSortedSet(iterable, Sort.selfComparableComparator())
        }

        @JvmStatic
        fun <T> toSortedSet(iterable: Iterable<T>, comparator: Comparator<in T>): SortedSet<T> {
            return iterable.toSortedSet(comparator)
        }

        @JvmStatic
        fun <T> toList(iterable: Iterable<T>): List<T> {
            return iterable.toList()
        }

        @JvmStatic
        fun <T> toMutableList(iterable: Iterable<T>): MutableList<T> {
            return iterable.toMutableList()
        }

        @JvmStatic
        fun <T> toStream(iterable: Iterable<T>): Stream<T> {
            return toStream(iterable, false)
        }

        @JvmStatic
        fun <T> toStream(iterable: Iterable<T>, parallel: Boolean): Stream<T> {
            return StreamSupport.stream(iterable.spliterator(), parallel)
        }

        @JvmStatic
        fun <T> toSequence(iterable: Iterable<T>): Sequence<T> {
            return iterable.asSequence()
        }

        @JvmStatic
        fun <T> toArray(iterable: Iterable<T>): Array<Any?> {
            val list = toCollection(iterable, LinkedList())
            return list.toArray()
        }

        @JvmStatic
        inline fun <T> toArray(iterable: Iterable<T>, generator: (size: Int) -> Array<T>): Array<T> {
            val list = toCollection(iterable, LinkedList())
            return list.toArray(generator(list.size))
        }

        @JvmStatic
        inline fun <reified T> toTypedArray(iterable: Iterable<T>): Array<T> {
            val list = toCollection(iterable, LinkedList())
            return list.toTypedArray()
        }

        @JvmStatic
        fun <T> toBooleanArray(iterable: Iterable<T>): BooleanArray {
            return toBooleanArray(iterable) { To.toBoolean(it) }
        }

        @JvmStatic
        inline fun <T> toBooleanArray(iterable: Iterable<T>, selector: (T) -> Boolean): BooleanArray {
            val list = toCollection(iterable, LinkedList())
            val result = BooleanArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> toByteArray(iterable: Iterable<T>): ByteArray {
            return toByteArray(iterable) { To.toByte(it) }
        }

        @JvmStatic
        inline fun <T> toByteArray(iterable: Iterable<T>, selector: (T) -> Byte): ByteArray {
            val list = toCollection(iterable, LinkedList())
            val result = ByteArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> toShortArray(iterable: Iterable<T>): ShortArray {
            return toShortArray(iterable) { To.toShort(it) }
        }

        @JvmStatic
        inline fun <T> toShortArray(iterable: Iterable<T>, selector: (T) -> Short): ShortArray {
            val list = toCollection(iterable, LinkedList())
            val result = ShortArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> toCharArray(iterable: Iterable<T>): CharArray {
            return toCharArray(iterable) { To.toChar(it) }
        }

        @JvmStatic
        inline fun <T> toCharArray(iterable: Iterable<T>, selector: (T) -> Char): CharArray {
            val list = toCollection(iterable, LinkedList())
            val result = CharArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> toIntArray(iterable: Iterable<T>): IntArray {
            return toIntArray(iterable) { To.toInt(it) }
        }

        @JvmStatic
        inline fun <T> toIntArray(iterable: Iterable<T>, selector: (T) -> Int): IntArray {
            val list = toCollection(iterable, LinkedList())
            val result = IntArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> toLongArray(iterable: Iterable<T>): LongArray {
            return toLongArray(iterable) { To.toLong(it) }
        }

        @JvmStatic
        inline fun <T> toLongArray(iterable: Iterable<T>, selector: (T) -> Long): LongArray {
            val list = toCollection(iterable, LinkedList())
            val result = LongArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> toFloatArray(iterable: Iterable<T>): FloatArray {
            return toFloatArray(iterable) { To.toFloat(it) }
        }

        @JvmStatic
        inline fun <T> toFloatArray(iterable: Iterable<T>, selector: (T) -> Float): FloatArray {
            val list = toCollection(iterable, LinkedList())
            val result = FloatArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> toDoubleArray(iterable: Iterable<T>): DoubleArray {
            return toDoubleArray(iterable) { To.toDouble(it) }
        }

        @JvmStatic
        inline fun <T> toDoubleArray(iterable: Iterable<T>, selector: (T) -> Double): DoubleArray {
            val list = toCollection(iterable, LinkedList())
            val result = DoubleArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }
    }
}