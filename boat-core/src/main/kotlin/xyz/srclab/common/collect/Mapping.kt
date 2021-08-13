package xyz.srclab.common.collect

import xyz.srclab.common.lang.asAny
import xyz.srclab.common.lang.comparableComparator
import java.util.*

/**
 * Map interface support chain operation.
 */
open class Mapping<K, V>(
    protected open var map: Map<K, V>
) {

    protected open fun <RK, RV> Map<RK, RV>.asSelf(): Mapping<RK, RV> {
        map = this.asAny()
        return this@Mapping.asAny()
    }

    open fun finalMap(): Map<K, V> {
        return map
    }

    open fun finalMutableMap(): MutableMap<K, V> {
        val map = map
        if (map is MutableMap<K, V>) {
            return map
        }
        return map.toMutableMap()
    }

    open fun containsKeys(keys: Array<out K>): Boolean {
        return finalMap().containsKeys(keys)
    }

    open fun containsKeys(keys: Iterable<K>): Boolean {
        return finalMap().containsKeys(keys)
    }

    open fun containsKeys(keys: Collection<K>): Boolean {
        return finalMap().containsKeys(keys)
    }

    open fun containsValues(values: Array<out V>): Boolean {
        return finalMap().containsValues(values)
    }

    open fun containsValues(values: Iterable<V>): Boolean {
        return finalMap().containsValues(values)
    }

    open fun containsValues(values: Collection<V>): Boolean {
        return finalMap().containsValues(values)
    }

    open fun filter(predicate: (Map.Entry<K, V>) -> Boolean): Mapping<K, V> {
        return finalMap().filter(predicate).asSelf()
    }

    open fun <M : MutableMap<in K, in V>> filterTo(destination: M, predicate: (Map.Entry<K, V>) -> Boolean): M {
        return finalMap().filterTo(destination, predicate)
    }

    open fun <R> map(transform: (Map.Entry<K, V>) -> R): Collecting<R> {
        return finalMap().map(transform).collect()
    }

    open fun <RK, RV> map(keySelector: (K) -> RK, valueTransform: (V) -> RV): Mapping<RK, RV> {
        return finalMap().mapTo(LinkedHashMap(), keySelector, valueTransform).asSelf()
    }

    open fun <RK, RV> map(transform: (K, V) -> Pair<RK, RV>): Mapping<RK, RV> {
        return finalMap().mapTo(LinkedHashMap(), transform).asSelf()
    }

    open fun <R, C : MutableCollection<in R>> mapTo(destination: C, transform: (Map.Entry<K, V>) -> R): C {
        return finalMap().mapTo(destination, transform)
    }

    open fun <RK, RV, C : MutableMap<in RK, in RV>> mapTo(
        destination: C,
        keySelector: (K) -> RK,
        valueTransform: (V) -> RV
    ): C {
        return finalMap().mapTo(destination, keySelector, valueTransform)
    }

    open fun <RK, RV, C : MutableMap<in RK, in RV>> mapTo(destination: C, transform: (K, V) -> Pair<RK, RV>): C {
        return finalMap().mapTo(destination, transform)
    }

    open fun <R> flatMap(transform: (Map.Entry<K, V>) -> Iterable<R>): Collecting<R> {
        return finalMap().flatMap(transform).collect()
    }

    open fun <R, C : MutableCollection<in R>> flatMapTo(
        destination: C,
        transform: (Map.Entry<K, V>) -> Iterable<R>
    ): C {
        return finalMap().flatMapTo(destination, transform)
    }

    @JvmOverloads
    open fun sorted(comparator: Comparator<in Map.Entry<K, V>> = comparableComparator()): Map<K, V> {
        return finalMap().sorted(comparator)
    }

    open fun toImmutableMap(): ImmutableMap<K, V> {
        return finalMap().toImmutableMap()
    }

    open fun toTreeMap(comparator: java.util.Comparator<in K>): TreeMap<K, V> {
        return finalMap().toTreeMap(comparator)
    }

    open fun plus(other: Map<out K, V>): Mapping<K, V> {
        return finalMap().plus(other).asSelf()
    }

    open fun minus(key: K): Mapping<K, V> {
        return finalMap().minus(key).asSelf()
    }

    open fun minus(keys: Array<out K>): Mapping<K, V> {
        return finalMap().minus(keys).asSelf()
    }

    open fun minus(keys: Iterable<K>): Mapping<K, V> {
        return finalMap().minus(keys).asSelf()
    }

    open fun put(key: K, value: V): Mapping<K, V> {
        finalMutableMap()[key] = value
        return this
    }

    open fun putAll(entries: Map<out K, V>): Mapping<K, V> {
        finalMutableMap().putAll(entries)
        return this
    }

    open fun removeAll(keys: Iterable<K>): Mapping<K, V> {
        finalMutableMap().removeAll(keys)
        return this
    }

    open fun clear(): Mapping<K, V> {
        finalMutableMap().clear()
        return this
    }

    open fun collectEntries(): Collecting<Map.Entry<K, V>> {
        return finalMap().entries.collect()
    }

    open fun collectMutableEntry(): Collecting<MutableMap.MutableEntry<K, V>> {
        return finalMutableMap().entries.collect()
    }

    // Sync

    open fun toSync(): Mapping<K, V> {
        val map = map
        if (map is NavigableMap<K, V>) {
            return map.toSync().asSelf()
        }
        if (map is SortedMap<K, V>) {
            return map.toSync().asSelf()
        }
        return map.toSync().asSelf()
    }
}