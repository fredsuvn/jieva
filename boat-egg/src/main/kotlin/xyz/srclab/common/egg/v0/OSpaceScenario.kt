package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.sample.Scenario

interface OSpaceScenario : Scenario {

    fun onStart(data: OSpaceData, controller: OSpaceController)

    fun onTick(data: OSpaceData, controller: OSpaceController)

    fun onEnd(data: OSpaceData, controller: OSpaceController)

    fun onHitEnemy(ammo: Ammo, enemy: Enemy, data: OSpaceData, controller: OSpaceController)

    fun onHitPlayer(ammo: Ammo, player: Player, data: OSpaceData, controller: OSpaceController)
}