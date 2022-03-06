@file:JvmName("BMap")

package xyz.srclab.common.collect

import xyz.srclab.common.base.*
import xyz.srclab.common.convert.Converter
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
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

fun <K, V> newMap(vararg keyValues: Any?): LinkedHashMap<K, V> {
    return LinkedHashMap<K, V>().collect(*keyValues)
}

fun <K, V> newMap(keyValues: Iterable<Any?>): LinkedHashMap<K, V> {
    return LinkedHashMap<K, V>().collect(keyValues)
}

fun <K, V, C : MutableMap<in K, in V>> C.collect(vararg keyValues: Any?): C {
    return collect(keyValues.asList())
}

fun <K, V, C : MutableMap<in K, in V>> C.collect(keyValues: Iterable<Any?>): C {
    val iterator = keyValues.iterator()
    while (iterator.hasNext()) {
        val key = iterator.next()
        if (iterator.hasNext()) {
            val value = iterator.next()
            this[key.asTyped()] = value.asTyped()
        } else {
            break
        }
    }
    return this
}

fun <K, V> newEntry(key: K, value: V): MutableMap.MutableEntry<K, V> {
    return object : MutableMap.MutableEntry<K, V> {
        private var v = value
        override val key: K = key
        override val value: V
            get() = v

        override fun setValue(newValue: V): V {
            val result = v
            v = newValue
            return result
        }
    }
}

@JvmOverloads
fun <K, T : Any> Map<K, *>.get(key: K, type: Class<out T>, converter: Converter = Converter.defaultConverter()): T {
    return converter.convert(this[key], type)
}

@JvmOverloads
fun <K, T : Any> Map<K, *>.getOrNull(
    key: K,
    type: Class<out T>,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrNull(this[key], type)
}

fun <K, T : Any> Map<K, *>.getOrDefault(key: K, defaultValue: T, converter: Converter): T {
    return converter.convertOrNull(this[key], defaultValue.javaClass) ?: defaultValue
}

fun <K> Map<K, *>.getBoolean(key: K): Boolean {
    return this[key]!!.toBoolean()
}

fun <K> Map<K, *>.getBooleanOrNull(key: K): Boolean? {
    return this[key]?.toBoolean()
}

fun <K> Map<K, *>.getByte(key: K): Byte {
    return this[key]!!.toByte()
}

fun <K> Map<K, *>.getByteOrNull(key: K): Byte? {
    return this[key]?.toByte()
}

fun <K> Map<K, *>.getShort(key: K): Short {
    return this[key]!!.toShort()
}

fun <K> Map<K, *>.getShortOrNull(key: K): Short? {
    return this[key]?.toShort()
}

fun <K> Map<K, *>.getChar(key: K): Char {
    return this[key]!!.toChar()
}

fun <K> Map<K, *>.getCharOrNull(key: K): Char? {
    return this[key]?.toChar()
}

fun <K> Map<K, *>.getInt(key: K): Int {
    return this[key]!!.toInt()
}

fun <K> Map<K, *>.getIntOrNull(key: K): Int? {
    return this[key]?.toInt()
}

fun <K> Map<K, *>.getLong(key: K): Long {
    return this[key]!!.toLong()
}

fun <K> Map<K, *>.getLongOrNull(key: K): Long? {
    return this[key]?.toLong()
}

fun <K> Map<K, *>.getFloat(key: K): Float {
    return this[key]!!.toFloat()
}

fun <K> Map<K, *>.getFloatOrNull(key: K): Float? {
    return this[key]?.toFloat()
}

fun <K> Map<K, *>.getDouble(key: K): Double {
    return this[key]!!.toDouble()
}

fun <K> Map<K, *>.getDoubleOrNull(key: K): Double? {
    return this[key]?.toDouble()
}

fun <K, V, C : MutableCollection<V>> MutableMap<K, C>.add(key: K, value: V, collection: Function<K, C>): Boolean {
    val coll = this.computeIfAbsent(key, collection)
    return coll.add(value)
}

