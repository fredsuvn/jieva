package xyz.srclab.common.collection

/**
 * Operations for [Map]
 *
 * @author sunqian
 */
interface MapOps<K, V> : MutableMap<K, V> {

    companion object {

        @JvmStatic
        fun <K, V> of(map: Map<K, V>): MapOps<K, V> {
            TODO()
        }
    }
}