package xyz.srclab.common.collection

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

object SequenceOps {

    @JvmStatic
    inline fun <T> find(sequence: Sequence<T>, predicate: (T) -> Boolean): T? {
        return sequence.find(predicate)
    }

    @JvmStatic
    inline fun <T> findLast(sequence: Sequence<T>, predicate: (T) -> Boolean): T? {
        return sequence.findLast(predicate)
    }

    @JvmStatic
    fun <T> first(sequence: Sequence<T>): T {
        return sequence.first()
    }

    @JvmStatic
    inline fun <T> first(sequence: Sequence<T>, predicate: (T) -> Boolean): T {
        return sequence.first(predicate)
    }

    @JvmStatic
    fun <T> firstOrNull(sequence: Sequence<T>): T? {
        return sequence.firstOrNull()
    }

    @JvmStatic
    inline fun <T> firstOrNull(sequence: Sequence<T>, predicate: (T) -> Boolean): T? {
        return sequence.firstOrNull(predicate)
    }

    @JvmStatic
    fun <T> last(sequence: Sequence<T>): T {
        return sequence.last()
    }

    @JvmStatic
    inline fun <T> last(sequence: Sequence<T>, predicate: (T) -> Boolean): T {
        return sequence.last(predicate)
    }

    @JvmStatic
    fun <T> lastOrNull(sequence: Sequence<T>): T? {
        return sequence.lastOrNull()
    }

    @JvmStatic
    inline fun <T> lastOrNull(sequence: Sequence<T>, predicate: (T) -> Boolean): T? {
        return sequence.lastOrNull(predicate)
    }

    @JvmStatic
    inline fun <T> all(sequence: Sequence<T>, predicate: (T) -> Boolean): Boolean {
        return sequence.all(predicate)
    }

    @JvmStatic
    fun <T> any(sequence: Sequence<T>): Boolean {
        return sequence.any()
    }

    @JvmStatic
    inline fun <T> any(sequence: Sequence<T>, predicate: (T) -> Boolean): Boolean {
        return sequence.any(predicate)
    }

    @JvmStatic
    fun <T> none(sequence: Sequence<T>): Boolean {
        return sequence.none()
    }

    @JvmStatic
    inline fun <T> none(sequence: Sequence<T>, predicate: (T) -> Boolean): Boolean {
        return sequence.none(predicate)
    }

    @JvmStatic
    fun <T> single(sequence: Sequence<T>): T {
        return sequence.single()
    }

    @JvmStatic
    inline fun <T> single(sequence: Sequence<T>, predicate: (T) -> Boolean): T {
        return sequence.single(predicate)
    }

    @JvmStatic
    fun <T> singleOrNull(sequence: Sequence<T>): T? {
        return sequence.singleOrNull()
    }

    @JvmStatic
    inline fun <T> singleOrNull(sequence: Sequence<T>, predicate: (T) -> Boolean): T? {
        return sequence.singleOrNull(predicate)
    }

    @JvmStatic
    fun <T> contains(sequence: Sequence<T>, element: T): Boolean {
        return sequence.contains(element)
    }

    @JvmStatic
    fun <T> count(sequence: Sequence<T>): Int {
        return sequence.count()
    }

    @JvmStatic
    inline fun <T> count(sequence: Sequence<T>, predicate: (T) -> Boolean): Int {
        return sequence.count(predicate)
    }

    @JvmStatic
    fun <T> elementAt(sequence: Sequence<T>, index: Int): T {
        return sequence.elementAt(index)
    }

    @JvmStatic
    fun <T> elementAtOrElse(
        sequence: Sequence<T>,
        index: Int,
        defaultValue: (index: Int) -> T
    ): T {
        return sequence.elementAtOrElse(index, defaultValue)
    }

    @JvmStatic
    fun <T> elementAtOrNull(sequence: Sequence<T>, index: Int): T? {
        return sequence.elementAtOrNull(index)
    }

    @JvmStatic
    fun <T> indexOf(sequence: Sequence<T>, element: T): Int {
        return sequence.indexOf(element)
    }

    @JvmStatic
    inline fun <T> indexOf(sequence: Sequence<T>, predicate: (T) -> Boolean): Int {
        return sequence.indexOfFirst(predicate)
    }

