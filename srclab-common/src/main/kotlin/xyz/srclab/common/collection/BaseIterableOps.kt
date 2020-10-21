package xyz.srclab.common.collection

import xyz.srclab.common.base.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet
import kotlin.random.Random
import kotlin.collections.addAll as addAllKt
import kotlin.collections.all as allKt
import kotlin.collections.any as anyKt
import kotlin.collections.asSequence as asSequenceKt
import kotlin.collections.associate as associateKt
import kotlin.collections.associateBy as associateByKt
import kotlin.collections.associateByTo as associateByToKt
import kotlin.collections.associateTo as associateToKt
import kotlin.collections.associateWith as associateWithKt
import kotlin.collections.associateWithTo as associateWithToKt
import kotlin.collections.chunked as chunkedKt
import kotlin.collections.contains as containsKt
import kotlin.collections.count as countKt
import kotlin.collections.distinct as distinctKt
import kotlin.collections.distinctBy as distinctByKt
import kotlin.collections.drop as dropKt
import kotlin.collections.dropWhile as dropWhileKt
import kotlin.collections.elementAt as elementAtKt
import kotlin.collections.elementAtOrElse as elementAtOrElseKt
import kotlin.collections.elementAtOrNull as elementAtOrNullKt
import kotlin.collections.filter as filterKt
import kotlin.collections.filterIndexed as filterIndexedKt
import kotlin.collections.filterIndexedTo as filterIndexedToKt
import kotlin.collections.filterNotNull as filterNotNullKt
import kotlin.collections.filterTo as filterToKt
import kotlin.collections.find as findKt
import kotlin.collections.first as firstKt
import kotlin.collections.firstOrNull as firstOrNullKt
import kotlin.collections.flatMap as flatMapKt
import kotlin.collections.flatMapIndexed as flatMapIndexedKt
import kotlin.collections.flatMapIndexedTo as flatMapIndexedToKt
import kotlin.collections.flatMapTo as flatMapToKt
import kotlin.collections.fold as foldKt
import kotlin.collections.foldIndexed as foldIndexedKt
import kotlin.collections.forEachIndexed as forEachIndexedKt
import kotlin.collections.groupBy as groupByKt
import kotlin.collections.groupByTo as groupByToKt
import kotlin.collections.indexOf as indexOfKt
import kotlin.collections.indexOfFirst as indexOfFirstKt
import kotlin.collections.indexOfLast as indexOfLastKt
import kotlin.collections.intersect as intersectKt
import kotlin.collections.last as lastKt
import kotlin.collections.lastIndexOf as lastIndexOfKt
import kotlin.collections.lastOrNull as lastOrNullKt
import kotlin.collections.map as mapKt
import kotlin.collections.mapIndexed as mapIndexedKt
import kotlin.collections.mapIndexedNotNull as mapIndexedNotNullKt
import kotlin.collections.mapIndexedNotNullTo as mapIndexedNotNullToKt
import kotlin.collections.mapIndexedTo as mapIndexedToKt
import kotlin.collections.mapNotNull as mapNotNullKt
import kotlin.collections.mapNotNullTo as mapNotNullToKt
import kotlin.collections.mapTo as mapToKt
import kotlin.collections.maxWithOrNull as maxWithOrNullKt
import kotlin.collections.minWithOrNull as minWithOrNullKt
import kotlin.collections.minus as minusKt
import kotlin.collections.none as noneKt
import kotlin.collections.plus as plusKt
import kotlin.collections.random as randomKt
import kotlin.collections.randomOrNull as randomOrNullKt
import kotlin.collections.reduce as reduceKt
import kotlin.collections.reduceIndexed as reduceIndexedKt
import kotlin.collections.reduceIndexedOrNull as reduceIndexedOrNullKt
import kotlin.collections.reduceOrNull as reduceOrNullKt
import kotlin.collections.removeAll as removeAllKt
import kotlin.collections.retainAll as retainAllKt
import kotlin.collections.reversed as reversedKt
import kotlin.collections.shuffled as shuffledKt
import kotlin.collections.sortedWith as sortedWithKt
import kotlin.collections.subtract as subtractKt
import kotlin.collections.sumOf as sumOfKt
import kotlin.collections.take as takeKt
import kotlin.collections.takeWhile as takeWhileKt
import kotlin.collections.toHashSet as toHashSetKt
import kotlin.collections.toList as toListKt
import kotlin.collections.toMutableList as toMutableListKt
import kotlin.collections.toMutableSet as toMutableSetKt
import kotlin.collections.toSet as toSetKt
import kotlin.collections.toSortedSet as toSortedSetKt
import kotlin.collections.toTypedArray as toTypedArrayKt
import kotlin.collections.union as unionKt
import kotlin.collections.windowed as windowedKt
import kotlin.collections.zip as zipKt
import kotlin.collections.zipWithNext as zipWithNextKt

