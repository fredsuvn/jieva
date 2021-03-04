package xyz.srclab.common.collect

import xyz.srclab.common.base.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random

class SequenceOps<T>(private var sequence: Sequence<T>) : Sequence<T> {

    fun finalSequence(): Sequence<T> {
        return sequence
    }

    override fun iterator(): Iterator<T> {
        return finalSequence().iterator()
    }

    fun contains(element: T): Boolean {
        return finalSequence().contains(element)
    }

    fun containsAll(elements: Array<out T>): Boolean {
        return finalSequence().containsAll(elements)
    }

    fun containsAll(elements: Iterable<T>): Boolean {
        return finalSequence().containsAll(elements)
    }

    fun containsAll(elements: Collection<T>): Boolean {
        return finalSequence().containsAll(elements)
    }

    fun count(): Int {
        return finalSequence().count()
    }

    fun count(predicate: (T) -> Boolean): Int {
        return finalSequence().count(predicate)
    }

    fun isEmpty(): Boolean {
        return finalSequence().isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return finalSequence().isNotEmpty()
    }

    fun any(): Boolean {
        return finalSequence().any()
    }

    fun any(predicate: (T) -> Boolean): Boolean {
        return finalSequence().any(predicate)
    }

    fun none(): Boolean {
        return finalSequence().none()
    }

    fun none(predicate: (T) -> Boolean): Boolean {
        return finalSequence().none(predicate)
    }

    fun all(predicate: (T) -> Boolean): Boolean {
        return finalSequence().all(predicate)
    }

    fun first(): T {
        return finalSequence().first()
    }

    fun first(predicate: (T) -> Boolean): T {
        return finalSequence().first(predicate)
    }

    fun firstOrNull(): T? {
        return finalSequence().firstOrNull()
    }

    fun firstOrNull(predicate: (T) -> Boolean): T? {
        return finalSequence().firstOrNull(predicate)
    }

    fun last(): T {
        return finalSequence().last()
    }

    fun last(predicate: (T) -> Boolean): T {
        return finalSequence().last(predicate)
    }

    fun lastOrNull(): T? {
        return finalSequence().lastOrNull()
    }

    fun lastOrNull(predicate: (T) -> Boolean): T? {
        return finalSequence().lastOrNull(predicate)
    }

    fun elementAt(index: Int): T {
        return finalSequence().elementAt(index)
    }

    fun elementAtOrElse(index: Int, defaultValue: (index: Int) -> T): T {
        return finalSequence().elementAtOrElse(index, defaultValue)
    }

    fun elementAtOrNull(index: Int): T? {
        return finalSequence().elementAtOrNull(index)
    }

    fun find(predicate: (T) -> Boolean): T? {
        return finalSequence().find(predicate)
    }

    fun findLast(predicate: (T) -> Boolean): T? {
        return finalSequence().findLast(predicate)
    }

    fun indexOf(element: T): Int {
        return finalSequence().indexOf(element)
    }

    fun indexOf(predicate: (T) -> Boolean): Int {
        return finalSequence().indexOf(predicate)
    }

    fun lastIndexOf(element: T): Int {
        return finalSequence().lastIndexOf(element)
    }

    fun lastIndexOf(predicate: (T) -> Boolean): Int {
        return finalSequence().lastIndexOf(predicate)
    }

    fun take(n: Int): SequenceOps<T> {
        return finalSequence().take(n).toSequenceOps()
    }

    fun takeWhile(predicate: (T) -> Boolean): SequenceOps<T> {
        return finalSequence().takeWhile(predicate).toSequenceOps()
    }

    fun <C : MutableCollection<in T>> takeTo(n: Int, destination: C): C {
        return finalSequence().takeTo(n, destination)
    }

    fun <C : MutableCollection<in T>> takeWhileTo(destination: C, predicate: (T) -> Boolean): C {
        return finalSequence().takeWhileTo(destination, predicate)
    }

    fun <C : MutableCollection<in T>> takeAllTo(destination: C): C {
        return finalSequence().takeAllTo(destination)
    }

    fun drop(n: Int): SequenceOps<T> {
        return finalSequence().drop(n).toSequenceOps()
    }

