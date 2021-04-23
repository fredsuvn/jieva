package xyz.srclab.common.egg.v0

import java.util.*

internal interface WeaponActor {

    fun fire(attacker: Living, tickTime: Long, targetX: Double, targetY: Double): List<AmmoMeta>
}

internal data class AmmoMeta(
    var createTime: Long = 0,
    var preparedTime: Long = 0,
    var xStepUnit: Double,
    var yStepUnit: Double,
    var deathDuration: Long = 5000,
    var radius: Double = 12.0,
    var moveSpeed: Int = 80,
    var drawer: UnitDrawer,
)

private class CommonWeaponActor : WeaponActor{

    override fun fire(attacker: Living, tickTime: Long, targetX: Double, targetY: Double): List<AmmoMeta> {
        val p = step(attacker.x, attacker.y, targetX, targetY)
        return Collections.singletonList(
            AmmoMeta(
                0,
                0,
                p.x,
                p.y,
                Config.DEATH_DURATION,
                Config.AMMO_RADIUS,
                Config.AMMO_MOVE_SPEED,
                UnitDrawerManager.getDrawer()
            )
        )
    }
}

private object EnemyWeaponActor : WeaponActor {

    override val id: Int = ENEMY_WEAPON_ACTOR_ID

    override fun fire(attacker: Living, tickTime: Long, targetX: Double, targetY: Double): List<AmmoMeta> {
        val p = step(attacker.x, attacker.y, targetX, targetY)
        return Collections.singletonList(
            AmmoMeta(
                0,
                0,
                p.x,
                p.y,
                DEATH_DURATION,
                AMMO_RADIUS,
                AMMO_MOVE_SPEED,
                ENEMY_AMMO_DRAWER_ID
            )
        )
    }
}

private object CrazyWeaponActor : WeaponActor {

    override val id: Int = ENEMY_CRAZY_WEAPON_ACTOR_ID

    override fun fire(attacker: Living, tickTime: Long, targetX: Double, targetY: Double): List<AmmoMeta> {
        val ammo1 = AmmoMeta(
            0,
            0,
            -1.0,
            0.0,
            DEATH_DURATION,
            AMMO_RADIUS,
            AMMO_MOVE_SPEED,
            ENEMY_AMMO_DRAWER_ID
        )
        val ammo2 = ammo1.copy(xStepUnit = -1.0 * STEP_45_DEGREE_ANGLE, yStepUnit = -1.0 * STEP_45_DEGREE_ANGLE)
        val ammo3 = ammo1.copy(xStepUnit = 0.0, yStepUnit = -1.0)
        val ammo4 = ammo1.copy(xStepUnit = 1.0 * STEP_45_DEGREE_ANGLE, yStepUnit = -1.0 * STEP_45_DEGREE_ANGLE)
        val ammo5 = ammo1.copy(xStepUnit = 1.0, yStepUnit = 0.0)
        val ammo6 = ammo1.copy(xStepUnit = 1.0 * STEP_45_DEGREE_ANGLE, yStepUnit = 1.0 * STEP_45_DEGREE_ANGLE)
        val ammo7 = ammo1.copy(xStepUnit = 0.0, yStepUnit = 1.0)
        val ammo8 = ammo1.copy(xStepUnit = -1.0 * STEP_45_DEGREE_ANGLE, yStepUnit = 1.0 * STEP_45_DEGREE_ANGLE)
        return listOf(ammo1, ammo2, ammo3, ammo4, ammo5, ammo6, ammo7, ammo8)
    }
}

private object ContinuousWeaponActor : WeaponActor {

    override val id: Int = ENEMY_CONTINUOUS_WEAPON_ACTOR_ID

    override fun fire(attacker: Living, tickTime: Long, targetX: Double, targetY: Double): List<AmmoMeta> {
        val p = step(attacker.x, attacker.y, targetX, targetY)
        val ammo1 = AmmoMeta(
            tickTime,
            0,
            p.x,
            p.y,
            DEATH_DURATION,
            AMMO_RADIUS,
            AMMO_MOVE_SPEED,
            ENEMY_AMMO_DRAWER_ID
        )
        val ammo2 = ammo1.copy(preparedTime = 100)
        val ammo3 = ammo1.copy(preparedTime = 200)
        val ammo4 = ammo1.copy(preparedTime = 300)
        val ammo5 = ammo1.copy(preparedTime = 400)
        val ammo6 = ammo1.copy(preparedTime = 500)
        val ammo7 = ammo1.copy(preparedTime = 600)
        val ammo8 = ammo1.copy(preparedTime = 700)
        return listOf(ammo1, ammo2, ammo3, ammo4, ammo5, ammo6, ammo7, ammo8)
    }
}