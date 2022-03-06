@file:JvmName("BCollect")

package xyz.srclab.common.collect

import xyz.srclab.common.base.*
import xyz.srclab.common.convert.Converter
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.IntFunction
import java.util.function.Predicate
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.random.Random
import kotlin.collections.addAll as addAllKt
import kotlin.collections.all as allKt
import kotlin.collections.any as anyKt
import kotlin.collections.associate as associateKt
import kotlin.collections.associateBy as associateByKt
import kotlin.collections.associateByTo as associateByToKt
import kotlin.collections.associateTo as associateToKt
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
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt
import kotlin.collections.last as lastKt
import kotlin.collections.lastIndexOf as lastIndexOfKt
import kotlin.collections.lastOrNull as lastOrNullKt
import kotlin.collections.map as mapKt
import kotlin.collections.mapIndexed as mapIndexedKt
import kotlin.collections.mapIndexedTo as mapIndexedToKt
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
import kotlin.collections.take as takeKt
import kotlin.collections.takeWhile as takeWhileKt
import kotlin.collections.toCollection as toCollectionKt
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

fun <T, C : MutableCollection<in T>> C.collect(vararg elements: T): C = apply {
    this.addAllKt(elements)
}

fun <T, C : MutableCollection<in T>> C.collect(elements: Iterable<T>): C = apply {
    this.addAllKt(elements)
}

fun <T> Iterable<T>.contains(element: T): Boolean {
    return this.containsKt(element)
}

fun <T> Iterable<T>.count(): Int {
    return this.countKt()
}

