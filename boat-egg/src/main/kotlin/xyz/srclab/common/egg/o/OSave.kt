package xyz.srclab.common.egg.o

import xyz.srclab.common.serialize.json.toJson
import xyz.srclab.common.serialize.json.toJsonBytes

open class OSave {

    var group1: Group? = null
    var group2: Group? = null
    var tick: Long = 0

    companion object {

        fun OSave.serialize(): ByteArray {
            return this.toJsonBytes()
        }

        fun ByteArray.deserialize(): OSave {
            return this.toJson().toObject(OSave::class.java)
        }
    }

    open class Group {
        var players: Map<Long, Player>? = null
        var soldiers: Map<Long, Soldier>? = null
    }
}