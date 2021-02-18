package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.sample.Data
import xyz.srclab.common.serialize.json.toJson
import xyz.srclab.common.serialize.json.toJsonBytes
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author sunqian
 */
class OSpaceData : Data {

    var player1: Player? = null
    var player2: Player? = null
    var enemies: MutableList<Enemy>? = null

    override fun save(path: CharSequence) {
        val json = this.toJsonBytes()
        Files.write(Paths.get(path.toString()), json)
    }

    companion object {

        fun CharSequence.asOSpaceSavingFile(): OSpaceData {
            return URL("file:$this").toJson().toObject(OSpaceData::class.java)
        }
    }
}