    @JvmStatic
    fun <T> lastIndexOf(sequence: Sequence<T>, element: T): Int {
        return sequence.lastIndexOf(element)
    }

    @JvmStatic
    inline fun <T> lastIndexOf(sequence: Sequence<T>, predicate: (T) -> Boolean): Int {
        return sequence.indexOfLast(predicate)
    }

    @JvmStatic
    fun <T> drop(sequence: Sequence<T>, n: Int): Sequence<T> {
        return sequence.drop(n)
    }

    @JvmStatic
    fun <T> dropWhile(sequence: Sequence<T>, predicate: (T) -> Boolean): Sequence<T> {
        return sequence.dropWhile(predicate)
    }

    @JvmStatic
    fun <T> take(sequence: Sequence<T>, n: Int): Sequence<T> {
        return sequence.take(n)
    }

    @JvmStatic
    fun <T> takeWhile(sequence: Sequence<T>, predicate: (T) -> Boolean): Sequence<T> {
        return sequence.takeWhile(predicate)
    }

    @JvmStatic
    fun <T> filter(sequence: Sequence<T>, predicate: (T) -> Boolean): Sequence<T> {
        return sequence.filter(predicate)
    }

    @JvmStatic
    fun <T> filterIndexed(sequence: Sequence<T>, predicate: (index: Int, T) -> Boolean): Sequence<T> {
        return sequence.filterIndexed(predicate)
    }

    @JvmStatic
    fun <T : Any> filterNotNull(sequence: Iterable<T?>): List<T> {
        return sequence.filterNotNull()
    }

    @JvmStatic
    inline fun <T, C : MutableCollection<in T>> filterTo(
        sequence: Sequence<T>,
        destination: C,
        predicate: (T) -> Boolean
    ): C {
        return sequence.filterTo(destination, predicate)
    }

    @JvmStatic
    inline fun <T, C : MutableCollection<in T>> filterIndexedTo(
        sequence: Sequence<T>,
        destination: C,
        predicate: (index: Int, T) -> Boolean
    ): C {
        return sequence.filterIndexedTo(destination, predicate)
    }

    @JvmStatic
    fun <C : MutableCollection<in T>, T : Any> filterNotNullTo(
        sequence: Sequence<T?>,
        destination: C
    ): C {
        return sequence.filterNotNullTo(destination)
    }

    @JvmStatic
    fun <T, R> map(sequence: Sequence<T>, transform: (T) -> R): Sequence<R> {
        return sequence.map(transform)
    }

    @JvmStatic
    fun <T, R> mapIndexed(sequence: Sequence<T>, transform: (index: Int, T) -> R): Sequence<R> {
        return sequence.mapIndexed(transform)
    }

    @JvmStatic
    fun <T, R> mapNotNull(sequence: Sequence<T>, transform: (T) -> R): Sequence<R> {
        return sequence.mapNotNull(transform)
    }

    @JvmStatic
    fun <T, R> mapIndexedNotNull(sequence: Sequence<T>, transform: (index: Int, T) -> R): Sequence<R> {
        return sequence.mapIndexedNotNull(transform)
    }

    @JvmStatic
    inline fun <T, R, C : MutableCollection<in R>> mapTo(
        sequence: Sequence<T>,
        destination: C,
        transform: (T) -> R
    ): C {
        return sequence.mapTo(destination, transform)
    }

    @JvmStatic
    inline fun <T, R, C : MutableCollection<in R>> mapIndexedTo(
        sequence: Sequence<T>,
        destination: C,
        transform: (index: Int, T) -> R
    ): C {
        return sequence.mapIndexedTo(destination, transform)
    }

    @JvmStatic
    inline fun <T, R : Any, C : MutableCollection<in R>> mapNotNullTo(
        sequence: Sequence<T>,
        destination: C,
        transform: (T) -> R?
    ): C {
        return sequence.mapNotNullTo(destination, transform)
    }

    @JvmStatic
    inline fun <T, R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(
        sequence: Sequence<T>,
        destination: C,
        transform: (index: Int, T) -> R?
    ): C {
        return sequence.mapIndexedNotNullTo(destination, transform)
    }

    @JvmStatic
    fun <T, R> flatMap(sequence: Sequence<T>, transform: (T) -> Iterable<R>): Sequence<R> {
        return sequence.flatMap(transform)
    }

