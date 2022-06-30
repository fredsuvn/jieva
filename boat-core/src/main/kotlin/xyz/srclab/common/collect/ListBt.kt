/**
 * List utilities.
 */
@file:JvmName("ListBt")

package xyz.srclab.common.collect

import xyz.srclab.common.base.*
import xyz.srclab.common.convert.Converter
import java.util.function.BiFunction
import java.util.function.IntFunction
import java.util.function.Predicate
import kotlin.random.Random
import kotlin.collections.binarySearch as binarySearchKt
import kotlin.collections.dropLast as dropLastKt
import kotlin.collections.dropLastWhile as dropLastWhileKt
import kotlin.collections.elementAtOrElse as elementAtOrElseKt
import kotlin.collections.findLast as findLastKt
import kotlin.collections.first as firstKt
import kotlin.collections.firstOrNull as firstOrNullKt
import kotlin.collections.foldRight as foldRightKt
import kotlin.collections.foldRightIndexed as foldRightIndexedKt
import kotlin.collections.indexOfFirst as indexOfFirstKt
import kotlin.collections.indexOfLast as indexOfLastKt
import kotlin.collections.last as lastKt
import kotlin.collections.lastOrNull as lastOrNullKt
import kotlin.collections.reduceRight as reduceRightKt
import kotlin.collections.reduceRightIndexed as reduceRightIndexedKt
import kotlin.collections.reduceRightIndexedOrNull as reduceRightIndexedOrNullKt
import kotlin.collections.reduceRightOrNull as reduceRightOrNullKt
import kotlin.collections.removeAll as removeAllKt
import kotlin.collections.removeFirst as removeFirstKt
import kotlin.collections.removeFirstOrNull as removeFirstOrNullKt
import kotlin.collections.removeLast as removeLastKt
import kotlin.collections.removeLastOrNull as removeLastOrNullKt
import kotlin.collections.retainAll as retainAllKt
import kotlin.collections.reverse as reverseKt
import kotlin.collections.shuffle as shuffleKt
import kotlin.collections.slice as sliceKt
import kotlin.collections.takeLast as takeLastKt
import kotlin.collections.takeLastWhile as takeLastWhileKt

/**
 * Returns new [ArrayList] with [elements].
 */
fun <T> newList(vararg elements: T): ArrayList<T> {
    return collect(ArrayList(), *elements)
}

fun <T> List<T>.first(): T {
    return this.firstKt()
}

fun <T> List<T>.firstOrNull(): T? {
    return this.firstOrNullKt()
}

fun <T> List<T>.last(): T {
    return this.lastKt()
}

fun <T> List<T>.last(predicate: Predicate<in T>): T {
    return this.lastKt(predicate.asKotlinFun())
}

fun <T> List<T>.lastOrNull(): T? {
    return this.lastOrNullKt()
}

fun <T> List<T>.lastOrNull(predicate: Predicate<in T>): T? {
    return this.lastOrNullKt(predicate.asKotlinFun())
}

/**
 * Returns element at [index], or null if index out of bounds.
 */
fun <T> List<T>.getOrNull(index: Int): T? {
    return this.elementAtOrNull(index)
}

/**
 * Returns element at [index], or [defaultValue] if index out of bounds.
 */
fun <T> List<T>.getOrDefault(index: Int, defaultValue: T): T {
    return this.elementAtOrElseKt(index) { defaultValue }
}

/**
 * Returns element at [index], or [elseValue] if index out of bounds.
 */
fun <T> List<T>.getOrElse(index: Int, elseValue: IntFunction<out T>): T {
    return this.elementAtOrElseKt(index, elseValue.asKotlinFun())
}

/**
 * Returns conversion of result of `get` operation.
 */
@JvmOverloads
fun <T : Any> List<*>.get(
    index: Int,
    type: Class<out T>,
    converter: Converter = Converter.defaultConverter()
): T {
    return converter.convert(this[index], type)
}

/**
 * Returns conversion of result of `getOrNull` operation.
 */
@JvmOverloads
fun <T : Any> List<*>.getOrNull(
    index: Int,
    type: Class<out T>,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrNull(this.getOrNull(index), type)
}

/**
 * Returns conversion of result of `getOrNull` operation, or [defaultValue] if result of conversion is null.
 */
@JvmOverloads
fun <T : Any> List<*>.getOrConvert(
    index: Int,
    defaultValue: T,
    converter: Converter = Converter.defaultConverter()
): T {
    return converter.convertOrNull(this.getOrNull(index), defaultValue.javaClass) ?: defaultValue
}

fun List<*>.getBoolean(index: Int): Boolean {
    return get(index).toBoolean()
}

fun List<*>.getBooleanOrNull(index: Int): Boolean? {
    return getOrNull(index)?.toBoolean()
}

fun List<*>.getByte(index: Int): Byte {
    return get(index).toByte()
}

fun List<*>.getByteOrNull(index: Int): Byte? {
    return getOrNull(index)?.toByte()
}

fun List<*>.getShort(index: Int): Short {
    return get(index).toShort()
}

