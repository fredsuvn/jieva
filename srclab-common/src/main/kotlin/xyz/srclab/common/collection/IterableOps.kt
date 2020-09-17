package xyz.srclab.common.collection

import xyz.srclab.common.base.Check
import xyz.srclab.common.base.Require
import xyz.srclab.common.base.Sort
import xyz.srclab.common.base.To
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.collections.LinkedHashMap
import kotlin.random.Random

interface IterableOps<T> : Iterable<T> {

    fun find(predicate: (T) -> Boolean): T?

    fun findLast(predicate: (T) -> Boolean): T?

    fun first(): T

    fun first(predicate: (T) -> Boolean): T

    fun firstOrNull(): T?

    fun firstOrNull(predicate: (T) -> Boolean): T?

    fun last(): T

    fun last(predicate: (T) -> Boolean): T

    fun lastOrNull(): T?

    fun lastOrNull(predicate: (T) -> Boolean): T?

    fun all(predicate: (T) -> Boolean): Boolean

    fun any(): Boolean

    fun any(predicate: (T) -> Boolean): Boolean

    fun none(): Boolean

    fun none(predicate: (T) -> Boolean): Boolean

    fun single(): T

    fun single(predicate: (T) -> Boolean): T

    fun singleOrNull(): T?

    fun singleOrNull(predicate: (T) -> Boolean): T?

    fun contains(element: T): Boolean

    fun count(): Int

    fun count(predicate: (T) -> Boolean): Int

    fun elementAt(index: Int): T

    fun elementAtOrElse(
        index: Int,
        defaultValue: (index: Int) -> T
    ): T

    fun elementAtOrNull(index: Int): T?

    fun indexOf(element: T): Int

    fun indexOf(predicate: (T) -> Boolean): Int

    fun lastIndexOf(element: T): Int

    fun lastIndexOf(predicate: (T) -> Boolean): Int

    fun drop(n: Int): ListOps<T>

    fun dropWhile(predicate: (T) -> Boolean): ListOps<T>

    fun take(n: Int): ListOps<T>

    fun takeWhile(predicate: (T) -> Boolean): ListOps<T>

    fun filter(predicate: (T) -> Boolean): ListOps<T>

    fun filterIndexed(predicate: (index: Int, T) -> Boolean): ListOps<T>

    fun filterNotNull(): ListOps<T> {
        return filter { it != null }
    }

    fun <C : MutableCollection<in T>> filterTo(
        destination: C,
        predicate: (T) -> Boolean
    ): C

    fun <C : MutableCollection<in T>> filterIndexedTo(
        destination: C,
        predicate: (index: Int, T) -> Boolean
    ): C

    fun <C : MutableCollection<in T>> filterNotNullTo(
        destination: C
    ): C {
        return filterTo(destination) { it != null }
    }

    fun <R> map(transform: (T) -> R): ListOps<R>

    fun <R> mapIndexed(transform: (index: Int, T) -> R): ListOps<R>

    fun <R> mapNotNull(transform: (T) -> R): ListOps<R>

    fun <R> mapIndexedNotNull(transform: (index: Int, T) -> R): ListOps<R>

    fun <R, C : MutableCollection<in R>> mapTo(
        destination: C,
        transform: (T) -> R
    ): C

    fun <R, C : MutableCollection<in R>> mapIndexedTo(
        destination: C,
        transform: (index: Int, T) -> R
    ): C

    fun <R : Any, C : MutableCollection<in R>> mapNotNullTo(
        destination: C,
        transform: (T) -> R?
    ): C

    fun <R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(
        destination: C,
        transform: (index: Int, T) -> R?
    ): C

    fun <R> flatMap(transform: (T) -> Iterable<R>): ListOps<R>

