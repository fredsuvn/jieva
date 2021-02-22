package xyz.srclab.common.egg.v0

internal interface OSpaceWeaponActor {

    val id: Int

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
    var drawerId: Int,
)