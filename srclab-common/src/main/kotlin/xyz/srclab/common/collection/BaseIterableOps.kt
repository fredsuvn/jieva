package xyz.srclab.common.collection

import xyz.srclab.common.base.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.random.Random

abstract class BaseIterableOps<T, I : Iterable<T>, MI : MutableIterable<T>, THIS : BaseIterableOps<T, I, MI, THIS>>
protected constructor(operated: I) : MutableIterable<T> {

    protected var operated: I = operated

    override fun iterator(): MutableIterator<T> {
        return mutableOperated().iterator()
    }

    fun <R> map(transform: (T) -> R): ListOps<R> {
        return toListOps(map(operated(), transform))
    }

    fun <R> mapIndexed(transform: (index: Int, T) -> R): ListOps<R> {
        return toListOps(mapIndexed(operated(), transform))
    }

    fun <R> mapNotNull(transform: (T) -> R?): ListOps<R> {
        return toListOps(mapNotNull(operated(), transform))
    }

    fun <R> mapIndexedNotNull(transform: (index: Int, T) -> R?): ListOps<R> {
        return toListOps(mapIndexedNotNull(operated(), transform))
    }

    fun <R, C : MutableCollection<in R>> mapTo(
        destination: C,
        transform: (T) -> R
    ): C {
        return mapTo(operated(), destination, transform)
    }

    fun <R, C : MutableCollection<in R>> mapIndexedTo(
        destination: C,
        transform: (index: Int, T) -> R
    ): C {
        return mapIndexedTo(operated(), destination, transform)
    }

    fun <R : Any, C : MutableCollection<in R>> mapNotNullTo(
        destination: C,
        transform: (T) -> R?
    ): C {
        return mapNotNullTo(operated(), destination, transform)
    }

    fun <R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(
        destination: C,
        transform: (index: Int, T) -> R?
    ): C {
        return mapIndexedNotNullTo(operated(), destination, transform)
    }

    fun <R, V> zip(
        other: Array<out R>,
        transform: (T, R) -> V
    ): ListOps<V> {
        return toListOps(zip(operated(), other, transform))
    }

    fun <R, V> zip(
        other: Iterable<R>,
        transform: (T, R) -> V
    ): ListOps<V> {
        return toListOps(zip(operated(), other, transform))
    }

    fun <R> zipWithNext(transform: (T, T) -> R): ListOps<R> {
        return toListOps(zipWithNext(operated(), transform))
    }

    fun <R, V> unzip(transform: (T) -> Pair<R, V>): Pair<List<R>, List<V>> {
        return unzip(operated(), transform)
    }

    fun <R> unzipWithNext(transform: (T) -> Pair<R, R>): ListOps<R> {
        return toListOps(unzipWithNext(operated(), transform))
    }

    fun <K, V> associate(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return toMapOps(associate(operated(), keySelector, valueTransform))
    }

    fun <K, V> associate(transform: (T) -> Pair<K, V>): MapOps<K, V> {
        return toMapOps(associate(operated(), transform))
    }

    fun <K> associateKey(keySelector: (T) -> K): MapOps<K, T> {
        return toMapOps(associateKey(operated(), keySelector))
    }

    fun <V> associateValue(valueSelector: (T) -> V): MapOps<T, V> {
        return toMapOps(associateValue(operated(), valueSelector))
    }

    fun <K, V> associatePair(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return toMapOps(associatePair(operated(), keySelector, valueTransform))
    }

    fun <K, V> associatePair(transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return toMapOps(associatePair(operated(), transform))
    }

    fun <K, V> associatePair(
        complementValue: T,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return toMapOps(associatePair(operated(), complementValue, keySelector, valueTransform))
    }

    fun <K, V> associatePair(complementValue: T, transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return toMapOps(associatePair(operated(), complementValue, transform))
    }

    fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return associateTo(operated(), destination, keySelector, valueTransform)
    }

    fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        transform: (T) -> Pair<K, V>
    ): M {
        return associateTo(operated(), destination, transform)
    }

    fun <K, M : MutableMap<in K, in T>> associateKeyTo(
        destination: M,
        keySelector: (T) -> K
    ): M {
        return associateKeyTo(operated(), destination, keySelector)
    }

    fun <V, M : MutableMap<in T, in V>> associateValueTo(
        destination: M,
        valueSelector: (T) -> V
    ): M {
        return associateValueTo(operated(), destination, valueSelector)
    }

    fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return associatePairTo(operated(), destination, keySelector, valueTransform)
    }

    fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        destination: M,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return associatePairTo(operated(), destination, transform)
    }

    fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        complementValue: T,
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return associatePairTo(operated(), complementValue, destination, keySelector, valueTransform)
    }

    fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        complementValue: T,
        destination: M,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return associatePairTo(operated(), complementValue, destination, transform)
    }

    fun <R> flatMap(transform: (T) -> Iterable<R>): ListOps<R> {
        return toListOps(flatMap(operated(), transform))
    }

    fun <R> flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): ListOps<R> {
        return toListOps(flatMapIndexed(operated(), transform))
    }

    fun <R, C : MutableCollection<in R>> flatMapTo(
        destination: C,
        transform: (T) -> Iterable<R>
    ): C {
        return flatMapTo(operated(), destination, transform)
    }

    fun <R, C : MutableCollection<in R>> flatMapIndexedTo(
        destination: C,
        transform: (index: Int, T) -> Iterable<R>
    ): C {
        return flatMapIndexedTo(operated(), destination, transform)
    }

    fun filter(predicate: (T) -> Boolean): ListOps<T> {
        return toListOps(filter(operated(), predicate))
    }

    fun filterIndexed(predicate: (index: Int, T) -> Boolean): ListOps<T> {
        return toListOps(filterIndexed(operated(), predicate))
    }

    fun filterNotNull(): ListOps<T> {
        return toListOps(filterNotNull(operated()))
    }

    fun <C : MutableCollection<in T>> filterTo(
        destination: C,
        predicate: (T) -> Boolean
    ): C {
        return filterTo(operated(), destination, predicate)
    }

    fun <C : MutableCollection<in T>> filterIndexedTo(
        destination: C,
        predicate: (index: Int, T) -> Boolean
    ): C {
        return filterIndexedTo(operated(), destination, predicate)
    }

    fun <C : MutableCollection<in T>> filterNotNullTo(
        destination: C
    ): C {
        return filterNotNullTo(As.any(operated()), destination)
    }

    fun any(): Boolean {
        return any(operated())
    }

    fun any(predicate: (T) -> Boolean): Boolean {
        return any(operated(), predicate)
    }

    fun none(): Boolean {
        return none(operated())
    }

    fun none(predicate: (T) -> Boolean): Boolean {
        return none(operated(), predicate)
    }

    fun all(predicate: (T) -> Boolean): Boolean {
        return all(operated(), predicate)
    }

    fun find(predicate: (T) -> Boolean): T? {
        return find(operated(), predicate)
    }

    open fun findLast(predicate: (T) -> Boolean): T? {
        return findLast(operated(), predicate)
    }

    open fun first(): T {
        return first(operated())
    }

    fun first(predicate: (T) -> Boolean): T {
        return first(operated(), predicate)
    }

    open fun firstOrNull(): T? {
        return firstOrNull(operated())
    }

    fun firstOrNull(predicate: (T) -> Boolean): T? {
        return firstOrNull(operated(), predicate)
    }

    open fun last(): T {
        return last(operated())
    }

    open fun last(predicate: (T) -> Boolean): T {
        return last(operated(), predicate)
    }

    open fun lastOrNull(): T? {
        return lastOrNull(operated())
    }

    open fun lastOrNull(predicate: (T) -> Boolean): T? {
        return lastOrNull(operated(), predicate)
    }

    open fun single(): T {
        return single(operated())
    }

    fun single(predicate: (T) -> Boolean): T {
        return single(operated(), predicate)
    }

    open fun singleOrNull(): T? {
        return singleOrNull(operated())
    }

    fun singleOrNull(predicate: (T) -> Boolean): T? {
        return singleOrNull(operated(), predicate)
    }

    fun contains(element: T): Boolean {
        return contains(operated(), element)
    }

    open fun containsAll(elements: Array<out T>): Boolean {
        return containsAll(operated(), elements)
    }

    open fun containsAll(elements: Iterable<T>): Boolean {
        return containsAll(operated(), elements)
    }

    open fun containsAll(elements: Collection<T>): Boolean {
        return containsAll(operated(), elements)
    }

    open fun count(): Int {
        return count(operated())
    }

    fun count(predicate: (T) -> Boolean): Int {
        return count(operated(), predicate)
    }

    open fun elementAt(index: Int): T {
        return elementAt(operated(), index)
    }

    open fun elementAtOrElse(
        index: Int,
        defaultValue: (index: Int) -> T
    ): T {
        return elementAtOrElse(operated(), index, defaultValue)
    }

    open fun elementAtOrNull(index: Int): T? {
        return elementAtOrNull(operated(), index)
    }

    open fun indexOf(element: T): Int {
        return indexOf(operated(), element)
    }

    open fun indexOf(predicate: (T) -> Boolean): Int {
        return indexOf(operated(), predicate)
    }

    open fun lastIndexOf(element: T): Int {
        return lastIndexOf(operated(), element)
    }

    open fun lastIndexOf(predicate: (T) -> Boolean): Int {
        return lastIndexOf(operated(), predicate)
    }

    fun drop(n: Int): ListOps<T> {
        return toListOps(drop(operated(), n))
    }

    fun dropWhile(predicate: (T) -> Boolean): ListOps<T> {
        return toListOps(dropWhile(operated(), predicate))
    }

    fun take(n: Int): ListOps<T> {
        return toListOps(take(operated(), n))
    }

    fun takeWhile(predicate: (T) -> Boolean): ListOps<T> {
        return toListOps(takeWhile(operated(), predicate))
    }

    fun reduce(operation: (T, T) -> T): T {
        return reduce(operated(), operation)
    }

    fun reduceIndexed(operation: (index: Int, T, T) -> T): T {
        return reduceIndexed(operated(), operation)
    }

    fun reduceOrNull(operation: (T, T) -> T): T? {
        return reduceOrNull(operated(), operation)
    }

    fun reduceIndexedOrNull(operation: (index: Int, T, T) -> T): T? {
        return reduceIndexedOrNull(operated(), operation)
    }

    fun <R> reduce(
        initial: R,
        operation: (R, T) -> R
    ): R {
        return reduce(operated(), initial, operation)
    }

    fun <R> reduceIndexed(
        initial: R,
        operation: (index: Int, R, T) -> R
    ): R {
        return reduceIndexed(operated(), initial, operation)
    }

    fun <K> groupBy(keySelector: (T) -> K): MapOps<K, List<T>> {
        return toMapOps(groupBy(operated(), keySelector))
    }

    fun <K, V> groupBy(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, List<V>> {
        return toMapOps(groupBy(operated(), keySelector, valueTransform))
    }

    fun <K, M : MutableMap<in K, MutableList<T>>> groupByTo(
        destination: M,
        keySelector: (T) -> K
    ): M {
        return groupByTo(operated(), destination, keySelector)
    }

    fun <K, V, M : MutableMap<in K, MutableList<V>>> groupByTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return groupByTo(operated(), destination, keySelector, valueTransform)
    }

    fun chunked(size: Int): ListOps<List<T>> {
        return toListOps(chunked(operated(), size))
    }

    fun <R> chunked(
        size: Int,
        transform: (List<T>) -> R
    ): ListOps<R> {
        return toListOps(chunked(operated(), size, transform))
    }

    fun windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false
    ): ListOps<List<T>> {
        return toListOps(windowed(operated(), size, step, partialWindows))
    }

    fun <R> windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): ListOps<R> {
        return toListOps(windowed(operated(), size, step, partialWindows, transform))
    }

    fun max(): T {
        return max(operated())
    }

    fun max(comparator: Comparator<in T>): T {
        return max(operated(), comparator)
    }

    fun maxOrNull(): T? {
        return maxOrNull(operated())
    }

    fun maxOrNull(comparator: Comparator<in T>): T? {
        return maxOrNull(operated(), comparator)
    }

    fun min(): T {
        return min(operated())
    }

    fun min(comparator: Comparator<in T>): T {
        return min(operated(), comparator)
    }

    fun minOrNull(): T? {
        return minOrNull(operated())
    }

    fun minOrNull(comparator: Comparator<in T>): T? {
        return minOrNull(operated(), comparator)
    }

    fun sumInt(): Int {
        return sumInt(operated())
    }

    fun sumInt(selector: (T) -> Int): Int {
        return sumInt(operated(), selector)
    }

    fun sumLong(): Long {
        return sumLong(operated())
    }

    fun sumLong(selector: (T) -> Long): Long {
        return sumLong(operated(), selector)
    }

    fun sumDouble(): Double {
        return sumDouble(operated())
    }

    fun sumDouble(selector: (T) -> Double): Double {
        return sumDouble(operated(), selector)
    }

    fun sumBigInteger(): BigInteger {
        return sumBigInteger(operated())
    }

    fun sumBigInteger(selector: (T) -> BigInteger): BigInteger {
        return sumBigInteger(operated(), selector)
    }

    fun sumBigDecimal(): BigDecimal {
        return sumBigDecimal(operated())
    }

    fun sumBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
        return sumBigDecimal(operated(), selector)
    }

    fun intersect(other: Iterable<T>): SetOps<T> {
        return toSetOps(intersect(operated(), other))
    }

    fun union(other: Iterable<T>): SetOps<T> {
        return toSetOps(union(operated(), other))
    }

    fun subtract(other: Iterable<T>): SetOps<T> {
        return toSetOps(subtract(operated(), other))
    }

    fun reversed(): ListOps<T> {
        return toListOps(reversed(operated()))
    }

    fun sorted(): ListOps<T> {
        return toListOps(sorted(operated()))
    }

    fun sorted(comparator: Comparator<in T>): ListOps<T> {
        return toListOps(sorted(operated(), comparator))
    }

    fun shuffled(): ListOps<T> {
        return toListOps(shuffled(operated()))
    }

    fun shuffled(random: Random): ListOps<T> {
        return toListOps(shuffled(operated(), random))
    }

    fun distinct(): ListOps<T> {
        return toListOps(distinct(operated()))
    }

    fun <K> distinct(selector: (T) -> K): ListOps<T> {
        return toListOps(distinct(operated(), selector))
    }

    fun forEachIndexed(action: (index: Int, T) -> Unit): THIS {
        forEachIndexed(operated(), action)
        return toSelfOps()
    }

    open fun remove(element: T): THIS {
        remove(mutableOperated(), element)
        return toSelfOps()
    }

    fun remove(predicate: (T) -> Boolean): THIS {
        remove(mutableOperated(), predicate)
        return toSelfOps()
    }

    open fun removeAll(predicate: (T) -> Boolean): THIS {
        removeAll(mutableOperated(), predicate)
        return toSelfOps()
    }

    open fun removeFirst(): THIS {
        removeFirst(mutableOperated())
        return toSelfOps()
    }

    open fun removeFirstOrNull(): THIS {
        removeFirstOrNull(mutableOperated())
        return toSelfOps()
    }

    open fun removeLast(): THIS {
        removeLast(mutableOperated())
        return toSelfOps()
    }

    open fun removeLastOrNull(): THIS {
        removeLastOrNull(mutableOperated())
        return toSelfOps()
    }

    open fun retainAll(predicate: (T) -> Boolean): THIS {
        retainAll(mutableOperated(), predicate)
        return toSelfOps()
    }

    fun addTo(destination: Array<in T>): THIS {
        addTo(operated(), destination)
        return toSelfOps()
    }

    fun addTo(destination: Array<in T>, fromIndex: Int, toIndex: Int): THIS {
        addTo(operated(), destination, fromIndex, toIndex)
        return toSelfOps()
    }

    fun addTo(destination: MutableCollection<T>): THIS {
        addTo(operated(), destination)
        return toSelfOps()
    }

    fun <C : MutableCollection<in T>> toCollection(destination: C): C {
        return toCollection(operated(), destination)
    }

    fun toSet(): Set<T> {
        return toSet(operated())
    }

    fun toMutableSet(): MutableSet<T> {
        return toMutableSet(operated())
    }

    fun toHashSet(): HashSet<T> {
        return toHashSet(operated())
    }

    fun toSortedSet(): SortedSet<T> {
        return toSortedSet(operated())
    }

    fun toSortedSet(comparator: Comparator<in T>): SortedSet<T> {
        return toSortedSet(operated(), comparator)
    }

    fun toList(): List<T> {
        return toList(operated())
    }

    fun toMutableList(): MutableList<T> {
        return toMutableList(operated())
    }

    fun toStream(): Stream<T> {
        return toStream(operated())
    }

    fun toStream(parallel: Boolean): Stream<T> {
        return toStream(operated(), parallel)
    }

    fun toArray(): Array<Any?> {
        return toArray(operated())
    }

    fun toArray(generator: (size: Int) -> Array<T>): Array<T> {
        return toArray(operated(), generator)
    }

    fun toBooleanArray(): BooleanArray {
        return toBooleanArray(operated())
    }

    fun toBooleanArray(selector: (T) -> Boolean): BooleanArray {
        return toBooleanArray(operated(), selector)
    }

    fun toByteArray(): ByteArray {
        return toByteArray(operated())
    }

    fun toByteArray(selector: (T) -> Byte): ByteArray {
        return toByteArray(operated(), selector)
    }

    fun toShortArray(): ShortArray {
        return toShortArray(operated())
    }

    fun toShortArray(selector: (T) -> Short): ShortArray {
        return toShortArray(operated(), selector)
    }

    fun toCharArray(): CharArray {
        return toCharArray(operated())
    }

    fun toCharArray(selector: (T) -> Char): CharArray {
        return toCharArray(operated(), selector)
    }

    fun toIntArray(): IntArray {
        return toIntArray(operated())
    }

    fun toIntArray(selector: (T) -> Int): IntArray {
        return toIntArray(operated(), selector)
    }

    fun toLongArray(): LongArray {
        return toLongArray(operated())
    }

    fun toLongArray(selector: (T) -> Long): LongArray {
        return toLongArray(operated(), selector)
    }

    fun toFloatArray(): FloatArray {
        return toFloatArray(operated())
    }

    fun toFloatArray(selector: (T) -> Float): FloatArray {
        return toFloatArray(operated(), selector)
    }

    fun toDoubleArray(): DoubleArray {
        return toDoubleArray(operated())
    }

    fun toDoubleArray(selector: (T) -> Double): DoubleArray {
        return toDoubleArray(operated(), selector)
    }

    fun toSequenceOps(): SequenceOps<T> {
        return SequenceOps.opsFor(operated())
    }

    protected fun operated(): I {
        return operated
    }

    protected fun mutableOperated(): MI {
        return As.any(operated())
    }

    protected abstract fun toSelfOps(): THIS

    protected abstract fun <T> toIterableOps(iterable: Iterable<T>): IterableOps<T>

    protected abstract fun <T> toListOps(list: List<T>): ListOps<T>

    protected abstract fun <T> toSetOps(set: Set<T>): SetOps<T>

    protected abstract fun <K, V> toMapOps(map: Map<K, V>): MapOps<K, V>

    companion object {

        @JvmStatic
        inline fun <T, R> map(iterable: Iterable<T>, transform: (T) -> R): List<R> {
            return iterable.map(transform)
        }

        @JvmStatic
        inline fun <T, R> mapIndexed(iterable: Iterable<T>, transform: (index: Int, T) -> R): List<R> {
            return iterable.mapIndexed(transform)
        }

        @JvmStatic
        inline fun <T, R : Any> mapNotNull(iterable: Iterable<T>, transform: (T) -> R?): List<R> {
            return iterable.mapNotNull(transform)
        }

        @JvmStatic
        inline fun <T, R : Any> mapIndexedNotNull(iterable: Iterable<T>, transform: (index: Int, T) -> R?): List<R> {
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
        inline fun <T, R, V> unzip(
            iterable: Iterable<V>,
            transform: (V) -> Pair<T, R>
        ): Pair<List<T>, List<R>> {
            if (iterable is Collection<V>) {
                val listT = ArrayList<T>(iterable.size)
                val listR = ArrayList<R>(iterable.size)
                for (e in iterable) {
                    val pair = transform(e)
                    listT.add(pair.first)
                    listR.add(pair.second)
                }
                return listT to listR
            } else {
                val listT = LinkedList<T>()
                val listR = LinkedList<R>()
                for (e in iterable) {
                    val pair = transform(e)
                    listT.add(pair.first)
                    listR.add(pair.second)
                }
                return ArrayList(listT) to ArrayList(listR)
            }
        }

        @JvmStatic
        inline fun <T, R> unzipWithNext(
            iterable: Iterable<R>,
            transform: (R) -> Pair<T, T>
        ): List<T> {
            if (iterable is Collection<R>) {
                val listT = ArrayList<T>(iterable.size)
                for (e in iterable) {
                    val pair = transform(e)
                    listT.add(pair.first)
                    listT.add(pair.second)
                }
                return listT
            } else {
                val listT = LinkedList<T>()
                for (e in iterable) {
                    val pair = transform(e)
                    listT.add(pair.first)
                    listT.add(pair.second)
                }
                return ArrayList(listT)
            }
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
        inline fun <T, K, V> associatePair(
            iterable: Iterable<T>,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return associatePairTo(iterable, LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> associatePair(iterable: Iterable<T>, transform: (T, T) -> Pair<K, V>): Map<K, V> {
            return associatePairTo(iterable, LinkedHashMap(), transform)
        }

        @JvmStatic
        inline fun <T, K, V> associatePair(
            iterable: Iterable<T>,
            complementValue: T,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return associatePairTo(iterable, complementValue, LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> associatePair(
            iterable: Iterable<T>,
            complementValue: T,
            transform: (T, T) -> Pair<K, V>
        ): Map<K, V> {
            return associatePairTo(iterable, complementValue, LinkedHashMap(), transform)
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
        inline fun <T, K, V, M : MutableMap<in K, in V>> associatePairTo(
            iterable: Iterable<T>,
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): M {
            val iterator = iterable.iterator()
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

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> associatePairTo(
            iterable: Iterable<T>,
            destination: M,
            transform: (T, T) -> Pair<K, V>
        ): M {
            val iterator = iterable.iterator()
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

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> associatePairTo(
            iterable: Iterable<T>,
            complementValue: T,
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): M {
            val iterator = iterable.iterator()
            while (iterator.hasNext()) {
                val ik = iterator.next()
                if (iterator.hasNext()) {
                    val iv = iterator.next()
                    val k = keySelector(ik)
                    val v = valueTransform(iv)
                    destination.put(k, v)
                } else {
                    val k = keySelector(ik)
                    val v = valueTransform(complementValue)
                    destination.put(k, v)
                    break
                }
            }
            return destination
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> associatePairTo(
            iterable: Iterable<T>,
            complementValue: T,
            destination: M,
            transform: (T, T) -> Pair<K, V>
        ): M {
            val iterator = iterable.iterator()
            while (iterator.hasNext()) {
                val ik = iterator.next()
                if (iterator.hasNext()) {
                    val iv = iterator.next()
                    val pair = transform(ik, iv)
                    destination.put(pair.first, pair.second)
                } else {
                    val pair = transform(ik, complementValue)
                    destination.put(pair.first, pair.second)
                    break
                }
            }
            return destination
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
        inline fun <T> all(iterable: Iterable<T>, predicate: (T) -> Boolean): Boolean {
            return iterable.all(predicate)
        }

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
        fun <T> containsAll(iterable: Iterable<T>, elements: Array<out T>): Boolean {
            return containsAll(iterable, elements.toSet())
        }

        @JvmStatic
        fun <T> containsAll(iterable: Iterable<T>, elements: Iterable<T>): Boolean {
            return containsAll(iterable, elements.toSet())
        }

        @JvmStatic
        fun <T> containsAll(iterable: Iterable<T>, elements: Collection<T>): Boolean {
            return iterable.toSet().containsAll(elements)
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
        fun <T> max(iterable: Iterable<T>): T {
            return Require.notNull(maxOrNull(iterable))
        }

        @JvmStatic
        fun <T> max(iterable: Iterable<T>, comparator: Comparator<in T>): T {
            return Require.notNull(maxOrNull(iterable, comparator))
        }

        @JvmStatic
        fun <T> maxOrNull(iterable: Iterable<T>): T? {
            return maxOrNull(iterable, Sort.selfComparableComparator())
        }

        @JvmStatic
        fun <T> maxOrNull(iterable: Iterable<T>, comparator: Comparator<in T>): T? {
            return iterable.maxWithOrNull(comparator)
        }

        @JvmStatic
        fun <T> min(iterable: Iterable<T>): T {
            return Require.notNull(minOrNull(iterable))
        }

        @JvmStatic
        fun <T> min(iterable: Iterable<T>, comparator: Comparator<in T>): T {
            return Require.notNull(minOrNull(iterable, comparator))
        }

        @JvmStatic
        fun <T> minOrNull(iterable: Iterable<T>): T? {
            return minOrNull(iterable, Sort.selfComparableComparator())
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
        fun <T> sorted(iterable: Iterable<T>): List<T> {
            return sorted(iterable, Sort.selfComparableComparator())
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
        fun <T> remove(iterable: MutableIterable<T>, element: T): Boolean {
            val iterator = iterable.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next == element) {
                    iterator.remove()
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun <T> remove(iterable: MutableIterable<T>, predicate: (T) -> Boolean): Boolean {
            val iterator = iterable.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (predicate(next)) {
                    iterator.remove()
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun <T> removeAll(iterable: MutableIterable<T>): Boolean {
            return removeAll(iterable) { true }
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
        fun <T> addTo(iterable: Iterable<T>, destination: Array<in T>): Boolean {
            return addTo(iterable, destination, 0, destination.size)
        }

        @JvmStatic
        fun <T> addTo(iterable: Iterable<T>, destination: Array<in T>, fromIndex: Int, toIndex: Int): Boolean {
            Check.checkRangeInBounds(fromIndex, toIndex, destination.size)
            var result = false
            val iterator = iterable.iterator()
            for (i in fromIndex until toIndex) {
                if (iterator.hasNext()) {
                    destination[i] = iterator.next()
                    result = true
                } else {
                    return result
                }
            }
            return result
        }

        @JvmStatic
        fun <T> addTo(iterable: Iterable<T>, destination: MutableCollection<T>): Boolean {
            return destination.addAll(iterable)
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
        fun <T> plus(iterable: Iterable<T>, elements: Sequence<T>): List<T> {
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
        fun <T> minus(iterable: Iterable<T>, elements: Sequence<T>): List<T> {
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