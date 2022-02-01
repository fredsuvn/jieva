package xyz.srclab.common.collect

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import xyz.srclab.common.base.asTyped
import java.io.Serializable

/**
 * A type of Multi-Map of which values are [MutableSet].
 */
open class MutableSetMap<K, V> constructor(
    private val map: MutableMap<K, MutableSet<V>>,
    private val valueSet: (K) -> MutableSet<V>
) : Serializable, MutableMap<K, MutableSet<V>> by map {

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

    fun getFirst(key: K): V {
        return get(key)?.iterator()?.next().asTyped()
    }

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

/**
 * A type of Multi-Map of which values are [Set].
 */
open class SetMap<K, V> constructor(
    private val map: Map<K, Set<V>>
) : Serializable, Map<K, Set<V>> by map {

    fun getFirst(key: K): V {
        return get(key)?.iterator()?.next().asTyped()
    }

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

/**
 * A type of Multi-Map of which values are [MutableList].
 */
open class MutableListMap<K, V> constructor(
    private val map: MutableMap<K, MutableList<V>>,
    private val valueList: (K) -> MutableList<V>
) : Serializable, MutableMap<K, MutableList<V>> by map {

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

    fun getFirst(key: K): V {
        return get(key)?.get(0).asTyped()
    }

    fun getLast(key: K): V {
        return get(key)?.last().asTyped()
    }

    fun get(key: K, index: Int): V {
        return get(key)?.get(index).asTyped()
    }

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

/**
 * A type of Multi-Map of which values are [List].
 */
open class ListMap<K, V> constructor(
    private val map: Map<K, List<V>>,
) : Serializable, Map<K, List<V>> by map {

    fun getFirst(key: K): V {
        return get(key)?.get(0).asTyped()
    }

    fun getLast(key: K): V {
        return get(key)?.last().asTyped()
    }

    fun get(key: K, index: Int): V {
        return get(key)?.get(index).asTyped()
    }

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}