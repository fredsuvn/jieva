@file:JvmName("MultiMaps")

package xyz.srclab.common.collect

import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * Represents a type of [Map] of which key associated to a [Set].
 */
interface SetMap<K, V> : Map<K, Set<V>> {
}

/**
 * Represents a type of [MutableMap] of which key associated to [MutableSet].
 */
interface MutableSetMap<K, V> : MutableMap<K, MutableSet<V>> {

    /**
     * Add [value] for given [key], return result after adding.
     */
    fun add(key: K, value: V): MutableSet<V>

    /**
     * Add [values] for given [key], return result after adding.
     */
    fun addAll(key: K, values: Iterable<V>): MutableSet<V>
}

/**
 * Represents a type of [Map] of which key associated to a [List].
 */
interface ListMap<K, V> : Map<K, List<V>> {
}

/**
 * Represents a type of [MutableMap] of which key associated to [MutableList].
 */
interface MutableListMap<K, V> : MutableMap<K, MutableList<V>> {

    /**
     * Add [value] for given [key], return result after adding.
     */
    fun add(key: K, value: V): MutableList<V>

    /**
     * Add [values] for given [key], return result after adding.
     */
    fun addAll(key: K, values: Iterable<V>): MutableList<V>
}

@JvmName("setMap")
fun <K, V> Map<K, Set<V>>.toSetMap(): SetMap<K, V> {
    return SetMapImpl(this)
}

@JvmName("mutableSetMap")
@JvmOverloads
fun <K, V> MutableMap<K, MutableSet<V>>.toMutableSetMap(
    setGenerator: () -> MutableSet<V> = { LinkedHashSet() }
): MutableSetMap<K, V> {
    return MutableSetMapImpl(this, setGenerator)
}

private open class SetMapBase<K, V>(map: Map<K, Set<V>>) : Map<K, Set<V>> by map
private class SetMapImpl<K, V>(private val map: Map<K, Set<V>>) : SetMapBase<K, V>(map), SetMap<K, V> {

    override fun equals(other: Any?): Boolean {
        return map == other
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun toString(): String {
        return map.toString()
    }
}

private open class MutableSetMapBase<K, V>(map: MutableMap<K, MutableSet<V>>) : MutableMap<K, MutableSet<V>> by map
private class MutableSetMapImpl<K, V>(
    private val map: MutableMap<K, MutableSet<V>>,
    private val setGenerator: () -> MutableSet<V>,
) : MutableSetMapBase<K, V>(map), MutableSetMap<K, V> {

    override fun add(key: K, value: V): MutableSet<V> {
        val result = this.getOrPut(key, setGenerator)
        result.add(value)
        return result
    }

    override fun addAll(key: K, values: Iterable<V>): MutableSet<V> {
        val result = this.getOrPut(key, setGenerator)
        result.addAll(values)
        return result
    }

    override fun equals(other: Any?): Boolean {
        return map == other
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun toString(): String {
        return map.toString()
    }
}

@JvmName("listMap")
fun <K, V> Map<K, List<V>>.toListMap(): ListMap<K, V> {
    return ListMapImpl(this)
}

@JvmName("mutableListMap")
@JvmOverloads
fun <K, V> MutableMap<K, MutableList<V>>.toMutableListMap(
    setGenerator: () -> MutableList<V> = { LinkedList() }
): MutableListMap<K, V> {
    return MutableListMapImpl(this, setGenerator)
}

private open class ListMapBase<K, V>(map: Map<K, List<V>>) : Map<K, List<V>> by map
private class ListMapImpl<K, V>(private val map: Map<K, List<V>>) : ListMapBase<K, V>(map), ListMap<K, V> {

    override fun equals(other: Any?): Boolean {
        return map == other
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun toString(): String {
        return map.toString()
    }
}

private open class MutableListMapBase<K, V>(map: MutableMap<K, MutableList<V>>) : MutableMap<K, MutableList<V>> by map
private class MutableListMapImpl<K, V>(
    private val map: MutableMap<K, MutableList<V>>,
    private val setGenerator: () -> MutableList<V>,
) : MutableListMapBase<K, V>(map), MutableListMap<K, V> {

    override fun add(key: K, value: V): MutableList<V> {
        val result = this.getOrPut(key, setGenerator)
        result.add(value)
        return result
    }

    override fun addAll(key: K, values: Iterable<V>): MutableList<V> {
        val result = this.getOrPut(key, setGenerator)
        result.addAll(values)
        return result
    }

    override fun equals(other: Any?): Boolean {
        return map == other
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun toString(): String {
        return map.toString()
    }
}