package xyz.srclab.common.collection

import xyz.srclab.common.base.Require
import xyz.srclab.common.base.Sort

/**
 * Base iterable operations.
 *
 * @author sunqian
 */
interface BaseIterableOps<T, I : Iterable<T>, O : BaseIterableOps<T, I, O>> : Iterable<T> {

    fun operated(): I

    fun self(iterable: Iterable<T>): O

    fun contains(element: T): Boolean {
        return operated().contains(element)
    }

    fun find(predicate: (T) -> Boolean): T? {
        return operated().find(predicate)
    }

    fun findLast(predicate: (T) -> Boolean): T? {
        return operated().findLast(predicate)
    }

    fun first(): T {
        return operated().first()
    }

    fun first(predicate: (T) -> Boolean): T {
        return operated().first(predicate)
    }

    fun firstOrNull(): T? {
        return operated().firstOrNull()
    }

    fun firstOrNull(predicate: (T) -> Boolean): T? {
        return operated().firstOrNull(predicate)
    }

    fun last(): T {
        return operated().last()
    }

    fun last(predicate: (T) -> Boolean): T {
        return operated().last(predicate)
    }

    fun lastOrNull(): T? {
        return lastOrNull()
    }

    fun lastOrNull(predicate: (T) -> Boolean): T? {
        return operated().lastOrNull(predicate)
    }

    fun all(predicate: (T) -> Boolean): Boolean {
        return operated().all(predicate)
    }

    fun any(): Boolean {
        return operated().any()
    }

    fun any(predicate: (T) -> Boolean): Boolean {
        return operated().any(predicate)
    }

    fun none(): Boolean {
        return operated().none()
    }

    fun none(predicate: (T) -> Boolean): Boolean {
        return operated().none(predicate)
    }

    fun elementAt(index: Int): T {
        return operated().elementAt(index)
    }

    fun elementAtOrElse(index: Int, defaultValue: (Int) -> T): T {
        return operated().elementAtOrElse(index, defaultValue)
    }

    fun elementAtOrNull(index: Int): T? {
        return operated().elementAtOrNull(index)
    }

    fun indexOf(element: T): Int {
        return operated().indexOf(element)
    }

    fun lastIndexOf(element: T): Int {
        return operated().lastIndexOf(element)
    }

    fun indexOfPredicate(predicate: (T) -> Boolean): Int {
        return operated().indexOfFirst(predicate)
    }

    fun lastIndexOfPredicate(predicate: (T) -> Boolean): Int {
        return operated().indexOfLast(predicate)
    }

    fun count(): Int {
        return operated().count()
    }

    fun count(predicate: (T) -> Boolean): Int {
        return operated().count(predicate)
    }

    fun drop(n: Int): O {
        return self(operated().drop(n))
    }

    fun dropWhile(predicate: (T) -> Boolean): O {
        return self(operated().dropWhile(predicate))
    }

    fun take(n: Int): O {
        return self(operated().take(n))
    }

    fun takeWhile(predicate: (T) -> Boolean): O {
        return self(operated().takeWhile(predicate))
    }

    fun distinct(): O {
        return self(operated().distinct())
    }

    fun <U> distinct(mapper: (T) -> U): O {
        return self(operated().distinctBy(mapper))
    }

    fun filter(predicate: (T) -> Boolean): O {
        return self(operated().filter(predicate))
    }

    fun filterIndexed(predicate: (Int, T) -> Boolean): O {
        return self(operated().filterIndexed(predicate))
    }

    fun filterNot(predicate: (T) -> Boolean): O {
        return self(operated().filterNot(predicate))
    }

    fun <C : MutableCollection<in T>> filterTo(destination: C, predicate: (T) -> Boolean): C {
        return operated().filterTo(destination, predicate)
    }

    fun <C : MutableCollection<in T>> filterIndexedTo(destination: C, predicate: (Int, T) -> Boolean): C {
        return operated().filterIndexedTo(destination, predicate)
    }

    fun <C : MutableCollection<in T>> filterNotTo(destination: C, predicate: (T) -> Boolean): C {
        return operated().filterNotTo(destination, predicate)
    }

    fun filterNotNull(predicate: (T?) -> Boolean): O {
        return filter { it != null }
    }

    fun <K, V> associate(keyMapper: (T) -> K, valueMapper: (T) -> V): MapOps<K, V> {
        return MapOps.of(operated().associateBy(keyMapper, valueMapper))
    }

    fun <K> associateKey(keyMapper: (T) -> K): MapOps<K, T> {
        return MapOps.of(operated().associateBy(keyMapper))
    }

    fun <V> associateValue(valueMapper: (T) -> V): MapOps<T, V> {
        return MapOps.of(operated().associateWith(valueMapper))
    }

    fun <K, V, M : MutableMap<in K, in V>> associateTo(destination: M, keyMapper: (T) -> K, valueMapper: (T) -> V): M {
        return operated().associateByTo(destination, keyMapper, valueMapper)
    }

    fun <K, M : MutableMap<in K, in T>> associateKeyTo(destination: M, keyMapper: (T) -> K): M {
        return operated().associateByTo(destination, keyMapper)
    }

