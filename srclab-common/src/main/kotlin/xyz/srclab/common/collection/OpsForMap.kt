package xyz.srclab.common.collection

/**
 * @author sunqian
 */
object OpsForMap {

    inline fun <K, V> all(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
        return map.all(predicate)
    }

    fun <K, V> any(map: Map<K, V>): Boolean {
        return map.any()
    }

    inline fun <K, V> any(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Boolean {
        return map.any(predicate)
    }

    fun <K, V> count(map: Map<K, V>): Int {
        return map.count()
    }

    inline fun <K, V> count(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Int {
        return map.count(predicate)
    }

    inline fun <K, V, R> flatMap(map: Map<K, V>, transform: (Map.Entry<K, V>) -> Iterable<R>): List<R> {
        return map.flatMap(transform)
    }

    inline fun <K, V, R, C : MutableCollection<in R>> flatMapTo(
        map: Map<K, V>,
        destination: C,
        transform: (Map.Entry<K, V>) -> Iterable<R>
    ): C {
        return map.flatMapTo(destination, transform)
    }

    inline fun <K, V, R> map(map: Map<K, V>, transform: (Map.Entry<K, V>) -> R): List<R> {
        return map.map(transform)
    }

    inline fun <K, V, R : Any> mapNotNull(map: Map<K, V>, transform: (Map.Entry<K, V>) -> R?): List<R> {
        return map.mapNotNull(transform)
    }

    inline fun <K, V, R : Any, C : MutableCollection<in R>> mapNotNullTo(
        map: Map<K, V>,
        destination: C,
        transform: (Map.Entry<K, V>) -> R?
    ): C {
        return map.mapNotNullTo(destination, transform)
    }

    inline fun <K, V, R, C : MutableCollection<in R>> mapTo(
        map: Map<K, V>,
        destination: C,
        transform: (Map.Entry<K, V>) -> R
    ): C {
    }

    inline fun <K, V, R : Comparable<R>> maxBy(map: Map<K, V>, selector: (Map.Entry<K, V>) -> R): Map.Entry<K, V>? {}

    inline fun <K, V, R : Comparable<R>> maxByOrNull(
        map: Map<K, V>,
        selector: (Map.Entry<K, V>) -> R
    ): Map.Entry<K, V>? {
    }

    inline fun <K, V, R : Comparable<R>> maxOf(map: Map<K, V>, selector: (Map.Entry<K, V>) -> R): R {}

    inline fun <K, V> maxOf(map: Map<K, V>, selector: (Map.Entry<K, V>) -> Double): Double {}

    inline fun <K, V> maxOf(map: Map<K, V>, selector: (Map.Entry<K, V>) -> Float): Float {}

    inline fun <K, V, R : Comparable<R>> maxOfOrNull(map: Map<K, V>, selector: (Map.Entry<K, V>) -> R): R? {}

    inline fun <K, V> maxOfOrNull(map: Map<K, V>, selector: (Map.Entry<K, V>) -> Double): Double? {}

    inline fun <K, V> maxOfOrNull(map: Map<K, V>, selector: (Map.Entry<K, V>) -> Float): Float? {}

    inline fun <K, V, R> maxOfWith(map: Map<K, V>, comparator: Comparator<in R>, selector: (Map.Entry<K, V>) -> R): R {}

    inline fun <K, V, R> maxOfWithOrNull(
        map: Map<K, V>,
        comparator: Comparator<in R>,
        selector: (Map.Entry<K, V>) -> R
    ): R? {
    }

    inline fun <K, V> maxWith(map: Map<K, V>, comparator: Comparator<in Map.Entry<K, V>>): Map.Entry<K, V>? {}

    inline fun <K, V> maxWithOrNull(map: Map<K, V>, comparator: Comparator<in Map.Entry<K, V>>): Map.Entry<K, V>? {}

    inline fun <K, V, R : Comparable<R>> minBy(map: Map<K, V>, selector: (Map.Entry<K, V>) -> R): Map.Entry<K, V>? {}

    inline fun <K, V, R : Comparable<R>> minByOrNull(
        map: Map<K, V>,
        selector: (Map.Entry<K, V>) -> R
    ): Map.Entry<K, V>? {
    }

    inline fun <K, V, R : Comparable<R>> minOf(map: Map<K, V>, selector: (Map.Entry<K, V>) -> R): R {}

    inline fun <K, V> minOf(map: Map<K, V>, selector: (Map.Entry<K, V>) -> Double): Double {}

    inline fun <K, V> minOf(map: Map<K, V>, selector: (Map.Entry<K, V>) -> Float): Float {}

    inline fun <K, V, R : Comparable<R>> minOfOrNull(map: Map<K, V>, selector: (Map.Entry<K, V>) -> R): R? {}

    inline fun <K, V> minOfOrNull(map: Map<K, V>, selector: (Map.Entry<K, V>) -> Double): Double? {}

    inline fun <K, V> minOfOrNull(map: Map<K, V>, selector: (Map.Entry<K, V>) -> Float): Float? {}

    inline fun <K, V, R> minOfWith(map: Map<K, V>, comparator: Comparator<in R>, selector: (Map.Entry<K, V>) -> R): R {}

    inline fun <K, V, R> minOfWithOrNull(
        map: Map<K, V>,
        comparator: Comparator<in R>,
        selector: (Map.Entry<K, V>) -> R
    ): R? {
    }

    fun <K, V> minWith(map: Map<K, V>, comparator: Comparator<in Map.Entry<K, V>>): Map.Entry<K, V>? {}

    inline fun <K, V> minWithOrNull(map: Map<K, V>, comparator: Comparator<in Map.Entry<K, V>>): Map.Entry<K, V>? {}

    fun <K, V> none(map: Map<K, V>): Boolean {}

    inline fun <K, V> none(map: Map<K, V>, predicate: (Map.Entry<K, V>) -> Boolean): Boolean {}

    inline fun <K, V, M : Map<out K, V>> M.onEach(action: (Map.Entry<K, V>) -> Unit): M {}

    inline fun <K, V, M : Map<out K, V>> M.onEachIndexed(action: (Int, Map.Entry<K, V>) -> Unit): M {}

    fun <K, V> toList(map: Map<K, V>): List<Pair<K, V>> {}

}