package xyz.srclab.common.collect

import xyz.srclab.common.base.defaultSerialVersion
import java.io.Serializable
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function

/**
 * Copy-On-Write [Map].
 */
open class CopyOnWriteMap<K, V>(
    initMap: Map<out K, V>,
    private val newMap: Function<Map<out K, V>, MutableMap<K, V>>
) : Serializable, MutableMap<K, V> {

    private var currentMap: MutableMap<K, V> = newMap(initMap)

    override val size: Int
        get() = currentMap.size

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = currentMap.entries

    override val keys: MutableSet<K>
        get() = currentMap.keys

    override val values: MutableCollection<V>
        get() = currentMap.values

    override fun containsKey(key: K): Boolean {
        return currentMap.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        return currentMap.containsValue(value)
    }

    override fun clear() {
        return cow { it.clear() }
    }

    override fun compute(key: K, remappingFunction: BiFunction<in K, in V?, out V?>): V? {
        return cow { it.compute(key, remappingFunction) }
    }

    override fun computeIfAbsent(key: K, mappingFunction: Function<in K, out V>): V {
        return cow { it.computeIfAbsent(key, mappingFunction) }
    }

    override fun computeIfPresent(key: K, remappingFunction: BiFunction<in K, in V, out V?>): V? {
        return cow { it.computeIfPresent(key, remappingFunction) }
    }

    override fun forEach(action: BiConsumer<in K, in V>) {
        currentMap.forEach(action)
    }

    override fun get(key: K): V? {
        return currentMap[key]
    }

    override fun getOrDefault(key: K, defaultValue: V): V {
        return currentMap.getOrDefault(key, defaultValue)
    }

    override fun isEmpty(): Boolean {
        return currentMap.isEmpty()
    }

    override fun merge(key: K, value: V, remappingFunction: BiFunction<in V, in V, out V?>): V? {
        return cow { it.merge(key, value!!, remappingFunction) }
    }

    override fun put(key: K, value: V): V? {
        return cow { it.put(key, value) }
    }

    override fun putAll(from: Map<out K, V>) {
        return cow { it.putAll(from) }
    }

    override fun putIfAbsent(key: K, value: V): V? {
        return cow { it.putIfAbsent(key, value) }
    }

    override fun remove(key: K): V? {
        return cow { it.remove(key) }
    }

    override fun remove(key: K, value: V): Boolean {
        return cow { it.remove(key, value) }
    }

    override fun replace(key: K, oldValue: V, newValue: V): Boolean {
        return cow { it.replace(key, oldValue, newValue) }
    }

    override fun replace(key: K, value: V): V? {
        return cow { it.replace(key, value) }
    }

    override fun replaceAll(function: BiFunction<in K, in V, out V>) {
        return cow { it.replaceAll(function) }
    }

    private inline fun <T> cow(action: (MutableMap<K, V>) -> T): T {
        return synchronized(this) {
            val curMap = currentMap
            val newMap = newMap.apply(curMap)
            val result = action(newMap)
            currentMap = newMap
            result
        }
    }

    override fun equals(other: Any?): Boolean {
        return currentMap == other
    }

    override fun hashCode(): Int {
        return currentMap.hashCode()
    }

    override fun toString(): String {
        return currentMap.toString()
    }

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}