    @JvmStatic
    fun <T, R> flatMapIndexed(sequence: Sequence<T>, transform: (index: Int, T) -> Iterable<R>): Sequence<R> {
        return sequence.flatMapIndexed(transform)
    }

    @JvmStatic
    inline fun <T, R, C : MutableCollection<in R>> flatMapTo(
        sequence: Sequence<T>,
        destination: C,
        transform: (T) -> Iterable<R>
    ): C {
        return sequence.flatMapTo(destination, transform)
    }

    @JvmStatic
    inline fun <T, R, C : MutableCollection<in R>> flatMapIndexedTo(
        sequence: Sequence<T>,
        destination: C,
        transform: (index: Int, T) -> Iterable<R>
    ): C {
        return sequence.flatMapIndexedTo(destination, transform)
    }

    @JvmStatic
    inline fun <S, T : S> reduce(sequence: Sequence<T>, operation: (S, T) -> S): S {
        return sequence.reduce(operation)
    }

    @JvmStatic
    inline fun <S, T : S> reduceIndexed(sequence: Sequence<T>, operation: (index: Int, S, T) -> S): S {
        return sequence.reduceIndexed(operation)
    }

    @JvmStatic
    inline fun <S, T : S> reduceOrNull(sequence: Sequence<T>, operation: (S, T) -> S): S? {
        return sequence.reduceOrNull(operation)
    }

    @JvmStatic
    inline fun <S, T : S> reduceIndexedOrNull(sequence: Sequence<T>, operation: (index: Int, S, T) -> S): S? {
        return sequence.reduceIndexedOrNull(operation)
    }

    @JvmStatic
    inline fun <T, R> reduce(
        sequence: Sequence<T>,
        initial: R,
        operation: (R, T) -> R
    ): R {
        return sequence.fold(initial, operation)
    }

    @JvmStatic
    inline fun <T, R> reduceIndexed(
        sequence: Sequence<T>,
        initial: R,
        operation: (index: Int, R, T) -> R
    ): R {
        return sequence.foldIndexed(initial, operation)
    }

    @JvmStatic
    inline fun <T, K, V> associate(
        sequence: Sequence<T>,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): Map<K, V> {
        return sequence.associateBy(keySelector, valueTransform)
    }

    @JvmStatic
    inline fun <T, K, V> associate(sequence: Sequence<T>, transform: (T) -> Pair<K, V>): Map<K, V> {
        return sequence.associate(transform)
    }

    @JvmStatic
    inline fun <T, K> associateKey(sequence: Sequence<T>, keySelector: (T) -> K): Map<K, T> {
        return sequence.associateBy(keySelector)
    }

    @JvmStatic
    inline fun <T, V> associateValue(sequence: Sequence<T>, valueSelector: (T) -> V): Map<T, V> {
        return sequence.associateWith(valueSelector)
    }

    @JvmStatic
    inline fun <T, K, V> associateWithNext(
        sequence: Sequence<T>,
        keySelector: (T) -> K,
        valueTransform: (T?) -> V
    ): Map<K, V> {
        return associateWithNextTo(sequence, LinkedHashMap(), keySelector, valueTransform)
    }

    @JvmStatic
    inline fun <T, K, V> associateWithNext(sequence: Sequence<T>, transform: (T, T?) -> Pair<K, V>): Map<K, V> {
        return associateWithNextTo(sequence, LinkedHashMap(), transform)
    }

    @JvmStatic
    inline fun <T, K, V, M : MutableMap<in K, in V>> associateTo(
        sequence: Sequence<T>,
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return sequence.associateByTo(destination, keySelector, valueTransform)
    }

    @JvmStatic
    inline fun <T, K, V, M : MutableMap<in K, in V>> associateTo(
        sequence: Sequence<T>,
        destination: M,
        transform: (T) -> Pair<K, V>
    ): M {
        return sequence.associateTo(destination, transform)
    }

    @JvmStatic
    inline fun <T, K, M : MutableMap<in K, in T>> associateKeyTo(
        sequence: Sequence<T>,
        destination: M,
        keySelector: (T) -> K
    ): M {
        return sequence.associateByTo(destination, keySelector)
    }

    @JvmStatic
    inline fun <T, V, M : MutableMap<in T, in V>> associateValueTo(
        sequence: Sequence<T>,
        destination: M,
        valueSelector: (T) -> V
    ): M {
        return sequence.associateWithTo(destination, valueSelector)
    }

