package xyz.srclab.common.cache

/**
 * [Cache] based on [MutableMap].
 *
 * `null` value is permitted.
 */
open class MapCache<K : Any, V>(
    private val map: MutableMap<K, V>
) : Cache<K, V> {

    override fun cleanUp() {
    }

    override fun asMap(): MutableMap<K, V> {
        return map
    }
}