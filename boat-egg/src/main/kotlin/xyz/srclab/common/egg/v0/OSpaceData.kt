package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.sample.Data
import java.util.*

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
        TODO()
    }

    companion object {

        fun CharSequence.fromOSpaceSavingFile(): OSpaceData {
            TODO()
        }
    }
}