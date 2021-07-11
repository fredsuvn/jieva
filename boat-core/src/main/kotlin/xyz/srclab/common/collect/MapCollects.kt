@file:JvmName("Collects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import xyz.srclab.common.lang.asComparableComparator
import java.util.*
import kotlin.collections.associateTo as associateToKt
import kotlin.collections.filter as filterKt
import kotlin.collections.filterTo as filterToKt
import kotlin.collections.flatMap as flatMapKt
import kotlin.collections.flatMapTo as flatMapToKt
import kotlin.collections.map as mapKt
import kotlin.collections.mapTo as mapToKt
import kotlin.collections.minus as minusKt
import kotlin.collections.plus as plusKt
import kotlin.collections.sortedWith as sortedWithKt
import kotlin.collections.toSet as toSetKt

fun <K, V> Map<K, V>.containsKeys(keys: Array<out K>): Boolean {
    return this.keys.containsAll(keys.toSetKt())
}

fun <K, V> Map<K, V>.containsKeys(keys: Iterable<K>): Boolean {
    return this.keys.containsAll(keys.toSetKt())
}

fun <K, V> Map<K, V>.containsKeys(keys: Collection<K>): Boolean {
    return this.keys.containsAll(keys)
}

fun <K, V> Map<K, V>.containsValues(values: Array<out V>): Boolean {
    return this.values.toSetKt().containsAll(values.toSetKt())
}

fun <K, V> Map<K, V>.containsValues(values: Iterable<V>): Boolean {
    return this.values.toSetKt().containsAll(values.toSetKt())
}

fun <K, V> Map<K, V>.containsValues(values: Collection<V>): Boolean {
    return this.values.toSetKt().containsAll(values)
}

inline fun <K, V> Map<K, V>.filter(predicate: (Map.Entry<K, V>) -> Boolean): Map<K, V> {
    return this.filterKt(predicate)
}

inline fun <K, V, M : MutableMap<in K, in V>> Map<K, V>.filterTo(
    destination: M,
    predicate: (Map.Entry<K, V>) -> Boolean
): M {
    return this.filterToKt(destination, predicate)
}

inline fun <K, V, R> Map<K, V>.map(transform: (Map.Entry<K, V>) -> R): List<R> {
    return this.mapKt(transform)
}

inline fun <K, V, RK, RV> Map<K, V>.map(
    crossinline keySelector: (K) -> RK,
    crossinline valueTransform: (V) -> RV
): Map<RK, RV> {
    return mapTo(LinkedHashMap(), keySelector, valueTransform)
}

inline fun <K, V, RK, RV> Map<K, V>.map(transform: (K, V) -> Pair<RK, RV>): Map<RK, RV> {
    return mapTo(LinkedHashMap(), transform)
}

inline fun <K, V, R, C : MutableCollection<in R>> Map<K, V>.mapTo(
    destination: C,
    transform: (Map.Entry<K, V>) -> R
): C {
    return this.mapToKt(destination, transform)
}

inline fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> Map<K, V>.mapTo(
    destination: C,
    crossinline keySelector: (K) -> RK,
    crossinline valueTransform: (V) -> RV
): C {
    this.forEach { (k, v) ->
        val rk = keySelector(k)
        val rv = valueTransform(v)
        destination.put(rk, rv)
    }
    return destination
}

inline fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> Map<K, V>.mapTo(
    destination: C,
    transform: (K, V) -> Pair<RK, RV>
): C {
    this.forEach { (k, v) ->
        val pair = transform(k, v)
        destination.put(pair.first, pair.second)
    }
    return destination
}

inline fun <K, V, R> Map<K, V>.flatMap(transform: (Map.Entry<K, V>) -> Iterable<R>): List<R> {
    return this.flatMapKt(transform)
}

inline fun <K, V, R, C : MutableCollection<in R>> Map<K, V>.flatMapTo(
    destination: C,
    transform: (Map.Entry<K, V>) -> Iterable<R>
): C {
    return this.flatMapToKt(destination, transform)
}

@JvmOverloads
fun <K, V> Map<K, V>.sorted(comparator: Comparator<in Map.Entry<K, V>> = asComparableComparator()): Map<K, V> {
    return this.entries.sortedWithKt(comparator).associateToKt(LinkedHashMap()) { it.key to it.value }
}

fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> {
    return if (this is ImmutableMap) this else ImmutableMap(this)
}

fun <K, V> Map<K, V>.toTreeMap(comparator: Comparator<in K>): TreeMap<K, V> {
    val result: TreeMap<K, V> = TreeMap(comparator)
    result.putAll(this)
    return result
}

fun <K, V> Map<K, V>.plus(other: Map<out K, V>): Map<K, V> {
    return this.plusKt(other)
}

fun <K, V> Map<K, V>.minus(key: K): Map<K, V> {
    return this.minusKt(key)
}

fun <K, V> Map<K, V>.minus(keys: Array<out K>): Map<K, V> {
    return this.minusKt(keys)
}

fun <K, V> Map<K, V>.minus(keys: Iterable<K>): Map<K, V> {
    return this.minusKt(keys)
}

fun <K, V> MutableMap<K, V>.removeAll(keys: Iterable<K>) {
    for (key in keys) {
        this.remove(key)
    }
}