abstract class BaseIterableOps<T, I : Iterable<T>, MI : MutableIterable<T>, THIS : BaseIterableOps<T, I, MI, THIS>>
protected constructor(protected var iterable: I) : MutableIterable<T> {

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

    open fun <K, V> associate(keySelector: (T) -> K, valueTransform: (T) -> V): MapOps<K, V> {
        return finalIterable().associate(keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associate(transform: (T) -> Pair<K, V>): MapOps<K, V> {
        return finalIterable().associate(transform).toMapOps()
    }

    open fun <K> associateKey(keySelector: (T) -> K): MapOps<K, T> {
        return finalIterable().associateKey(keySelector).toMapOps()
    }

    open fun <V> associateValue(valueSelector: (T) -> V): MapOps<T, V> {
        return finalIterable().associateValue(valueSelector).toMapOps()
    }

    open fun <K, V> associatePair(keySelector: (T) -> K, valueTransform: (T) -> V): MapOps<K, V> {
        return finalIterable().associatePair(keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associatePair(transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return finalIterable().associatePair(transform).toMapOps()
    }

    open fun <K, V> associatePair(complement: T, keySelector: (T) -> K, valueTransform: (T) -> V): MapOps<K, V> {
        return finalIterable().associatePair(complement, keySelector, valueTransform).toMapOps()
    }

    open fun <K, V> associatePair(complement: T, transform: (T, T) -> Pair<K, V>): MapOps<K, V> {
        return finalIterable().associatePairTo(complement, LinkedHashMap(), transform).toMapOps()
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateTo(
        destination: M, keySelector: (T) -> K, valueTransform: (T) -> V
    ): M {
        return finalIterable().associateTo(destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associateTo(destination: M, transform: (T) -> Pair<K, V>): M {
        return finalIterable().associateTo(destination, transform)
    }

    open fun <K, M : MutableMap<in K, in T>> associateKeyTo(destination: M, keySelector: (T) -> K): M {
        return finalIterable().associateKeyTo(destination, keySelector)
    }

    open fun <V, M : MutableMap<in T, in V>> associateValueTo(destination: M, valueSelector: (T) -> V): M {
        return finalIterable().associateValueTo(destination, valueSelector)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalIterable().associatePairTo(destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairTo(destination: M, transform: (T, T) -> Pair<K, V>): M {
        return finalIterable().associatePairTo(destination, transform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        complement: T,
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M {
        return finalIterable().associatePairTo(complement, destination, keySelector, valueTransform)
    }

    open fun <K, V, M : MutableMap<in K, in V>> associatePairTo(
        complement: T,
        destination: M,
        transform: (T, T) -> Pair<K, V>
    ): M {
        return finalIterable().associatePairTo(complement, destination, transform)
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

    open fun <L, R> unzip(transform: (T) -> Pair<L, R>): Pair<List<L>, List<R>> {
        return finalIterable().unzip(transform)
    }

    open fun <R> unzipWithNext(transform: (T) -> Pair<R, R>): ListOps<R> {
        return finalIterable().unzipWithNext(transform).toListOps()
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

    open fun toCollection(): Collection<T> {
        return finalIterable().toCollection()
    }

    open fun toMutableCollection(): MutableCollection<T> {
        return finalIterable().toMutableCollection()
    }

    open fun toSet(): Set<T> {
        return finalIterable().toSet()
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

    @JvmOverloads
    open fun toSortedSet(comparator: Comparator<in T> = castSelfComparableComparator()): SortedSet<T> {
        return finalIterable().toSortedSet(comparator)
    }

    open fun toList(): List<T> {
        return finalIterable().toList()
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

    open fun asToHashSet(): HashSet<T> {
        return finalIterable().asToHashSet()
    }

    open fun asToLinkedHashSet(): LinkedHashSet<T> {
        return finalIterable().asToLinkedHashSet()
    }

    open fun asToSortedSet(): SortedSet<T> {
        return finalIterable().asToSortedSet()
    }

    open fun asToList(): List<T> {
        return finalIterable().asToList()
    }

    open fun asToMutableList(): MutableList<T> {
        return finalIterable().asToMutableList()
    }

    open fun asToArrayList(): ArrayList<T> {
        return finalIterable().asToArrayList()
    }

    open fun asToLinkedList(): LinkedList<T> {
        return finalIterable().asToLinkedList()
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

    override fun iterator(): MutableIterator<T> {
        return finalMutableIterable().iterator()
    }

    fun toSequenceOps(): SequenceOps<T> {
        return SequenceOps.opsFor(finalIterable())
    }

    protected fun finalIterable(): Iterable<T> {
        return iterable
    }

    protected fun finalMutableIterable(): MutableIterable<T> {
        return iterable.asAny()
    }

    protected abstract fun I.asThis(): THIS

    protected abstract fun <T> Iterable<T>.toIterableOps(): IterableOps<T>

    protected abstract fun <T> List<T>.toListOps(): ListOps<T>

    protected abstract fun <T> Set<T>.toSetOps(): SetOps<T>

    protected abstract fun <K, V> Map<K, V>.toMapOps(): MapOps<K, V>

    companion object {

        @JvmStatic
        fun <T> Iterable<T>.contains(element: T): Boolean {
            return this.containsKt(element)
        }

        @JvmStatic
        fun <T> Iterable<T>.containsAll(elements: Array<out T>): Boolean {
            return this.containsAll(elements.toSetKt())
        }

        @JvmStatic
        fun <T> Iterable<T>.containsAll(elements: Iterable<T>): Boolean {
            return this.containsAll(elements.toSet())
        }

        @JvmStatic
        fun <T> Iterable<T>.containsAll(elements: Collection<T>): Boolean {
            return this.toSet().containsAll(elements)
        }

        @JvmStatic
        fun <T> Iterable<T>.count(): Int {
            return this.countKt()
        }

        @JvmStatic
        inline fun <T> Iterable<T>.count(predicate: (T) -> Boolean): Int {
            return this.countKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.isEmpty(): Boolean {
            var hasElement = false
            for (t in this) {
                hasElement = true
                break
            }
            return !hasElement
        }

        @JvmStatic
        fun <T> Iterable<T>.isNotEmpty(): Boolean {
            return !this.isEmpty()
        }

        @JvmStatic
        fun <T> Iterable<T>.any(): Boolean {
            return this.anyKt()
        }

        @JvmStatic
        inline fun <T> Iterable<T>.any(predicate: (T) -> Boolean): Boolean {
            return this.anyKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.none(): Boolean {
            return this.noneKt()
        }

        @JvmStatic
        inline fun <T> Iterable<T>.none(predicate: (T) -> Boolean): Boolean {
            return this.noneKt(predicate)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.all(predicate: (T) -> Boolean): Boolean {
            return this.allKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.first(): T {
            return this.firstKt()
        }

        @JvmStatic
        inline fun <T> Iterable<T>.first(predicate: (T) -> Boolean): T {
            return this.firstKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.firstOrNull(): T? {
            return this.firstOrNullKt()
        }

        @JvmStatic
        inline fun <T> Iterable<T>.firstOrNull(predicate: (T) -> Boolean): T? {
            return this.firstOrNullKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.last(): T {
            return this.lastKt()
        }

        @JvmStatic
        inline fun <T> Iterable<T>.last(predicate: (T) -> Boolean): T {
            return this.lastKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.lastOrNull(): T? {
            return this.lastOrNullKt()
        }

        @JvmStatic
        inline fun <T> Iterable<T>.lastOrNull(predicate: (T) -> Boolean): T? {
            return this.lastOrNullKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.random(): T {
            return this.toList().randomKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.random(random: Random): T {
            return this.toList().randomKt(random)
        }

        @JvmStatic
        fun <T> Iterable<T>.randomOrNull(): T? {
            return this.toList().randomOrNullKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.randomOrNull(random: Random): T? {
            return this.toList().randomOrNullKt(random)
        }

        @JvmStatic
        fun <T> Iterable<T>.elementAt(index: Int): T {
            return this.elementAtKt(index)
        }

        @JvmStatic
        fun <T> Iterable<T>.elementAtOrElse(index: Int, defaultValue: (index: Int) -> T): T {
            return this.elementAtOrElseKt(index, defaultValue)
        }

        @JvmStatic
        fun <T> Iterable<T>.elementAtOrNull(index: Int): T? {
            return this.elementAtOrNullKt(index)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.find(predicate: (T) -> Boolean): T? {
            return this.findKt(predicate)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.findLast(predicate: (T) -> Boolean): T? {
            return this.firstKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.indexOf(element: T): Int {
            return this.indexOfKt(element)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.indexOf(predicate: (T) -> Boolean): Int {
            return this.indexOfFirstKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.lastIndexOf(element: T): Int {
            return this.lastIndexOfKt(element)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.lastIndexOf(predicate: (T) -> Boolean): Int {
            return this.indexOfLastKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.take(n: Int): List<T> {
            return this.takeKt(n)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.takeWhile(predicate: (T) -> Boolean): List<T> {
            return this.takeWhileKt(predicate)
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> Iterable<T>.takeTo(n: Int, destination: C): C {
            destination.addAll(this.take(n))
            return destination
        }

        @JvmStatic
        inline fun <T, C : MutableCollection<in T>> Iterable<T>.takeWhileTo(
            destination: C,
            predicate: (T) -> Boolean
        ): C {
            destination.addAll(this.takeWhile(predicate))
            return destination
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> Iterable<T>.takeAllTo(destination: C): C {
            destination.addAllKt(this)
            return destination
        }

        @JvmStatic
        fun <T> Iterable<T>.drop(n: Int): List<T> {
            return this.dropKt(n)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.dropWhile(predicate: (T) -> Boolean): List<T> {
            return this.dropWhileKt(predicate)
        }

        @JvmStatic
        fun <T, C : MutableCollection<in T>> Iterable<T>.dropTo(n: Int, destination: C): C {
            destination.addAll(this.drop(n))
            return destination
        }

        @JvmStatic
        inline fun <T, C : MutableCollection<in T>> Iterable<T>.dropWhileTo(
            destination: C,
            predicate: (T) -> Boolean
        ): C {
            destination.addAll(this.dropWhile(predicate))
            return destination
        }

        @JvmStatic
        inline fun <T> Iterable<T>.forEachIndexed(action: (index: Int, T) -> Unit) {
            return this.forEachIndexedKt(action)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): List<T> {
            return this.filterKt(predicate)
        }

        @JvmStatic
        inline fun <T> Iterable<T>.filterIndexed(predicate: (index: Int, T) -> Boolean): List<T> {
            return this.filterIndexedKt(predicate)
        }

        @JvmStatic
        fun <T> Iterable<T>.filterNotNull(): List<T> {
            return this.filterNotNullKt()
        }

        @JvmStatic
        inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterTo(destination: C, predicate: (T) -> Boolean): C {
            return this.filterToKt(destination, predicate)
        }

        @JvmStatic
        inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterIndexedTo(
            destination: C,
            predicate: (index: Int, T) -> Boolean
        ): C {
            return this.filterIndexedToKt(destination, predicate)
        }

        @JvmStatic
        fun <C : MutableCollection<in T>, T> Iterable<T>.filterNotNullTo(destination: C): C {
            return this.filterTo(destination, { it !== null })
        }

        @JvmStatic
        inline fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R> {
            return this.mapKt(transform)
        }

        @JvmStatic
        inline fun <T, R> Iterable<T>.mapIndexed(transform: (index: Int, T) -> R): List<R> {
            return this.mapIndexedKt(transform)
        }

        @JvmStatic
        inline fun <T, R : Any> Iterable<T>.mapNotNull(transform: (T) -> R?): List<R> {
            return this.mapNotNullKt(transform)
        }

        @JvmStatic
        inline fun <T, R : Any> Iterable<T>.mapIndexedNotNull(transform: (index: Int, T) -> R?): List<R> {
            return this.mapIndexedNotNullKt(transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapTo(destination: C, transform: (T) -> R): C {
            return this.mapToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapIndexedTo(
            destination: C,
            transform: (index: Int, T) -> R
        ): C {
            return this.mapIndexedToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R : Any, C : MutableCollection<in R>> Iterable<T>.mapNotNullTo(
            destination: C,
            transform: (T) -> R?
        ): C {
            return this.mapNotNullToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R : Any, C : MutableCollection<in R>> Iterable<T>.mapIndexedNotNullTo(
            destination: C,
            transform: (index: Int, T) -> R?
        ): C {
            return this.mapIndexedNotNullToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R> Iterable<T>.flatMap(transform: (T) -> Iterable<R>): List<R> {
            return this.flatMapKt(transform)
        }

        @JvmStatic
        inline fun <T, R> Iterable<T>.flatMapIndexed(transform: (index: Int, T) -> Iterable<R>): List<R> {
            return this.flatMapIndexedKt(transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapTo(
            destination: C,
            transform: (T) -> Iterable<R>
        ): C {
            return this.flatMapToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapIndexedTo(
            destination: C,
            transform: (index: Int, T) -> Iterable<R>
        ): C {
            return this.flatMapIndexedToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, K, V> Iterable<T>.associate(
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return this.associateByKt(keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>): Map<K, V> {
            return this.associateKt(transform)
        }

        @JvmStatic
        inline fun <T, K> Iterable<T>.associateKey(keySelector: (T) -> K): Map<K, T> {
            return this.associateByKt(keySelector)
        }

        @JvmStatic
        inline fun <T, V> Iterable<T>.associateValue(valueSelector: (T) -> V): Map<T, V> {
            return this.associateWithKt(valueSelector)
        }

        @JvmStatic
        inline fun <T, K, V> Iterable<T>.associatePair(
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return this.associatePairTo(LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> Iterable<T>.associatePair(transform: (T, T) -> Pair<K, V>): Map<K, V> {
            return this.associatePairTo(LinkedHashMap(), transform)
        }

        @JvmStatic
        inline fun <T, K, V> Iterable<T>.associatePair(
            complement: T,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, V> {
            return this.associatePairTo(complement, LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V> Iterable<T>.associatePair(
            complement: T,
            transform: (T, T) -> Pair<K, V>
        ): Map<K, V> {
            return this.associatePairTo(complement, LinkedHashMap(), transform)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateTo(
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): M {
            return this.associateByToKt(destination, keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateTo(
            destination: M,
            transform: (T) -> Pair<K, V>
        ): M {
            return this.associateToKt(destination, transform)
        }

        @JvmStatic
        inline fun <T, K, M : MutableMap<in K, in T>> Iterable<T>.associateKeyTo(
            destination: M,
            keySelector: (T) -> K
        ): M {
            return this.associateByToKt(destination, keySelector)
        }

        @JvmStatic
        inline fun <T, V, M : MutableMap<in T, in V>> Iterable<T>.associateValueTo(
            destination: M,
            valueSelector: (T) -> V
        ): M {
            return this.associateWithToKt(destination, valueSelector)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associatePairTo(
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
        inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associatePairTo(
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
        inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associatePairTo(
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
        inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associatePairTo(
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
        inline fun <T, K> Iterable<T>.groupBy(keySelector: (T) -> K): Map<K, List<T>> {
            return this.groupByKt(keySelector)
        }

        @JvmStatic
        inline fun <T, K, V> Iterable<T>.groupBy(
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): Map<K, List<V>> {
            return this.groupByKt(keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <T, K, M : MutableMap<in K, MutableList<T>>> Iterable<T>.groupByTo(
            destination: M,
            keySelector: (T) -> K
        ): M {
            return this.groupByToKt(destination, keySelector)
        }

        @JvmStatic
        inline fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Iterable<T>.groupByTo(
            destination: M,
            keySelector: (T) -> K,
            valueTransform: (T) -> V
        ): M {
            return this.groupByToKt(destination, keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <S, T : S> Iterable<T>.reduce(operation: (S, T) -> S): S {
            return this.reduceKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> Iterable<T>.reduceIndexed(operation: (index: Int, S, T) -> S): S {
            return this.reduceIndexedKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> Iterable<T>.reduceOrNull(operation: (S, T) -> S): S? {
            return this.reduceOrNullKt(operation)
        }

        @JvmStatic
        inline fun <S, T : S> Iterable<T>.reduceIndexedOrNull(operation: (index: Int, S, T) -> S): S? {
            return this.reduceIndexedOrNullKt(operation)
        }

        @JvmStatic
        inline fun <T, R> Iterable<T>.reduce(initial: R, operation: (R, T) -> R): R {
            return this.foldKt(initial, operation)
        }

        @JvmStatic
        inline fun <T, R> Iterable<T>.reduceIndexed(initial: R, operation: (index: Int, R, T) -> R): R {
            return this.foldIndexedKt(initial, operation)
        }

        @JvmStatic
        inline fun <T, R, V> Iterable<T>.zip(other: Array<out R>, transform: (T, R) -> V): List<V> {
            return this.zipKt(other, transform)
        }

        @JvmStatic
        inline fun <T, R, V> Iterable<T>.zip(other: Iterable<R>, transform: (T, R) -> V): List<V> {
            return this.zipKt(other, transform)
        }

        @JvmStatic
        inline fun <T, R> Iterable<T>.zipWithNext(transform: (T, T) -> R): List<R> {
            return this.zipWithNextKt(transform)
        }

        @JvmStatic
        inline fun <T, R, V> Iterable<V>.unzip(transform: (V) -> Pair<T, R>): Pair<List<T>, List<R>> {
            if (this is Collection<V>) {
                val listT = ArrayList<T>(this.size)
                val listR = ArrayList<R>(this.size)
                for (e in this) {
                    val pair = transform(e)
                    listT.add(pair.first)
                    listR.add(pair.second)
                }
                return listT to listR
            } else {
                val listT = LinkedList<T>()
                val listR = LinkedList<R>()
                for (e in this) {
                    val pair = transform(e)
                    listT.add(pair.first)
                    listR.add(pair.second)
                }
                return ArrayList(listT) to ArrayList(listR)
            }
        }

        @JvmStatic
        inline fun <T, R> Iterable<R>.unzipWithNext(transform: (R) -> Pair<T, T>): List<T> {
            if (this is Collection<R>) {
                val listT = ArrayList<T>(this.size)
                for (e in this) {
                    val pair = transform(e)
                    listT.add(pair.first)
                    listT.add(pair.second)
                }
                return listT
            } else {
                val listT = LinkedList<T>()
                for (e in this) {
                    val pair = transform(e)
                    listT.add(pair.first)
                    listT.add(pair.second)
                }
                return ArrayList(listT)
            }
        }

        @JvmStatic
        fun <T> Iterable<T>.chunked(size: Int): List<List<T>> {
            return this.chunkedKt(size)
        }

        @JvmStatic
        fun <T, R> Iterable<T>.chunked(size: Int, transform: (List<T>) -> R): List<R> {
            return this.chunkedKt(size, transform)
        }

        @JvmStatic
        fun <T> Iterable<T>.windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<T>> {
            return this.windowedKt(size, step, partialWindows)
        }

        @JvmStatic
        fun <T, R> Iterable<T>.windowed(
            size: Int,
            step: Int = 1,
            partialWindows: Boolean = false,
            transform: (List<T>) -> R
        ): List<R> {
            return this.windowedKt(size, step, partialWindows, transform)
        }

        @JvmStatic
        fun <T> Iterable<T>.intersect(other: Iterable<T>): Set<T> {
            return this.intersectKt(other)
        }

        @JvmStatic
        fun <T> Iterable<T>.union(other: Iterable<T>): Set<T> {
            return this.unionKt(other)
        }

        @JvmStatic
        fun <T> Iterable<T>.subtract(other: Iterable<T>): Set<T> {
            return this.subtractKt(other)
        }

        @JvmStatic
        fun <T> Iterable<T>.distinct(): List<T> {
            return this.distinctKt()
        }

        @JvmStatic
        inline fun <T, K> Iterable<T>.distinct(selector: (T) -> K): List<T> {
            return this.distinctByKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.sorted(comparator: Comparator<in T> = castSelfComparableComparator()): List<T> {
            return this.sortedWithKt(comparator)
        }

        @JvmStatic
        fun <T> Iterable<T>.reversed(): List<T> {
            return this.reversedKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.shuffled(): List<T> {
            return this.shuffledKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.shuffled(random: Random): List<T> {
            return this.shuffledKt(random)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.max(comparator: Comparator<in T> = castSelfComparableComparator()): T {
            return this.maxOrNull(comparator).notNull()
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.maxOrNull(comparator: Comparator<in T> = castSelfComparableComparator()): T? {
            return this.maxWithOrNullKt(comparator)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.min(comparator: Comparator<in T> = castSelfComparableComparator()): T {
            return this.minOrNull(comparator).notNull()
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.minOrNull(comparator: Comparator<in T> = castSelfComparableComparator()): T? {
            return this.minWithOrNullKt(comparator)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.sumInt(selector: (T) -> Int = { it.toInt() }): Int {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.sumLong(selector: (T) -> Long = { it.toLong() }): Long {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.sumDouble(selector: (T) -> Double = { it.toDouble() }): Double {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.sumBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.sumBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
            return this.sumOfKt(selector)
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.averageInt(selector: (T) -> Int = { it.toInt() }): Int {
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
        inline fun <T> Iterable<T>.averageLong(selector: (T) -> Long = { it.toLong() }): Long {
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
        inline fun <T> Iterable<T>.averageDouble(selector: (T) -> Double = { it.toDouble() }): Double {
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
        inline fun <T> Iterable<T>.averageBigInteger(selector: (T) -> BigInteger = { it.toBigInteger() }): BigInteger {
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
        inline fun <T> Iterable<T>.averageBigDecimal(selector: (T) -> BigDecimal = { it.toBigDecimal() }): BigDecimal {
            var sum = BigDecimal.ZERO
            var count = 0
            for (t in this) {
                sum += selector(t)
                count++
            }
            return if (count == 0) BigDecimal.ZERO else sum / count.toBigDecimal()
        }

        @JvmStatic
        fun <T> Iterable<T>.toCollection(): Collection<T> {
            return this.toSet()
        }

        @JvmStatic
        fun <T> Iterable<T>.toMutableCollection(): MutableCollection<T> {
            return this.toMutableSet()
        }

        @JvmStatic
        fun <T> Iterable<T>.toSet(): Set<T> {
            return this.toSetKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.toMutableSet(): MutableSet<T> {
            return this.toMutableSetKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.toHashSet(): HashSet<T> {
            return this.toHashSetKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.toLinkedHashSet(): LinkedHashSet<T> {
            return this.takeAllTo(LinkedHashSet())
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.toSortedSet(comparator: Comparator<in T> = castSelfComparableComparator()): SortedSet<T> {
            return this.toSortedSetKt(comparator)
        }

        @JvmStatic
        fun <T> Iterable<T>.toList(): List<T> {
            return this.toListKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.toMutableList(): MutableList<T> {
            return this.toMutableListKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.toArrayList(): ArrayList<T> {
            return this.takeAllTo(ArrayList())
        }

        @JvmStatic
        fun <T> Iterable<T>.toLinkedList(): LinkedList<T> {
            return this.takeAllTo(LinkedList())
        }

        @JvmStatic
        fun <T> Iterable<T>.asToCollection(): Collection<T> {
            return asToSet()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToMutableCollection(): MutableCollection<T> {
            return this.asToMutableSet()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToSet(): Set<T> {
            return if (this is Set<T>) this else this.toSetKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToMutableSet(): MutableSet<T> {
            return if (this is MutableSet<T>) this else this.toMutableSetKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToHashSet(): HashSet<T> {
            return if (this is HashSet<T>) this else this.toHashSetKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToLinkedHashSet(): LinkedHashSet<T> {
            return if (this is LinkedHashSet<T>) this else this.toLinkedHashSet()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToSortedSet(): SortedSet<T> {
            return if (this is SortedSet<T>) this else this.toSortedSet()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToList(): List<T> {
            return if (this is List<T>) this else this.toListKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToMutableList(): MutableList<T> {
            return if (this is MutableList<T>) this else this.toMutableListKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToArrayList(): ArrayList<T> {
            return if (this is ArrayList<T>) this else this.toArrayList()
        }

        @JvmStatic
        fun <T> Iterable<T>.asToLinkedList(): LinkedList<T> {
            return if (this is LinkedList<T>) this else this.toLinkedList()
        }

        @JvmStatic
        @JvmOverloads
        fun <T> Iterable<T>.toStream(parallel: Boolean = false): Stream<T> {
            return StreamSupport.stream(this.spliterator(), parallel)
        }

        @JvmStatic
        fun <T> Iterable<T>.toSequence(): Sequence<T> {
            return this.asSequenceKt()
        }

        @JvmStatic
        fun <T> Iterable<T>.toArray(): Array<Any?> {
            val list = this.asToLinkedList()
            return list.toArray()
        }

        @JvmStatic
        inline fun <T> Iterable<T>.toArray(generator: (size: Int) -> Array<T>): Array<T> {
            val list = this.asToLinkedList()
            return list.toArray(generator(list.size))
        }

        @JvmStatic
        fun <T> Iterable<T>.toArray(componentType: Class<*>): Array<T> {
            val list = this.asToLinkedList()
            val array: Array<T> = componentType.componentTypeToArrayInstance(0)
            return list.toArray(array)
        }

        @JvmStatic
        inline fun <reified T> Iterable<T>.toTypedArray(): Array<T> {
            val list = this.asToList()
            return list.toTypedArrayKt()
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.toBooleanArray(selector: (T) -> Boolean = { it.toBoolean() }): BooleanArray {
            val list = this.asToList()
            val result = BooleanArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
            val list = this.asToList()
            val result = ByteArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
            val list = this.asToList()
            val result = ShortArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
            val list = this.asToList()
            val result = CharArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
            val list = this.asToList()
            val result = IntArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
            val list = this.asToList()
            val result = LongArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
            val list = this.asToList()
            val result = FloatArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Iterable<T>.toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
            val list = this.asToList()
            val result = DoubleArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> Iterable<T>.plus(element: T): List<T> {
            return this.plusKt(element)
        }

        @JvmStatic
        fun <T> Iterable<T>.plus(elements: Array<out T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Iterable<T>.plus(elements: Iterable<T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Iterable<T>.plus(elements: Sequence<T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Iterable<T>.minus(element: T): List<T> {
            return this.minusKt(element)
        }

        @JvmStatic
        fun <T> Iterable<T>.minus(elements: Array<out T>): List<T> {
            return this.minusKt(elements)
        }

        @JvmStatic
        fun <T> Iterable<T>.minus(elements: Iterable<T>): List<T> {
            return this.minusKt(elements)
        }

        @JvmStatic
        fun <T> Iterable<T>.minus(elements: Sequence<T>): List<T> {
            return this.minusKt(elements)
        }

        @JvmStatic
        fun <T> MutableIterable<T>.remove(element: T): Boolean {
            val iterator = this.iterator()
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
        fun <T> MutableIterable<T>.remove(n: Int): Boolean {
            val iterator = this.iterator()
            var success = true
            for (i in 1..n) {
                if (iterator.hasNext()) {
                    iterator.next()
                    iterator.remove()
                } else {
                    success = false
                    break
                }
            }
            return success
        }

        @JvmStatic
        fun <T> MutableIterable<T>.removeWhile(predicate: (T) -> Boolean): Boolean {
            return this.removeAllKt(predicate)
        }

        @JvmStatic
        fun <T> MutableIterable<T>.removeAll(): Boolean {
            return this.removeAllKt { true }
        }

        @JvmStatic
        fun <T> MutableIterable<T>.retainAll(predicate: (T) -> Boolean): Boolean {
            return this.retainAllKt(predicate)
        }
    }
}