    fun dropWhile(predicate: (T) -> Boolean): SequenceOps<T> {
        return finalSequence().dropWhile(predicate).toSequenceOps()
    }

    fun <C : MutableCollection<in T>> dropTo(n: Int, destination: C): C {
        return finalSequence().dropTo(n, destination)
    }

    fun <C : MutableCollection<in T>> dropWhileTo(destination: C, predicate: (T) -> Boolean): C {
        return finalSequence().dropWhileTo(destination, predicate)
    }

    fun forEachIndexed(action: (index: Int, T) -> Unit) {
        return finalSequence().forEachIndexed(action)
    }

    fun filter(predicate: (T) -> Boolean): SequenceOps<T> {
        return finalSequence().filter(predicate).toSequenceOps()
    }

    fun filterIndexed(predicate: (index: Int, T) -> Boolean): SequenceOps<T> {
        return finalSequence().filterIndexed(predicate).toSequenceOps()
    }

    fun filterNotNull(): SequenceOps<T> {
        return finalSequence().filterNotNull().toSequenceOps()
    }

    fun <C : MutableCollection<in T>> filterTo(destination: C, predicate: (T) -> Boolean): C {
        return finalSequence().filterTo(destination, predicate)
    }

    fun <C : MutableCollection<in T>> filterIndexedTo(destination: C, predicate: (index: Int, T) -> Boolean): C {
        return finalSequence().filterIndexedTo(destination, predicate)
    }

    fun <C : MutableCollection<in T>> filterNotNullTo(destination: C): C {
        return finalSequence().filterNotNullTo(destination)
    }

    fun <R> map(transform: (T) -> R): SequenceOps<R> {
        return finalSequence().map(transform).toSequenceOps()
    }

    fun <R> mapIndexed(transform: (index: Int, T) -> R): SequenceOps<R> {
        return finalSequence().mapIndexed(transform).toSequenceOps()
    }

    fun <R> mapNotNull(transform: (T) -> R?): SequenceOps<R> {
        return finalSequence().mapNotNull(transform).toSequenceOps()
    }

    fun <R> mapIndexedNotNull(transform: (index: Int, T) -> R?): SequenceOps<R> {
        return finalSequence().mapIndexedNotNull(transform).toSequenceOps()
    }

    fun <R, C : MutableCollection<in R>> mapTo(destination: C, transform: (T) -> R): C {
        return finalSequence().mapTo(destination, transform)
    }

    fun <R, C : MutableCollection<in R>> mapIndexedTo(destination: C, transform: (index: Int, T) -> R): C {
        return finalSequence().mapIndexedTo(destination, transform)
    }

    fun <R : Any, C : MutableCollection<in R>> mapNotNullTo(destination: C, transform: (T) -> R?): C {
        return finalSequence().mapNotNullTo(destination, transform)
    }

    fun <R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(
        destination: C,
        transform: (index: Int, T) -> R?
    ): C {
        return finalSequence().mapIndexedNotNullTo(destination, transform)
    }

    fun <R> flatMap(transform: (T) -> Iterable<R>): SequenceOps<R> {
        return finalSequence().flatMap(transform).toSequenceOps()
    }

