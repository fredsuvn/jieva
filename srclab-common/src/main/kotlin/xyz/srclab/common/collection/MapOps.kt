package xyz.srclab.common.collection

interface MapOps {

    companion object {

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