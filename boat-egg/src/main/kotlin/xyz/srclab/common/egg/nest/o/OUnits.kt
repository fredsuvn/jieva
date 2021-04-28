package xyz.srclab.common.egg.nest.o

import java.awt.Color

internal const val HUMAN_FORCE = 1
internal const val ENEMY_FORCE = 2

internal const val STEP_UNIT = 1.0
internal const val STEP_45_DEGREE_ANGLE = STEP_UNIT * 0.7071067812

internal interface OElement {
    var id: Long
}

internal interface OObjectUnit : OElement {
    var x: Double
    var y: Double
    var lastX: Double
    var lastY: Double
    var stepX: Double
    var stepY: Double
    var radius: Double
    var moveSpeed: Int
    var lastMoveTime: Long
    var deathTime: Long
    var deathDuration: Long
    var keepBody: Boolean
    var player: OPlayer
    var drawResource: DrawResource

    val isDead: Boolean
        get() = deathTime > 0

    fun isDisappeared(tickTime: Long): Boolean {
        return isDead && tickTime - deathTime > deathDuration
    }

    fun isBody(tickTime: Long): Boolean {
        return keepBody && isDisappeared(tickTime)
    }
}

internal interface OSubjectUnit : OObjectUnit {
    var type: OSubjectType
    var hp: Int
    var defense: Int
    var weapons: List<OWeapon>
}

internal interface OAmmoUnit : OObjectUnit {
    var weapon: OWeapon
    var createTime: Long
    var preparedTime: Long
}

internal open class OBaseUnit : OElement {

    override var id: Long = idSequence++

    companion object {
        private var idSequence: Long = 100000L
    }
}

internal data class OSubject(
    override var x: Double,
    override var y: Double,
    override var lastX: Double,
    override var lastY: Double,
    override var stepX: Double,
    override var stepY: Double,
    override var radius: Double,
    override var moveSpeed: Int,
    override var lastMoveTime: Long,
    override var deathTime: Long,
    override var deathDuration: Long,
    override var keepBody: Boolean,
    override var player: OPlayer,
    override var drawResource: DrawResource,
    override var type: OSubjectType,
    override var hp: Int,
    override var defense: Int,
    override var weapons: List<OWeapon>,
    var score: Long,
) : OBaseUnit(), OSubjectUnit

internal data class OAmmo(
    override var x: Double,
    override var y: Double,
    override var lastX: Double,
    override var lastY: Double,
    override var stepX: Double,
    override var stepY: Double,
    override var radius: Double,
    override var moveSpeed: Int,
    override var lastMoveTime: Long,
    override var deathTime: Long,
    override var deathDuration: Long,
    override var keepBody: Boolean,
    override var player: OPlayer,
    override var drawResource: DrawResource,
    var weapon: OWeapon,
    var createTime: Long,
    var preparedTime: Long,
) : OBaseUnit(), OObjectUnit

internal data class OWeapon(
    var holder: OSubjectUnit,
    var damage: Int,
    var fireSpeed: Int,
    var lastFireTime: Long,
    var actor: WeaponActor,
    var type: OWeaponType,
) : OBaseUnit()

internal data class OPlayer(
    var number: Int = 0,
    var hit: Long,
    var score: Long,
    var force: Int,
    var color: Color,
)