    fun <R> flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): SequenceOps<R> {
        return finalSequence().flatMapIndexed(transform).toSequenceOps()
    }

    fun <R, C : MutableCollection<in R>> flatMapTo(destination: C, transform: (T) -> Iterable<R>): C {
        return finalSequence().flatMapTo(destination, transform)
    }

    fun <R, C : MutableCollection<in R>> flatMapIndexedTo(
        destination: C,
        transform: (index: Int, T) -> Iterable<R>
    ): C {
        return finalSequence().flatMapIndexedTo(destination, transform)
    }

    open fun <K, V> associate(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return finalSequence().associate(keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associate(transform: (T) -> Pair<K, V>): MapOps<K, V> {
        return finalSequence().associate(transform).toMapOps()
    }

    open fun <K> associateKeys(keySelector: (T) -> K): MapOps<K, T> {
        return finalSequence().associateKeys(keySelector).toMapOps()
    }

    open fun <K> associateKeys(keys: Iterable<K>): MapOps<K, T> {
        return finalSequence().associateKeys(keys).toMapOps()
    }

    open fun <V> associateValues(valueSelector: (T) -> V): MapOps<T, V> {
        return finalSequence().associateValues(valueSelector).toMapOps()
    }

    open fun <V> associateValues(values: Iterable<V>): MapOps<T, V> {
        return finalSequence().associateValues(values).toMapOps()
    }

    open fun <K, V> associateWithNext(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return finalSequence().associateWithNext(keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associateWithNext(transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return finalSequence().associateWithNext(transform).toMapOps()
    }

    open fun <K, V> associatePairs(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return finalSequence().associatePairs(keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associatePairs(transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return finalSequence().associatePairs(transform).toMapOps()
    }

    open fun <K, V> associatePairs(
        complement: T,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return finalSequence().associatePairs(complement, keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associatePairs(
        complement: T,
        transform: (T, T) -> Pair<K, V>
    ): MapOps<K, V> {
        return finalSequence().associatePairs(complement, transform).toMapOps()
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalSequence().associateTo(destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        transform: (T) -> Pair<K, V>
    ): M {
        return finalSequence().associateTo(destination, transform)
    }

    open fun <K, M : MutableMap<in K, in T>> associateKeysTo(
        destination: M,
        keySelector: (T) -> K
    ): M {
        return finalSequence().associateKeysTo(destination, keySelector)
    }

    open fun <K, M : MutableMap<in K, in T>> associateKeysTo(
        destination: M,
        keys: Iterable<K>
    ): M {
        return finalSequence().associateKeysTo(destination, keys)
    }

    open fun <V, M : MutableMap<in T, in V>> associateValuesTo(
        destination: M,
        valueSelector: (T) -> V
    ): M {
        return finalSequence().associateValuesTo(destination, valueSelector)
    }

    open fun <V, M : MutableMap<in T, in V>> associateValuesTo(
        destination: M,
        values: Iterable<V>
    ): M {
        return finalSequence().associateValuesTo(destination, values)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalSequence().associateWithNextTo(destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        destination: M,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return finalSequence().associateWithNextTo(destination, transform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairsTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalSequence().associatePairsTo(destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairsTo(
        destination: M,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return finalSequence().associatePairsTo(destination, transform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairsTo(
        destination: M,
        complement: T,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalSequence().associatePairsTo(destination, complement, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairsTo(
        destination: M,
        complement: T,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return finalSequence().associatePairsTo(destination, complement, transform)
    }

    fun <K> groupBy(keySelector: (T) -> K): MapOps<K, List<T>> {
        return finalSequence().groupBy(keySelector).toMapOps()
    }

    fun <K, V> groupBy(keySelector: (T) -> K, valueTransform: (T) -> V): MapOps<K, List<V>> {
        return finalSequence().groupBy(keySelector, valueTransform).toMapOps()
    }

    fun <K, M : MutableMap<in K, MutableList<T>>> groupByTo(destination: M, keySelector: (T) -> K): M {
        return finalSequence().groupByTo(destination, keySelector)
    }

    fun <K, V, M : MutableMap<in K, MutableList<V>>> groupByTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalSequence().groupByTo(destination, keySelector, valueTransform)
    }

    fun reduce(operation: (T, T) -> T): T {
        return finalSequence().reduce(operation)
    }

    fun reduceIndexed(operation: (index: Int, T, T) -> T): T {
        return finalSequence().reduceIndexed(operation)
    }

    fun reduceOrNull(operation: (T, T) -> T): T? {
        return finalSequence().reduceOrNull(operation)
    }

    fun reduceIndexedOrNull(operation: (index: Int, T, T) -> T): T? {
        return finalSequence().reduceIndexedOrNull(operation)
    }

    fun <R> reduce(initial: R, operation: (R, T) -> R): R {
        return finalSequence().reduce(initial, operation)
    }

    fun <R> reduceIndexed(initial: R, operation: (index: Int, R, T) -> R): R {
        return finalSequence().reduceIndexed(initial, operation)
    }

    fun <R, V> zip(other: Sequence<R>, transform: (T, R) -> V): SequenceOps<V> {
        return finalSequence().zip(other, transform).toSequenceOps()
    }

    fun <R> zipWithNext(transform: (T, T) -> R): SequenceOps<R> {
        return finalSequence().zipWithNext(transform).toSequenceOps()
    }

    fun chunked(size: Int): SequenceOps<List<T>> {
        return finalSequence().chunked(size).toSequenceOps()
    }

    fun <R> chunked(size: Int, transform: (List<T>) -> R): SequenceOps<R> {
        return finalSequence().chunked(size, transform).toSequenceOps()
    }

    fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): SequenceOps<List<T>> {
        return finalSequence().windowed(size, step, partialWindows).toSequenceOps()
    }

    fun <R> windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): SequenceOps<R> {
        return finalSequence().windowed(size, step, partialWindows, transform).toSequenceOps()
    }

    fun distinct(): SequenceOps<T> {
        return finalSequence().distinct().toSequenceOps()
    }

    fun <K> distinct(selector: (T) -> K): SequenceOps<T> {
        return finalSequence().distinct(selector).toSequenceOps()
    }

    @JvmOverloads
    fun sorted(comparator: Comparator<in T> = castSelfComparableComparator()): SequenceOps<T> {
        return finalSequence().sorted(comparator).toSequenceOps()
    }

    fun shuffled(): SequenceOps<T> {
        return finalSequence().shuffled().toSequenceOps()
    }

    fun shuffled(random: Random): SequenceOps<T> {
        return finalSequence().shuffled(random).toSequenceOps()
    }

    @JvmOverloads
    fun max(comparator: Comparator<in T> = castSelfComparableComparator()): T {
        return finalSequence().max(comparator)
    }

    @JvmOverloads
    fun maxOrNull(comparator: Comparator<in T> = castSelfComparableComparator()): T? {
        return finalSequence().maxOrNull(comparator)
    }

    @JvmOverloads
    fun min(comparator: Comparator<in T> = castSelfComparableComparator()): T {
        return finalSequence().min(comparator)
    }

    @JvmOverloads
    fun minOrNull(comparator: Comparator<in T> = castSelfComparableComparator()): T? {
        return finalSequence().minOrNull(comparator)
    }

    @JvmOverloads
    fun sumInt(selector: (T) -> Int = { it.toInt() }): Int {
        return finalSequence().sumInt(selector)
    }

    @JvmOverloads
    fun sumLong(selector: (T) -> Long = { it.toLong() }): Long {
        return finalSequence().sumLong(selector)
    }

    @JvmOverloads
    fun sumDouble(selector: (T) -> Double = { it.toDouble() }): Double {
        return finalSequence().sumDouble(selector)
    }

    @JvmOverloads
    fun sumBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
        return finalSequence().sumBigInteger(selector)
    }

    @JvmOverloads
    fun sumBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
        return finalSequence().sumBigDecimal(selector)
    }

    @JvmOverloads
    fun averageInt(selector: (T) -> Int = { it.toInt() }): Int {
        return finalSequence().averageInt(selector)
    }

    @JvmOverloads
    fun averageLong(selector: (T) -> Long = { it.toLong() }): Long {
        return finalSequence().averageLong(selector)
    }

    @JvmOverloads
    fun averageDouble(selector: (T) -> Double = { it.toDouble() }): Double {
        return finalSequence().averageDouble(selector)
    }

    @JvmOverloads
    fun averageBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
        return finalSequence().averageBigInteger(selector)
    }

    @JvmOverloads
    fun averageBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
        return finalSequence().averageBigDecimal(selector)
    }

    fun <C : MutableCollection<in T>> toCollection(destination: C): C {
        return finalSequence().toCollection(destination)
    }

    fun toSet(): Set<T> {
        return finalSequence().toSet()
    }

    fun toImmutableSet(): ImmutableSet<T> {
        return finalSequence().toImmutableSet()
    }

    fun toMutableSet(): MutableSet<T> {
        return finalSequence().toMutableSet()
    }

    fun toHashSet(): HashSet<T> {
        return finalSequence().toHashSet()
    }

    fun toLinkedHashSet(): LinkedHashSet<T> {
        return finalSequence().toLinkedHashSet()
    }

    @JvmOverloads
    fun toSortedSet(comparator: Comparator<in T> = castSelfComparableComparator()): SortedSet<T> {
        return finalSequence().toSortedSet(comparator)
    }

    fun toList(): List<T> {
        return finalSequence().toList()
    }

    fun toImmutableList(): ImmutableList<T> {
        return finalSequence().toImmutableList()
    }

    fun toMutableList(): MutableList<T> {
        return finalSequence().toMutableList()
    }

    fun toArrayList(): ArrayList<T> {
        return finalSequence().toArrayList()
    }

    fun toLinkedList(): LinkedList<T> {
        return finalSequence().toLinkedList()
    }

    @JvmOverloads
    fun toStream(parallel: Boolean = false): Stream<T> {
        return finalSequence().toStream(parallel)
    }

    open fun toArray(): Array<Any?> {
        return finalSequence().toArray()
    }

    open fun toArray(generator: (size: Int) -> Array<T>): Array<T> {
        return finalSequence().toArray(generator)
    }

    open fun toArray(componentType: Class<*>): Array<T> {
        return finalSequence().toArray(componentType)
    }

    open fun <R> toArray(componentType: Class<*>, selector: (T) -> R): Array<R> {
        return finalSequence().toArray(componentType, selector)
    }

    @JvmOverloads
    open fun toBooleanArray(selector: (T) -> Boolean = { it.toBoolean() }): BooleanArray {
        return finalSequence().toBooleanArray(selector)
    }

    @JvmOverloads
    open fun toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
        return finalSequence().toByteArray(selector)
    }

    @JvmOverloads
    open fun toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
        return finalSequence().toShortArray(selector)
    }

    @JvmOverloads
    open fun toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
        return finalSequence().toCharArray(selector)
    }

    @JvmOverloads
    open fun toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
        return finalSequence().toIntArray(selector)
    }

    @JvmOverloads
    open fun toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
        return finalSequence().toLongArray(selector)
    }

    @JvmOverloads
    open fun toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
        return finalSequence().toFloatArray(selector)
    }

    @JvmOverloads
    open fun toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
        return finalSequence().toDoubleArray(selector)
    }

    open fun <A> toAnyArray(componentType: Class<*>): A {
        return finalSequence().toAnyArray(componentType)
    }

    fun plus(element: T): SequenceOps<T> {
        return finalSequence().plus(element).toSequenceOps()
    }

    fun plus(elements: Array<out T>): SequenceOps<T> {
        return finalSequence().plus(elements).toSequenceOps()
    }

    fun plus(elements: Iterable<T>): SequenceOps<T> {
        return finalSequence().plus(elements).toSequenceOps()
    }

    fun plus(elements: Sequence<T>): SequenceOps<T> {
        return finalSequence().plus(elements).toSequenceOps()
    }

    fun minus(element: T): SequenceOps<T> {
        return finalSequence().minus(element).toSequenceOps()
    }

    fun minus(elements: Array<out T>): SequenceOps<T> {
        return finalSequence().minus(elements).toSequenceOps()
    }

    fun minus(elements: Iterable<T>): SequenceOps<T> {
        return finalSequence().minus(elements).toSequenceOps()
    }

    fun minus(elements: Sequence<T>): SequenceOps<T> {
        return finalSequence().minus(elements).toSequenceOps()
    }

    private fun <T> Sequence<T>.toSequenceOps(): SequenceOps<T> {
        sequence = this.asAny()
        return this@SequenceOps.asAny()
    }

    private fun <K, V> Map<K, V>.toMapOps(): MapOps<K, V> {
        return MapOps.opsFor(this)
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(sequence: Sequence<T>): SequenceOps<T> {
            return SequenceOps(sequence)
        }

        @JvmStatic
        fun <T> opsFor(iterable: Iterable<T>): SequenceOps<T> {
            return SequenceOps(iterable.asSequence())
        }
    }
}