fun <K, V, C : MutableCollection<V>> MutableMap<K, C>.addAll(
    key: K,
    values: Iterable<V>,
    collection: Function<K, C>
): Boolean {
    val coll = this.computeIfAbsent(key, collection)
    return coll.addAll(values)
}

fun <K, V, C : Collection<V>> Map<K, C>.getFirst(key: K): V {
    return this[key]?.iterator()?.next().asTyped()
}

fun <K, V> Map<K, V>.filter(predicate: Predicate<in Map.Entry<K, V>>): Map<K, V> {
    return this.filterKt(predicate.asKotlinFun())
}

fun <K, V, M : MutableMap<in K, in V>> Map<K, V>.filterTo(
    destination: M,
    predicate: Predicate<in Map.Entry<K, V>>
): M {
    return this.filterToKt(destination, predicate.asKotlinFun())
}

fun <K, V, RK, RV> Map<K, V>.mapEntries(
    keySelector: Function<in K, RK>,
    valueTransform: Function<in V, RV>
): Map<RK, RV> {
    return mapEntriesTo(LinkedHashMap(), keySelector, valueTransform)
}

fun <K, V, RK, RV> Map<K, V>.mapEntries(transform: BiFunction<in K, in V, Map.Entry<RK, RV>>): Map<RK, RV> {
    return mapEntriesTo(LinkedHashMap(), transform)
}

@JvmSynthetic
inline fun <K, V, RK, RV> Map<K, V>.mapEntries(
    crossinline keySelector: (K) -> RK,
    crossinline valueTransform: (V) -> RV
): Map<RK, RV> {
    return mapEntriesTo(LinkedHashMap(), keySelector, valueTransform)
}

@JvmSynthetic
inline fun <K, V, RK, RV> Map<K, V>.mapEntries(transform: (K, V) -> Pair<RK, RV>): Map<RK, RV> {
    return mapEntriesTo(LinkedHashMap(), transform)
}

fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> Map<K, V>.mapEntriesTo(
    destination: C,
    keySelector: Function<in K, RK>,
    valueTransform: Function<in V, RV>
): C {
    return mapEntriesTo(destination, keySelector.asKotlinFun(), valueTransform.asKotlinFun())
}

fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> Map<K, V>.mapEntriesTo(
    destination: C,
    transform: BiFunction<in K, in V, Map.Entry<RK, RV>>
): C {
    return mapEntriesTo(destination) { it0, it1 -> transform.apply(it0, it1).toPair() }
}

