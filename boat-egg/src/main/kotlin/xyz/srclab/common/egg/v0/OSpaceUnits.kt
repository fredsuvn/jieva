package xyz.srclab.common.egg.v0

import java.awt.Component

interface Player : Living {

    val number: Int

    var hit: Long

    var score: Long
}

interface Enemy : Living, AutoMovableUnit {

    var score: Long
}

interface Living : OUnit, ForceUnit, BodyUnit, MovableUnit {

    var hp: Int

    var defense: Int

    val weapons: List<Weapon>
}

interface Weapon : OUnit {

    val damage: Int

    val fireSpeed: Int

    var lastFireTime: Long

    val owner: Living

    val ammoManager: AmmoManager
}

interface AmmoManager : ForceUnit {

    val weapon: Weapon

    val ammos: MutableList<Ammo>

    fun newAmmos(): List<Ammo>
}

interface Ammo : BodyUnit, AutoMovableUnit

interface BodyUnit : CanDieUnit, SizeUnit, DisplayableUnit {

    fun isOutOfBounds(config: OSpaceConfig): Boolean {
        return this.x < -this.radius
                || this.x > config.width + this.radius
                || this.y > config.height + this.radius
                || this.y < -config.preparedHeight - this.radius
    }

    fun isDisappeared(currentTime: Long, config: OSpaceConfig): Boolean {
        return (isDead && (currentTime - deathTime) > deathDuration)
                || isOutOfBounds(config)
    }
}

interface DisplayableUnit {

    fun display(component: Component, time: Long, faceX: Int, faceY: Int)
}

interface AutoMovableUnit : MovableUnit {

    var stepX: Double

    var stepY: Double
}

interface MovableUnit : PointUnit {

    val speed: Int

    var lastMoveTime: Long

    var lastX: Double

    var lastY: Double
}

interface SizeUnit : PointUnit {

    val radius: Double
}

interface PointUnit {

    var x: Double

    var y: Double
}

interface CanDieUnit {

    var deathTime: Long

    val deathDuration: Long

    val isDead: Boolean
        get() = deathTime > 0
}

interface ForceUnit {

    val force: Int
}

interface OUnit {

    val type: String

    val name: String
}

const val FORCE_PLAYER = 1
const val FORCE_ENEMY = 2