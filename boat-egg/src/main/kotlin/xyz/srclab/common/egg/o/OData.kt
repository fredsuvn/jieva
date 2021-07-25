package xyz.srclab.common.egg.o

import xyz.srclab.common.serialize.json.toJson
import xyz.srclab.common.serialize.json.toJsonBytes

open class OData {
    var players: Map<Long, Player>? = null
    var enemies: Map<Long, Enemy>? = null
    var playerBullets: Map<Long, Bullet>? = null
    var enemyBullets: Map<Long, Bullet>? = null
    var tick: OTick? = null

    fun serialize(): ByteArray {
        val map: MutableMap<String, Any?> = HashMap()
        map["players"] = this.players
        map["enemies"] = this.enemies
        map["playerBullets"] = this.playerBullets
        map["enemyBullets"] = this.enemyBullets
        map["tick"] = this.tick
        return map.toJsonBytes()
    }

    companion object {

        //fun load(bytes: ByteArray): OData {
        //    val json = bytes.toJson().toMap()
        //    val data = OData()
        //    data.players = map["players"].asAny()
        //    data.enemies = map["enemies"].asAny()
        //    data.playerBullets = map["playerBullets"].asAny()
        //    data.enemyBullets = map["enemyBullets"].asAny()
        //    val tickTime = map["tick"]?.toLong() ?: 0
        //    data.tick = OTick(tickTime)
        //    return data
        //}
    }
}