    @JvmStatic
    inline fun <T, K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        sequence: Sequence<T>,
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T?) -> V
    ): M {
        val iterator = sequence.iterator()
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
        sequence: Sequence<T>,
        destination: M,
        transform: (T, T?) -> Pair<K, V>
    ): M {
        val iterator = sequence.iterator()
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
    inline fun <T, K> groupBy(sequence: Sequence<T>, keySelector: (T) -> K): Map<K, List<T>> {
        return sequence.groupBy(keySelector)
    }

    @JvmStatic
    inline fun <T, K, V> groupBy(
        sequence: Sequence<T>,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): Map<K, List<V>> {
        return sequence.groupBy(keySelector, valueTransform)
    }

    @JvmStatic
    inline fun <T, K, M : MutableMap<in K, MutableList<T>>> groupByTo(
        sequence: Sequence<T>,
        destination: M,
        keySelector: (T) -> K
    ): M {
        return sequence.groupByTo(destination, keySelector)
    }

    @JvmStatic
    inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> groupByTo(
        sequence: Sequence<T>,
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return sequence.groupByTo(destination, keySelector, valueTransform)
    }

    @JvmStatic
    fun <T> chunked(sequence: Sequence<T>, size: Int): Sequence<List<T>> {
        return sequence.chunked(size)
    }

    @JvmStatic
    fun <T, R> chunked(
        sequence: Sequence<T>,
        size: Int,
        transform: (List<T>) -> R
    ): Sequence<R> {
        return sequence.chunked(size, transform)
    }

    @JvmStatic
    fun <T> windowed(
        sequence: Sequence<T>,
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false
    ): Sequence<List<T>> {
        return sequence.windowed(size, step, partialWindows)
    }

    @JvmStatic
    fun <T, R> windowed(
        sequence: Sequence<T>,
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): Sequence<R> {
        return sequence.windowed(size, step, partialWindows, transform)
    }

    @JvmStatic
    fun <T, R, V> zip(
        sequence: Sequence<T>,
        other: Sequence<R>,
        transform: (T, R) -> V
    ): Sequence<V> {
        return sequence.zip(other, transform)
    }

    @JvmStatic
    fun <T, R> zipWithNext(sequence: Sequence<T>, transform: (T, T) -> R): Sequence<R> {
        return sequence.zipWithNext(transform)
    }

    @JvmStatic
    fun <T : Comparable<T>> max(sequence: Sequence<T>): T {
        return Require.notNull(maxOrNull(sequence))
    }

    @JvmStatic
    fun <T> max(sequence: Sequence<T>, comparator: Comparator<in T>): T {
        return Require.notNull(maxOrNull(sequence, comparator))
    }

    @JvmStatic
    fun <T : Comparable<T>> maxOrNull(sequence: Sequence<T>): T? {
        return sequence.maxOrNull()
    }

    @JvmStatic
    fun <T> maxOrNull(sequence: Sequence<T>, comparator: Comparator<in T>): T? {
        return sequence.maxWithOrNull(comparator)
    }

    @JvmStatic
    fun <T : Comparable<T>> min(sequence: Sequence<T>): T {
        return Require.notNull(minOrNull(sequence))
    }

    @JvmStatic
    fun <T> min(sequence: Sequence<T>, comparator: Comparator<in T>): T {
        return Require.notNull(minOrNull(sequence, comparator))
    }

    @JvmStatic
    fun <T : Comparable<T>> minOrNull(sequence: Sequence<T>): T? {
        return sequence.minOrNull()
    }

    @JvmStatic
    fun <T> minOrNull(sequence: Sequence<T>, comparator: Comparator<in T>): T? {
        return sequence.minWithOrNull(comparator)
    }

    @JvmStatic
    fun <T> sumInt(sequence: Sequence<T>): Int {
        return sumInt(sequence) { To.toInt(it) }
    }

    @JvmStatic
    inline fun <T> sumInt(sequence: Sequence<T>, selector: (T) -> Int): Int {
        return sequence.sumOf(selector)
    }

    @JvmStatic
    fun <T> sumLong(sequence: Sequence<T>): Long {
        return sumLong(sequence) { To.toLong(it) }
    }

    @JvmStatic
    inline fun <T> sumLong(sequence: Sequence<T>, selector: (T) -> Long): Long {
        return sequence.sumOf(selector)
    }

    @JvmStatic
    fun <T> sumDouble(sequence: Sequence<T>): Double {
        return sumDouble(sequence) { To.toDouble(it) }
    }

    @JvmStatic
    fun <T> sumDouble(sequence: Sequence<T>, selector: (T) -> Double): Double {
        return sequence.sumOf(selector)
    }

    @JvmStatic
    fun <T> sumBigInteger(sequence: Sequence<T>): BigInteger {
        return sumBigInteger(sequence) { To.toBigInteger(it) }
    }

    @JvmStatic
    fun <T> sumBigInteger(sequence: Sequence<T>, selector: (T) -> BigInteger): BigInteger {
        return sequence.sumOf(selector)
    }

    @JvmStatic
    fun <T> sumBigDecimal(sequence: Sequence<T>): BigDecimal {
        return sumBigDecimal(sequence) { To.toBigDecimal(it) }
    }

    @JvmStatic
    fun <T> sumBigDecimal(sequence: Sequence<T>, selector: (T) -> BigDecimal): BigDecimal {
        return sequence.sumOf(selector)
    }

    @JvmStatic
    fun <T : Comparable<T>> sorted(sequence: Sequence<T>): Sequence<T> {
        return sequence.sorted()
    }

    @JvmStatic
    fun <T> sorted(sequence: Sequence<T>, comparator: Comparator<in T>): Sequence<T> {
        return sequence.sortedWith(comparator)
    }

    @JvmStatic
    fun <T> shuffled(sequence: Sequence<T>): Sequence<T> {
        return sequence.shuffled()
    }

    @JvmStatic
    fun <T> shuffled(sequence: Sequence<T>, random: Random): Sequence<T> {
        return sequence.shuffled(random)
    }

    @JvmStatic
    fun <T> distinct(sequence: Sequence<T>): Sequence<T> {
        return sequence.distinct()
    }

    @JvmStatic
    fun <T, K> distinct(sequence: Sequence<T>, selector: (T) -> K): Sequence<T> {
        return sequence.distinctBy(selector)
    }

    @JvmStatic
    inline fun <T> forEachIndexed(sequence: Sequence<T>, action: (index: Int, T) -> Unit) {
        return sequence.forEachIndexed(action)
    }

    @JvmStatic
    fun <T> plus(sequence: Sequence<T>, element: T): Sequence<T> {
        return sequence.plus(element)
    }

    @JvmStatic
    fun <T> plus(sequence: Sequence<T>, elements: Array<out T>): Sequence<T> {
        return sequence.plus(elements)
    }

    @JvmStatic
    fun <T> plus(sequence: Sequence<T>, elements: Iterable<T>): Sequence<T> {
        return sequence.plus(elements)
    }

    @JvmStatic
    fun <T> minus(sequence: Sequence<T>, element: T): Sequence<T> {
        return sequence.minus(element)
    }

    @JvmStatic
    fun <T> minus(sequence: Sequence<T>, elements: Array<out T>): Sequence<T> {
        return sequence.minus(elements)
    }

    @JvmStatic
    fun <T> minus(sequence: Sequence<T>, elements: Iterable<T>): Sequence<T> {
        return sequence.minus(elements)
    }

    @JvmStatic
    fun <T, C : MutableCollection<in T>> toCollection(sequence: Sequence<T>, destination: C): C {
        return sequence.toCollection(destination)
    }

    @JvmStatic
    fun <T> toSet(sequence: Sequence<T>): Set<T> {
        return sequence.toSet()
    }

    @JvmStatic
    fun <T> toMutableSet(sequence: Sequence<T>): MutableSet<T> {
        return sequence.toMutableSet()
    }

    @JvmStatic
    fun <T> toHashSet(sequence: Sequence<T>): HashSet<T> {
        return sequence.toHashSet()
    }

    @JvmStatic
    fun <T> toSortedSet(sequence: Sequence<T>): SortedSet<T> {
        return toSortedSet(sequence, Sort.selfComparableComparator())
    }

    @JvmStatic
    fun <T> toSortedSet(sequence: Sequence<T>, comparator: Comparator<in T>): SortedSet<T> {
        return sequence.toSortedSet(comparator)
    }

    @JvmStatic
    fun <T> toList(sequence: Sequence<T>): List<T> {
        return sequence.toList()
    }

    @JvmStatic
    fun <T> toMutableList(sequence: Sequence<T>): MutableList<T> {
        return sequence.toMutableList()
    }

    @JvmStatic
    fun <T> toStream(sequence: Sequence<T>): Stream<T> {
        return toStream(sequence, false)
    }

    @JvmStatic
    fun <T> toStream(sequence: Sequence<T>, parallel: Boolean): Stream<T> {
        return StreamSupport.stream(sequence.asIterable().spliterator(), parallel)
    }

    @JvmStatic
    fun <T> toArray(sequence: Sequence<T>): Array<Any?> {
        val list = toCollection(sequence, LinkedList())
        return list.toArray()
    }

    @JvmStatic
    inline fun <T> toArray(sequence: Sequence<T>, generator: (size: Int) -> Array<T>): Array<T> {
        val list = toCollection(sequence, LinkedList())
        return list.toArray(generator(list.size))
    }

    @JvmStatic
    inline fun <reified T> toTypedArray(sequence: Sequence<T>): Array<T> {
        val list = toCollection(sequence, LinkedList())
        return list.toTypedArray()
    }

    @JvmStatic
    fun <T> toBooleanArray(sequence: Sequence<T>): BooleanArray {
        return toBooleanArray(sequence) { To.toBoolean(it) }
    }

    @JvmStatic
    inline fun <T> toBooleanArray(sequence: Sequence<T>, selector: (T) -> Boolean): BooleanArray {
        val list = toCollection(sequence, LinkedList())
        val result = BooleanArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    @JvmStatic
    fun <T> toByteArray(sequence: Sequence<T>): ByteArray {
        return toByteArray(sequence) { To.toByte(it) }
    }

    @JvmStatic
    inline fun <T> toByteArray(sequence: Sequence<T>, selector: (T) -> Byte): ByteArray {
        val list = toCollection(sequence, LinkedList())
        val result = ByteArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    @JvmStatic
    fun <T> toShortArray(sequence: Sequence<T>): ShortArray {
        return toShortArray(sequence) { To.toShort(it) }
    }

    @JvmStatic
    inline fun <T> toShortArray(sequence: Sequence<T>, selector: (T) -> Short): ShortArray {
        val list = toCollection(sequence, LinkedList())
        val result = ShortArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    @JvmStatic
    fun <T> toCharArray(sequence: Sequence<T>): CharArray {
        return toCharArray(sequence) { To.toChar(it) }
    }

    @JvmStatic
    inline fun <T> toCharArray(sequence: Sequence<T>, selector: (T) -> Char): CharArray {
        val list = toCollection(sequence, LinkedList())
        val result = CharArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    @JvmStatic
    fun <T> toIntArray(sequence: Sequence<T>): IntArray {
        return toIntArray(sequence) { To.toInt(it) }
    }

    @JvmStatic
    inline fun <T> toIntArray(sequence: Sequence<T>, selector: (T) -> Int): IntArray {
        val list = toCollection(sequence, LinkedList())
        val result = IntArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    @JvmStatic
    fun <T> toLongArray(sequence: Sequence<T>): LongArray {
        return toLongArray(sequence) { To.toLong(it) }
    }

    @JvmStatic
    inline fun <T> toLongArray(sequence: Sequence<T>, selector: (T) -> Long): LongArray {
        val list = toCollection(sequence, LinkedList())
        val result = LongArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    @JvmStatic
    fun <T> toFloatArray(sequence: Sequence<T>): FloatArray {
        return toFloatArray(sequence) { To.toFloat(it) }
    }

    @JvmStatic
    inline fun <T> toFloatArray(sequence: Sequence<T>, selector: (T) -> Float): FloatArray {
        val list = toCollection(sequence, LinkedList())
        val result = FloatArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }

    @JvmStatic
    fun <T> toDoubleArray(sequence: Sequence<T>): DoubleArray {
        return toDoubleArray(sequence) { To.toDouble(it) }
    }

    @JvmStatic
    inline fun <T> toDoubleArray(sequence: Sequence<T>, selector: (T) -> Double): DoubleArray {
        val list = toCollection(sequence, LinkedList())
        val result = DoubleArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }
}