fun <T> Iterable<T>.count(predicate: Predicate<in T>): Int {
    return this.countKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.isEmpty(): Boolean {
    if (this is Collection) {
        return this.isEmpty()
    }
    for (it in this) {
        return false
    }
    return true
}

fun <T> Iterable<T>.isNotEmpty(): Boolean {
    return !isEmpty()
}

fun <T> Iterable<T>.any(): Boolean {
    return this.anyKt()
}

fun <T> Iterable<T>.any(predicate: Predicate<in T>): Boolean {
    return this.anyKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.none(): Boolean {
    return this.noneKt()
}

fun <T> Iterable<T>.none(predicate: Predicate<in T>): Boolean {
    return this.noneKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.all(predicate: Predicate<in T>): Boolean {
    return this.allKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.first(): T {
    return this.firstKt()
}

fun <T> Iterable<T>.first(predicate: Predicate<in T>): T {
    return this.firstKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.firstOrNull(): T? {
    return this.firstOrNullKt()
}

fun <T> Iterable<T>.firstOrNull(predicate: Predicate<in T>): T? {
    return this.firstOrNullKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.last(): T {
    return this.lastKt()
}

fun <T> Iterable<T>.last(predicate: Predicate<in T>): T {
    return this.lastKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.lastOrNull(): T? {
    return this.lastOrNullKt()
}

fun <T> Iterable<T>.lastOrNull(predicate: Predicate<in T>): T? {
    return this.lastOrNullKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.random(): T {
    return asToList().randomKt()
}

fun <T> Iterable<T>.random(random: Random): T {
    return asToList().randomKt(random)
}

fun <T> Iterable<T>.randomOrNull(): T? {
    return asToList().randomOrNullKt()
}

fun <T> Iterable<T>.randomOrNull(random: Random): T? {
    return asToList().randomOrNullKt(random)
}

fun <T> Iterable<T>.get(index: Int): T {
    return this.elementAtKt(index)
}

fun <T> Iterable<T>.getOrNull(index: Int): T? {
    return this.elementAtOrNullKt(index)
}

fun <T> Iterable<T>.getOrDefault(index: Int, defaultValue: T): T {
    return this.elementAtOrElseKt(index) { defaultValue }
}

fun <T> Iterable<T>.getOrElse(index: Int, defaultValue: IntFunction<T>): T {
    return this.elementAtOrElseKt(index, defaultValue.asKotlinFun())
}

@JvmOverloads
fun <T : Any> Iterable<*>.get(index: Int, type: Class<out T>, converter: Converter = Converter.defaultConverter()): T {
    return converter.convert(getOrNull(index), type)
}

@JvmOverloads
fun <T : Any> Iterable<*>.getOrNull(
    index: Int,
    type: Class<out T>,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrNull(getOrNull(index), type)
}

fun <T : Any> Iterable<*>.getOrDefault(index: Int, defaultValue: T, converter: Converter): T {
    return converter.convertOrNull(getOrNull(index), defaultValue.javaClass) ?: defaultValue
}

fun Iterable<*>.getBoolean(index: Int): Boolean {
    return get(index).toBoolean()
}

fun Iterable<*>.getBooleanOrNull(index: Int): Boolean? {
    return getOrNull(index)?.toBoolean()
}

fun Iterable<*>.getByte(index: Int): Byte {
    return get(index).toByte()
}

fun Iterable<*>.getByteOrNull(index: Int): Byte? {
    return getOrNull(index)?.toByte()
}

fun Iterable<*>.getShort(index: Int): Short {
    return get(index).toShort()
}

fun Iterable<*>.getShortOrNull(index: Int): Short? {
    return getOrNull(index)?.toShort()
}

fun Iterable<*>.getChar(index: Int): Char {
    return get(index).toChar()
}

fun Iterable<*>.getCharOrNull(index: Int): Char? {
    return getOrNull(index)?.toChar()
}

fun Iterable<*>.getInt(index: Int): Int {
    return get(index).toInt()
}

fun Iterable<*>.getIntOrNull(index: Int): Int? {
    return getOrNull(index)?.toInt()
}

fun Iterable<*>.getLong(index: Int): Long {
    return get(index).toLong()
}

fun Iterable<*>.getLongOrNull(index: Int): Long? {
    return getOrNull(index)?.toLong()
}

fun Iterable<*>.getFloat(index: Int): Float {
    return get(index).toFloat()
}

fun Iterable<*>.getFloatOrNull(index: Int): Float? {
    return getOrNull(index)?.toFloat()
}

fun Iterable<*>.getDouble(index: Int): Double {
    return get(index).toDouble()
}

fun Iterable<*>.getDoubleOrNull(index: Int): Double? {
    return getOrNull(index)?.toDouble()
}

fun <T> Iterable<T>.find(predicate: Predicate<in T>): T? {
    return this.findKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.findLast(predicate: Predicate<in T>): T? {
    return this.firstKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.indexOf(element: T): Int {
    return this.indexOfKt(element)
}

fun <T> Iterable<T>.indexOf(predicate: Predicate<in T>): Int {
    return this.indexOfFirstKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.lastIndexOf(element: T): Int {
    return this.lastIndexOfKt(element)
}

fun <T> Iterable<T>.lastIndexOf(predicate: Predicate<in T>): Int {
    return this.indexOfLastKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.take(n: Int): List<T> {
    return this.takeKt(n)
}

fun <T> Iterable<T>.take(predicate: Predicate<in T>): List<T> {
    return this.takeWhileKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.drop(n: Int): List<T> {
    return this.dropKt(n)
}

fun <T> Iterable<T>.drop(predicate: Predicate<in T>): List<T> {
    return this.dropWhileKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.filter(predicate: Predicate<in T>): List<T> {
    return this.filterKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.filterIndexed(predicate: IndexedPredicate<in T>): List<T> {
    return this.filterIndexedKt(predicate.asKotlinFun())
}

fun <T> Iterable<T>.filterNotNull(): List<T> {
    return this.filterNotNullKt()
}

fun <T, C : MutableCollection<in T>> Iterable<T>.filterTo(destination: C, predicate: Predicate<in T>): C {
    return this.filterToKt(destination, predicate.asKotlinFun())
}

fun <T, C : MutableCollection<in T>> Iterable<T>.filterIndexedTo(destination: C, predicate: IndexedPredicate<in T>): C {
    return this.filterIndexedToKt(destination, predicate.asKotlinFun())
}

fun <C : MutableCollection<in T>, T> Iterable<T>.filterNotNullTo(destination: C): C {
    return this.filterTo(destination) { it !== null }
}

fun <T, R> Iterable<T>.map(transform: Function<in T, R>): List<R> {
    return this.mapKt(transform.asKotlinFun())
}

fun <T, R> Iterable<T>.mapIndexed(transform: IndexedFunction<in T, R>): List<R> {
    return this.mapIndexedKt(transform.asKotlinFun())
}

fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapTo(destination: C, transform: Function<in T, R>): C {
    return this.mapToKt(destination, transform.asKotlinFun())
}

fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapIndexedTo(
    destination: C,
    transform: IndexedFunction<in T, R>
): C {
    return this.mapIndexedToKt(destination, transform.asKotlinFun())
}

fun <T, R> Iterable<T>.flatMap(transform: Function<in T, Iterable<R>>): List<R> {
    return this.flatMapKt(transform.asKotlinFun())
}

fun <T, R> Iterable<T>.flatMapIndexed(transform: IndexedFunction<in T, Iterable<R>>): List<R> {
    return this.flatMapIndexedKt(transform.asKotlinFun())
}

fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapTo(
    destination: C,
    transform: Function<in T, Iterable<R>>
): C {
    return this.flatMapToKt(destination, transform.asKotlinFun())
}

fun <T, R, C : MutableCollection<in R>> Iterable<T>.flatMapIndexedTo(
    destination: C,
    transform: IndexedFunction<in T, Iterable<R>>
): C {
    return this.flatMapIndexedToKt(destination, transform.asKotlinFun())
}

fun <T, K, V> Iterable<T>.toMap(
    keySelector: Function<in T, K>,
    valueTransform: Function<in T, V>
): Map<K, V> {
    return this.associateByKt(keySelector.asKotlinFun(), valueTransform.asKotlinFun())
}

fun <T, K, V> Iterable<T>.toMap(transform: Function<in T, Map.Entry<K, V>>): Map<K, V> {
    return this.associateKt { transform.apply(it).toPair() }
}

fun <T, K, V> Iterable<T>.toMapWithNext(
    keySelector: Function<in T, K>,
    valueTransform: Function<in T, V>
): Map<K, V> {
    return toMapWithNext(LinkedHashMap(), keySelector, valueTransform)
}

fun <T, K, V> Iterable<T>.toMapWithNext(transform: BiFunction<in T, in T, Map.Entry<K, V>>): Map<K, V> {
    return toMapWithNext(LinkedHashMap(), transform)
}

fun <T, K, V> Iterable<T>.toMapWithNext(
    defaultValue: T,
    keySelector: Function<in T, K>,
    valueTransform: Function<in T, V>
): Map<K, V> {
    return toMapWithNext(LinkedHashMap(), defaultValue, keySelector, valueTransform)
}

fun <T, K, V> Iterable<T>.toMapWithNext(
    defaultValue: T,
    transform: BiFunction<in T, in T, Map.Entry<K, V>>
): Map<K, V> {
    return toMapWithNext(LinkedHashMap(), defaultValue, transform)
}

@JvmSynthetic
inline fun <T, K, V> Iterable<T>.toMapWithNext(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, V> {
    return toMapWithNext(LinkedHashMap(), keySelector, valueTransform)
}

@JvmSynthetic
inline fun <T, K, V> Iterable<T>.toMapWithNext(transform: (T, T) -> Pair<K, V>): Map<K, V> {
    return toMapWithNext(LinkedHashMap(), transform)
}

@JvmSynthetic
inline fun <T, K, V> Iterable<T>.toMapWithNext(
    defaultValue: T,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): Map<K, V> {
    return toMapWithNext(LinkedHashMap(), defaultValue, keySelector, valueTransform)
}

@JvmSynthetic
inline fun <T, K, V> Iterable<T>.toMapWithNext(defaultValue: T, transform: (T, T) -> Pair<K, V>): Map<K, V> {
    return toMapWithNext(LinkedHashMap(), defaultValue, transform)
}

fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMap(
    destination: M,
    keySelector: Function<in T, K>,
    valueTransform: Function<in T, V>
): M {
    return this.associateByToKt(destination, keySelector.asKotlinFun(), valueTransform.asKotlinFun())
}

fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMap(
    destination: M,
    transform: Function<in T, Map.Entry<K, V>>
): M {
    return this.associateToKt(destination) { transform.apply(it).toPair() }
}

fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithNext(
    destination: M,
    keySelector: Function<in T, K>,
    valueTransform: Function<in T, V>
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val e1 = iterator.next()
        if (!iterator.hasNext()) {
            return destination
        }
        val e2 = iterator.next()
        val k = keySelector.apply(e1)
        val v = valueTransform.apply(e2)
        destination[k] = v
    }
    return destination
}

fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithNext(
    destination: M,
    transform: BiFunction<in T, in T, Map.Entry<K, V>>
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val e1 = iterator.next()
        if (!iterator.hasNext()) {
            return destination
        }
        val e2 = iterator.next()
        val entry = transform.apply(e1, e2)
        destination[entry.key] = entry.value
    }
    return destination
}

fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithNext(
    destination: M,
    defaultValue: T,
    keySelector: Function<in T, K>,
    valueTransform: Function<in T, V>
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val e1 = iterator.next()
        val e2 = if (iterator.hasNext()) {
            iterator.next()
        } else {
            defaultValue
        }
        val k = keySelector.apply(e1)
        val v = valueTransform.apply(e2)
        destination[k] = v
    }
    return destination
}

fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithNext(
    destination: M,
    defaultValue: T,
    transform: BiFunction<in T, in T, Map.Entry<K, V>>
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val e1 = iterator.next()
        val e2 = if (iterator.hasNext()) {
            iterator.next()
        } else {
            defaultValue
        }
        val entry = transform.apply(e1, e2)
        destination[entry.key] = entry.value
    }
    return destination
}

@JvmSynthetic
inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithNext(
    destination: M,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val e1 = iterator.next()
        if (!iterator.hasNext()) {
            return destination
        }
        val e2 = iterator.next()
        val k = keySelector(e1)
        val v = valueTransform(e2)
        destination[k] = v
    }
    return destination
}

@JvmSynthetic
inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithNext(
    destination: M,
    transform: (T, T) -> Pair<K, V>
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val e1 = iterator.next()
        if (!iterator.hasNext()) {
            return destination
        }
        val e2 = iterator.next()
        val entry = transform(e1, e2)
        destination[entry.first] = entry.second
    }
    return destination
}

@JvmSynthetic
inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithNext(
    destination: M,
    defaultValue: T,
    keySelector: (T) -> K,
    valueTransform: (T) -> V
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val e1 = iterator.next()
        val e2 = if (iterator.hasNext()) {
            iterator.next()
        } else {
            defaultValue
        }
        val k = keySelector(e1)
        val v = valueTransform(e2)
        destination[k] = v
    }
    return destination
}

@JvmSynthetic
inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.toMapWithNext(
    destination: M,
    defaultValue: T,
    transform: (T, T) -> Pair<K, V>
): M {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val e1 = iterator.next()
        val e2 = if (iterator.hasNext()) {
            iterator.next()
        } else {
            defaultValue
        }
        val entry = transform(e1, e2)
        destination[entry.first] = entry.second
    }
    return destination
}

fun <T, K> Iterable<T>.groupBy(keySelector: Function<in T, K>): Map<K, List<T>> {
    return this.groupByKt(keySelector.asKotlinFun())
}

fun <T, K, V> Iterable<T>.groupBy(
    keySelector: Function<in T, K>,
    valueTransform: Function<in T, V>
): Map<K, List<V>> {
    return this.groupByKt(keySelector.asKotlinFun(), valueTransform.asKotlinFun())
}

fun <T, K, M : MutableMap<in K, MutableList<T>>> Iterable<T>.groupByTo(
    destination: M,
    keySelector: Function<in T, K>
): M {
    return this.groupByToKt(destination, keySelector.asKotlinFun())
}

fun <T, K, V, M : MutableMap<in K, MutableList<V>>> Iterable<T>.groupByTo(
    destination: M,
    keySelector: Function<in T, K>,
    valueTransform: Function<in T, V>
): M {
    return this.groupByToKt(destination, keySelector.asKotlinFun(), valueTransform.asKotlinFun())
}

fun <S, T : S> Iterable<T>.reduce(operation: BiFunction<in S, in T, S>): S {
    return this.reduceKt(operation.asKotlinFun())
}

fun <S, T : S> Iterable<T>.reduceIndexed(operation: IndexedBiFunction<in S, in T, S>): S {
    return this.reduceIndexedKt(operation.asKotlinFun())
}

fun <S, T : S> Iterable<T>.reduceOrNull(operation: BiFunction<in S, in T, S>): S? {
    return this.reduceOrNullKt(operation.asKotlinFun())
}

fun <S, T : S> Iterable<T>.reduceIndexedOrNull(operation: IndexedBiFunction<in S, in T, S>): S? {
    return this.reduceIndexedOrNullKt(operation.asKotlinFun())
}

fun <T, R> Iterable<T>.reduce(initial: R, operation: BiFunction<in R, in T, R>): R {
    return this.foldKt(initial, operation.asKotlinFun())
}

fun <T, R> Iterable<T>.reduceIndexed(initial: R, operation: IndexedBiFunction<in R, in T, R>): R {
    return this.foldIndexedKt(initial, operation.asKotlinFun())
}

fun <T, R, V> Iterable<T>.zip(other: Array<out R>, transform: BiFunction<in T, in R, V>): List<V> {
    return this.zipKt(other, transform.asKotlinFun())
}

fun <T, R, V> Iterable<T>.zip(other: Iterable<R>, transform: BiFunction<in T, in R, V>): List<V> {
    return this.zipKt(other, transform.asKotlinFun())
}

fun <T, R> Iterable<T>.zipWithNext(transform: BiFunction<in T, in T, R>): List<R> {
    return this.zipWithNextKt(transform.asKotlinFun())
}

fun <K, V> Iterable<K>.zipMap(other: Array<out V>): Map<K, V> {
    return zipMapTo(LinkedHashMap(), other)
}

fun <K, V> Iterable<K>.zipMap(other: Iterable<V>): Map<K, V> {
    return zipMapTo(LinkedHashMap(), other)
}

fun <K, V> Iterable<K>.zipMap(other: Array<out V>, defaultKey: K, defaultValue: V): Map<K, V> {
    return zipMapTo(LinkedHashMap(), other, defaultKey, defaultValue)
}

fun <K, V> Iterable<K>.zipMap(other: Iterable<V>, defaultKey: K, defaultValue: V): Map<K, V> {
    return zipMapTo(LinkedHashMap(), other, defaultKey, defaultValue)
}

fun <K, V, M : MutableMap<in K, in V>> Iterable<K>.zipMapTo(destination: M, other: Array<out V>): M {
    return zipMapTo(destination, other.asList())
}

fun <K, V, M : MutableMap<in K, in V>> Iterable<K>.zipMapTo(destination: M, other: Iterable<V>): M {
    val itk = this.iterator()
    val itv = other.iterator()
    while (itk.hasNext()) {
        if (itv.hasNext()) {
            destination[itk.next()] = itv.next()
        } else {
            break
        }
    }
    return destination
}

fun <K, V, M : MutableMap<in K, in V>> Iterable<K>.zipMapTo(
    destination: M,
    other: Array<out V>,
    defaultKey: K,
    defaultValue: V
): M {
    return zipMapTo(destination, other.asList(), defaultKey, defaultValue)
}

fun <K, V, M : MutableMap<in K, in V>> Iterable<K>.zipMapTo(
    destination: M,
    other: Iterable<V>,
    defaultKey: K,
    defaultValue: V
): M {
    val itk = this.iterator()
    val itv = other.iterator()
    while (itk.hasNext()) {
        val k = itk.next()
        val v = if (itv.hasNext()) {
            itv.next()
        } else {
            defaultValue
        }
        destination[k] = v
    }
    while (itv.hasNext()) {
        destination[defaultKey] = itv.next()
    }
    return destination
}

fun <T> Iterable<T>.chunked(size: Int): List<List<T>> {
    return this.chunkedKt(size)
}

fun <T, R> Iterable<T>.chunked(size: Int, transform: Function<in List<T>, R>): List<R> {
    return this.chunkedKt(size, transform.asKotlinFun())
}

@JvmOverloads
fun <T> Iterable<T>.windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<T>> {
    return this.windowedKt(size, step, partialWindows)
}

fun <T, R> Iterable<T>.windowed(size: Int, transform: Function<in List<T>, R>): List<R> {
    return this.windowedKt(size, 1, false, transform.asKotlinFun())
}

fun <T, R> Iterable<T>.windowed(
    size: Int,
    step: Int,
    partialWindows: Boolean,
    transform: Function<in List<T>, R>
): List<R> {
    return this.windowedKt(size, step, partialWindows, transform.asKotlinFun())
}

fun <T> Iterable<T>.intersect(other: Iterable<T>): Set<T> {
    return this.intersectKt(other)
}

fun <T> Iterable<T>.union(other: Iterable<T>): Set<T> {
    return this.unionKt(other)
}

fun <T> Iterable<T>.subtract(other: Iterable<T>): Set<T> {
    return this.subtractKt(other)
}

fun <T> Iterable<T>.distinct(): List<T> {
    return this.distinctKt()
}

fun <T, K> Iterable<T>.distinct(selector: Function<in T, K>): List<T> {
    return this.distinctByKt(selector.asKotlinFun())
}

@JvmOverloads
fun <T> Iterable<T>.sorted(comparator: Comparator<in T> = castComparableComparator()): List<T> {
    return this.sortedWithKt(comparator)
}

fun <T> Iterable<T>.reversed(): List<T> {
    return this.reversedKt()
}

fun <T> Iterable<T>.shuffled(): List<T> {
    return this.shuffledKt()
}

fun <T> Iterable<T>.shuffled(random: Random): List<T> {
    return this.shuffledKt(random)
}

fun <T> Iterable<T>.forEachIndexed(action: IndexedConsumer<in T>) {
    return this.forEachIndexedKt(action.asKotlinFun())
}

@JvmOverloads
fun <T> Iterable<T>.max(comparator: Comparator<in T> = castComparableComparator()): T {
    return maxOrNull(comparator) ?: throw NoSuchElementException()
}

@JvmOverloads
fun <T> Iterable<T>.maxOrNull(comparator: Comparator<in T> = castComparableComparator()): T? {
    return this.maxWithOrNullKt(comparator)
}

@JvmOverloads
fun <T> Iterable<T>.min(comparator: Comparator<in T> = castComparableComparator()): T {
    return minOrNull(comparator) ?: throw NoSuchElementException()
}

@JvmOverloads
fun <T> Iterable<T>.minOrNull(comparator: Comparator<in T> = castComparableComparator()): T? {
    return this.minWithOrNullKt(comparator)
}

fun <T> Iterable<T>.toMutableCollection(): MutableCollection<T> {
    return toMutableSet()
}

fun <T> Iterable<T>.toSet(): Set<T> {
    return this.toSetKt()
}

fun <T> Iterable<T>.toMutableSet(): MutableSet<T> {
    return this.toMutableSetKt()
}

fun <T> Iterable<T>.toHashSet(): HashSet<T> {
    return this.toHashSetKt()
}

fun <T> Iterable<T>.toLinkedHashSet(): LinkedHashSet<T> {
    return if (this is Collection<T>) this.toCollectionKt(LinkedHashSet(size)) else this.toCollectionKt(LinkedHashSet())
}

@JvmOverloads
fun <T> Iterable<T>.toSortedSet(comparator: Comparator<in T>? = null): SortedSet<T> {
    return if (comparator === null) this.toSortedSetKt(castComparableComparator()) else this.toSortedSetKt(comparator)
}

@JvmOverloads
fun <T> Iterable<T>.toTreeSet(comparator: Comparator<in T>? = null): TreeSet<T> {
    return this.toCollectionKt(if (comparator === null) TreeSet() else TreeSet(comparator))
}

fun <T> Iterable<T>.toList(): List<T> {
    return this.toListKt()
}

fun <T> Iterable<T>.toMutableList(): MutableList<T> {
    return this.toMutableListKt()
}

fun <T> Iterable<T>.toArrayList(): ArrayList<T> {
    return if (this is Collection<T>) this.toCollectionKt(ArrayList(size)) else this.toCollectionKt(ArrayList())
}

fun <T> Iterable<T>.toLinkedList(): LinkedList<T> {
    return this.toCollectionKt(LinkedList())
}

fun <T> Iterable<T>.asToCollection(): Collection<T> {
    return if (this is Collection<T>) this else this.toSetKt()
}

fun <T> Iterable<T>.asToMutableCollection(): MutableCollection<T> {
    return if (this is MutableCollection<T>) this else toMutableCollection()
}

fun <T> Iterable<T>.asToSet(): Set<T> {
    return if (this is Set<T>) this else toSet()
}

fun <T> Iterable<T>.asToMutableSet(): MutableSet<T> {
    return if (this is MutableSet<T>) this else toMutableSet()
}

fun <T> Iterable<T>.asToHashSet(): HashSet<T> {
    return if (this is HashSet<T>) this else toHashSet()
}

fun <T> Iterable<T>.asToLinkedHashSet(): LinkedHashSet<T> {
    return if (this is LinkedHashSet<T>) this else toLinkedHashSet()
}

@JvmOverloads
fun <T> Iterable<T>.asToSortedSet(comparator: Comparator<in T>? = null): SortedSet<T> {
    return if (this is SortedSet<T>) this else toSortedSet(comparator)
}

@JvmOverloads
fun <T> Iterable<T>.asToTreeSet(comparator: Comparator<in T>? = null): TreeSet<T> {
    return if (this is TreeSet<T>) this else toTreeSet(comparator)
}

fun <T> Iterable<T>.asToList(): List<T> {
    return if (this is List<T>) this else toList()
}

fun <T> Iterable<T>.asToMutableList(): MutableList<T> {
    return if (this is MutableList<T>) this else toMutableList()
}

fun <T> Iterable<T>.asToArrayList(): ArrayList<T> {
    return if (this is ArrayList<T>) this else toArrayList()
}

fun <T> Iterable<T>.asToLinkedList(): LinkedList<T> {
    return if (this is LinkedList<T>) this else toLinkedList()
}

@JvmOverloads
fun <T> Iterable<T>.toStream(parallel: Boolean = false): Stream<T> {
    return StreamSupport.stream(this.spliterator(), parallel)
}

inline fun <reified T> Iterable<T>.toTypedArray(): Array<T> {
    return asToCollection().toTypedArrayKt()
}

fun <T> Iterable<T>.toArray(): Array<Any?> {
    return asToCollection().toTypedArrayKt()
}

fun <T : R, R> Iterable<T>.toArray(type: Class<R>): Array<R> {
    val set = asToCollection()
    val array = type.newArray(set.size)
    return toArray(array)
}

fun <T : R, R> Iterable<T>.toArray(array: Array<R>): Array<R> {
    var i = 0
    for (t in this) {
        if (i < array.size) {
            array[i] = t
            i++
        } else {
            break
        }
    }
    return array
}

fun <T, R> Iterable<T>.toArray(type: Class<R>, transform: Function<in T, R>): Array<R> {
    return toArray(type, transform.asKotlinFun())
}

fun <T, R> Iterable<T>.toArray(array: Array<R>, transform: Function<in T, R>): Array<R> {
    return toArray(array, transform.asKotlinFun())
}

@JvmSynthetic
inline fun <T, R> Iterable<T>.toArray(type: Class<R>, transform: (T) -> R): Array<R> {
    val set = asToCollection()
    val array = type.newArray(set.size)
    return toArray(array, transform)
}

@JvmSynthetic
inline fun <T, R> Iterable<T>.toArray(array: Array<R>, transform: (T) -> R): Array<R> {
    var i = 0
    for (t in this) {
        if (i < array.size) {
            array[i] = transform(t)
            i++
        } else {
            break
        }
    }
    return array
}

fun <T> Iterable<T>.plus(element: T): List<T> {
    return this.plusKt(element)
}

fun <T> Iterable<T>.plus(elements: Array<out T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Iterable<T>.plus(elements: Iterable<T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Iterable<T>.minus(element: T): List<T> {
    return this.minusKt(element)
}

fun <T> Iterable<T>.minus(elements: Array<out T>): List<T> {
    return this.minusKt(elements)
}

fun <T> Iterable<T>.minus(elements: Iterable<T>): List<T> {
    return this.minusKt(elements)
}

fun <T> Iterable<T>.plusBefore(index: Int, element: T): List<T> {
    return plusBefore(index, listOf(element))
}

fun <T> Iterable<T>.plusBefore(index: Int, elements: Array<out T>): List<T> {
    return plusBefore(index, elements.asList())
}

fun <T> Iterable<T>.plusBefore(index: Int, elements: Iterable<T>): List<T> {
    if (index == 0) {
        return elements.plusKt(this)
    }
    val list = asToList()
    val front = list.subList(0, index)
    val back = list.subList(index, list.size)
    return concatList(front, elements, back)
    //return front.plusKt(elements).plusKt(back)
}

fun <T> Iterable<T>.plusAfter(index: Int, element: T): List<T> {
    return plusAfter(index, listOf(element))
}

fun <T> Iterable<T>.plusAfter(index: Int, elements: Array<out T>): List<T> {
    return plusAfter(index, elements.asList())
}

fun <T> Iterable<T>.plusAfter(index: Int, elements: Iterable<T>): List<T> {
    val list = asToList()
    if (index == list.size - 1) {
        return list.plusKt(this)
    }
    val front = list.subList(0, index + 1)
    val back = list.subList(index + 1, list.size)
    return concatList(front, elements, back)
    //return front.plusKt(elements).plusKt(back)
}

@JvmOverloads
fun <T> Iterable<T>.minusAt(index: Int, count: Int = 1): List<T> {
    val list = asToList()
    if (index == 0) {
        return if (count < list.size) list.subList(count, list.size) else emptyList()
    }
    val front = list.subList(0, index)
    if (count >= list.size - index) {
        return front
    }
    return front.plusKt(list.subList(index + count, list.size))
}

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

fun <T> MutableIterable<T>.removeFirst(n: Int): Boolean {
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

fun <T> MutableIterable<T>.removeAll(predicate: Predicate<in T>): Boolean {
    return this.removeAllKt(predicate.asKotlinFun())
}

fun <T> MutableCollection<T>.removeAll(elements: Array<out T>): Boolean {
    return this.removeAllKt(elements)
}

fun <T> MutableCollection<T>.removeAll(elements: Iterable<T>): Boolean {
    return this.removeAllKt(elements)
}

fun <T> MutableIterable<T>.retainAll(predicate: Predicate<in T>): Boolean {
    return this.retainAllKt(predicate.asKotlinFun())
}

fun <T> MutableCollection<T>.retainAll(elements: Array<out T>): Boolean {
    return this.retainAllKt(elements)
}

fun <T> MutableCollection<T>.retainAll(elements: Iterable<T>): Boolean {
    return this.retainAllKt(elements)
}

fun <T> Collection<T>.plus(element: T): List<T> {
    return this.plusKt(element)
}

fun <T> Collection<T>.plus(elements: Array<out T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Collection<T>.plus(elements: Iterable<T>): List<T> {
    return this.plusKt(elements)
}

fun <T> MutableCollection<T>.addAll(elements: Array<out T>): Boolean {
    return this.addAllKt(elements)
}

fun <T> MutableCollection<T>.addAll(elements: Iterable<T>): Boolean {
    return this.addAllKt(elements)
}

//Join to String:

@JvmOverloads
fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    transform: Function<in T, CharSequence>? = null
): String {
    return this.joinToStringKt(separator = separator, transform = transform?.asKotlinFun())
}

fun <T> Iterable<T>.joinToString(
    separator: CharSequence,
    prefix: CharSequence,
    suffix: CharSequence,
    limit: Int,
    truncated: CharSequence,
    transform: Function<in T, CharSequence>?
): String {
    return this.joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
}

@JvmOverloads
fun <T, A : Appendable> Iterable<T>.joinTo(
    destination: A,
    separator: CharSequence = ", ",
    transform: Function<in T, CharSequence>? = null
): A {
    return this.joinToKt(buffer = destination, separator = separator, transform = transform?.asKotlinFun())
}

fun <T, A : Appendable> Iterable<T>.joinTo(
    destination: A,
    separator: CharSequence,
    prefix: CharSequence,
    suffix: CharSequence,
    limit: Int,
    truncated: CharSequence,
    transform: Function<in T, CharSequence>?
): A {
    return this.joinToKt(destination, separator, prefix, suffix, limit, truncated, transform?.asKotlinFun())
}

//Iterator

fun <T> Iterator<T>.asIterable(): Iterable<T> {
    return object : Iterable<T> {
        override fun iterator(): Iterator<T> = this@asIterable
    }
}

//Enumeration

fun <T> Enumeration<T>.asIterator(): Iterator<T> {
    return this.iterator()
}

fun <T> Enumeration<T>.asIterable(): Iterable<T> {
    return asIterator().asIterable()
}

fun <T> Iterator<T>.asEnumeration(): Enumeration<T> {
    return object : Enumeration<T> {
        override fun hasMoreElements(): Boolean = this@asEnumeration.hasNext()
        override fun nextElement(): T = this@asEnumeration.next()
    }
}

fun <T> Iterable<T>.asEnumeration(): Enumeration<T> {
    return this.iterator().asEnumeration()
}