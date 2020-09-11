package xyz.srclab.common.collection

import xyz.srclab.common.base.Require
import xyz.srclab.common.base.Sort
import xyz.srclab.common.base.To
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.random.Random

/**
 * @author sunqian
 */
object OpsForCollection {

    @JvmStatic
    inline fun <T> find(iterable: Iterable<T>, predicate: (T) -> Boolean): T? {
        return iterable.find(predicate)
    }

    @JvmStatic
    inline fun <T> findLast(iterable: Iterable<T>, predicate: (T) -> Boolean): T? {
        return iterable.findLast(predicate)
    }

    @JvmStatic
    inline fun <T> findLast(list: List<T>, predicate: (T) -> Boolean): T? {
        return list.findLast(predicate)
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
    fun <T> first(list: List<T>): T {
        return list.first()
    }

    @JvmStatic
    fun <T> firstOrNull(list: List<T>): T? {
        return list.firstOrNull()
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
    fun <T> last(list: List<T>): T {
        return list.last()
    }

    @JvmStatic
    inline fun <T> last(list: List<T>, predicate: (T) -> Boolean): T {
        return list.last(predicate)
    }

    @JvmStatic
    fun <T> lastOrNull(list: List<T>): T? {
        return list.lastOrNull()
    }

    @JvmStatic
    inline fun <T> lastOrNull(list: List<T>, predicate: (T) -> Boolean): T? {
        return list.lastOrNull(predicate)
    }

    @JvmStatic
    inline fun <T> all(iterable: Iterable<T>, predicate: (T) -> Boolean): Boolean {
        return iterable.all(predicate)
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
    fun <T> single(list: List<T>): T {
        return list.single()
    }

    @JvmStatic
    fun <T> singleOrNull(list: List<T>): T? {
        return list.singleOrNull()
    }

    @JvmStatic
    fun <T> contains(iterable: Iterable<T>, element: T): Boolean {
        return iterable.contains(element)
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
    fun <T> count(collection: Collection<T>): Int {
        return collection.count()
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
    fun <T> elementAt(list: List<T>, index: Int): T {
        return list.elementAt(index)
    }

    @JvmStatic
    inline fun <T> elementAtOrElse(list: List<T>, index: Int, defaultValue: (Int) -> T): T {
        return list.elementAtOrElse(index, defaultValue)
    }

    @JvmStatic
    fun <T> elementAtOrNull(list: List<T>, index: Int): T? {
        return list.elementAtOrNull(index)
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
    fun <T> indexOf(list: List<T>, element: T): Int {
        return list.indexOf(element)
    }

    @JvmStatic
    inline fun <T> indexOf(list: List<T>, predicate: (T) -> Boolean): Int {
        return list.indexOfFirst(predicate)
    }

    @JvmStatic
    fun <T> lastIndexOf(list: List<T>, element: T): Int {
        return list.lastIndexOf(element)
    }

    @JvmStatic
    inline fun <T> lastIndexOf(list: List<T>, predicate: (T) -> Boolean): Int {
        return list.indexOfLast(predicate)
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
    fun <T> dropLast(list: List<T>, n: Int): List<T> {
        return list.dropLast(n)
    }

    @JvmStatic
    inline fun <T> dropLastWhile(list: List<T>, predicate: (T) -> Boolean): List<T> {
        return list.dropLastWhile(predicate)
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
    fun <T> takeLast(list: List<T>, n: Int): List<T> {
        return list.takeLast(n)
    }

    @JvmStatic
    inline fun <T> takeLastWhile(list: List<T>, predicate: (T) -> Boolean): List<T> {
        return list.takeLastWhile(predicate)
    }

    @JvmStatic
    fun <T> slice(list: List<T>, indices: IntArray): List<T> {
        return slice(list, indices.asIterable())
    }

    @JvmStatic
    fun <T> slice(list: List<T>, indices: Iterable<Int>): List<T> {
        return list.slice(indices)
    }

    @JvmStatic
    fun <T> slice(list: List<T>, indices: IntRange): List<T> {
        return list.slice(indices)
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
    inline fun <T, R> map(iterable: Iterable<T>, transform: (T) -> R): List<R> {
        return iterable.map(transform)
    }

    @JvmStatic
    inline fun <T, R> mapIndexed(iterable: Iterable<T>, transform: (index: Int, T) -> R): List<R> {
        return iterable.mapIndexed(transform)
    }

    @JvmStatic
    inline fun <T, R> mapNotNull(iterable: Iterable<T>, transform: (T) -> R): List<R> {
        return iterable.mapNotNull(transform)
    }

    @JvmStatic
    inline fun <T, R> mapIndexedNotNull(iterable: Iterable<T>, transform: (index: Int, T) -> R): List<R> {
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
    inline fun <S, T : S> reduceRight(list: List<T>, operation: (T, S) -> S): S {
        return list.reduceRight(operation)
    }

    @JvmStatic
    inline fun <S, T : S> reduceRightIndexed(list: List<T>, operation: (Int, T, S) -> S): S {
        return list.reduceRightIndexed(operation)
    }

    @JvmStatic
    inline fun <S, T : S> reduceRightOrNull(list: List<T>, operation: (T, S) -> S): S? {
        return list.reduceRightOrNull(operation)
    }

    @JvmStatic
    inline fun <S, T : S> reduceRightIndexedOrNull(
        list: List<T>,
        operation: (Int, T, S) -> S
    ): S? {
        return list.reduceRightIndexedOrNull(operation)
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
    inline fun <T, R> reduceRight(list: List<T>, initial: R, operation: (T, R) -> R): R {
        return list.foldRight(initial, operation)
    }

    @JvmStatic
    inline fun <T, R> reduceRightIndexed(
        list: List<T>,
        initial: R,
        operation: (Int, T, R) -> R
    ): R {
        return list.foldRightIndexed(initial, operation)
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
    inline fun <T, K> associateKey(iterable: Iterable<T>, keySelector: (T) -> K): Map<K, T> {
        return iterable.associateBy(keySelector)
    }

    @JvmStatic
    inline fun <T, V> associateValue(iterable: Iterable<T>, valueSelector: (T) -> V): Map<T, V> {
        return iterable.associateWith(valueSelector)
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
    fun <T : Comparable<T>> max(iterable: Iterable<T>): T {
        return Require.notNull(maxOrNull(iterable))
    }

    @JvmStatic
    fun <T : Comparable<T>> maxOrNull(iterable: Iterable<T>): T? {
        return iterable.maxOrNull()
    }

    @JvmStatic
    fun <T> max(iterable: Iterable<T>, comparator: Comparator<in T>): T {
        return Require.notNull(maxOrNull(iterable, comparator))
    }

    @JvmStatic
    fun <T> maxOrNull(iterable: Iterable<T>, comparator: Comparator<in T>): T? {
        return iterable.maxWithOrNull(comparator)
    }

    @JvmStatic
    fun <T : Comparable<T>> min(iterable: Iterable<T>): T {
        return Require.notNull(minOrNull(iterable))
    }

    @JvmStatic
    fun <T : Comparable<T>> minOrNull(iterable: Iterable<T>): T? {
        return iterable.minOrNull()
    }

    @JvmStatic
    fun <T> min(iterable: Iterable<T>, comparator: Comparator<in T>): T {
        return Require.notNull(minOrNull(iterable, comparator))
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
    fun <T> sumLong(iterable: Iterable<T>): Long {
        return sumLong(iterable) { To.toLong(it) }
    }

    @JvmStatic
    fun <T> sumDouble(iterable: Iterable<T>): Double {
        return sumDouble(iterable) { To.toDouble(it) }
    }

    @JvmStatic
    fun <T> sumBigInteger(iterable: Iterable<T>): BigInteger {
        return sumBigInteger(iterable) { To.toBigInteger(it) }
    }

    @JvmStatic
    fun <T> sumBigDecimal(iterable: Iterable<T>): BigDecimal {
        return sumBigDecimal(iterable) { To.toBigDecimal(it) }
    }

    @JvmStatic
    inline fun <T> sumInt(iterable: Iterable<T>, selector: (T) -> Int): Int {
        return iterable.sumOf(selector)
    }

    @JvmStatic
    inline fun <T> sumLong(iterable: Iterable<T>, selector: (T) -> Long): Long {
        return iterable.sumOf(selector)
    }

    @JvmStatic
    fun <T> sumDouble(iterable: Iterable<T>, selector: (T) -> Double): Double {
        return iterable.sumOf(selector)
    }

    @JvmStatic
    fun <T> sumBigInteger(iterable: Iterable<T>, selector: (T) -> BigInteger): BigInteger {
        return iterable.sumOf(selector)
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
    fun <T> reverse(list: MutableList<T>) {
        list.reverse()
    }

    @JvmStatic
    fun <T> reversed(iterable: Iterable<T>): List<T> {
        return iterable.reversed()
    }

    @JvmStatic
    fun <T : Comparable<T>> sort(list: MutableList<T>) {
        list.sort()
    }

    @JvmStatic
    fun <T> sort(list: MutableList<T>, comparator: Comparator<in T>) {
        list.sortWith(comparator)
    }

    @JvmStatic
    fun <T : Comparable<T>> sorted(iterable: Iterable<T>): List<T> {
        return iterable.sorted()
    }

    @JvmStatic
    fun <T> sorted(iterable: Iterable<T>, comparator: Comparator<in T>): List<T> {
        return iterable.sortedWith(comparator)
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
    fun <T> shuffle(list: MutableList<T>) {
        list.shuffle()
    }

    @JvmStatic
    fun <T> shuffle(list: MutableList<T>, random: Random) {
        list.shuffle(random)
    }

    @JvmStatic
    inline fun <T> forEachIndexed(iterable: Iterable<T>, action: (index: Int, T) -> Unit) {
        return iterable.forEachIndexed(action)
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
    fun <T> plus(collection: Collection<T>, element: T): List<T> {
        return collection.plus(element)
    }

    @JvmStatic
    fun <T> plus(collection: Collection<T>, elements: Array<out T>): List<T> {
        return collection.plus(elements)
    }

    @JvmStatic
    fun <T> plus(collection: Collection<T>, elements: Iterable<T>): List<T> {
        return collection.plus(elements)
    }

    @JvmStatic
    fun <T> plus(collection: Collection<T>, elements: Sequence<T>): List<T> {
        return collection.plus(elements)
    }

    @JvmStatic
    fun <T> plus(set: Set<T>, element: T): Set<T> {
        return set.plus(element)
    }

    @JvmStatic
    fun <T> plus(set: Set<T>, elements: Array<out T>): Set<T> {
        return set.plus(elements)
    }

    @JvmStatic
    fun <T> plus(set: Set<T>, elements: Iterable<T>): Set<T> {
        return set.plus(elements)
    }

    @JvmStatic
    fun <T> plus(set: Set<T>, elements: Sequence<T>): Set<T> {
        return set.plus(elements)
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
    fun <T> minus(set: Set<T>, element: T): Set<T> {
        return set.minus(element)
    }

    @JvmStatic
    fun <T> minus(set: Set<T>, elements: Array<out T>): Set<T> {
        return set.minus(elements)
    }

    @JvmStatic
    fun <T> minus(set: Set<T>, elements: Iterable<T>): Set<T> {
        return set.minus(elements)
    }

    @JvmStatic
    fun <T> minus(set: Set<T>, elements: Sequence<T>): Set<T> {
        return set.minus(elements)
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
    fun <T> toSortedSet(iterable: Iterable<T>, comparator: java.util.Comparator<in T>): SortedSet<T> {
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
    fun <T> toMutableList(collection: Collection<T>): MutableList<T> {
        return collection.toMutableList()
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
    fun <T> toArray(iterable: Iterable<T>): Array<Any?> {
        val list = toList(iterable)
        val result = arrayOfNulls<Any?>(list.size)
        list.forEachIndexed { i, t -> result[i] = t }
        return result
    }

    @JvmStatic
    inline fun <T> toArray(iterable: Iterable<T>, generator: (size: Int) -> Array<T>): Array<T> {
        val list = toList(iterable)
        val result = generator(list.size)
        list.forEachIndexed { i, t -> result[i] = t }
        return result
    }

    @JvmStatic
    fun <T> toBooleanArray(iterable: Iterable<T>): BooleanArray {
        return toBooleanArray(iterable) { To.toBoolean(it) }
    }

    @JvmStatic
    inline fun <T> toBooleanArray(iterable: Iterable<T>, selector: (T) -> Boolean): BooleanArray {
        val list = toList(iterable)
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
        val list = toList(iterable)
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
        val list = toList(iterable)
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
        val list = toList(iterable)
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
        val list = toList(iterable)
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
        val list = toList(iterable)
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
        val list = toList(iterable)
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
        val list = toList(iterable)
        val result = DoubleArray(list.size)
        list.forEachIndexed { i, t -> result[i] = selector(t) }
        return result
    }
}