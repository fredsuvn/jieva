package xyz.srclab.common.egg.v0

import xyz.srclab.common.reflect.shortName

internal const val NEUTRAL_FORCE = 0
internal const val PLAYER_FORCE = 1
internal const val ENEMY_FORCE = 2

internal const val STEP_UNIT = 1.0
internal const val STEP_45_DEGREE_ANGLE = STEP_UNIT * 0.7071067812

private var idSequence: Long = 100000L

internal interface OUnit {
    var id: Long
    var name: String
}

internal interface SubjectUnit : OUnit {
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
    var force: Int
    var drawerId: Int

    val isDead: Boolean
        get() = deathTime > 0

    fun isDisappeared(tickTime: Long): Boolean {
        return isDead && tickTime - deathTime > deathDuration
    }

    fun isBody(tickTime: Long): Boolean {
        return keepBody && isDisappeared(tickTime)
    }
}

internal interface Living : SubjectUnit {
    var hp: Int
    var defense: Int
    var weapons: List<Weapon>
}

internal open class BaseUnit : OUnit {
    override var id: Long = idSequence++
    override var name: String = "$${javaClass.shortName}-$id"
}

internal data class Weapon(
    var holder: Living,
    var damage: Int = 50,
    var fireSpeed: Int = 50,
    var lastFireTime: Long = 0,
    var actorId: Int = 0,
) : BaseUnit()

internal data class Ammo(
    var weapon: Weapon,
    var createTime: Long = 0,
    var preparedTime: Long = 0,
    override var x: Double = 0.0,
    override var y: Double = 0.0,
    override var lastX: Double = x,
    override var lastY: Double = y,
    override var stepX: Double = 0.0,
    override var stepY: Double = 0.0,
    override var radius: Double = 12.0,
    override var moveSpeed: Int = 80,
    override var lastMoveTime: Long = 0,
    override var deathTime: Long = 0,
    override var deathDuration: Long = 5000,
    override var keepBody: Boolean = false,
    override var force: Int = NEUTRAL_FORCE,
    override var drawerId: Int = 0,
) : BaseUnit(), SubjectUnit {
}

internal data class Enemy(
    var score: Long = 10,
    override var x: Double = 0.0,
    override var y: Double = 0.0,
    override var lastX: Double = x,
    override var lastY: Double = y,
    override var stepX: Double = 0.0,
    override var stepY: Double = 0.0,
    override var radius: Double = 18.0,
    override var moveSpeed: Int = 50,
    override var lastMoveTime: Long = 0,
    override var deathTime: Long = 0,
    override var deathDuration: Long = 5000,
    override var keepBody: Boolean = false,
    override var force: Int = ENEMY_FORCE,
    override var drawerId: Int = 0,
    override var hp: Int = 50,
    override var defense: Int = 0,
    override var weapons: List<Weapon>,
) : BaseUnit(), Living

internal data class Player(
    var number: Int = 0,
    var hit: Long = 0,
    var score: Long = 10,
    override var x: Double = 0.0,
    override var y: Double = 0.0,
    override var lastX: Double = x,
    override var lastY: Double = y,
    override var stepX: Double = 0.0,
    override var stepY: Double = 0.0,
    override var radius: Double = 18.0,
    override var moveSpeed: Int = 90,
    override var lastMoveTime: Long = 0,
    override var deathTime: Long = 0,
    override var deathDuration: Long = 5000,
    override var keepBody: Boolean = true,
    override var force: Int = PLAYER_FORCE,
    override var drawerId: Int = 0,
    override var hp: Int = 100,
    override var defense: Int = 0,
    override var weapons: List<Weapon>,
) : BaseUnit(), Living