package xyz.srclab.common.collection

class MapOps<K, V> private constructor(map: Map<K, V>) {

    companion object {

        @JvmStatic
        fun <K, V> opsFor(map: Map<K, V>): MapOps<K, V> {
            return MapOps(map)
        }

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
    }
}