package xyz.srclab.common.collect

import java.util.*

/**
 * A type of Multi-Map of which values are [MutableSet].
 */
open class SetMap<K, V> @JvmOverloads constructor(
    private val map: MutableMap<K, MutableSet<V>> = LinkedHashMap(),
    private val valueSet: (K) -> MutableSet<V> = { LinkedHashSet() }
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
open class ListMap<K, V> @JvmOverloads constructor(
    private val map: MutableMap<K, MutableList<V>> = LinkedHashMap(),
    private val valueList: (K) -> MutableList<V> = { LinkedList() }
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