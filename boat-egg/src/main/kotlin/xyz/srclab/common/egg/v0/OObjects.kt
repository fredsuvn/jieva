package xyz.srclab.common.egg.v0

import java.awt.Graphics
import java.awt.Rectangle

interface Player : Living {

    var hit: Long

    var score: Long
}

interface Enemy : Living, AutoMovable {

    var score: Long
}

interface Living : Movable {

    var hp: Int

    var defense: Int

    val weapons: List<Weapon>
}

interface Weapon : OObject {

    var damage: Int

    var lastFireTime: Long

    var coolDownTime: Long

    val owner: Living

    fun attack(): Ammo
}

interface Ammo : AutoMovable {

    var damage: Int

    val weapon: Weapon
}

interface AutoMovable : Movable {

    var stepX: Double

    var stepY: Double
}

interface Movable : TangibleObject {

    var speed: Int

    var lastMoveTime: Long

    val moveCoolDownTime: Long
        get() = if (speed > 100) 0L else (100 - speed).toLong()
}

interface TangibleObject : OObject {

    var x: Double

    var y: Double

    val radius: Int

    var deadTime: Long

    val deadKeepTime: Long

    val isDead: Boolean
        get() = deadTime > 0

    fun isDisappeared(now: Long): Boolean {
        return isDead && (now - deadTime) > deadKeepTime
    }

    fun show(graphics: Graphics, x: Int, y: Int): Rectangle
}

interface OObject {

    val type: String

    var name: String

    var force: Force
}

enum class Force {
    PLAYER, ENEMY, NEUTRAL
}