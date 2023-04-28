package xyz.srclab.common.egg.boat

import xyz.srclab.common.reflect.classForName
import xyz.srclab.common.reflect.newInst

open class OScenario(private val config: OConfig) {

    private val weaponManager: OWeaponManager

    init {
        weaponManager = config.weaponManager.classForName<OWeaponManager>().newInst()
    }

    fun init(data: OData) {

    }

    fun tick(data: OData) {

    }

    fun onHit(bullet: OBullet, tank: OTank, tick: Long) {
        bullet.hp = 0
        bullet.deathTick = tick
        tank.hp -= bullet.damage
        if (tank.hp <= 0) {
            tank.hp = 0
            tank.deathTick = tick
        }
    }

    fun onClean(unit: OUnit, tick: Long) {

    }

    fun onMove(movable: OMovable, stepX: Double, stepY: Double, tick: Long) {
        if (movable.state != OUnit.STATE_ALIVE || tick - movable.lastMoveTick <= movable.moveCollDownTick) {
            return
        }
        movable.x += stepX
        movable.y += stepY
        movable.lastMoveTick = tick
    }

    fun onFire(tank: OTank, targetX: Double, targetY: Double, tick: Long) {
        //if (tank.state != OUnit.STATE_ALIVE) {
        //    return
        //}
        //for (weapon in tank.weapons) {
        //    if (tick - weapon.lastFileTick <= weapon.coolDownTick) {
        //        continue
        //    }
        //    //TODO
        //    weapon.lastFileTick = tick
        //}
    }

    fun onBotFire(tank: OTank, tick: Long) {

    }
}