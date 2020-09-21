package xyz.srclab.common.collection

import xyz.srclab.common.base.As

class MapOps<K, V> private constructor(map: Map<K, V>) {

    private var operated: Map<K, V> = map

    fun find(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
        return find(operated(), predicate)
    }

    fun findLast(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
        return findLast(operated(), predicate)
    }

    fun first(): Map.Entry<K, V> {
        return first(operated())
    }

    fun first(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V> {
        return first(operated(), predicate)
    }

    fun firstOrNull(): Map.Entry<K, V>? {
        return firstOrNull(operated())
    }

    fun firstOrNull(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
        return firstOrNull(operated(), predicate)
    }

    fun last(): Map.Entry<K, V> {
        return last(operated())
    }

    fun last(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V> {
        return last(operated(), predicate)
    }

    fun lastOrNull(): Map.Entry<K, V>? {
        return lastOrNull(operated())
    }

    fun lastOrNull(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
        return lastOrNull(operated(), predicate)
    }

    fun all(predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
        return all(operated(), predicate)
    }

    fun any(): Boolean {
        return any(operated())
    }

    fun any(predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
        return any(operated(), predicate)
    }

    fun none(): Boolean {
        return none(operated())
    }

    fun none(predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
        return none(operated(), predicate)
    }

    fun single(): Map.Entry<K, V> {
        return single(operated())
    }

    fun single(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V> {
        return single(operated(), predicate)
    }

    fun singleOrNull(): Map.Entry<K, V>? {
        return singleOrNull(operated())
    }

    fun singleOrNull(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
        return singleOrNull(operated(), predicate)
    }

    fun count(): Int {
        return count(operated())
    }

    fun count(predicate: (Map.Entry<K, V>) -> Boolean): Int {
        return count(operated(), predicate)
    }

    fun filter(predicate: (Map.Entry<K, V>) -> Boolean): MapOps<K, V> {
        return toMapOps(filter(operated(), predicate))
    }

    fun <M : MutableMap<in K, in V>> filterTo(destination: M, predicate: (Map.Entry<K, V>) -> Boolean): M {
        return filterTo(operated(), destination, predicate)
    }

    fun <R> map(transform: (Map.Entry<K, V>) -> R): ListOps<R> {
        return toListOps(map(operated(), transform))
    }

    fun <R : Any> mapNotNull(transform: (Map.Entry<K, V>) -> R?): ListOps<R> {
        return toListOps(mapNotNull(operated(), transform))
    }

    fun <RK, RV> map(keySelector: (K) -> RK, valueTransform: (V) -> RV): MapOps<RK, RV> {
        return toMapOps(map(operated(), keySelector, valueTransform))
    }

    fun <RK, RV> map(transform: (K, V) -> Pair<RK, RV>): MapOps<RK, RV> {
        return toMapOps(map(operated(), transform))
    }

    fun <R, C : MutableCollection<in R>> mapTo(destination: C, transform: (Map.Entry<K, V>) -> R): C {
        return mapTo(operated(), destination, transform)
    }

    fun <R : Any, C : MutableCollection<in R>> mapNotNullTo(destination: C, transform: (Map.Entry<K, V>) -> R?): C {
        return mapNotNullTo(operated(), destination, transform)
    }

    fun <RK, RV, C : MutableMap<in RK, in RV>> mapTo(
        destination: C,
        keySelector: (K) -> RK,
        valueTransform: (V) -> RV
    ): C {
        return mapTo(operated(), destination, keySelector, valueTransform)
    }

    fun <RK, RV, C : MutableMap<in RK, in RV>> mapTo(destination: C, transform: (K, V) -> Pair<RK, RV>): C {
        return mapTo(operated(), destination, transform)
    }

    fun <R> flatMap(transform: (Map.Entry<K, V>) -> Iterable<R>): ListOps<R> {
        return toListOps(flatMap(operated(), transform))
    }

    fun <R, C : MutableCollection<in R>> flatMapTo(destination: C, transform: (Map.Entry<K, V>) -> Iterable<R>): C {
        return flatMapTo(operated(), destination, transform)
    }

    fun plus(other: Map<out K, V>): MapOps<K, V> {
        return toMapOps(plus(operated(), other))
    }

    fun minus(key: K): MapOps<K, V> {
        return toMapOps(minus(operated(), key))
    }

    fun minus(keys: Array<out K>): MapOps<K, V> {
        return toMapOps(minus(operated(), keys))
    }

    fun minus(keys: Iterable<K>): MapOps<K, V> {
        return toMapOps(minus(operated(), keys))
    }

    fun toMap(): Map<K, V> {
        return toMap(mutableOperated())
    }

    fun <M : MutableMap<in K, in V>> toMutableMap(destination: M): M {
        return toMutableMap(operated(), destination)
    }

    fun toSequenceOps(): SequenceOps<Map.Entry<K, V>> {
        return SequenceOps.opsFor(operated().entries)
    }

    fun entriesOps(): SetOps<MutableMap.MutableEntry<K, V>> {
        return toSetOps(mutableOperated().entries)
    }

    fun finalMap(): Map<K, V> {
        return operated()
    }

    fun finalMutableMap(): MutableMap<K, V> {
        return mutableOperated()
    }

    private fun operated(): Map<K, V> {
        return operated
    }

    private fun mutableOperated(): MutableMap<K, V> {
        return As.any(operated())
    }

    private fun <T> toListOps(list: List<T>): ListOps<T> {
        return ListOps.opsFor(list)
    }

    private fun <T> toSetOps(set: Set<T>): SetOps<T> {
        return SetOps.opsFor(set)
    }

    private fun <K, V> toMapOps(map: Map<K, V>): MapOps<K, V> {
        return MapOps.opsFor(map)
    }

    companion object {

        @JvmStatic
        fun <K, V> opsFor(map: Map<K, V>): MapOps<K, V> {
            return MapOps(map)
        }

        @JvmStatic
        inline fun <K, V> find(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
            return map.entries.find(predicate)
        }

        @JvmStatic
        inline fun <K, V> findLast(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
            return map.entries.findLast(predicate)
        }

        @JvmStatic
        fun <K, V> first(map: Map<K, V>): Map.Entry<K, V> {
            return map.entries.first()
        }

        @JvmStatic
        inline fun <K, V> first(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V> {
            return map.entries.first(predicate)
        }

        @JvmStatic
        fun <K, V> firstOrNull(map: Map<K, V>): Map.Entry<K, V>? {
            return map.entries.firstOrNull()
        }

        @JvmStatic
        inline fun <K, V> firstOrNull(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
            return map.entries.firstOrNull(predicate)
        }

        @JvmStatic
        fun <K, V> last(map: Map<K, V>): Map.Entry<K, V> {
            return map.entries.last()
        }

        @JvmStatic
        inline fun <K, V> last(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V> {
            return map.entries.last(predicate)
        }

        @JvmStatic
        fun <K, V> lastOrNull(map: Map<K, V>): Map.Entry<K, V>? {
            return map.entries.lastOrNull()
        }

        @JvmStatic
        inline fun <K, V> lastOrNull(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
            return map.entries.lastOrNull(predicate)
        }

        @JvmStatic
        inline fun <K, V> all(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
            return map.all(predicate)
        }

        @JvmStatic
        fun <K, V> any(map: Map<K, V>): Boolean {
            return map.any()
        }

        @JvmStatic
        inline fun <K, V> any(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
            return map.any(predicate)
        }

        @JvmStatic
        fun <K, V> none(map: Map<K, V>): Boolean {
            return map.none()
        }

        @JvmStatic
        inline fun <K, V> none(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
            return map.none(predicate)
        }

        @JvmStatic
        fun <K, V> single(map: Map<K, V>): Map.Entry<K, V> {
            return map.entries.single()
        }

        @JvmStatic
        inline fun <K, V> single(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V> {
            return map.entries.single(predicate)
        }

        @JvmStatic
        fun <K, V> singleOrNull(map: Map<K, V>): Map.Entry<K, V>? {
            return map.entries.singleOrNull()
        }

        @JvmStatic
        inline fun <K, V> singleOrNull(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
            return map.entries.singleOrNull(predicate)
        }

        @JvmStatic
        fun <K, V> count(map: Map<K, V>): Int {
            return map.count()
        }

        @JvmStatic
        inline fun <K, V> count(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Int {
            return map.count(predicate)
        }

        @JvmStatic
        inline fun <K, V> filter(
            map: Map<K, V>,
            predicate: (Map.Entry<K, V>) -> Boolean
        ): Map<K, V> {
            return map.filter(predicate)
        }

        @JvmStatic
        inline fun <K, V, M : MutableMap<in K, in V>> filterTo(
            map: Map<K, V>,
            destination: M,
            predicate: (Map.Entry<K, V>) -> Boolean
        ): M {
            return map.filterTo(destination, predicate)
        }

        @JvmStatic
        inline fun <K, V, R> map(map: Map<K, V>, transform: (Map.Entry<K, V>) -> R): List<R> {
            return map.map(transform)
        }

        @JvmStatic
        inline fun <K, V, R : Any> mapNotNull(map: Map<K, V>, transform: (Map.Entry<K, V>) -> R?): List<R> {
            return map.mapNotNull(transform)
        }

        @JvmStatic
        inline fun <K, V, RK, RV> map(
            map: Map<K, V>,
            crossinline keySelector: (K) -> RK,
            crossinline valueTransform: (V) -> RV
        ): Map<RK, RV> {
            return mapTo(map, LinkedHashMap(), keySelector, valueTransform)
        }

        @JvmStatic
        inline fun <K, V, RK, RV> map(
            map: Map<K, V>,
            transform: (K, V) -> Pair<RK, RV>
        ): Map<RK, RV> {
            return mapTo(map, LinkedHashMap(), transform)
        }

        @JvmStatic
        inline fun <K, V, R, C : MutableCollection<in R>> mapTo(
            map: Map<K, V>,
            destination: C,
            transform: (Map.Entry<K, V>) -> R
        ): C {
            return map.mapTo(destination, transform)
        }

        @JvmStatic
        inline fun <K, V, R : Any, C : MutableCollection<in R>> mapNotNullTo(
            map: Map<K, V>,
            destination: C,
            transform: (Map.Entry<K, V>) -> R?
        ): C {
            return map.mapNotNullTo(destination, transform)
        }

        @JvmStatic
        inline fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> mapTo(
            map: Map<K, V>,
            destination: C,
            crossinline keySelector: (K) -> RK,
            crossinline valueTransform: (V) -> RV
        ): C {
            map.forEach { (k, v) ->
                val rk = keySelector(k)
                val rv = valueTransform(v)
                destination.put(rk, rv)
            }
            return destination
        }

        @JvmStatic
        inline fun <K, V, RK, RV, C : MutableMap<in RK, in RV>> mapTo(
            map: Map<K, V>,
            destination: C,
            transform: (K, V) -> Pair<RK, RV>
        ): C {
            map.forEach { (k, v) ->
                val pair = transform(k, v)
                destination.put(pair.first, pair.second)
            }
            return destination
        }

        @JvmStatic
        inline fun <K, V, R> flatMap(map: Map<K, V>, transform: (Map.Entry<K, V>) -> Iterable<R>): List<R> {
            return map.flatMap(transform)
        }

        @JvmStatic
        inline fun <K, V, R, C : MutableCollection<in R>> flatMapTo(
            map: Map<K, V>,
            destination: C,
            transform: (Map.Entry<K, V>) -> Iterable<R>
        ): C {
            return map.flatMapTo(destination, transform)
        }

        @JvmStatic
        fun <K, V> plus(map: Map<K, V>, other: Map<out K, V>): Map<K, V> {
            return map.plus(other)
        }

        @JvmStatic
        fun <K, V> minus(map: Map<K, V>, key: K): Map<K, V> {
            return map.minus(key)
        }

        @JvmStatic
        fun <K, V> minus(map: Map<K, V>, keys: Array<out K>): Map<K, V> {
            return map.minus(keys)
        }

        @JvmStatic
        fun <K, V> minus(map: Map<K, V>, keys: Iterable<K>): Map<K, V> {
            return map.minus(keys)
        }

        @JvmStatic
        fun <K, V> toMap(map: MutableMap<K, V>): Map<K, V> {
            return map.toMap()
        }

        @JvmStatic
        fun <K, V, M : MutableMap<in K, in V>> toMutableMap(map: Map<K, V>, destination: M): M {
            for (entry in map) {
                destination.put(entry.key, entry.value)
            }
            return destination
        }

        @JvmStatic
        fun <K, V> toMap(entries: Set<Map.Entry<K, V>>): Map<K, V> {
            return toMutableMap(entries, LinkedHashMap())
        }

        @JvmStatic
        fun <K, V, M : MutableMap<in K, in V>> toMutableMap(entries: Set<Map.Entry<K, V>>, destination: M): M {
            for (entry in entries) {
                destination.put(entry.key, entry.value)
            }
            return destination
        }

        @JvmStatic
        fun <K, V> entry(key: K, value: V): Map.Entry<K, V> {
            return object : Map.Entry<K, V> {
                override val key = key
                override val value = value
            }
        }

        @JvmStatic
        fun <K, V> mutableEntry(key: K, value: V): MutableMap.MutableEntry<K, V> {
            return object : MutableMap.MutableEntry<K, V> {

                private var _value: V = value

                override val key: K
                    get() = key
                override val value: V
                    get() = _value

                override fun setValue(newValue: V): V {
                    val old = value
                    _value = value
                    return old
                }
            }
        }

        @JvmStatic
        fun <K, V> entryToPair(entry: Map.Entry<K, V>): Pair<K, V> {
            return entry.toPair()
        }
    }
}