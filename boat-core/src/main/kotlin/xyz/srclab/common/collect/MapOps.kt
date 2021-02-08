package xyz.srclab.common.collect

import xyz.srclab.common.base.asAny

class MapOps<K, V>(private var map: Map<K, V>) {

    fun finalMap(): Map<K, V> {
        return map
    }

    fun finalMutableMap(): MutableMap<K, V> {
        return map.asAny()
    }

    fun containsKeys(keys: Array<out K>): Boolean {
        return finalMap().containsKeys(keys)
    }

    fun containsKeys(keys: Iterable<K>): Boolean {
        return finalMap().containsKeys(keys)
    }

    fun containsKeys(keys: Collection<K>): Boolean {
        return finalMap().containsKeys(keys)
    }

    fun containsValues(values: Array<out V>): Boolean {
        return finalMap().containsValues(values)
    }

    fun containsValues(values: Iterable<V>): Boolean {
        return finalMap().containsValues(values)
    }

    fun containsValues(values: Collection<V>): Boolean {
        return finalMap().containsValues(values)
    }

    fun filter(predicate: (Map.Entry<K, V>) -> Boolean): MapOps<K, V> {
        return finalMap().filter(predicate).toMapOps()
    }

    fun <M : MutableMap<in K, in V>> filterTo(destination: M, predicate: (Map.Entry<K, V>) -> Boolean): M {
        return finalMap().filterTo(destination, predicate)
    }

    fun <R> map(transform: (Map.Entry<K, V>) -> R): ListOps<R> {
        return finalMap().map(transform).toListOps()
    }

    fun <RK, RV> map(keySelector: (K) -> RK, valueTransform: (V) -> RV): MapOps<RK, RV> {
        return finalMap().mapTo(LinkedHashMap(), keySelector, valueTransform).toMapOps()
    }

    fun <RK, RV> map(transform: (K, V) -> Pair<RK, RV>): MapOps<RK, RV> {
        return finalMap().mapTo(LinkedHashMap(), transform).toMapOps()
    }

    fun <R, C : MutableCollection<in R>> mapTo(destination: C, transform: (Map.Entry<K, V>) -> R): C {
        return finalMap().mapTo(destination, transform)
    }

    fun <RK, RV, C : MutableMap<in RK, in RV>> mapTo(
        destination: C,
        keySelector: (K) -> RK,
        valueTransform: (V) -> RV
    ): C {
        return finalMap().mapTo(destination, keySelector, valueTransform)
    }

    fun <RK, RV, C : MutableMap<in RK, in RV>> mapTo(destination: C, transform: (K, V) -> Pair<RK, RV>): C {
        return finalMap().mapTo(destination, transform)
    }

    fun <R> flatMap(transform: (Map.Entry<K, V>) -> Iterable<R>): ListOps<R> {
        return finalMap().flatMap(transform).toListOps()
    }

    fun <R, C : MutableCollection<in R>> flatMapTo(destination: C, transform: (Map.Entry<K, V>) -> Iterable<R>): C {
        return finalMap().flatMapTo(destination, transform)
    }

    fun plus(other: Map<out K, V>): MapOps<K, V> {
        return finalMap().plus(other).toMapOps()
    }

    fun minus(key: K): MapOps<K, V> {
        return finalMap().minus(key).toMapOps()
    }

    fun minus(keys: Array<out K>): MapOps<K, V> {
        return finalMap().minus(keys).toMapOps()
    }

    fun minus(keys: Iterable<K>): MapOps<K, V> {
        return finalMap().minus(keys).toMapOps()
    }

    fun put(key: K, value: V): MapOps<K, V> {
        finalMutableMap()[key] = value
        return this
    }

    fun putAll(entries: Map<out K, V>): MapOps<K, V> {
        finalMutableMap().putAll(entries)
        return this
    }

    fun removeAll(keys: Iterable<K>): MapOps<K, V> {
        finalMutableMap().removeAll(keys)
        return this
    }

    fun clear(): MapOps<K, V> {
        finalMutableMap().clear()
        return this
    }

    fun toEntrySetOps(): SetOps<Map.Entry<K, V>> {
        return finalMap().entries.toSetOps()
    }

    fun toMutableEntrySetOps(): SetOps<MutableMap.MutableEntry<K, V>> {
        return finalMutableMap().entries.toSetOps()
    }

    private fun <T> Iterable<T>.toIterableOps(): IterableOps<T> {
        return IterableOps.opsFor(this)
    }

    private fun <T> List<T>.toListOps(): ListOps<T> {
        return ListOps.opsFor(this)
    }

    private fun <T> Set<T>.toSetOps(): SetOps<T> {
        return SetOps.opsFor(this)
    }

    private fun <K, V> Map<K, V>.toMapOps(): MapOps<K, V> {
        map = this.asAny()
        return this@MapOps.asAny()
    }

    companion object {

        @JvmStatic
        fun <K, V> opsFor(map: Map<K, V>): MapOps<K, V> {
            return MapOps(map)
        }
    }
}