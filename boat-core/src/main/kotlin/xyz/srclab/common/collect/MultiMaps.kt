package xyz.srclab.common.collect

import xyz.srclab.common.collect.ListMap.Companion.toListMap
import xyz.srclab.common.collect.MutableListMap.Companion.toMutableListMap
import xyz.srclab.common.collect.MutableSetMap.Companion.toMutableSetMap
import xyz.srclab.common.collect.SetMap.Companion.toSetMap
import xyz.srclab.common.lang.asAny
import java.util.*

/**
 * Represents a type of [Map] of which key associated to a [Set].
 */
interface SetMap<K, V> : Map<K, Set<V>> {

    fun toMutableSetMap(): MutableSetMap<K, V>

    companion object {

        @JvmName("newSetMap")
        @JvmStatic
        fun <K, V> Map<K, Set<V>>.toSetMap(): SetMap<K, V> {
            return SetMapImpl(this)
        }
    }

    private open class SetMapBase<K, V>(map: Map<K, Set<V>>) : Map<K, Set<V>> by map
    private class SetMapImpl<K, V>(private val map: Map<K, Set<V>>) : SetMapBase<K, V>(map), SetMap<K, V> {

        override fun toMutableSetMap(): MutableSetMap<K, V> {
            return map.mapTo(LinkedHashMap()) { k, v ->
                k to LinkedHashSet(v)
            }.toMutableSetMap()
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

    fun toSetMap(): SetMap<K, V>

    companion object {

        @JvmName("newMutableSetMap")
        @JvmOverloads
        @JvmStatic
        fun <K, V> MutableMap<K, out MutableSet<V>>.toMutableSetMap(
            setGenerator: () -> MutableSet<V> = { LinkedHashSet() }
        ): MutableSetMap<K, V> {
            return MutableSetMapImpl(this.asAny(), setGenerator)
        }

        @JvmOverloads
        @JvmStatic
        fun <K, V> newMutableSetMap(
            setGenerator: () -> MutableSet<V> = { LinkedHashSet() }
        ): MutableSetMap<K, V> {
            return MutableSetMapImpl(LinkedHashMap(), setGenerator)
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

        override fun toSetMap(): SetMap<K, V> {
            return map.toSetMap()
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
}

/**
 * Represents a type of [Map] of which key associated to a [List].
 */
interface ListMap<K, V> : Map<K, List<V>> {

    fun toMutableListMap(): MutableListMap<K, V>

    companion object {

        @JvmName("newListMap")
        @JvmStatic
        fun <K, V> Map<K, List<V>>.toListMap(): ListMap<K, V> {
            return ListMapImpl(this)
        }
    }

    private open class ListMapBase<K, V>(map: Map<K, List<V>>) : Map<K, List<V>> by map
    private class ListMapImpl<K, V>(private val map: Map<K, List<V>>) : ListMapBase<K, V>(map), ListMap<K, V> {

        override fun toMutableListMap(): MutableListMap<K, V> {
            return map.mapTo(LinkedHashMap()) { k, v ->
                k to LinkedList(v)
            }.toMutableListMap()
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

    fun toListMap(): ListMap<K, V>

    companion object {

        @JvmName("newMutableListMap")
        @JvmOverloads
        @JvmStatic
        fun <K, V> MutableMap<K, out MutableList<V>>.toMutableListMap(
            listGenerator: () -> MutableList<V> = { LinkedList() }
        ): MutableListMap<K, V> {
            return MutableListMapImpl(this.asAny(), listGenerator)
        }

        @JvmOverloads
        @JvmStatic
        fun <K, V> newMutableListMap(
            listGenerator: () -> MutableList<V> = { LinkedList() }
        ): MutableListMap<K, V> {
            return MutableListMapImpl(LinkedHashMap(), listGenerator)
        }
    }

    private open class MutableListMapBase<K, V>(map: MutableMap<K, MutableList<V>>) :
        MutableMap<K, MutableList<V>> by map

    private class MutableListMapImpl<K, V>(
        private val map: MutableMap<K, MutableList<V>>,
        private val listGenerator: () -> MutableList<V>,
    ) : MutableListMapBase<K, V>(map), MutableListMap<K, V> {

        override fun add(key: K, value: V): MutableList<V> {
            val result = this.getOrPut(key, listGenerator)
            result.add(value)
            return result
        }

        override fun addAll(key: K, values: Iterable<V>): MutableList<V> {
            val result = this.getOrPut(key, listGenerator)
            result.addAll(values)
            return result
        }

        override fun toListMap(): ListMap<K, V> {
            return map.toListMap()
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
}