package xyz.srclab.common.collect

import xyz.srclab.common.base.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random

abstract class BaseIterableOps<T, I : Iterable<T>, MI : MutableIterable<T>, THIS : BaseIterableOps<T, I, MI, THIS>>
protected constructor(protected var operated: I) : MutableIterable<T> {

    open fun finalIterable(): Iterable<T> {
        return operated
    }

    open fun finalMutableIterable(): MutableIterable<T> {
        return operated.asAny()
    }

    protected open fun <T> Iterable<T>.toIterableOps(): IterableOps<T> {
        return IterableOps.opsFor(this)
    }

    protected open fun <T> List<T>.toListOps(): ListOps<T> {
        return ListOps.opsFor(this)
    }

    protected open fun <T> Set<T>.toSetOps(): SetOps<T> {
        return SetOps.opsFor(this)
    }

    protected open fun <K, V> Map<K, V>.toMapOps(): MapOps<K, V> {
        return MapOps.opsFor(this)
    }

    fun toSequenceOps(): SequenceOps<T> {
        return SequenceOps.opsFor(finalIterable())
    }

    override fun iterator(): MutableIterator<T> {
        return finalMutableIterable().iterator()
    }

    open fun contains(element: T): Boolean {
        return finalIterable().contains(element)
    }

    open fun containsAll(elements: Array<out T>): Boolean {
        return finalIterable().containsAll(elements)
    }

    open fun containsAll(elements: Iterable<T>): Boolean {
        return finalIterable().containsAll(elements)
    }

    open fun containsAll(elements: Collection<T>): Boolean {
        return finalIterable().containsAll(elements)
    }

    open fun count(): Int {
        return finalIterable().count()
    }

    open fun count(predicate: (T) -> Boolean): Int {
        return finalIterable().count(predicate)
    }

    open fun isEmpty(): Boolean {
        return finalIterable().isEmpty()
    }

    open fun isNotEmpty(): Boolean {
        return finalIterable().isNotEmpty()
    }

    open fun any(): Boolean {
        return finalIterable().any()
    }

    open fun any(predicate: (T) -> Boolean): Boolean {
        return finalIterable().any(predicate)
    }

    open fun none(): Boolean {
        return finalIterable().none()
    }

    open fun none(predicate: (T) -> Boolean): Boolean {
        return finalIterable().none(predicate)
    }

    open fun all(predicate: (T) -> Boolean): Boolean {
        return finalIterable().all(predicate)
    }

    open fun first(): T {
        return finalIterable().first()
    }

    open fun first(predicate: (T) -> Boolean): T {
        return finalIterable().first(predicate)
    }

    open fun firstOrNull(): T? {
        return finalIterable().firstOrNull()
    }

    open fun firstOrNull(predicate: (T) -> Boolean): T? {
        return finalIterable().firstOrNull(predicate)
    }

    open fun last(): T {
        return finalIterable().last()
    }

    open fun last(predicate: (T) -> Boolean): T {
        return finalIterable().last(predicate)
    }

    open fun lastOrNull(): T? {
        return finalIterable().lastOrNull()
    }

    open fun lastOrNull(predicate: (T) -> Boolean): T? {
        return finalIterable().lastOrNull(predicate)
    }

    open fun random(): T {
        return finalIterable().random()
    }

    open fun random(random: Random): T {
        return finalIterable().random(random)
    }

    open fun randomOrNull(): T? {
        return finalIterable().randomOrNull()
    }

    open fun randomOrNull(random: Random): T? {
        return finalIterable().randomOrNull(random)
    }

    open fun elementAt(index: Int): T {
        return finalIterable().elementAt(index)
    }

    open fun elementAtOrElse(index: Int, defaultValue: (index: Int) -> T): T {
        return finalIterable().elementAtOrElse(index, defaultValue)
    }

    open fun elementAtOrNull(index: Int): T? {
        return finalIterable().elementAtOrNull(index)
    }

    open fun find(predicate: (T) -> Boolean): T? {
        return finalIterable().find(predicate)
    }

    open fun findLast(predicate: (T) -> Boolean): T? {
        return finalIterable().findLast(predicate)
    }

    open fun indexOf(element: T): Int {
        return finalIterable().indexOf(element)
    }

    open fun indexOf(predicate: (T) -> Boolean): Int {
        return finalIterable().indexOf(predicate)
    }

    open fun lastIndexOf(element: T): Int {
        return finalIterable().lastIndexOf(element)
    }

    open fun lastIndexOf(predicate: (T) -> Boolean): Int {
        return finalIterable().lastIndexOf(predicate)
    }

    open fun take(n: Int): ListOps<T> {
        return finalIterable().take(n).toListOps()
    }

    open fun takeWhile(predicate: (T) -> Boolean): ListOps<T> {
        return finalIterable().takeWhile(predicate).toListOps()
    }

    open fun <C : MutableCollection<in T>> takeTo(n: Int, destination: C): C {
        return finalIterable().takeTo(n, destination)
    }

    open fun <C : MutableCollection<in T>> takeWhileTo(destination: C, predicate: (T) -> Boolean): C {
        return finalIterable().takeWhileTo(destination, predicate)
    }

    open fun <C : MutableCollection<in T>> takeAllTo(destination: C): C {
        return finalIterable().takeAllTo(destination)
    }

    open fun drop(n: Int): ListOps<T> {
        return finalIterable().drop(n).toListOps()
    }

    open fun dropWhile(predicate: (T) -> Boolean): ListOps<T> {
        return finalIterable().dropWhile(predicate).toListOps()
    }

    open fun <C : MutableCollection<in T>> dropTo(n: Int, destination: C): C {
        return finalIterable().dropTo(n, destination)
    }

    open fun <C : MutableCollection<in T>> dropWhileTo(destination: C, predicate: (T) -> Boolean): C {
        return finalIterable().dropWhileTo(destination, predicate)
    }

    open fun forEachIndexed(action: (index: Int, T) -> Unit) {
        return finalIterable().forEachIndexed(action)
    }

    open fun filter(predicate: (T) -> Boolean): ListOps<T> {
        return finalIterable().filter(predicate).toListOps()
    }

    open fun filterIndexed(predicate: (index: Int, T) -> Boolean): ListOps<T> {
        return finalIterable().filterIndexed(predicate).toListOps()
    }

    open fun filterNotNull(): ListOps<T> {
        return finalIterable().filterNotNull().toListOps()
    }

    open fun <C : MutableCollection<in T>> filterTo(destination: C, predicate: (T) -> Boolean): C {
        return finalIterable().filterTo(destination, predicate)
    }

    open fun <C : MutableCollection<in T>> filterIndexedTo(destination: C, predicate: (index: Int, T) -> Boolean): C {
        return finalIterable().filterIndexedTo(destination, predicate)
    }

    open fun <C : MutableCollection<in T>> filterNotNullTo(destination: C): C {
        return finalIterable().filterNotNullTo(destination)
    }

    open fun <R> map(transform: (T) -> R): ListOps<R> {
        return finalIterable().map(transform).toListOps()
    }

    open fun <R> mapIndexed(transform: (index: Int, T) -> R): ListOps<R> {
        return finalIterable().mapIndexed(transform).toListOps()
    }

    open fun <R> mapNotNull(transform: (T) -> R?): ListOps<R> {
        return finalIterable().mapNotNull(transform).toListOps()
    }

    open fun <R> mapIndexedNotNull(transform: (index: Int, T) -> R?): ListOps<R> {
        return finalIterable().mapIndexedNotNull(transform).toListOps()
    }

    open fun <R, C : MutableCollection<in R>> mapTo(destination: C, transform: (T) -> R): C {
        return finalIterable().mapTo(destination, transform)
    }

    open fun <R, C : MutableCollection<in R>> mapIndexedTo(destination: C, transform: (index: Int, T) -> R): C {
        return finalIterable().mapIndexedTo(destination, transform)
    }

    open fun <R : Any, C : MutableCollection<in R>> mapNotNullTo(destination: C, transform: (T) -> R?): C {
        return finalIterable().mapNotNullTo(destination, transform)
    }

    open fun <R : Any, C : MutableCollection<in R>> mapIndexedNotNullTo(
        destination: C,
        transform: (index: Int, T) -> R?
    ): C {
        return finalIterable().mapIndexedNotNullTo(destination, transform)
    }

    open fun <R> flatMap(transform: (T) -> Iterable<R>): ListOps<R> {
        return finalIterable().flatMap(transform).toListOps()
    }

    open fun <R> flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): ListOps<R> {
        return finalIterable().flatMapIndexed(transform).toListOps()
    }

    open fun <R, C : MutableCollection<in R>> flatMapTo(destination: C, transform: (T) -> Iterable<R>): C {
        return finalIterable().flatMapTo(destination, transform)
    }

    open fun <R, C : MutableCollection<in R>> flatMapIndexedTo(
        destination: C,
        transform: (index: Int, T) -> Iterable<R>
    ): C {
        return finalIterable().flatMapIndexedTo(destination, transform)
    }

    open fun <K, V> associate(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return finalIterable().associate(keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associate(transform: (T) -> Pair<K, V>): MapOps<K, V> {
        return finalIterable().associate(transform).toMapOps()
    }

    open fun <K> associateKeys(keySelector: (T) -> K): MapOps<K, T> {
        return finalIterable().associateKeys(keySelector).toMapOps()
    }

    open fun <K> associateKeys(keys: Iterable<K>): MapOps<K, T> {
        return finalIterable().associateKeys(keys).toMapOps()
    }

    open fun <V> associateValues(valueSelector: (T) -> V): MapOps<T, V> {
        return finalIterable().associateValues(valueSelector).toMapOps()
    }

    open fun <V> associateValues(values: Iterable<V>): MapOps<T, V> {
        return finalIterable().associateValues(values).toMapOps()
    }

    open fun <K, V> associateWithNext(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return finalIterable().associateWithNext(keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associateWithNext(transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return finalIterable().associateWithNext(transform).toMapOps()
    }

    open fun <K, V> associatePairs(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return finalIterable().associatePairs(keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associatePairs(transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return finalIterable().associatePairs(transform).toMapOps()
    }

    open fun <K, V> associatePairs(
        complement: T,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): MapOps<K, V> {
        return finalIterable().associatePairs(complement, keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associatePairs(
        complement: T,
        transform: (T, T) -> Pair<K, V>
    ): MapOps<K, V> {
        return finalIterable().associatePairs(complement, transform).toMapOps()
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalIterable().associateTo(destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M,
        transform: (T) -> Pair<K, V>
    ): M {
        return finalIterable().associateTo(destination, transform)
    }

    open fun <K, M : MutableMap<in K, in T>> associateKeysTo(
        destination: M,
        keySelector: (T) -> K
    ): M {
        return finalIterable().associateKeysTo(destination, keySelector)
    }

    open fun <K, M : MutableMap<in K, in T>> associateKeysTo(
        destination: M,
        keys: Iterable<K>
    ): M {
        return finalIterable().associateKeysTo(destination, keys)
    }

    open fun <V, M : MutableMap<in T, in V>> associateValuesTo(
        destination: M,
        valueSelector: (T) -> V
    ): M {
        return finalIterable().associateValuesTo(destination, valueSelector)
    }

    open fun <V, M : MutableMap<in T, in V>> associateValuesTo(
        destination: M,
        values: Iterable<V>
    ): M {
        return finalIterable().associateValuesTo(destination, values)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalIterable().associateWithNextTo(destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateWithNextTo(
        destination: M,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return finalIterable().associateWithNextTo(destination, transform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairsTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalIterable().associatePairsTo(destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairsTo(
        destination: M,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return finalIterable().associatePairsTo(destination, transform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairsTo(
        destination: M,
        complement: T,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalIterable().associatePairsTo(destination, complement, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairsTo(
        destination: M,
        complement: T,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return finalIterable().associatePairsTo(destination, complement, transform)
    }

    open fun <K> groupBy(keySelector: (T) -> K): MapOps<K, List<T>> {
        return finalIterable().groupBy(keySelector).toMapOps()
    }

    open fun <K, V> groupBy(keySelector: (T) -> K, valueTransform: (T) -> V): MapOps<K, List<V>> {
        return finalIterable().groupBy(keySelector, valueTransform).toMapOps()
    }

    open fun <K, M : MutableMap<in K, MutableList<T>>> groupByTo(destination: M, keySelector: (T) -> K): M {
        return finalIterable().groupByTo(destination, keySelector)
    }

    open fun <K, V, M : MutableMap<in K, MutableList<V>>> groupByTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalIterable().groupByTo(destination, keySelector, valueTransform)
    }

    open fun reduce(operation: (T, T) -> T): T {
        return finalIterable().reduce(operation)
    }

    open fun reduceIndexed(operation: (index: Int, T, T) -> T): T {
        return finalIterable().reduceIndexed(operation)
    }

    open fun reduceOrNull(operation: (T, T) -> T): T? {
        return finalIterable().reduceOrNull(operation)
    }

    open fun reduceIndexedOrNull(operation: (index: Int, T, T) -> T): T? {
        return finalIterable().reduceIndexedOrNull(operation)
    }

    open fun <R> reduce(initial: R, operation: (R, T) -> R): R {
        return finalIterable().reduce(initial, operation)
    }

    open fun <R> reduceIndexed(initial: R, operation: (index: Int, R, T) -> R): R {
        return finalIterable().reduceIndexed(initial, operation)
    }

    open fun <R, V> zip(other: Array<out R>, transform: (T, R) -> V): ListOps<V> {
        return finalIterable().zip(other, transform).toListOps()
    }

    open fun <R, V> zip(other: Iterable<R>, transform: (T, R) -> V): ListOps<V> {
        return finalIterable().zip(other, transform).toListOps()
    }

    open fun <R> zipWithNext(transform: (T, T) -> R): ListOps<R> {
        return finalIterable().zipWithNext(transform).toListOps()
    }

    open fun chunked(size: Int): ListOps<List<T>> {
        return finalIterable().chunked(size).toListOps()
    }

    open fun <R> chunked(size: Int, transform: (List<T>) -> R): ListOps<R> {
        return finalIterable().chunked(size, transform).toListOps()
    }

    open fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): ListOps<List<T>> {
        return finalIterable().windowed(size, step, partialWindows).toListOps()
    }

    open fun <R> windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): ListOps<R> {
        return finalIterable().windowed(size, step, partialWindows, transform).toListOps()
    }

    open fun intersect(other: Iterable<T>): SetOps<T> {
        return finalIterable().intersect(other).toSetOps()
    }

    open fun union(other: Iterable<T>): SetOps<T> {
        return finalIterable().union(other).toSetOps()
    }

    open fun subtract(other: Iterable<T>): SetOps<T> {
        return finalIterable().subtract(other).toSetOps()
    }

    open fun distinct(): ListOps<T> {
        return finalIterable().distinct().toListOps()
    }

    open fun <K> distinct(selector: (T) -> K): ListOps<T> {
        return finalIterable().distinct(selector).toListOps()
    }

    @JvmOverloads
    open fun sorted(comparator: Comparator<in T> = castSelfComparableComparator()): ListOps<T> {
        return finalIterable().sorted(comparator).toListOps()
    }

    open fun reversed(): ListOps<T> {
        return finalIterable().reversed().toListOps()
    }

    open fun shuffled(): ListOps<T> {
        return finalIterable().shuffled().toListOps()
    }

    open fun shuffled(random: Random): ListOps<T> {
        return finalIterable().shuffled(random).toListOps()
    }

    @JvmOverloads
    open fun max(comparator: Comparator<in T> = castSelfComparableComparator()): T {
        return finalIterable().max(comparator)
    }

    @JvmOverloads
    open fun maxOrNull(comparator: Comparator<in T> = castSelfComparableComparator()): T? {
        return finalIterable().maxOrNull(comparator)
    }

    @JvmOverloads
    open fun min(comparator: Comparator<in T> = castSelfComparableComparator()): T {
        return finalIterable().min(comparator)
    }

    @JvmOverloads
    open fun minOrNull(comparator: Comparator<in T> = castSelfComparableComparator()): T? {
        return finalIterable().minOrNull(comparator)
    }

    @JvmOverloads
    open fun sumInt(selector: (T) -> Int = { it.toInt() }): Int {
        return finalIterable().sumInt(selector)
    }

    @JvmOverloads
    open fun sumLong(selector: (T) -> Long = { it.toLong() }): Long {
        return finalIterable().sumLong(selector)
    }

    @JvmOverloads
    open fun sumDouble(selector: (T) -> Double = { it.toDouble() }): Double {
        return finalIterable().sumDouble(selector)
    }

    @JvmOverloads
    open fun sumBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
        return finalIterable().sumBigInteger(selector)
    }

    @JvmOverloads
    open fun sumBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
        return finalIterable().sumBigDecimal(selector)
    }

    @JvmOverloads
    open fun averageInt(selector: (T) -> Int = { it.toInt() }): Int {
        return finalIterable().averageInt(selector)
    }

    @JvmOverloads
    open fun averageLong(selector: (T) -> Long = { it.toLong() }): Long {
        return finalIterable().averageLong(selector)
    }

    @JvmOverloads
    open fun averageDouble(selector: (T) -> Double = { it.toDouble() }): Double {
        return finalIterable().averageDouble(selector)
    }

    @JvmOverloads
    open fun averageBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
        return finalIterable().averageBigInteger(selector)
    }

    @JvmOverloads
    open fun averageBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
        return finalIterable().averageBigDecimal(selector)
    }

    open fun <C : MutableCollection<in T>> toCollection(destination: C): C {
        return finalIterable().toCollection(destination)
    }

    open fun toImmutableCollection(): ImmutableCollection<T> {
        return finalIterable().toImmutableCollection()
    }

    open fun toSet(): Set<T> {
        return finalIterable().toSet()
    }

    open fun toImmutableSet(): ImmutableSet<T> {
        return finalIterable().toImmutableSet()
    }

    open fun toMutableSet(): MutableSet<T> {
        return finalIterable().toMutableSet()
    }

    open fun toHashSet(): HashSet<T> {
        return finalIterable().toHashSet()
    }

    open fun toLinkedHashSet(): LinkedHashSet<T> {
        return finalIterable().toLinkedHashSet()
    }

    open fun toTreeSet(): TreeSet<T> {
        return finalIterable().toTreeSet()
    }

    @JvmOverloads
    open fun toSortedSet(comparator: Comparator<in T> = castSelfComparableComparator()): SortedSet<T> {
        return finalIterable().toSortedSet(comparator)
    }

    open fun toList(): List<T> {
        return finalIterable().toList()
    }

    open fun toImmutableList(): ImmutableList<T> {
        return finalIterable().toImmutableList()
    }

    open fun toMutableList(): MutableList<T> {
        return finalIterable().toMutableList()
    }

    open fun toArrayList(): ArrayList<T> {
        return finalIterable().toArrayList()
    }

    open fun toLinkedList(): LinkedList<T> {
        return finalIterable().toLinkedList()
    }

    open fun asToCollection(): Collection<T> {
        return finalIterable().asToCollection()
    }

    open fun asToMutableCollection(): MutableCollection<T> {
        return finalIterable().asToMutableCollection()
    }

    open fun asToSet(): Set<T> {
        return finalIterable().asToSet()
    }

    open fun asToMutableSet(): MutableSet<T> {
        return finalIterable().asToMutableSet()
    }

    @JvmOverloads
    open fun asToSortedSet(comparator: Comparator<in T> = castSelfComparableComparator()): SortedSet<T> {
        return finalIterable().asToSortedSet(comparator)
    }

    open fun asToList(): List<T> {
        return finalIterable().asToList()
    }

    open fun asToMutableList(): MutableList<T> {
        return finalIterable().asToMutableList()
    }

    @JvmOverloads
    open fun toStream(parallel: Boolean = false): Stream<T> {
        return finalIterable().toStream(parallel)
    }

    open fun toSequence(): Sequence<T> {
        return finalIterable().toSequence()
    }

    open fun toArray(): Array<Any?> {
        return finalIterable().toArray()
    }

    open fun toArray(generator: (size: Int) -> Array<T>): Array<T> {
        return finalIterable().toArray(generator)
    }

    open fun toArray(componentType: Class<*>): Array<T> {
        return finalIterable().toArray(componentType)
    }

    open fun <R> toArray(componentType: Class<*>, selector: (T) -> R): Array<R> {
        return finalIterable().toArray(componentType, selector)
    }

    @JvmOverloads
    open fun toBooleanArray(selector: (T) -> Boolean = { it.toBoolean() }): BooleanArray {
        return finalIterable().toBooleanArray(selector)
    }

    @JvmOverloads
    open fun toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
        return finalIterable().toByteArray(selector)
    }

    @JvmOverloads
    open fun toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
        return finalIterable().toShortArray(selector)
    }

    @JvmOverloads
    open fun toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
        return finalIterable().toCharArray(selector)
    }

    @JvmOverloads
    open fun toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
        return finalIterable().toIntArray(selector)
    }

    @JvmOverloads
    open fun toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
        return finalIterable().toLongArray(selector)
    }

    @JvmOverloads
    open fun toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
        return finalIterable().toFloatArray(selector)
    }

    @JvmOverloads
    open fun toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
        return finalIterable().toDoubleArray(selector)
    }

    open fun <A> toAnyArray(componentType: Class<*>): A {
        return finalIterable().toAnyArray(componentType)
    }

    abstract fun plus(element: T): THIS

    abstract fun plus(elements: Array<out T>): THIS

    abstract fun plus(elements: Iterable<T>): THIS

    abstract fun plus(elements: Sequence<T>): THIS

    abstract fun minus(element: T): THIS

    abstract fun minus(elements: Array<out T>): THIS

    abstract fun minus(elements: Iterable<T>): THIS

    abstract fun minus(elements: Sequence<T>): THIS

    open fun plusBefore(index: Int, element: T): ListOps<T> {
        return finalIterable().plusBefore(index, element).toListOps()
    }

    open fun plusBefore(index: Int, elements: Array<out T>): ListOps<T> {
        return finalIterable().plusBefore(index, elements).toListOps()
    }

    open fun plusBefore(index: Int, elements: Iterable<T>): ListOps<T> {
        return finalIterable().plusBefore(index, elements).toListOps()
    }

    open fun plusBefore(index: Int, elements: Sequence<T>): ListOps<T> {
        return finalIterable().plusBefore(index, elements).toListOps()
    }

    open fun plusAfter(index: Int, element: T): ListOps<T> {
        return finalIterable().plusAfter(index, element).toListOps()
    }

    open fun plusAfter(index: Int, elements: Array<out T>): ListOps<T> {
        return finalIterable().plusAfter(index, elements).toListOps()
    }

    open fun plusAfter(index: Int, elements: Iterable<T>): ListOps<T> {
        return finalIterable().plusAfter(index, elements).toListOps()
    }

    open fun plusAfter(index: Int, elements: Sequence<T>): ListOps<T> {
        return finalIterable().plusAfter(index, elements).toListOps()
    }

    @JvmOverloads
    open fun minusAt(index: Int, count: Int = 1): ListOps<T> {
        return finalIterable().minusAt(index, count).toListOps()
    }

    open fun remove(element: T): THIS {
        finalMutableIterable().remove(element)
        return this.asAny()
    }

    open fun remove(n: Int): THIS {
        finalMutableIterable().remove(n)
        return this.asAny()
    }

    open fun removeWhile(predicate: (T) -> Boolean): THIS {
        finalMutableIterable().removeWhile(predicate)
        return this.asAny()
    }

    open fun removeAll(): THIS {
        finalMutableIterable().removeAll()
        return this.asAny()
    }

    open fun retainAll(predicate: (T) -> Boolean): THIS {
        finalMutableIterable().retainAll(predicate)
        return this.asAny()
    }

    @JvmOverloads
    open fun joinToString(
        separator: CharSequence = ", ",
        transform: ((T) -> CharSequence)? = null
    ): String {
        return finalIterable().joinToString(separator, transform)
    }

    open fun joinToString(
        separator: CharSequence = ", ",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T) -> CharSequence)? = null
    ): String {
        return finalIterable().joinToString(separator, limit, truncated, transform)
    }

    open fun joinToString(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T) -> CharSequence)? = null
    ): String {
        return finalIterable().joinToString(separator, prefix, postfix, limit, truncated, transform)
    }

    @JvmOverloads
    open fun <A : Appendable> joinTo(
        buffer: A,
        separator: CharSequence = ", ",
        transform: ((T) -> CharSequence)? = null
    ): A {
        return finalIterable().joinTo(buffer, separator, transform)
    }

    open fun <A : Appendable> joinTo(
        buffer: A,
        separator: CharSequence = ", ",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T) -> CharSequence)? = null
    ): A {
        return finalIterable().joinTo(buffer, separator, limit, truncated, transform)
    }

    open fun <A : Appendable> joinTo(
        buffer: A,
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T) -> CharSequence)? = null
    ): A {
        return finalIterable().joinTo(buffer, separator, prefix, postfix, limit, truncated, transform)
    }
}