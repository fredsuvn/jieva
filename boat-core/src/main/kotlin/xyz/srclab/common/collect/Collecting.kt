package xyz.srclab.common.collect

import xyz.srclab.common.lang.*
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import kotlin.random.Random

/**
 * Collection interface chain operation, integrated [Iterable], [Collection], [Set], [List] and their mutable version.
 */
open class Collecting<T> constructor(
    protected open var operand: Iterable<T>
) : MutableIterable<T> {

    protected open fun <R> Iterable<R>.asSelf(): Collecting<R> {
        operand = this.asAny()
        return this@Collecting.asAny()
    }

    open fun finalIterable(): Iterable<T> {
        return operand
    }

    open fun finalMutableIterable(): MutableIterable<T> {
        val op = operand
        if (op is MutableIterable<T>) {
            return op
        }
        return op.toMutableList()
    }

    open fun finalCollection(): Collection<T> {
        val op = operand
        if (op is Collection<T>) {
            return op
        }
        return op.toSet()
    }

    open fun finalMutableCollection(): MutableCollection<T> {
        val op = operand
        if (op is MutableCollection<T>) {
            return op
        }
        return op.toMutableSet()
    }

    open fun finalSet(): Set<T> {
        val op = operand
        if (op is Set<T>) {
            return op
        }
        return op.toSet()
    }

    open fun finalMutableSet(): MutableSet<T> {
        val op = operand
        if (op is MutableSet<T>) {
            return op
        }
        return op.toMutableSet()
    }

    open fun finalList(): List<T> {
        val op = operand
        if (op is List<T>) {
            return op
        }
        return op.toList()
    }

    open fun finalMutableList(): MutableList<T> {
        val op = operand
        if (op is MutableList<T>) {
            return op
        }
        return op.toMutableList()
    }

    override fun iterator(): MutableIterator<T> {
        return finalMutableIterable().iterator()
    }

    //Iterable:

    open fun contains(element: T): Boolean {
        return finalIterable().contains(element)
    }

    open fun containsAll(elements: Array<out T>): Boolean {
        return when (val op = operand) {
            is Collection<T> -> op.containsAll(elements)
            else -> op.containsAll(elements)
        }
    }

    open fun containsAll(elements: Iterable<T>): Boolean {
        return when (val op = operand) {
            is Collection<T> -> op.containsAll(elements)
            else -> op.containsAll(elements)
        }
    }

    open fun containsAll(elements: Collection<T>): Boolean {
        return finalIterable().containsAll(elements)
    }

    open fun count(): Int {
        return when (val op = operand) {
            is Collection<T> -> op.count()
            else -> op.count()
        }
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
        return when (val op = operand) {
            is List<T> -> op.first()
            else -> op.first()
        }
    }

    open fun first(predicate: (T) -> Boolean): T {
        return finalIterable().first(predicate)
    }

    open fun firstOrNull(): T? {
        return when (val op = operand) {
            is List<T> -> op.firstOrNull()
            else -> op.firstOrNull()
        }
    }

    open fun firstOrNull(predicate: (T) -> Boolean): T? {
        return finalIterable().firstOrNull(predicate)
    }

    open fun last(): T {
        return when (val op = operand) {
            is List<T> -> op.last()
            else -> op.last()
        }
    }

    open fun last(predicate: (T) -> Boolean): T {
        return when (val op = operand) {
            is List<T> -> op.last(predicate)
            else -> op.last(predicate)
        }
    }

    open fun lastOrNull(): T? {
        return when (val op = operand) {
            is List<T> -> op.lastOrNull()
            else -> op.lastOrNull()
        }
    }

    open fun lastOrNull(predicate: (T) -> Boolean): T? {
        return when (val op = operand) {
            is List<T> -> op.lastOrNull(predicate)
            else -> op.lastOrNull(predicate)
        }
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
        return when (val op = operand) {
            is List<T> -> op.elementAt(index)
            else -> op.elementAt(index)
        }
    }

    open fun elementAtOrElse(index: Int, defaultValue: (index: Int) -> T): T {
        return when (val op = operand) {
            is List<T> -> op.elementAtOrElse(index, defaultValue)
            else -> op.elementAtOrElse(index, defaultValue)
        }
    }

    open fun elementAtOrNull(index: Int): T? {
        return when (val op = operand) {
            is List<T> -> op.elementAtOrNull(index)
            else -> op.elementAtOrNull(index)
        }
    }

    open fun find(predicate: (T) -> Boolean): T? {
        return finalIterable().find(predicate)
    }

    open fun findLast(predicate: (T) -> Boolean): T? {
        return when (val op = operand) {
            is List<T> -> op.findLast(predicate)
            else -> op.findLast(predicate)
        }
    }

    open fun indexOf(element: T): Int {
        return when (val op = operand) {
            is List<T> -> op.indexOf(element)
            else -> op.indexOf(element)
        }
    }

    open fun indexOf(predicate: (T) -> Boolean): Int {
        return when (val op = operand) {
            is List<T> -> op.indexOf(predicate)
            else -> op.indexOf(predicate)
        }
    }

    open fun lastIndexOf(element: T): Int {
        return when (val op = operand) {
            is List<T> -> op.lastIndexOf(element)
            else -> op.lastIndexOf(element)
        }
    }

    open fun lastIndexOf(predicate: (T) -> Boolean): Int {
        return when (val op = operand) {
            is List<T> -> op.lastIndexOf(predicate)
            else -> op.lastIndexOf(predicate)
        }
    }

    open fun take(n: Int): Collecting<T> {
        return finalIterable().take(n).asSelf()
    }

    open fun takeWhile(predicate: (T) -> Boolean): Collecting<T> {
        return finalIterable().takeWhile(predicate).asSelf()
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

    open fun drop(n: Int): Collecting<T> {
        return finalIterable().drop(n).asSelf()
    }

    open fun dropWhile(predicate: (T) -> Boolean): Collecting<T> {
        return finalIterable().dropWhile(predicate).asSelf()
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

    open fun filter(predicate: (T) -> Boolean): Collecting<T> {
        return finalIterable().filter(predicate).asSelf()
    }

    open fun filterIndexed(predicate: (index: Int, T) -> Boolean): Collecting<T> {
        return finalIterable().filterIndexed(predicate).asSelf()
    }

    open fun filterNotNull(): Collecting<T> {
        return finalIterable().filterNotNull().asSelf()
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

    open fun <R> map(transform: (T) -> R): Collecting<R> {
        return finalIterable().map(transform).asSelf()
    }

    open fun <R> mapIndexed(transform: (index: Int, T) -> R): Collecting<R> {
        return finalIterable().mapIndexed(transform).asSelf()
    }

    open fun <R> mapNotNull(transform: (T) -> R?): Collecting<R> {
        return finalIterable().mapNotNull(transform).asSelf()
    }

    open fun <R> mapIndexedNotNull(transform: (index: Int, T) -> R?): Collecting<R> {
        return finalIterable().mapIndexedNotNull(transform).asSelf()
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

    open fun <R> flatMap(transform: (T) -> Iterable<R>): Collecting<R> {
        return finalIterable().flatMap(transform).asSelf()
    }

    open fun <R> flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): Collecting<R> {
        return finalIterable().flatMapIndexed(transform).asSelf()
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
    ): Mapping<K, V> {
        return finalIterable().associate(keySelector, valueTransform).mapping()
    }

    open fun <K, V> associate(transform: (T) -> Pair<K, V>): Mapping<K, V> {
        return finalIterable().associate(transform).mapping()
    }

    open fun <K> associateKeys(keySelector: (T) -> K): Mapping<K, T> {
        return finalIterable().associateKeys(keySelector).mapping()
    }

    open fun <K> associateKeys(keys: Iterable<K>): Mapping<K, T> {
        return finalIterable().associateKeys(keys).mapping()
    }

    open fun <V> associateValues(valueSelector: (T) -> V): Mapping<T, V> {
        return finalIterable().associateValues(valueSelector).mapping()
    }

    open fun <V> associateValues(values: Iterable<V>): Mapping<T, V> {
        return finalIterable().associateValues(values).mapping()
    }

    open fun <K, V> associateWithNext(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): Mapping<K, V> {
        return finalIterable().associateWithNext(keySelector, valueTransform).mapping()
    }

    open fun <K, V> associateWithNext(transform: (T, T) -> Pair<K, V>): Mapping<K, V> {
        return finalIterable().associateWithNext(transform).mapping()
    }

    open fun <K, V> associatePairs(
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): Mapping<K, V> {
        return finalIterable().associatePairs(keySelector, valueTransform).mapping()
    }

    open fun <K, V> associatePairs(transform: (T, T) -> Pair<K, V>): Mapping<K, V> {
        return finalIterable().associatePairs(transform).mapping()
    }

    open fun <K, V> associatePairs(
        complement: T,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): Mapping<K, V> {
        return finalIterable().associatePairs(complement, keySelector, valueTransform).mapping()
    }

    open fun <K, V> associatePairs(
        complement: T,
        transform: (T, T) -> Pair<K, V>
    ): Mapping<K, V> {
        return finalIterable().associatePairs(complement, transform).mapping()
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

    open fun <K> groupBy(keySelector: (T) -> K): Mapping<K, List<T>> {
        return finalIterable().groupBy(keySelector).mapping()
    }

    open fun <K, V> groupBy(keySelector: (T) -> K, valueTransform: (T) -> V): Mapping<K, List<V>> {
        return finalIterable().groupBy(keySelector, valueTransform).mapping()
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

    open fun <R, V> zip(other: Array<out R>, transform: (T, R) -> V): Collecting<V> {
        return finalIterable().zip(other, transform).asSelf()
    }

    open fun <R, V> zip(other: Iterable<R>, transform: (T, R) -> V): Collecting<V> {
        return finalIterable().zip(other, transform).asSelf()
    }

    open fun <R> zipWithNext(transform: (T, T) -> R): Collecting<R> {
        return finalIterable().zipWithNext(transform).asSelf()
    }

    open fun chunked(size: Int): Collecting<List<T>> {
        return finalIterable().chunked(size).asSelf()
    }

    open fun <R> chunked(size: Int, transform: (List<T>) -> R): Collecting<R> {
        return finalIterable().chunked(size, transform).asSelf()
    }

    open fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): Collecting<List<T>> {
        return finalIterable().windowed(size, step, partialWindows).asSelf()
    }

    open fun <R> windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): Collecting<R> {
        return finalIterable().windowed(size, step, partialWindows, transform).asSelf()
    }

    open fun intersect(other: Iterable<T>): Collecting<T> {
        return finalIterable().intersect(other).asSelf()
    }

    open fun union(other: Iterable<T>): Collecting<T> {
        return finalIterable().union(other).asSelf()
    }

    open fun subtract(other: Iterable<T>): Collecting<T> {
        return finalIterable().subtract(other).asSelf()
    }

    open fun distinct(): Collecting<T> {
        return finalIterable().distinct().asSelf()
    }

    open fun <K> distinct(selector: (T) -> K): Collecting<T> {
        return finalIterable().distinct(selector).asSelf()
    }

    @JvmOverloads
    open fun sorted(comparator: Comparator<in T> = asComparableComparator()): Collecting<T> {
        return finalIterable().sorted(comparator).asSelf()
    }

    open fun reversed(): Collecting<T> {
        return finalIterable().reversed().asSelf()
    }

    open fun shuffled(): Collecting<T> {
        return finalIterable().shuffled().asSelf()
    }

    open fun shuffled(random: Random): Collecting<T> {
        return finalIterable().shuffled(random).asSelf()
    }

    @JvmOverloads
    open fun max(comparator: Comparator<in T> = asComparableComparator()): T {
        return finalIterable().max(comparator)
    }

    @JvmOverloads
    open fun maxOrNull(comparator: Comparator<in T> = asComparableComparator()): T? {
        return finalIterable().maxOrNull(comparator)
    }

    @JvmOverloads
    open fun min(comparator: Comparator<in T> = asComparableComparator()): T {
        return finalIterable().min(comparator)
    }

    @JvmOverloads
    open fun minOrNull(comparator: Comparator<in T> = asComparableComparator()): T? {
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
    open fun toSortedSet(comparator: Comparator<in T> = asComparableComparator()): SortedSet<T> {
        return finalIterable().toSortedSet(comparator)
    }

    open fun toList(): List<T> {
        return finalIterable().toList()
    }

    open fun toImmutableList(): ImmutableList<T> {
        return finalIterable().toImmutableList()
    }

    open fun toMutableList(): MutableList<T> {
        return when (val op = operand) {
            is Collection<T> -> op.toMutableList()
            else -> op.toMutableList()
        }
    }

    open fun toArrayList(): ArrayList<T> {
        return finalIterable().toArrayList()
    }

    open fun toLinkedList(): LinkedList<T> {
        return finalIterable().toLinkedList()
    }

    open fun toEnumeration(): Enumeration<T> {
        return finalIterable().toEnumeration()
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
    open fun asToSortedSet(comparator: Comparator<in T> = asComparableComparator()): SortedSet<T> {
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

    @JvmOverloads
    open fun <R> toArray(componentType: Type, selector: ((T) -> R)? = null): Array<R> {
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

    @JvmOverloads
    open fun <R, A> toAnyArray(componentType: Type, selector: ((T) -> R)? = null): A {
        return finalIterable().toAnyArray(componentType, selector)
    }

    open fun plus(element: T): Collecting<T> {
        return when (val op = operand) {
            is List<T> -> op.plus(element).asSelf()
            is Set<T> -> op.plus(element).asSelf()
            else -> op.plus(element).asSelf()
        }
    }

    open fun plus(elements: Array<out T>): Collecting<T> {
        return when (val op = operand) {
            is List<T> -> op.plus(elements).asSelf()
            is Set<T> -> op.plus(elements).asSelf()
            else -> op.plus(elements).asSelf()
        }
    }

    open fun plus(elements: Iterable<T>): Collecting<T> {
        return when (val op = operand) {
            is List<T> -> op.plus(elements).asSelf()
            is Set<T> -> op.plus(elements).asSelf()
            else -> op.plus(elements).asSelf()
        }
    }

    open fun plus(elements: Sequence<T>): Collecting<T> {
        return when (val op = operand) {
            is List<T> -> op.plus(elements).asSelf()
            is Set<T> -> op.plus(elements).asSelf()
            else -> op.plus(elements).asSelf()
        }
    }

    open fun minus(element: T): Collecting<T> {
        return when (val op = operand) {
            is List<T> -> op.minus(element).asSelf()
            is Set<T> -> op.minus(element).asSelf()
            else -> op.minus(element).asSelf()
        }
    }

    open fun minus(elements: Array<out T>): Collecting<T> {
        return when (val op = operand) {
            is List<T> -> op.minus(elements).asSelf()
            is Set<T> -> op.minus(elements).asSelf()
            else -> op.minus(elements).asSelf()
        }
    }

    open fun minus(elements: Iterable<T>): Collecting<T> {
        return when (val op = operand) {
            is List<T> -> op.minus(elements).asSelf()
            is Set<T> -> op.minus(elements).asSelf()
            else -> op.minus(elements).asSelf()
        }
    }

    open fun minus(elements: Sequence<T>): Collecting<T> {
        return when (val op = operand) {
            is List<T> -> op.minus(elements).asSelf()
            is Set<T> -> op.minus(elements).asSelf()
            else -> op.minus(elements).asSelf()
        }
    }

    open fun plusBefore(index: Int, element: T): Collecting<T> {
        return finalIterable().plusBefore(index, element).asSelf()
    }

    open fun plusBefore(index: Int, elements: Array<out T>): Collecting<T> {
        return finalIterable().plusBefore(index, elements).asSelf()
    }

    open fun plusBefore(index: Int, elements: Iterable<T>): Collecting<T> {
        return finalIterable().plusBefore(index, elements).asSelf()
    }

    open fun plusBefore(index: Int, elements: Sequence<T>): Collecting<T> {
        return finalIterable().plusBefore(index, elements).asSelf()
    }

    open fun plusAfter(index: Int, element: T): Collecting<T> {
        return finalIterable().plusAfter(index, element).asSelf()
    }

    open fun plusAfter(index: Int, elements: Array<out T>): Collecting<T> {
        return finalIterable().plusAfter(index, elements).asSelf()
    }

    open fun plusAfter(index: Int, elements: Iterable<T>): Collecting<T> {
        return finalIterable().plusAfter(index, elements).asSelf()
    }

    open fun plusAfter(index: Int, elements: Sequence<T>): Collecting<T> {
        return finalIterable().plusAfter(index, elements).asSelf()
    }

    @JvmOverloads
    open fun minusAt(index: Int, count: Int = 1): Collecting<T> {
        return finalIterable().minusAt(index, count).asSelf()
    }

    open fun remove(element: T): Collecting<T> {
        finalMutableIterable().remove(element)
        return this
    }

    open fun remove(n: Int): Collecting<T> {
        finalMutableIterable().remove(n)
        return this
    }

    open fun removeWhile(predicate: (T) -> Boolean): Collecting<T> {
        finalMutableIterable().removeWhile(predicate)
        return this
    }

    open fun removeAll(): Collecting<T> {
        finalMutableIterable().removeAll()
        return this
    }

    open fun retainAll(predicate: (T) -> Boolean): Collecting<T> {
        when (val op = operand) {
            is MutableList<T> -> op.retainAll(predicate)
            else -> finalMutableIterable().retainAll(predicate)
        }
        return this
    }

    //Collection:

    open fun addAll(elements: Array<out T>): Collecting<T> {
        finalMutableCollection().addAll(elements)
        return this
    }

    open fun addAll(elements: Iterable<T>): Collecting<T> {
        finalMutableCollection().addAll(elements)
        return this
    }

    open fun addAll(elements: Sequence<T>): Collecting<T> {
        finalMutableCollection().addAll(elements)
        return this
    }

    open fun removeAll(elements: Array<out T>): Collecting<T> {
        finalMutableCollection().removeAll(elements)
        return this
    }

    open fun removeAll(elements: Iterable<T>): Collecting<T> {
        finalMutableCollection().removeAll(elements)
        return this
    }

    open fun removeAll(elements: Sequence<T>): Collecting<T> {
        finalMutableCollection().removeAll(elements)
        return this
    }

    open fun retainAll(elements: Array<out T>): Collecting<T> {
        finalMutableCollection().retainAll(elements)
        return this
    }

    open fun retainAll(elements: Iterable<T>): Collecting<T> {
        finalMutableCollection().retainAll(elements)
        return this
    }

    open fun retainAll(elements: Sequence<T>): Collecting<T> {
        finalMutableCollection().retainAll(elements)
        return this
    }

    open fun clear(): Collecting<T> {
        finalMutableCollection().clear()
        return this
    }

    // Set:

    // List:

    open fun takeLast(n: Int): Collecting<T> {
        return finalList().takeLast(n).asSelf()
    }

    open fun takeLastWhile(predicate: (T) -> Boolean): Collecting<T> {
        return finalList().takeLastWhile(predicate).asSelf()
    }

    open fun dropLast(n: Int): Collecting<T> {
        return finalList().dropLast(n).asSelf()
    }

    open fun dropLastWhile(predicate: (T) -> Boolean): Collecting<T> {
        return finalList().dropLastWhile(predicate).asSelf()
    }

    open fun reduceRight(operation: (T, T) -> T): T {
        return finalList().reduceRight(operation)
    }

    open fun reduceRightIndexed(operation: (Int, T, T) -> T): T {
        return finalList().reduceRightIndexed(operation)
    }

    open fun reduceRightOrNull(operation: (T, T) -> T): T? {
        return finalList().reduceRightOrNull(operation)
    }

    open fun reduceRightIndexedOrNull(operation: (Int, T, T) -> T): T? {
        return finalList().reduceRightIndexedOrNull(operation)
    }

    open fun <R> reduceRight(initial: R, operation: (T, R) -> R): R {
        return finalList().reduceRight(initial, operation)
    }

    open fun <R> reduceRightIndexed(initial: R, operation: (Int, T, R) -> R): R {
        return finalList().reduceRightIndexed(initial, operation)
    }

    @JvmOverloads
    open fun subList(fromIndex: Int, toIndex: Int = count()): Collecting<T> {
        return finalList().subList(fromIndex, toIndex).asSelf()
    }

    open fun slice(indices: IntArray): Collecting<T> {
        return finalList().slice(indices.asIterable()).asSelf()
    }

    open fun slice(indices: Iterable<Int>): Collecting<T> {
        return finalList().slice(indices).asSelf()
    }

    open fun slice(indices: IntRange): Collecting<T> {
        return finalList().slice(indices).asSelf()
    }

    @JvmOverloads
    open fun binarySearch(element: T, comparator: Comparator<in T> = asComparableComparator()): Int {
        return finalList().binarySearch(element, comparator)
    }

    open fun reverse(): Collecting<T> {
        finalMutableList().reverse()
        return this.asAny()
    }

    @JvmOverloads
    open fun sort(comparator: Comparator<in T> = asComparableComparator()): Collecting<T> {
        finalMutableList().sort(comparator)
        return this.asAny()
    }

    open fun shuffle(): Collecting<T> {
        finalMutableList().shuffle()
        return this.asAny()
    }

    open fun shuffle(random: Random): Collecting<T> {
        finalMutableList().shuffle(random)
        return this.asAny()
    }

    open fun removeAll(predicate: (T) -> Boolean): Collecting<T> {
        finalMutableList().removeAll(predicate)
        return this.asAny()
    }

    open fun removeFirst(): Collecting<T> {
        finalMutableList().removeFirst()
        return this.asAny()
    }

    open fun removeFirstOrNull(): Collecting<T> {
        finalMutableList().removeFirstOrNull()
        return this.asAny()
    }

    open fun removeLast(): Collecting<T> {
        finalMutableList().removeLast()
        return this.asAny()
    }

    open fun removeLastOrNull(): Collecting<T> {
        finalMutableList().removeLastOrNull()
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

    // Sync

    open fun toSync(): Collecting<T> {
        val op = operand
        if (op is Collection<T>) {
            return op.toSync().asSelf()
        }
        if (op is List<T>) {
            return op.toSync().asSelf()
        }
        if (op is NavigableSet<T>) {
            return op.toSync().asSelf()
        }
        if (op is SortedSet<T>) {
            return op.toSync().asSelf()
        }
        if (op is Set<T>) {
            return op.toSync().asSelf()
        }
        return op.toSet().toSync().asSelf()
    }
}