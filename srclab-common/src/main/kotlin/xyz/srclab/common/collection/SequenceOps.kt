package xyz.srclab.common.collection

import xyz.srclab.common.base.*
import xyz.srclab.common.collection.BaseIterableOps.Companion.forEachIndexed
import xyz.srclab.common.collection.BaseIterableOps.Companion.toSet
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet
import kotlin.random.Random
import kotlin.streams.asStream
import kotlin.sequences.all as allKt
import kotlin.sequences.any as anyKt
import kotlin.sequences.asSequence as asSequenceKt
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
import kotlin.sequences.toHashSet as toHashSetKt
import kotlin.sequences.toList as toListKt
import kotlin.sequences.toMutableList as toMutableListKt
import kotlin.sequences.toMutableSet as toMutableSetKt
import kotlin.sequences.toSet as toSetKt
import kotlin.sequences.toSortedSet as toSortedSetKt
import kotlin.sequences.windowed as windowedKt
import kotlin.sequences.zip as zipKt
import kotlin.sequences.zipWithNext as zipWithNextKt

class SequenceOps<T>(private var sequence: Sequence<T>) : Iterable<T> {

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

    fun <K, V> associate(keySelector: (T) -> K, valueTransform: (T) -> V): MapOps<K, V> {
        return finalSequence().associate(keySelector, valueTransform).toMapOps()
    }

    fun <K, V> associate(transform: (T) -> Pair<K, V>): MapOps<K, V> {
        return finalSequence().associate(transform).toMapOps()
    }

    fun <K> associateKey(keySelector: (T) -> K): MapOps<K, T> {
        return finalSequence().associateKey(keySelector).toMapOps()
    }

    fun <V> associateValue(valueSelector: (T) -> V): MapOps<T, V> {
        return finalSequence().associateValue(valueSelector).toMapOps()
    }

    fun <K, V> associatePair(keySelector: (T) -> K, valueTransform: (T) -> V): MapOps<K, V> {
        return finalSequence().associatePair(keySelector, valueTransform).toMapOps()
    }

    fun <K, V> associatePair(transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return finalSequence().associatePair(transform).toMapOps()
    }

    fun <K, V> associatePair(complement: T, keySelector: (T) -> K, valueTransform: (T) -> V): MapOps<K, V> {
        return finalSequence().associatePair(complement, keySelector, valueTransform).toMapOps()
    }

    fun <K, V> associatePair(complement: T, transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return finalSequence().associatePairTo(complement, LinkedHashMap(), transform).toMapOps()
    }

    fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M, keySelector: (T) -> K, valueTransform: (T) -> V
    ): M {
        return finalSequence().associateTo(destination, keySelector, valueTransform)
    }

    fun <K, V, M : MutableMap<in K, in V>> associateTo(destination: M, transform: (T) -> Pair<K, V>): M {
        return finalSequence().associateTo(destination, transform)
    }

    fun <K, M : MutableMap<in K, in T>> associateKeyTo(destination: M, keySelector: (T) -> K): M {
        return finalSequence().associateKeyTo(destination, keySelector)
    }

    fun <V, M : MutableMap<in T, in V>> associateValueTo(destination: M, valueSelector: (T) -> V): M {
        return finalSequence().associateValueTo(destination, valueSelector)
    }

    fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalSequence().associatePairTo(destination, keySelector, valueTransform)
    }

    fun <K, V, M : MutableMap<in K, in V>> associatePairTo(destination: M, transform: (T, T) -> Pair<K, V>): M {
        return finalSequence().associatePairTo(destination, transform)
    }

    fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        complement: T,
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalSequence().associatePairTo(complement, destination, keySelector, valueTransform)
    }

    fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        complement: T,
        destination: M,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return finalSequence().associatePairTo(complement, destination, transform)
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

    fun <R, V> zip(other: Sequence<out R>, transform: (T, R) -> V): SequenceOps<V> {
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

    fun toCollection(): Collection<T> {
        return finalSequence().toCollection()
    }

    fun toMutableCollection(): MutableCollection<T> {
        return finalSequence().toMutableCollection()
    }

    fun toSet(): Set<T> {
        return finalSequence().toSet()
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

    fun toSequence(): Sequence<T> {
        return finalSequence().toSequence()
    }

    fun toArray(): Array<Any?> {
        return finalSequence().toArray()
    }

    fun toArray(generator: (size: Int) -> Array<T>): Array<T> {
        return finalSequence().toArray(generator)
    }

    fun toArray(componentType: Class<*>): Array<T> {
        return finalSequence().toArray(componentType)
    }

    @JvmOverloads
    fun toBooleanArray(selector: (T) -> Boolean = { it.toBoolean() }): BooleanArray {
        return finalSequence().toBooleanArray(selector)
    }

    @JvmOverloads
    fun toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
        return finalSequence().toByteArray(selector)
    }

    @JvmOverloads
    fun toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
        return finalSequence().toShortArray(selector)
    }

    @JvmOverloads
    fun toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
        return finalSequence().toCharArray(selector)
    }

    @JvmOverloads
    fun toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
        return finalSequence().toIntArray(selector)
    }

    @JvmOverloads
    fun toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
        return finalSequence().toLongArray(selector)
    }

    @JvmOverloads
    fun toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
        return finalSequence().toFloatArray(selector)
    }

    @JvmOverloads
    fun toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
        return finalSequence().toDoubleArray(selector)
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

    override fun iterator(): Iterator<T> {
        return finalSequence().iterator()
    }

    fun finalSequence(): Sequence<T> {
        return sequence
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

        @JvmStatic
        fun <T> Sequence<T>.contains(element: T): Boolean {
            return this.containsKt(element)
        }

        @JvmStatic
        fun <T> Sequence<T>.containsAll(elements: Array<out T>): Boolean {
            return this.containsAll(elements.toSet())
        }

        @JvmStatic
        fun <T> Sequence<T>.containsAll(elements: Iterable<T>): Boolean {
            return this.containsAll(elements.toSet())
        }

        @JvmStatic
        fun <T> Sequence<T>.containsAll(elements: Collection<T>): Boolean {
            return this.toSet().containsAll(elements)
        }

        @JvmStatic
        fun <T> Sequence<T>.count(): Int {
            return this.countKt()
        }

        @JvmStatic
        inline fun <T> Sequence<T>.count(predicate: (T) -> Boolean): Int {
            return this.countKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.isEmpty(): Boolean {
            var hasElement = false
            for (t in this) {
                hasElement = true
                break
            }
            return !hasElement
        }

        @JvmStatic
        fun <T> Sequence<T>.isNotEmpty(): Boolean {
            return !this.isEmpty()
        }

        @JvmStatic
        fun <T> Sequence<T>.any(): Boolean {
            return this.anyKt()
        }

        @JvmStatic
        inline fun <T> Sequence<T>.any(predicate: (T) -> Boolean): Boolean {
            return this.anyKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.none(): Boolean {
            return this.noneKt()
        }

        @JvmStatic
        inline fun <T> Sequence<T>.none(predicate: (T) -> Boolean): Boolean {
            return this.noneKt(predicate)
        }

        @JvmStatic
        inline fun <T> Sequence<T>.all(predicate: (T) -> Boolean): Boolean {
            return this.allKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.first(): T {
            return this.firstKt()
        }

        @JvmStatic
        inline fun <T> Sequence<T>.first(predicate: (T) -> Boolean): T {
            return this.firstKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.firstOrNull(): T? {
            return this.firstOrNullKt()
        }

        @JvmStatic
        inline fun <T> Sequence<T>.firstOrNull(predicate: (T) -> Boolean): T? {
            return this.firstOrNullKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.last(): T {
            return this.lastKt()
        }

        @JvmStatic
        inline fun <T> Sequence<T>.last(predicate: (T) -> Boolean): T {
            return this.lastKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.lastOrNull(): T? {
            return this.lastOrNullKt()
        }

        @JvmStatic
        inline fun <T> Sequence<T>.lastOrNull(predicate: (T) -> Boolean): T? {
            return this.lastOrNullKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.elementAt(index: Int): T {
            return this.elementAtKt(index)
        }

        @JvmStatic
        fun <T> Sequence<T>.elementAtOrElse(index: Int, defaultValue: (index: Int) -> T): T {
            return this.elementAtOrElseKt(index, defaultValue)
        }

        @JvmStatic
        fun <T> Sequence<T>.elementAtOrNull(index: Int): T? {
            return this.elementAtOrNullKt(index)
        }

        @JvmStatic
        inline fun <T> Sequence<T>.find(predicate: (T) -> Boolean): T? {
            return this.findKt(predicate)
        }

        @JvmStatic
        inline fun <T> Sequence<T>.findLast(predicate: (T) -> Boolean): T? {
            return this.firstKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.indexOf(element: T): Int {
            return this.indexOfKt(element)
        }

        @JvmStatic
        inline fun <T> Sequence<T>.indexOf(predicate: (T) -> Boolean): Int {
            return this.indexOfFirstKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.lastIndexOf(element: T): Int {
            return this.lastIndexOfKt(element)
        }

        @JvmStatic
        inline fun <T> Sequence<T>.lastIndexOf(predicate: (T) -> Boolean): Int {
            return this.indexOfLastKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.take(n: Int): Sequence<T> {
            return this.takeKt(n)
        }

        @JvmStatic
        fun <T> Sequence<T>.takeWhile(predicate: (T) -> Boolean): Sequence<T> {
            return this.takeWhileKt(predicate)
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> Sequence<T>.takeTo(n: Int, destination: C): C {
            destination.addAll(this.take(n))
            return destination
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> Sequence<T>.takeWhileTo(
            destination: C,
            predicate: (T) -> Boolean
        ): C {
            destination.addAll(this.takeWhile(predicate))
            return destination
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> Sequence<T>.takeAllTo(destination: C): C {
            destination.addAll(this)
            return destination
        }

        @JvmStatic
        fun <T> Sequence<T>.drop(n: Int): Sequence<T> {
            return this.dropKt(n)
        }

        @JvmStatic
        fun <T> Sequence<T>.dropWhile(predicate: (T) -> Boolean): Sequence<T> {
            return this.dropWhileKt(predicate)
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> Sequence<T>.dropTo(n: Int, destination: C): C {
            destination.addAll(this.drop(n))
            return destination
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> Sequence<T>.dropWhileTo(
            destination: C,
            predicate: (T) -> Boolean
        ): C {
            destination.addAll(this.dropWhile(predicate))
            return destination
        }

        @JvmStatic
        inline fun <T> Sequence<T>.forEachIndexed(action: (index: Int, T) -> Unit) {
            return this.forEachIndexedKt(action)
        }

        @JvmStatic
        fun <T> Sequence<T>.filter(predicate: (T) -> Boolean): Sequence<T> {
            return this.filterKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.filterIndexed(predicate: (index: Int, T) -> Boolean): Sequence<T> {
            return this.filterIndexedKt(predicate)
        }

        @JvmStatic
        fun <T> Sequence<T>.filterNotNull(): Sequence<T> {
            return this.filterNotNullKt()
        }

        @JvmStatic
        inline fun <T, C : MutableCollection<in T>> Sequence<T>.filterTo(destination: C, predicate: (T) -> Boolean): C {
            return this.filterToKt(destination, predicate)
        }

        @JvmStatic
        inline fun <T, C : MutableCollection<in T>> Sequence<T>.filterIndexedTo(
            destination: C,
            predicate: (index: Int, T) -> Boolean
        ): C {
            return this.filterIndexedToKt(destination, predicate)
        }

        @JvmStatic
        fun <C : MutableCollection<in T>, T> Sequence<T>.filterNotNullTo(destination: C): C {
            return this.filterTo(destination, { it !== null })
        }

        @JvmStatic
        fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
            return this.mapKt(transform)
        }

        @JvmStatic
        fun <T, R> Sequence<T>.mapIndexed(transform: (index: Int, T) -> R): Sequence<R> {
            return this.mapIndexedKt(transform)
        }

        @JvmStatic
        fun <T, R : Any> Sequence<T>.mapNotNull(transform: (T) -> R?): Sequence<R> {
            return this.mapNotNullKt(transform)
        }

        @JvmStatic
        fun <T, R : Any> Sequence<T>.mapIndexedNotNull(transform: (index: Int, T) -> R?): Sequence<R> {
            return this.mapIndexedNotNullKt(transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.mapTo(destination: C, transform: (T) -> R): C {
            return this.mapToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.mapIndexedTo(
            destination: C,
            transform: (index: Int, T) -> R
        ): C {
            return this.mapIndexedToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R : Any, C : MutableCollection<in R>> Sequence<T>.mapNotNullTo(
            destination: C,
            transform: (T) -> R?
        ): C {
            return this.mapNotNullToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R : Any, C : MutableCollection<in R>> Sequence<T>.mapIndexedNotNullTo(
            destination: C,
            transform: (index: Int, T) -> R?
        ): C {
            return this.mapIndexedNotNullToKt(destination, transform)
        }

        @JvmStatic
        fun <T, R> Sequence<T>.flatMap(transform: (T) -> Iterable<R>): Sequence<R> {
            return this.flatMapKt(transform)
        }

        @JvmStatic
        fun <T, R> Sequence<T>.flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): Sequence<R> {
            return this.flatMapIndexedKt(transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.flatMapTo(
            destination: C,
            transform: (T) -> Iterable<R>
        ): C {
            return this.flatMapToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> Sequence<T>.flatMapIndexedTo(
            destination: C,
            transform: (index: Int, T) -> Iterable<R>
        ): C {
            return this.flatMapIndexedToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, K, V> Sequence<T>.associate(
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return this.associateByKt(keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> Sequence<T>.associate(transform: (T) -> Pair<K, V>): Map<K, V> {
            return this.associateKt(transform)
        }

        @JvmStatic
        inline fun <T, K> Sequence<T>.associateKey(keySelector: (T) -> K): Map<K, T> {
            return this.associateByKt(keySelector)
        }

        @JvmStatic
        inline fun <T, V> Sequence<T>.associateValue(valueSelector: (T) -> V): Map<T, V> {
            return this.associateWithKt(valueSelector)
        }

        @JvmStatic
        inline fun <T, K, V> Sequence<T>.associatePair(
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return this.associatePairTo(LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> Sequence<T>.associatePair(transform: (T, T) -> Pair<K, V>): Map<K, V> {
            return this.associatePairTo(LinkedHashMap(), transform)
        }

        @JvmStatic
        inline fun <T, K, V> Sequence<T>.associatePair(
            complement: T,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return this.associatePairTo(complement, LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> Sequence<T>.associatePair(
            complement: T,
            transform: (T, T) -> Pair<K, V>
        ): Map<K, V> {
            return this.associatePairTo(complement, LinkedHashMap(), transform)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associateTo(
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): M {
            return this.associateByToKt(destination, keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associateTo(
            destination: M,
            transform: (T) -> Pair<K, V>
        ): M {
            return this.associateToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, K, M : MutableMap<in K, in T>> Sequence<T>.associateKeyTo(
            destination: M,
            keySelector: (T) -> K
        ): M {
            return this.associateByToKt(destination, keySelector)
        }

        @JvmStatic
        inline fun <T, V, M : MutableMap<in T, in V>> Sequence<T>.associateValueTo(
            destination: M,
            valueSelector: (T) -> V
        ): M {
            return this.associateWithToKt(destination, valueSelector)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associatePairTo(
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

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associatePairTo(
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

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associatePairTo(
            complement: T,
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
                    val k = keySelector(ik)
                    val v = valueTransform(complement)
                    destination.put(k, v)
                    break
                }
            }
            return destination
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Sequence<T>.associatePairTo(
            complement: T,
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
                    val pair = transform(ik, complement)
                    destination.put(pair.first, pair.second)
                    break
                }
            }
            return destination
        }

        @JvmStatic
        inline fun <T, K> Sequence<T>.groupBy(keySelector: (T) -> K): Map<K, List<T>> {
            return this.groupByKt(keySelector)
        }

        @JvmStatic
        inline fun <T, K, V> Sequence<T>.groupBy(
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, List<V>> {
            return this.groupByKt(keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Sequence<T>.groupByTo(
            destination: M,
            keySelector: (T) -> K
        ): M {
            return this.groupByToKt(destination, keySelector)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Sequence<T>.groupByTo(
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): M {
            return this.groupByToKt(destination, keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <S, T : S> Sequence<T>.reduce(operation: (S, T) -> S): S {
            return this.reduceKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> Sequence<T>.reduceIndexed(operation: (index: Int, S, T) -> S): S {
            return this.reduceIndexedKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> Sequence<T>.reduceOrNull(operation: (S, T) -> S): S? {
            return this.reduceOrNullKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> Sequence<T>.reduceIndexedOrNull(operation: (index: Int, S, T) -> S): S? {
            return this.reduceIndexedOrNullKt(operation)
        }

        @JvmStatic
        inline fun <T, R> Sequence<T>.reduce(initial: R, operation: (R, T) -> R): R {
            return this.foldKt(initial, operation)
        }

        @JvmStatic
        inline fun <T, R> Sequence<T>.reduceIndexed(initial: R, operation: (index: Int, R, T) -> R): R {
            return this.foldIndexedKt(initial, operation)
        }

        @JvmStatic
        fun <T, R, V> Sequence<T>.zip(other: Sequence<out R>, transform: (T, R) -> V): Sequence<V> {
            return this.zipKt(other, transform)
        }

        @JvmStatic
        fun <T, R> Sequence<T>.zipWithNext(transform: (T, T) -> R): Sequence<R> {
            return this.zipWithNextKt(transform)
        }

        @JvmStatic
        fun <T> Sequence<T>.chunked(size: Int): Sequence<List<T>> {
            return this.chunkedKt(size)
        }

        @JvmStatic
        fun <T, R> Sequence<T>.chunked(size: Int, transform: (List<T>) -> R): Sequence<R> {
            return this.chunkedKt(size, transform)
        }

        @JvmStatic
        fun <T> Sequence<T>.windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): Sequence<List<T>> {
            return this.windowedKt(size, step, partialWindows)
        }

        @JvmStatic
        fun <T, R> Sequence<T>.windowed(
            size: Int,
            step: Int = 1,
            partialWindows: Boolean = false,
            transform: (List<T>) -> R
        ): Sequence<R> {
            return this.windowedKt(size, step, partialWindows, transform)
        }

        @JvmStatic
        fun <T> Sequence<T>.distinct(): Sequence<T> {
            return this.distinctKt()
        }

        @JvmStatic
        fun <T, K> Sequence<T>.distinct(selector: (T) -> K): Sequence<T> {
            return this.distinctByKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.sorted(comparator: Comparator<in T> = castSelfComparableComparator()): Sequence<T> {
            return this.sortedWithKt(comparator)
        }

        @JvmStatic
        fun <T> Sequence<T>.shuffled(): Sequence<T> {
            return this.shuffledKt()
        }

        @JvmStatic
        fun <T> Sequence<T>.shuffled(random: Random): Sequence<T> {
            return this.shuffledKt(random)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.max(comparator: Comparator<in T> = castSelfComparableComparator()): T {
            return this.maxOrNull(comparator).notNull()
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.maxOrNull(comparator: Comparator<in T> = castSelfComparableComparator()): T? {
            return this.maxWithOrNullKt(comparator)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.min(comparator: Comparator<in T> = castSelfComparableComparator()): T {
            return this.minOrNull(comparator).notNull()
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.minOrNull(comparator: Comparator<in T> = castSelfComparableComparator()): T? {
            return this.minWithOrNullKt(comparator)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.sumInt(selector: (T) -> Int = { it.toInt() }): Int {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.sumLong(selector: (T) -> Long = { it.toLong() }): Long {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.sumDouble(selector: (T) -> Double = { it.toDouble() }): Double {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.sumBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.sumBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
            return this.sumOfKt(selector)
        }

        @JvmStatic
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

        @JvmStatic
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

        @JvmStatic
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

        @JvmStatic
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

        @JvmStatic
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

        @JvmStatic
        fun <T> Sequence<T>.toCollection(): Collection<T> {
            return this.toSet()
        }

        @JvmStatic
        fun <T> Sequence<T>.toMutableCollection(): MutableCollection<T> {
            return this.toMutableSet()
        }

        @JvmStatic
        fun <T> Sequence<T>.toSet(): Set<T> {
            return this.toSetKt()
        }

        @JvmStatic
        fun <T> Sequence<T>.toMutableSet(): MutableSet<T> {
            return this.toMutableSetKt()
        }

        @JvmStatic
        fun <T> Sequence<T>.toHashSet(): HashSet<T> {
            return this.toHashSetKt()
        }

        @JvmStatic
        fun <T> Sequence<T>.toLinkedHashSet(): LinkedHashSet<T> {
            return this.takeAllTo(LinkedHashSet())
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.toSortedSet(comparator: Comparator<in T> = castSelfComparableComparator()): SortedSet<T> {
            return this.toSortedSetKt(comparator)
        }

        @JvmStatic
        fun <T> Sequence<T>.toList(): List<T> {
            return this.toListKt()
        }

        @JvmStatic
        fun <T> Sequence<T>.toMutableList(): MutableList<T> {
            return this.toMutableListKt()
        }

        @JvmStatic
        fun <T> Sequence<T>.toArrayList(): ArrayList<T> {
            return this.takeAllTo(ArrayList())
        }

        @JvmStatic
        fun <T> Sequence<T>.toLinkedList(): LinkedList<T> {
            return this.takeAllTo(LinkedList())
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Sequence<T>.toStream(parallel: Boolean = false): Stream<T> {
            return if (parallel) this.asStream().parallel() else this.asStream()
        }

        @JvmStatic
        fun <T> Sequence<T>.toSequence(): Sequence<T> {
            return this.asSequenceKt()
        }

        @JvmStatic
        fun <T> Sequence<T>.toArray(): Array<Any?> {
            val list = this.toLinkedList()
            return list.toArray()
        }

        @JvmStatic
        inline fun <T> Sequence<T>.toArray(generator: (size: Int) -> Array<T>): Array<T> {
            val list = this.toLinkedList()
            return list.toArray(generator(list.size))
        }

        @JvmStatic
        fun <T> Sequence<T>.toArray(componentType: Class<*>): Array<T> {
            val list = this.toLinkedList()
            val array: Array<T> = componentType.componentTypeToArrayInstance(0)
            return list.toArray(array)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.toBooleanArray(selector: (T) -> Boolean = { it.toBoolean() }): BooleanArray {
            val list = this.toList()
            val result = BooleanArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
            val list = this.toList()
            val result = ByteArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
            val list = this.toList()
            val result = ShortArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
            val list = this.toList()
            val result = CharArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
            val list = this.toList()
            val result = IntArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
            val list = this.toList()
            val result = LongArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
            val list = this.toList()
            val result = FloatArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Sequence<T>.toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
            val list = this.toList()
            val result = DoubleArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> Sequence<T>.plus(element: T): Sequence<T> {
            return this.plusKt(element)
        }

        @JvmStatic
        fun <T> Sequence<T>.plus(elements: Array<out T>): Sequence<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Sequence<T>.plus(elements: Iterable<T>): Sequence<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Sequence<T>.plus(elements: Sequence<T>): Sequence<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Sequence<T>.minus(element: T): Sequence<T> {
            return this.minusKt(element)
        }

        @JvmStatic
        fun <T> Sequence<T>.minus(elements: Array<out T>): Sequence<T> {
            return this.minusKt(elements)
        }

        @JvmStatic
        fun <T> Sequence<T>.minus(elements: Iterable<T>): Sequence<T> {
            return this.minusKt(elements)
        }

        @JvmStatic
        fun <T> Sequence<T>.minus(elements: Sequence<T>): Sequence<T> {
            return this.minusKt(elements)
        }
    }
}