    fun <V, M : MutableMap<in T, in V>> associateValueTo(destination: M, valueMapper: (T) -> V): M {
        return operated().associateWithTo(destination, valueMapper)
    }

    fun <K, V> associateToMap(keyMapper: (T) -> K, valueMapper: (T) -> V): Map<K, V> {
        return operated().associateBy(keyMapper, valueMapper)
    }

    fun <K> associateKeyToMap(keyMapper: (T) -> K): Map<K, T> {
        return operated().associateBy(keyMapper)
    }

    fun <V> associateValueToMap(valueMapper: (T) -> V): Map<T, V> {
        return operated().associateWith(valueMapper)
    }

    fun <K> groupBy(keyMapper: (T) -> K): MapOps<K, List<T>> {
        return MapOps.of(operated().groupBy(keyMapper))
    }

    fun <K, V> groupBy(keyMapper: (T) -> K, valueMapper: (T) -> V): MapOps<K, List<V>> {
        return MapOps.of(operated().groupBy(keyMapper, valueMapper))
    }

    fun <K, M : MutableMap<in K, MutableList<T>>> groupByTo(destination: M, keyMapper: (T) -> K): M {
        return operated().groupByTo(destination, keyMapper)
    }

    fun <K, V, M : MutableMap<in K, MutableList<V>>> groupByTo(
        destination: M,
        keyMapper: (T) -> K,
        valueMapper: (T) -> V
    ): M {
        return operated().groupByTo(destination, keyMapper, valueMapper)
    }

    fun <K> groupByToMap(keyMapper: (T) -> K): Map<K, List<T>> {
        return operated().groupBy(keyMapper)
    }

    fun <K, V> groupByToMap(keyMapper: (T) -> K, valueMapper: (T) -> V): Map<K, List<V>> {
        return operated().groupBy(keyMapper, valueMapper)
    }

    infix fun intersect(other: Iterable<T>): SetOps<T> {
        return SetOps.of(operated().intersect(other))
    }

    infix fun intersectToSet(other: Iterable<T>): Set<T> {
        return operated().intersect(other)
    }

    infix fun union(other: Iterable<T>): SetOps<T> {
        return SetOps.of(operated().union(other))
    }

    infix fun unionToSet(other: Iterable<T>): Set<T> {
        return operated().union(other)
    }

    fun forEachIndexed(action: (Int, T) -> Unit) {
        operated().forEachIndexed(action)
    }

    fun max(): T {
        return max(Sort.selfComparableComparator())
    }

    fun max(comparator: Comparator<in T>): T {
        val maxOrNull = maxOrNull(comparator)
        return Require.notNull(maxOrNull, "Collection is empty.")
    }

    fun maxOrNull(): T?

    fun maxOrNull(comparator: Comparator<in T>): T?

    fun min(): T

    fun min(comparator: Comparator<in T>): T

    fun minOrNull(): T?

    fun minOrNull(comparator: Comparator<in T>): T?

    fun reduce(operation: (T, T) -> T): T {
        return operated().reduce(operation)
    }

    fun reduceIndexed(operation: (Int, T, T) -> T): T {
        return operated().reduceIndexed(operation)
    }

    fun reduceOrNull(operation: (T, T) -> T): T? {
        return operated().reduceOrNull(operation)
    }

    fun reduceIndexedOrNull(operation: (Int, T, T) -> T): T? {
        return operated().reduceIndexedOrNull(operation)
    }

    fun reversed(): O {
        return self(operated().reversed())
    }

    fun sorted(): O

    fun sorted(comparator: Comparator<in T>): O

    fun sumInt(intMapper: (T) -> Int): Int {
        return operated().sumBy(intMapper)
    }

    fun sumLong(longMapper: (T) -> Double): Double {
        return operated().sumOf(longMapper)
    }

    fun sumDouble(doubleMapper: (T) -> Double): Double {
        return operated().sumByDouble(doubleMapper)
    }

    fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): ListOps<List<T>> {
        return ListOps.of(operated().windowed(size, step, partialWindows))
    }

    fun <R> windowed(size: Int, step: Int = 1, partialWindows: Boolean = false, transform: (List<T>) -> R): ListOps<R> {
        return ListOps.of(operated().windowed(size, step, partialWindows, transform))
    }

    fun Collection<Boolean>.toBooleanArray(): BooleanArray

    fun Collection<Byte>.toByteArray(): ByteArray

    fun Collection<Char>.toCharArray(): CharArray

    fun Collection<Double>.toDoubleArray(): DoubleArray

    fun Collection<Float>.toFloatArray(): FloatArray

    fun Collection<Int>.toIntArray(): IntArray

    fun toHashSet(): HashSet<T>

    fun <T, C : MutableCollection<in T>> toCollection(destination: C): C

    fun toList(): List<T>

    fun Collection<Long>.toLongArray(): LongArray

    fun Collection<T>.toMutableList(): MutableList<T>

    fun toMutableList(): MutableList<T>

    fun toMutableSet(): MutableSet<T>

    fun toSet(): Set<T>

    fun Collection<Short>.toShortArray(): ShortArray
}