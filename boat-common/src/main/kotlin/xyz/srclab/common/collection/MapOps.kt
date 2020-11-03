package xyz.srclab.common.collection

import xyz.srclab.common.base.asAny
import kotlin.collections.filter as filterKt
import kotlin.collections.filterTo as filterToKt
import kotlin.collections.flatMap as flatMapKt
import kotlin.collections.flatMapTo as flatMapToKt
import kotlin.collections.map as mapKt
import kotlin.collections.mapTo as mapToKt
import kotlin.collections.minus as minusKt
import kotlin.collections.plus as plusKt
import kotlin.collections.toSet as toSetKt

class MapOps<K, V>(private var map: Map<K, V>) {

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

    fun finalMap(): Map<K, V> {
        return map
    }

    fun finalMutableMap(): MutableMap<K, V> {
        return map.asAny()
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

        @JvmStatic
        fun <K, V> Map<K, V>.containsKeys(keys: Array<out K>): Boolean {
            return this.keys.containsAll(keys.toSetKt())
        }

        @JvmStatic
        fun <K, V> Map<K, V>.containsKeys(keys: Iterable<K>): Boolean {
            return this.keys.containsAll(keys.toSetKt())
        }

        @JvmStatic
        fun <K, V> Map<K, V>.containsKeys(keys: Collection<K>): Boolean {
            return this.keys.containsAll(keys)
        }

        @JvmStatic
        fun <K, V> Map<K, V>.containsValues(values: Array<out V>): Boolean {
            return this.values.toSetKt().containsAll(values.toSetKt())
        }

        @JvmStatic
        fun <K, V> Map<K, V>.containsValues(values: Iterable<V>): Boolean {
            return this.values.toSetKt().containsAll(values.toSetKt())
        }

        @JvmStatic
        fun <K, V> Map<K, V>.containsValues(values: Collection<V>): Boolean {
            return this.values.toSetKt().containsAll(values)
        }

        @JvmStatic
        inline fun <K, V> Map<K, V>.filter(predicate: (Map.Entry<K, V>) -> Boolean): Map<K, V> {
            return this.filterKt(predicate)
        }

        @JvmStatic
        inline fun <K, V, M : MutableMap<in K, in V>> Map<K, V>.filterTo(
            destination: M,
            predicate: (Map.Entry<K, V>) -> Boolean
        ): M {
            return this.filterToKt(destination, predicate)
        }

        @JvmStatic
        inline fun <K, V, R> Map<K, V>.map(transform: (Map.Entry<K, V>) -> R): List<R> {
            return this.mapKt(transform)
        }

        @JvmStatic
        inline fun <K, V, RK, RV> Map<K, V>.map(
            crossinline keySelector: (K) -> RK,
            crossinline valueTransform: (V) -> RV
        ): Map<RK, RV> {
            return this.mapTo(LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <K, V, RK, RV> Map<K, V>.map(transform: (K, V) -> Pair<RK, RV>): Map<RK, RV> {
            return this.mapTo(LinkedHashMap(), transform)
        }

        @JvmStatic
        inline fun <K, V, R, C : MutableCollection<in R>> Map<K, V>.mapTo(
            destination: C,
            transform: (Map.Entry<K, V>) -> R
        ): C {
            return this.mapToKt(destination, transform)
        }

        @JvmStatic
        inline fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> Map<K, V>.mapTo(
            destination: C,
            crossinline keySelector: (K) -> RK,
            crossinline valueTransform: (V) -> RV
        ): C {
            this.forEach { (k, v) ->
                val rk = keySelector(k)
                val rv = valueTransform(v)
                destination.put(rk, rv)
            }
            return destination
        }

        @JvmStatic
        inline fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> Map<K, V>.mapTo(
            destination: C,
            transform: (K, V) -> Pair<RK, RV>
        ): C {
            this.forEach { (k, v) ->
                val pair = transform(k, v)
                destination.put(pair.first, pair.second)
            }
            return destination
        }

        @JvmStatic
        inline fun <K, V, R> Map<K, V>.flatMap(transform: (Map.Entry<K, V>) -> Iterable<R>): List<R> {
            return this.flatMapKt(transform)
        }

        @JvmStatic
        inline fun <K, V, R, C : MutableCollection<in R>> Map<K, V>.flatMapTo(
            destination: C,
            transform: (Map.Entry<K, V>) -> Iterable<R>
        ): C {
            return this.flatMapToKt(destination, transform)
        }

        @JvmStatic
        fun <K, V> Map<K, V>.plus(other: Map<out K, V>): Map<K, V> {
            return this.plusKt(other)
        }

        @JvmStatic
        fun <K, V> Map<K, V>.minus(key: K): Map<K, V> {
            return this.minusKt(key)
        }

        @JvmStatic
        fun <K, V> Map<K, V>.minus(keys: Array<out K>): Map<K, V> {
            return this.minusKt(keys)
        }

        @JvmStatic
        fun <K, V> Map<K, V>.minus(keys: Iterable<K>): Map<K, V> {
            return this.minusKt(keys)
        }

        @JvmStatic
        fun <K, V> MutableMap<K, V>.removeAll(keys: Iterable<K>) {
            for (key in keys) {
                this.remove(key)
            }
        }
    }
}