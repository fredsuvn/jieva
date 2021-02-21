package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.sample.Data
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.reflect.toInstance
import xyz.srclab.common.serialize.json.jsonToObject
import xyz.srclab.common.serialize.json.toJson
import xyz.srclab.common.serialize.json.toJsonBytes
import xyz.srclab.common.serialize.json.toJsonString
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap

/**
 * @author sunqian
 */
internal class OSpaceData(
    override val scenario: OSpaceScenario
) : Data<OSpaceScenario> {

    private var _players: MutableList<Player>? = null
    private var _enemies: MutableList<Enemy>? = null
    private var _playersAmmos: MutableList<Ammo>? = null
    private var _enemiesAmmos: MutableList<Ammo>? = null

    val player1: Player
        get() = players[0]

    val player2: Player
        get() = players[1]

    val players: MutableList<Player>
        get() {
            var result = _players
            if (result === null) {
                result = LinkedList()
                _players = result
            }
            return result
        }
    val enemies: MutableList<Enemy>
        get() {
            var result = _enemies
            if (result === null) {
                result = LinkedList()
                _enemies = result
            }
            return result
        }
    val playersAmmos: MutableList<Ammo>
        get() {
            var result = _playersAmmos
            if (result === null) {
                result = LinkedList()
                _playersAmmos = result
            }
            return result
        }
    val enemiesAmmos: MutableList<Ammo>
        get() {
            var result = _enemiesAmmos
            if (result === null) {
                result = LinkedList()
                _enemiesAmmos = result
            }
            return result
        }

    constructor() : this(OSpaceScenario())

    override fun save(path: CharSequence) {
        val map = HashMap<String, String>()
        map["_players"] = this.players.toJsonString()
        map["_enemies"] = this.enemies.toJsonString()
        map["_playersAmmos"] = this.playersAmmos.toJsonString()
        map["_enemiesAmmos"] = this.enemiesAmmos.toJsonString()
        map["_scenario"] = this.scenario.javaClass.name
        Files.write(Paths.get(path.toString()), map.toJsonBytes())
    }

    companion object {

        fun CharSequence.fromOSpaceSavingFile(): OSpaceData {
            val map = URL("file:$this").toJson().toObject(object : TypeRef<HashMap<String, String>>() {})
            val data = OSpaceData(map["_scenario"]!!.toInstance())
            data._players = map["_players"]!!.jsonToObject(object : TypeRef<MutableList<Player>>() {})
            data._enemies = map["_enemies"]!!.jsonToObject(object : TypeRef<MutableList<Enemy>>() {})
            data._playersAmmos = map["_playersAmmos"]!!.jsonToObject(object : TypeRef<MutableList<Ammo>>() {})
            data._enemiesAmmos = map["_enemiesAmmos"]!!.jsonToObject(object : TypeRef<MutableList<Ammo>>() {})
            return data
        }
    }
}