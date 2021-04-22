package xyz.srclab.common.egg.v0

import java.util.*

/**
 * @author sunqian
 */
internal object OSpaceData {

    val player1: Player
        get() = players[0]

    val player2: Player
        get() = players[1]

    val players: MutableList<Player> = LinkedList()

    val enemies: MutableList<Enemy> = LinkedList()

    val playersAmmos: MutableList<Ammo> = LinkedList()

    val enemiesAmmos: MutableList<Ammo> = LinkedList()
}