@JvmSynthetic
inline fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> Map<K, V>.mapEntriesTo(
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

@JvmSynthetic
inline fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> Map<K, V>.mapEntriesTo(
    destination: C,
    transform: (K, V) -> Pair<RK, RV>
): C {
    this.forEach { (k, v) ->
        val pair = transform(k, v)
        destination.put(pair.first, pair.second)
    }
    return destination
}

fun <K, V, R> Map<K, V>.map(transform: Function<in Map.Entry<K, V>, R>): List<R> {
    return this.mapKt(transform.asKotlinFun())
}

fun <K, V, R, C : MutableCollection<in R>> Map<K, V>.mapTo(
    destination: C,
    transform: Function<in Map.Entry<K, V>, R>
): C {
    return this.mapToKt(destination, transform.asKotlinFun())
}

fun <K, V, R> Map<K, V>.flatMap(transform: Function<in Map.Entry<K, V>, Iterable<R>>): List<R> {
    return this.flatMapKt(transform.asKotlinFun())
}

fun <K, V, R, C : MutableCollection<in R>> Map<K, V>.flatMapTo(
    destination: C,
    transform: Function<in Map.Entry<K, V>, Iterable<R>>
): C {
    return this.flatMapToKt(destination, transform.asKotlinFun())
}

@JvmOverloads
fun <K, V> Map<K, V>.sorted(comparator: Comparator<in Map.Entry<K, V>> = castComparableComparator()): Map<K, V> {
    return this.entries.sortedWithKt(comparator).associateToKt(LinkedHashMap()) { it.key to it.value }
}

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> {
    return HashMap(this)
}

fun <K, V> Map<K, V>.toLinkedHashMap(): LinkedHashMap<K, V> {
    return LinkedHashMap(this)
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

fun <K, V> MutableMap<K, V>.removeAll(keys: Array<out K>) {
    for (key in keys) {
        this.remove(key)
    }
}

fun <K, V> MutableMap<K, V>.removeAll(keys: Iterable<K>) {
    for (key in keys) {
        this.remove(key)
    }
}

fun <K, V> copyOnWriteMap(
    newMap: Function<in Map<out K, V>, MutableMap<K, V>>
): CopyOnWriteMap<K, V> {
    return copyOnWriteMap(emptyMap(), newMap)
}

@JvmOverloads
fun <K, V> copyOnWriteMap(
    initMap: Map<out K, V> = emptyMap(),
    newMap: Function<in Map<out K, V>, MutableMap<K, V>> = Function { HashMap(it) }
): CopyOnWriteMap<K, V> {
    return newCopyOnWriteMap(initMap, newMap.asKotlinFun())
}

@JvmSynthetic
fun <K, V> newCopyOnWriteMap(
    initMap: Map<out K, V> = emptyMap(),
    newMap: (Map<out K, V>) -> MutableMap<K, V> = { HashMap(it) }
): CopyOnWriteMap<K, V> {
    return CopyOnWriteMap(initMap, newMap)
}

fun <K, V> mutableSetMap(
    valueSet: Function<K, MutableSet<V>>
): MutableSetMap<K, V> {
    return mutableSetMap(LinkedHashMap(), valueSet)
}

@JvmOverloads
fun <K, V> mutableSetMap(
    map: MutableMap<K, MutableSet<V>> = LinkedHashMap(),
    valueSet: Function<K, MutableSet<V>> = Function { LinkedHashSet() }
): MutableSetMap<K, V> {
    return newMutableSetMap(map, valueSet.asKotlinFun())
}

@JvmSynthetic
fun <K, V> newMutableSetMap(
    map: MutableMap<K, MutableSet<V>> = LinkedHashMap(),
    valueSet: (K) -> MutableSet<V> = { LinkedHashSet() }
): MutableSetMap<K, V> {
    return MutableSetMap(map, valueSet)
}

fun <K, V> MutableMap<K, MutableSet<V>>.toMutableSetMap(): MutableSetMap<K, V> {
    return MutableSetMap(this) { LinkedHashSet() }
}

fun <K, V> Map<K, Set<V>>.toSetMap(): SetMap<K, V> {
    return SetMap(this)
}

fun <K, V> mutableListMap(
    valueList: Function<K, MutableList<V>>
): MutableListMap<K, V> {
    return mutableListMap(LinkedHashMap(), valueList)
}

@JvmOverloads
fun <K, V> mutableListMap(
    map: MutableMap<K, MutableList<V>> = LinkedHashMap(),
    valueList: Function<K, MutableList<V>> = Function { LinkedList() }
): MutableListMap<K, V> {
    return newMutableListMap(map, valueList.asKotlinFun())
}

@JvmSynthetic
fun <K, V> newMutableListMap(
    map: MutableMap<K, MutableList<V>> = LinkedHashMap(),
    valueList: (K) -> MutableList<V> = { LinkedList() }
): MutableListMap<K, V> {
    return MutableListMap(map, valueList)
}

fun <K, V> Map<K, List<V>>.toListMap(): ListMap<K, V> {
    return ListMap(this)
}

fun <K, V> MutableMap<K, MutableList<V>>.toMutableListMap(): MutableListMap<K, V> {
    return MutableListMap(this) { LinkedList() }
}