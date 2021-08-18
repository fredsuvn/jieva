package xyz.srclab.common.collect

import java.util.*

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