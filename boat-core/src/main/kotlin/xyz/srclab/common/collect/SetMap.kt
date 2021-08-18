package xyz.srclab.common.collect

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