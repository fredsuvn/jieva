package xyz.srclab.common.collection

import xyz.srclab.common.base.Sort
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashSet

/**
 * Base iterable operations.
 *
 * @author sunqian
 */
interface BaseCollectionOps<T, I : Iterable<T>, O : BaseCollectionOps<T, I, O>> : Iterable<T> {

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
        return self(dropToList(n))
    }

    fun dropWhile(predicate: (T) -> Boolean): O {
        return self(dropWhileToList(predicate))
    }

    fun dropToList(n: Int): List<T> {
        return operated().drop(n)
    }

    fun dropWhileToList(predicate: (T) -> Boolean): List<T> {
        return operated().dropWhile(predicate)
    }

    fun take(n: Int): O {
        return self(takeToList(n))
    }

    fun takeWhile(predicate: (T) -> Boolean): O {
        return self(takeWhileToList(predicate))
    }

    fun takeToList(n: Int): List<T> {
        return operated().take(n)
    }

    fun takeWhileToList(predicate: (T) -> Boolean): List<T> {
        return operated().takeWhile(predicate)
    }

    fun distinct(): O {
        return self(distinctToList())
    }

    fun <U> distinct(mapper: (T) -> U): O {
        return self(distinctToList(mapper))
    }

    fun distinctToList(): List<T> {
        return operated().distinct()
    }

    fun <U> distinctToList(mapper: (T) -> U): List<T> {
        return operated().distinctBy(mapper)
    }

    fun filter(predicate: (T) -> Boolean): O {
        return self(filterToList(predicate))
    }

    fun filterIndexed(predicate: (Int, T) -> Boolean): O {
        return self(filterIndexedToList(predicate))
    }

    fun filterNot(predicate: (T) -> Boolean): O {
        return self(filterNotToList(predicate))
    }

    fun filterNotNull(): O {
        return self(filterNotNullToList())
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

    fun <C : MutableCollection<in T>> filterNotNull(destination: C): C {
        return filterTo(destination) { it != null }
    }

    fun filterToList(predicate: (T) -> Boolean): List<T> {
        return operated().filter(predicate)
    }

    fun filterIndexedToList(predicate: (Int, T) -> Boolean): List<T> {
        return operated().filterIndexed(predicate)
    }

    fun filterNotToList(predicate: (T) -> Boolean): List<T> {
        return operated().filterNot(predicate)
    }

    fun filterNotNullToList(): List<T> {
        return filterToList { it != null }
    }

    fun <K, V> associate(keyMapper: (T) -> K, valueMapper: (T) -> V): MapOps<K, V> {
        return MapOps.of(associateToMap(keyMapper, valueMapper))
    }

    fun <K> associateKey(keyMapper: (T) -> K): MapOps<K, T> {
        return MapOps.of(associateKeyToMap(keyMapper))
    }

    fun <V> associateValue(valueMapper: (T) -> V): MapOps<T, V> {
        return MapOps.of(associateValueToMap(valueMapper))
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
        return MapOps.of(groupByToMap(keyMapper))
    }

    fun <K, V> groupBy(keyMapper: (T) -> K, valueMapper: (T) -> V): MapOps<K, List<V>> {
        return MapOps.of(groupByToMap(keyMapper, valueMapper))
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

    infix fun intersect(other: Iterable<T>): SetOps<T> {
        return SetOps.of(intersectToSet(other))
    }

    infix fun intersectToSet(other: Iterable<T>): Set<T> {
        return operated().intersect(other)
    }

    infix fun union(other: Iterable<T>): SetOps<T> {
        return SetOps.of(unionToSet(other))
    }

    infix fun unionToSet(other: Iterable<T>): Set<T> {
        return operated().union(other)
    }

    fun max(): T {
        return max(Sort.selfComparableComparator())
    }

    fun max(comparator: Comparator<in T>): T {
        return operated().maxOfWith(comparator) { t -> t }
    }

    fun maxOrNull(): T? {
        return maxOrNull(Sort.selfComparableComparator())
    }

    fun maxOrNull(comparator: Comparator<in T>): T? {
        return operated().maxWithOrNull(comparator)
    }

    fun min(): T {
        return min(Sort.selfComparableComparator())
    }

    fun min(comparator: Comparator<in T>): T {
        return operated().minOfWith(comparator) { t -> t }
    }

    fun minOrNull(): T? {
        return minOrNull(Sort.selfComparableComparator())
    }

    fun minOrNull(comparator: Comparator<in T>): T? {
        return operated().minWithOrNull(comparator)
    }

    fun chunked(size: Int): ListOps<List<T>> {
        return ListOps.of(chunkedToList(size))
    }

    fun <R> chunked(size: Int, transform: (List<T>) -> R): ListOps<R> {
        return ListOps.of(chunkedToList(size, transform))
    }

    fun chunkedToList(size: Int): List<List<T>> {
        return operated().chunked(size)
    }

    fun <R> chunkedToList(size: Int, transform: (List<T>) -> R): List<R> {
        return operated().chunked(size, transform)
    }

    fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): ListOps<List<T>> {
        return ListOps.of(windowedToList(size, step, partialWindows))
    }

    fun <R> windowed(size: Int, step: Int = 1, partialWindows: Boolean = false, transform: (List<T>) -> R): ListOps<R> {
        return ListOps.of(windowedToList(size, step, partialWindows, transform))
    }

    fun windowedToList(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<T>> {
        return operated().windowed(size, step, partialWindows)
    }

    fun <R> windowedToList(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): List<R> {
        return operated().windowed(size, step, partialWindows, transform)
    }

    fun sorted(): O {
        return sorted(Sort.selfComparableComparator())
    }

    fun sorted(comparator: Comparator<in T>): O {
        return self(sortedToList(comparator))
    }

    fun sortedToList(): List<T> {
        return sortedToList(Sort.selfComparableComparator())
    }

    fun sortedToList(comparator: Comparator<in T>): List<T> {
        return operated().sortedWith(comparator)
    }

    fun reversed(): O {
        return self(reversedToList())
    }

    fun reversedToList(): List<T> {
        return operated().reversed()
    }

    fun sumInt(intMapper: (T) -> Int): Int {
        return operated().sumOf(intMapper)
    }

    fun sumLong(longMapper: (T) -> Long): Long {
        return operated().sumOf(longMapper)
    }

    fun sumDouble(doubleMapper: (T) -> Double): Double {
        return operated().sumOf(doubleMapper)
    }

    fun sumBigInteger(bigIntegerMapper: (T) -> BigInteger): BigInteger {
        return operated().sumOf(bigIntegerMapper)
    }

    fun sumBigDecimal(bigDecimalMapper: (T) -> BigDecimal): BigDecimal {
        return operated().sumOf(bigDecimalMapper)
    }

    fun forEachIndexed(action: (Int, T) -> Unit) {
        operated().forEachIndexed(action)
    }

    fun <C : MutableCollection<in T>> toCollection(destination: C): C {
        return operated().toCollection(destination)
    }

    fun toSet(): Set<T> {
        return operated().toSet()
    }

    fun toMutableSet(): MutableSet<T>{return operated().toMutableSet()}

    fun toHashSet(): HashSet<T>{return operated().toHashSet()}

    fun toSortedSet(): SortedSet<T>{return operated().toSortedSet()}

    fun toList(): List<T> {return operated().toList()}
    fun toMutableList(): MutableList<T> {return operated().toMutableList()}

    fun Collection<Boolean>.toBooleanArray(): BooleanArray

    fun Collection<Byte>.toByteArray(): ByteArray

    fun Collection<Char>.toCharArray(): CharArray

    fun Collection<Double>.toDoubleArray(): DoubleArray

    fun Collection<Float>.toFloatArray(): FloatArray

    fun Collection<Int>.toIntArray(): IntArray

    fun Collection<Long>.toLongArray(): LongArray

    fun Collection<Short>.toShortArray(): ShortArray

    companion object {
    }
}