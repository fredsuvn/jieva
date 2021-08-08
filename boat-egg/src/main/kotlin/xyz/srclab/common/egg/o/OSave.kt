package xyz.srclab.common.egg.o

import xyz.srclab.common.serialize.json.toJson
import xyz.srclab.common.serialize.json.toJsonBytes

open class OSave {

    val players: List<Player>? = null
    val computers: List<Soldier>? = null
    var tick: Long = 0
    var config: OConfig? = null

    companion object {

        fun OSave.serialize(): ByteArray {
            return this.toJsonBytes()
        }

        fun ByteArray.deserialize(): OSave {
            return this.toJson().toObject(OSave::class.java)
        }
    }
}