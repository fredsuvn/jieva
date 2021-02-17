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

interface Living : OUnit, ForceUnit, CanDieUnit, SizeUnit, MovableUnit, DisplayableUnit {

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

    fun newAmmo(): Ammo
}

interface Ammo : CanDieUnit, SizeUnit, AutoMovableUnit, DisplayableUnit

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

    val size: Int
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

    fun isDisappeared(currentTime: Long): Boolean {
        return isDead && (currentTime - deathTime) > deathDuration
    }
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