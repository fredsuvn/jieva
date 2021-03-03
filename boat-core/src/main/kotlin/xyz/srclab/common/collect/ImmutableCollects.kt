@file:JvmName("ImmutableCollects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import java.util.*
import kotlin.collections.LinkedHashMap

class ImmutableCollection<T>(private val iterable: Iterable<T>) : Collection<T>
by Collections.unmodifiableCollection(iterable.toList())

class ImmutableSet<T>(val iterable: Iterable<T>) : Set<T>
by Collections.unmodifiableSet(iterable.toSet())

class ImmutableList<T>(val iterable: Iterable<T>) : List<T>
by Collections.unmodifiableList(iterable.toList())

class ImmutableMap<K, V>(val map: Map<K, V>) : Map<K, V>
by Collections.unmodifiableMap(LinkedHashMap(map))