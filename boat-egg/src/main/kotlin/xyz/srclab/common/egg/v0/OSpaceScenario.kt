package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.Scenario

interface OSpaceScenario : Scenario {

    val player1: Player

    val player2: Player

    val enemies: MutableIterable<Enemy>

    fun refreshEnemies()

    fun randomPlayer(): Player

    fun onHitEnemy(ammo: Ammo, ammoManager: AmmoManager, enemy: Enemy)

    fun onHitPlayer(ammo: Ammo, ammoManager: AmmoManager, player: Player)

    fun onPlayer1Dead()

    fun onPlayer2Dead()
}