package xyz.srclab.common.collection

/**
 * @author sunqian
 */
object OpsForMap {

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
    fun <K, V> plus(map: MutableMap<K, V>, other: Map<out K, V>): Map<K, V> {
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
    fun <K, V, M : MutableMap<in K, in V>> toMutableMap(map: Map<K, V>, destination: M): M {
        for (entry in map) {
            destination.put(entry.key, entry.value)
        }
        return destination
    }

    @JvmStatic
    fun <K, V> toMutableMap(map: Map<K, V>): MutableMap<K, V> {
        return toMutableMap(map, LinkedHashMap())
    }

    @JvmStatic
    fun <K, V> toPair(entry: Map.Entry<K, V>): Pair<K, V> {
        return entry.toPair()
    }
}