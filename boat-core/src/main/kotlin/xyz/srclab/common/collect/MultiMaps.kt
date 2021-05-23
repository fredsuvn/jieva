package xyz.srclab.common.collect

import xyz.srclab.common.collect.ListMap.Companion.toListMap
import xyz.srclab.common.collect.MutableListMap.Companion.toMutableListMap
import xyz.srclab.common.collect.MutableSetMap.Companion.toMutableSetMap
import xyz.srclab.common.collect.SetMap.Companion.toSetMap
import xyz.srclab.common.lang.asAny
import java.util.*

/**
 * Represents a type of [Map] of which key associated to a [C].
 */
interface MultiMap<K, V, C : Collection<V>> : Map<K, C> {

    /**
     * Used to help implement [getFirstOrDefault] and [getFirstOrElse], should be return a singleton.
     */
    val defaultValueCollection: C

    @JvmDefault
    fun getFirst(key: K): V? {
        return get(key)?.first()
    }

    @JvmDefault
    fun getFirstOrDefault(key: K, defaultValue: V): V {
        val defaultSet: Set<V> = getOrDefault(key, defaultValueCollection).asAny()
        if (defaultSet === defaultValueCollection) {
            return defaultValue
        }
        return try {
            defaultSet.first()
        } catch (e: NoSuchElementException) {
            defaultValue
        }
    }

    @JvmDefault
    fun getFirstOrElse(key: K, defaultValue: () -> V): V {
        val defaultSet: Set<V> = getOrElse(key) { defaultValueCollection }.asAny()
        if (defaultSet === defaultValueCollection) {
            return defaultValue()
        }
        return try {
            defaultSet.first()
        } catch (e: NoSuchElementException) {
            defaultValue()
        }
    }
}

/**
 * Represents a type of [MutableMap] of which key associated to a [C].
 */
interface MutableMultiMap<K, V, C : MutableCollection<V>> : MultiMap<K, V, C>

/**
 * Represents a type of [Map] of which key associated to a [Set].
 */
interface SetMap<K, V> : MultiMap<K, V, Set<V>> {

    fun toMutableSetMap(): MutableSetMap<K, V>

    companion object {

        @JvmName("newSetMap")
        @JvmStatic
        fun <K, V> Map<K, Set<V>>.toSetMap(): SetMap<K, V> {
            return SetMapImpl(this)
        }

        private class SetMapImpl<K, V>(private val map: Map<K, Set<V>>) : MultiMapBase<K, V, Set<V>>(map),
            SetMap<K, V> {

            override val defaultValueCollection: Set<V> = DEFAULT_SET.asAny()

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
}

/**
 * Represents a type of [MutableMap] of which key associated to [MutableSet].
 */
interface MutableSetMap<K, V> : MutableMultiMap<K, V, MutableSet<V>> {

    /**
     * Add [value] for given [key], return result after adding.
     */
    fun add(key: K, value: V): MutableSet<V>

    /**
     * Add [values] for given [key], return result after adding.
     */
    @JvmDefault
    fun addAll(key: K, vararg values: V): MutableSet<V> {
        return addAll(key, values.toList())
    }

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

    private class MutableSetMapImpl<K, V>(
        private val map: MutableMap<K, MutableSet<V>>,
        private val setGenerator: () -> MutableSet<V>,
    ) : MutableMultiMapBase<K, V, MutableSet<V>>(map), MutableSetMap<K, V> {

        override val defaultValueCollection: MutableSet<V> = DEFAULT_SET.asAny()

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
interface ListMap<K, V> : MultiMap<K, V, List<V>> {

    fun toMutableListMap(): MutableListMap<K, V>

    companion object {

        @JvmName("newListMap")
        @JvmStatic
        fun <K, V> Map<K, List<V>>.toListMap(): ListMap<K, V> {
            return ListMapImpl(this)
        }
    }

    private class ListMapImpl<K, V>(
        private val map: Map<K, List<V>>
    ) : MultiMapBase<K, V, List<V>>(map), ListMap<K, V> {

        override val defaultValueCollection: List<V> = DEFAULT_LIST.asAny()

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
interface MutableListMap<K, V> : MutableMultiMap<K, V, MutableList<V>> {

    /**
     * Add [value] for given [key], return result after adding.
     */
    fun add(key: K, value: V): MutableList<V>

    /**
     * Add [values] for given [key], return result after adding.
     */
    @JvmDefault
    fun addAll(key: K, vararg values: V): MutableList<V> {
        return addAll(key, values.toList())
    }

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

    private class MutableListMapImpl<K, V>(
        private val map: MutableMap<K, MutableList<V>>,
        private val listGenerator: () -> MutableList<V>,
    ) : MutableMultiMapBase<K, V, MutableList<V>>(map), MutableListMap<K, V> {

        override val defaultValueCollection: MutableList<V> = DEFAULT_LIST.asAny()

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

private val DEFAULT_SET = Collections.unmodifiableSet(HashSet<Any?>())
private val DEFAULT_LIST = Collections.unmodifiableList(ArrayList<Any?>())

private abstract class MultiMapBase<K, V, C : Collection<V>>(map: Map<K, C>) : Map<K, C> by map
private abstract class MutableMultiMapBase<K, V, C : MutableCollection<V>>(map: MutableMap<K, C>) :
    MutableMap<K, C> by map