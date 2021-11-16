@file:JvmName("BCollects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import java.util.*

@JvmOverloads
fun <K, V> setMap(
    map: MutableMap<K, MutableSet<V>> = LinkedHashMap(),
    valueSet: (K) -> MutableSet<V> = { LinkedHashSet() }
): BSetMap<K, V> {
    return BSetMap(map, valueSet)
}

@JvmOverloads
fun <K, V> listMap(
    map: MutableMap<K, MutableList<V>> = LinkedHashMap(),
    valueList: (K) -> MutableList<V> = { LinkedList() }
): BListMap<K, V> {
    return BListMap(map, valueList)
}

/**
 * A type of Multi-Map of which values are [MutableSet].
 */
open class BSetMap<K, V> constructor(
    private val map: MutableMap<K, MutableSet<V>>,
    private val valueSet: (K) -> MutableSet<V>
) : MutableMap<K, MutableSet<V>> by map {

    fun add(key: K, value: V): Boolean {
        val set = map.computeIfAbsent(key, valueSet)
        return set.add(value)
    }

    fun addAll(key: K, vararg values: V): Boolean {
        val set = map.computeIfAbsent(key, valueSet)
        return set.addAll(values)
    }

    fun addAll(key: K, values: Iterable<V>): Boolean {
        val set = map.computeIfAbsent(key, valueSet)
        return set.addAll(values)
    }
}

/**
 * A type of Multi-Map of which values are [MutableList].
 */
open class BListMap<K, V> constructor(
    private val map: MutableMap<K, MutableList<V>>,
    private val valueList: (K) -> MutableList<V>
) : MutableMap<K, MutableList<V>> by map {

    fun add(key: K, value: V): Boolean {
        val list = map.computeIfAbsent(key, valueList)
        return list.add(value)
    }

    fun addAll(key: K, vararg values: V): Boolean {
        val list = map.computeIfAbsent(key, valueList)
        return list.addAll(values)
    }

    fun addAll(key: K, values: Iterable<V>): Boolean {
        val list = map.computeIfAbsent(key, valueList)
        return list.addAll(values)
    }
}