    fun <R> flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): ListOps<R>

    fun <R, C : MutableCollection<in R>> flatMapTo(
        destination: C,
        transform: (T) -> Iterable<R>
    ): C

    fun <R, C : MutableCollection<in R>> flatMapIndexedTo(
        destination: C,
        transform: (index: Int, T) -> Iterable<R>
    ): C

    fun reduce(operation: (T, T) -> T): T

    fun reduceIndexed(operation: (index: Int, T, T) -> T): T

    fun reduceOrNull(operation: (T, T) -> T): T?

    fun reduceIndexedOrNull(operation: (index: Int, T, T) -> T): T?

    fun <R> reduce(
        initial: R,
        operation: (R, T) -> R
    ): R

    fun <R> reduceIndexed(
        initial: R,
        operation: (index: Int, R, T) -> R
    ): R

    fun <K, V> associate(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V>

    fun <K, V> associate(transform: (T) -> Pair<K, V>): Map<K, V>

    fun <K> associateKey(keySelector: (T) -> K): Map<K, T>

    fun <V> associateValue(valueSelector: (T) -> V): Map<T, V>

    fun <K, V> associateWithNext(
        keySelector: (T) -> K,
        valueTransform: (T?) -> V
    ): Map<K, V>

    fun <K, V> associateWithNext(transform: (T, T?) -> Pair<K, V>): Map<K, V>

    fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M

    fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        transform: (T) -> Pair<K, V>
    ): M

    fun <K, M : MutableMap<in K, in T>> associateKeyTo(
        destination: M,
        keySelector: (T) -> K
    ): M

    fun <V, M : MutableMap<in T, in V>> associateValueTo(
        destination: M,
        valueSelector: (T) -> V
    ): M

    fun <K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T?) -> V
    ): M

    fun <K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        destination: M,
        transform: (T, T?) -> Pair<K, V>
    ): M

    fun <K> groupBy(keySelector: (T) -> K): Map<K, List<T>>

    fun <K, V> groupBy(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): Map<K, List<V>>

    fun <K, M : MutableMap<in K, MutableList<T>>> groupByTo(
        destination: M,
        keySelector: (T) -> K
    ): M

    fun <K, V, M : MutableMap<in K, MutableList<V>>> groupByTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M

    fun chunked(size: Int): List<List<T>>

    fun <R> chunked(
        size: Int,
        transform: (List<T>) -> R
    ): List<R>

    fun windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false
    ): List<List<T>>

    fun <R> windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): List<R>

    fun <R, V> zip(
        other: Array<out R>,
        transform: (T, R) -> V
    ): List<V>

    fun <R, V> zip(
        other: Iterable<R>,
        transform: (T, R) -> V
    ): List<V>

    fun <R> zipWithNext(transform: (T, T) -> R): List<R>

    fun max(): T {
        return max(Sort.selfComparableComparator())
    }

    fun max(comparator: Comparator<in T>): T {
        return Require.notNull(maxOrNull(comparator))
    }

    fun maxOrNull(): T? {
        return maxOrNull(Sort.selfComparableComparator())
    }

    fun maxOrNull(comparator: Comparator<in T>): T?

    fun min(): T {
        return min(Sort.selfComparableComparator())
    }

    fun min(comparator: Comparator<in T>): T {
        return Require.notNull(minOrNull(comparator))
    }

    fun minOrNull(): T?

    fun minOrNull(comparator: Comparator<in T>): T?

    fun sumInt(): Int {
        return sumInt { To.toInt(it) }
    }

    fun sumInt(selector: (T) -> Int): Int

    fun sumLong(): Long {
        return sumLong { To.toLong(it) }
    }

    fun sumLong(selector: (T) -> Long): Long

    fun sumDouble(): Double {
        return sumDouble { To.toDouble(it) }
    }

    fun sumDouble(selector: (T) -> Double): Double

    fun sumBigInteger(): BigInteger {
        return sumBigInteger { To.toBigInteger(it) }
    }

    fun sumBigInteger(selector: (T) -> BigInteger): BigInteger

    fun sumBigDecimal(): BigDecimal {
        return sumBigDecimal { To.toBigDecimal(it) }
    }

    fun sumBigDecimal(selector: (T) -> BigDecimal): BigDecimal

    fun intersect(other: Iterable<T>): Set<T>

    fun union(other: Iterable<T>): Set<T>

    fun subtract(other: Iterable<T>): Set<T>

    fun reversed(): List<T>

    fun sorted(): List<T> {
        return sorted(Sort.selfComparableComparator())
    }

    fun sorted(comparator: Comparator<in T>): List<T>

    fun shuffled(): List<T>

    fun shuffled(random: Random): List<T>

    fun distinct(): List<T>

    fun <K> distinct(selector: (T) -> K): List<T>

    fun forEachIndexed(action: (index: Int, T) -> Unit)

    fun removeAll(predicate: (T) -> Boolean): Boolean

    fun removeFirst(): T

    fun removeFirstOrNull(): T?

    fun removeLast(): T

    fun removeLastOrNull(): T?

    fun retainAll(predicate: (T) -> Boolean): Boolean

    fun plus(element: T): List<T>

    fun plus(elements: Array<out T>): List<T>

    fun plus(elements: Iterable<T>): List<T>

    fun minus(element: T): List<T>

    fun minus(elements: Array<out T>): List<T>

    fun minus(elements: Iterable<T>): List<T>

    fun <C : MutableCollection<in T>> toCollection(destination: C): C

    fun toSet(): Set<T>

    fun toMutableSet(): MutableSet<T>

    fun toHashSet(): HashSet<T>

    fun toSortedSet(): SortedSet<T>

    fun toSortedSet(comparator: Comparator<in T>): SortedSet<T>

    fun toList(): List<T>

    fun toMutableList(): MutableList<T>

    fun toStream(): Stream<T>

    fun toStream(parallel: Boolean): Stream<T>

    fun toArray(): Array<Any?>

    fun toArray(generator: (size: Int) -> Array<T>): Array<T>

    fun toBooleanArray(): BooleanArray {
        return toBooleanArray { To.toBoolean(it) }
    }

    fun toBooleanArray(selector: (T) -> Boolean): BooleanArray

    fun toByteArray(): ByteArray {
        return toByteArray { To.toByte(it) }
    }

    fun toByteArray(selector: (T) -> Byte): ByteArray

    fun toShortArray(): ShortArray {
        return toShortArray { To.toShort(it) }
    }

    fun toShortArray(selector: (T) -> Short): ShortArray

    fun toCharArray(): CharArray {
        return toCharArray { To.toChar(it) }
    }

    fun toCharArray(selector: (T) -> Char): CharArray

    fun toIntArray(): IntArray {
        return toIntArray { To.toInt(it) }
    }

    fun toIntArray(selector: (T) -> Int): IntArray

    fun toLongArray(): LongArray {
        return toLongArray { To.toLong(it) }
    }

    fun toLongArray(selector: (T) -> Long): LongArray

    fun toFloatArray(): FloatArray {
        return toFloatArray { To.toFloat(it) }
    }

    fun toFloatArray(selector: (T) -> Float): FloatArray

    fun toDoubleArray(): DoubleArray {
        return toDoubleArray { To.toDouble(it) }
    }

    fun toDoubleArray(selector: (T) -> Double): DoubleArray


}