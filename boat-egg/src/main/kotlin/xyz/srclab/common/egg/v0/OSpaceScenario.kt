package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.Scenario

interface OSpaceScenario : Scenario {

    val ammos: List<Ammo>

    val livings: List<Living>

    val player1: Player

    val player2: Player

    fun refreshEnemies(difficulty: Int)
}