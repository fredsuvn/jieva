package xyz.srclab.common.egg.nest.o

internal object OWeaponManager {

    fun getWeaponActor(id: String): WeaponActor {
        return when (id) {
            "common" -> CommonWeaponActor
            "bloom" -> BloomWeaponActor
            "bullet" -> BulletWeaponActor
            else -> throw IllegalArgumentException("Weapon actor not found: $id")
        }
    }
}

internal interface WeaponActor {

    fun fire(weapon: OWeapon, tickTime: Long, targetX: Double, targetY: Double): List<OAmmo>
}

private object CommonWeaponActor : WeaponActor {

    override fun fire(weapon: OWeapon, tickTime: Long, targetX: Double, targetY: Double): List<OAmmo> {
        val attacker = weapon.holder
        val weaponType = weapon.type
        val p = step(attacker.x, attacker.y, targetX, targetY)
        return listOf(
            OAmmo(
                attacker.x,
                attacker.y,
                attacker.x,
                attacker.y,
                p.x * OConfig.xUnit,
                p.y * OConfig.yUnit,
                weaponType.ammoRadius,
                weaponType.ammoMoveSpeed,
                0,
                0,
                weaponType.ammoDeathDuration,
                false,
                attacker.player,
                ODrawer.getDrawResource(weaponType.ammoDrawId),
                weapon,
                tickTime,
                0
            )
        )
    }
}

private object BloomWeaponActor : WeaponActor {

    override fun fire(weapon: OWeapon, tickTime: Long, targetX: Double, targetY: Double): List<OAmmo> {
        val attacker = weapon.holder
        val weaponType = weapon.type
        val ammo1 = OAmmo(
            attacker.x,
            attacker.y,
            attacker.x,
            attacker.y,
            -OConfig.xUnit,
            0.0,
            weaponType.ammoRadius,
            weaponType.ammoMoveSpeed,
            0,
            0,
            weaponType.ammoDeathDuration,
            false,
            attacker.player,
            ODrawer.getDrawResource(weaponType.ammoDrawId),
            weapon,
            tickTime,
            0
        )
        val ammo2 = ammo1.copy(
            stepX = -1.0 * STEP_45_DEGREE_ANGLE * OConfig.xUnit,
            stepY = -1.0 * STEP_45_DEGREE_ANGLE * OConfig.yUnit
        )
        val ammo3 = ammo1.copy(stepX = 0.0, stepY = -1.0 * OConfig.yUnit)
        val ammo4 = ammo1.copy(
            stepX = 1.0 * STEP_45_DEGREE_ANGLE * OConfig.xUnit,
            stepY = -1.0 * STEP_45_DEGREE_ANGLE * OConfig.yUnit
        )
        val ammo5 = ammo1.copy(stepX = 1.0 * OConfig.xUnit, stepY = 0.0)
        val ammo6 = ammo1.copy(
            stepX = 1.0 * STEP_45_DEGREE_ANGLE * OConfig.xUnit,
            stepY = 1.0 * STEP_45_DEGREE_ANGLE * OConfig.yUnit
        )
        val ammo7 = ammo1.copy(stepX = 0.0, stepY = 1.0 * OConfig.yUnit)
        val ammo8 = ammo1.copy(
            stepX = -1.0 * STEP_45_DEGREE_ANGLE * OConfig.xUnit,
            stepY = 1.0 * STEP_45_DEGREE_ANGLE * OConfig.yUnit
        )
        return listOf(ammo1, ammo2, ammo3, ammo4, ammo5, ammo6, ammo7, ammo8)
    }
}

private object BulletWeaponActor : WeaponActor {

    override fun fire(weapon: OWeapon, tickTime: Long, targetX: Double, targetY: Double): List<OAmmo> {
        val attacker = weapon.holder
        val weaponType = weapon.type
        val p = step(attacker.x, attacker.y, targetX, targetY)
        val ammo1 = OAmmo(
            attacker.x,
            attacker.y,
            attacker.x,
            attacker.y,
            p.x * OConfig.xUnit,
            p.y * OConfig.yUnit,
            weaponType.ammoRadius,
            weaponType.ammoMoveSpeed,
            0,
            0,
            weaponType.ammoDeathDuration,
            false,
            attacker.player,
            ODrawer.getDrawResource(weaponType.ammoDrawId),
            weapon,
            tickTime,
            0
        )
        val ammo2 = ammo1.copy(preparedTime = 100 * 2)
        val ammo3 = ammo1.copy(preparedTime = 200 * 2)
        val ammo4 = ammo1.copy(preparedTime = 300 * 2)
        val ammo5 = ammo1.copy(preparedTime = 400 * 2)
        val ammo6 = ammo1.copy(preparedTime = 500 * 2)
        val ammo7 = ammo1.copy(preparedTime = 600 * 2)
        val ammo8 = ammo1.copy(preparedTime = 700 * 2)
        return listOf(ammo1, ammo2, ammo3, ammo4, ammo5, ammo6, ammo7, ammo8)
    }
}