fun List<*>.getShortOrNull(index: Int): Short? {
    return getOrNull(index)?.toShort()
}

fun List<*>.getChar(index: Int): Char {
    return get(index).toChar()
}

fun List<*>.getCharOrNull(index: Int): Char? {
    return getOrNull(index)?.toChar()
}

fun List<*>.getInt(index: Int): Int {
    return get(index).toInt()
}

fun List<*>.getIntOrNull(index: Int): Int? {
    return getOrNull(index)?.toInt()
}

fun List<*>.getLong(index: Int): Long {
    return get(index).toLong()
}

fun List<*>.getLongOrNull(index: Int): Long? {
    return getOrNull(index)?.toLong()
}

fun List<*>.getFloat(index: Int): Float {
    return get(index).toFloat()
}

fun List<*>.getFloatOrNull(index: Int): Float? {
    return getOrNull(index)?.toFloat()
}

fun List<*>.getDouble(index: Int): Double {
    return get(index).toDouble()
}

fun List<*>.getDoubleOrNull(index: Int): Double? {
    return getOrNull(index)?.toDouble()
}

fun <T> List<T>.findLast(predicate: Predicate<in T>): T? {
    return this.findLastKt(predicate.asKotlinFun())
}

fun <T> List<T>.indexOf(predicate: Predicate<in T>): Int {
    return this.indexOfFirstKt(predicate.asKotlinFun())
}

fun <T> List<T>.lastIndexOf(predicate: Predicate<in T>): Int {
    return this.indexOfLastKt(predicate.asKotlinFun())
}

fun <T> List<T>.takeLast(n: Int): List<T> {
    return this.takeLastKt(n)
}

fun <T> List<T>.takeLast(predicate: Predicate<in T>): List<T> {
    return this.takeLastWhileKt(predicate.asKotlinFun())
}

fun <T> List<T>.dropLast(n: Int): List<T> {
    return this.dropLastKt(n)
}

fun <T> List<T>.dropLast(predicate: Predicate<in T>): List<T> {
    return this.dropLastWhileKt(predicate.asKotlinFun())
}

fun <S, T : S> List<T>.reduceRight(operation: BiFunction<in T, in S, out S>): S {
    return this.reduceRightKt(operation.asKotlinFun())
}

fun <S, T : S> List<T>.reduceRightIndexed(operation: IndexedBiFunction<in T, in S, out S>): S {
    return this.reduceRightIndexedKt(operation.asKotlinFun())
}

fun <S, T : S> List<T>.reduceRightOrNull(operation: BiFunction<in T, in S, out S>): S? {
    return this.reduceRightOrNullKt(operation.asKotlinFun())
}

fun <S, T : S> List<T>.reduceRightIndexedOrNull(operation: IndexedBiFunction<in T, in S, out S>): S? {
    return this.reduceRightIndexedOrNullKt(operation.asKotlinFun())
}

fun <T, R> List<T>.reduceRight(initial: R, operation: BiFunction<in T, in R, out R>): R {
    return this.foldRightKt(initial, operation.asKotlinFun())
}

fun <T, R> List<T>.reduceRightIndexed(initial: R, operation: IndexedBiFunction<in T, in R, out R>): R {
    return this.foldRightIndexedKt(initial, operation.asKotlinFun())
}

fun <T> List<T>.slice(indices: IntArray): List<T> {
    return this.sliceKt(indices.asIterable())
}

fun <T> List<T>.slice(indices: Iterable<Int>): List<T> {
    return this.sliceKt(indices)
}

fun <T> List<T>.slice(indices: IntRange): List<T> {
    return this.sliceKt(indices)
}

fun <T> List<T>.binarySearch(element: T, comparator: Comparator<in T>): Int {
    return this.binarySearchKt(element, comparator)
}

fun <T> MutableList<T>.reverse() {
    this.reverseKt()
}

fun <T> MutableList<T>.shuffle() {
    this.shuffleKt()
}

fun <T> MutableList<T>.shuffle(random: Random) {
    this.shuffleKt(random)
}

fun <T> MutableList<T>.removeAll(predicate: Predicate<in T>): Boolean {
    return this.removeAllKt(predicate.asKotlinFun())
}

fun <T> MutableList<T>.removeFirst(): T {
    return this.removeFirstKt()
}

fun <T> MutableList<T>.removeFirstOrNull(): T? {
    return this.removeFirstOrNullKt()
}

fun <T> MutableList<T>.removeLast(): T {
    return this.removeLastKt()
}

fun <T> MutableList<T>.removeLastOrNull(): T? {
    return this.removeLastOrNullKt()
}

fun <T> MutableList<T>.retainAll(predicate: Predicate<in T>): Boolean {
    return this.retainAllKt(predicate.asKotlinFun())
}

@JvmName("concat")
fun <T> concatList(vararg lists: Iterable<T>): List<T> {
    var size = 0
    for (list in lists) {
        if (list is Collection) {
            size += list.size
        } else {
            size = -1
            break
        }
    }
    val result = if (size <= 0) java.util.ArrayList() else java.util.ArrayList<T>(size)
    for (list in lists) {
        result.addAll(list)
    }
    return result
}