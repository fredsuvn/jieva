package xyz.srclab.common.egg.v0

import xyz.srclab.common.reflect.shortName

const val FORCE_PLAYER = 1
const val FORCE_ENEMY = 2
const val FORCE_NEUTRAL = 3

interface OUnit {

    var id: Int

    var type: String

    var name: String
}

interface SubjectUnit : OUnit {

    var x: Double

    var y: Double

    var radius: Double

    var lastX: Double

    var lastY: Double

    var moveSpeed: Int

    var lastMoveTime: Long

    var force: Int

    var deathTime: Long

    var deathDuration: Long

    var graphicsId: Int
}

interface AutoMovable {

    var stepX: Double

    var stepY: Double
}

interface Living : SubjectUnit {

    var hp: Int

    var defense: Int

    var weaponsId: List<Int>
}

open class BaseUnit : OUnit {

    override var id: Int = unitIdSeq++
    override var type: String = this.javaClass.shortName
    override var name: String = "$type-$id"

    companion object {
        private var unitIdSeq = 0
    }
}

class Weapon : BaseUnit() {
    var damage: Int = 0
    var fireSpeed: Int = 0
    var lastFireTime: Long = 0
    var ownerId: Long = 0
}

class Ammo : BaseUnit(), SubjectUnit, AutoMovable {
    var weaponId: Int = 0
    override var x: Double = 0.0
    override var y: Double = 0.0
    override var radius: Double = 0.0
    override var lastX: Double = 0.0
    override var lastY: Double = 0.0
    override var moveSpeed: Int = 0
    override var lastMoveTime: Long = 0
    override var force: Int = 0
    override var deathTime: Long = 0
    override var deathDuration: Long = 0
    override var graphicsId: Int = 0
    override var stepX: Double = 0.0
    override var stepY: Double = 0.0
}

class Enemy : BaseUnit(), Living, AutoMovable {
    var score: Long = 0
    override var hp: Int = 0
    override var defense: Int = 0
    override var weaponsId: List<Int> = emptyList()
    override var x: Double = 0.0
    override var y: Double = 0.0
    override var radius: Double = 0.0
    override var lastX: Double = 0.0
    override var lastY: Double = 0.0
    override var moveSpeed: Int = 0
    override var lastMoveTime: Long = 0
    override var force: Int = 0
    override var deathTime: Long = 0
    override var deathDuration: Long = 0
    override var graphicsId: Int = 0
    override var stepX: Double = 0.0
    override var stepY: Double = 0.0
}

class Player : BaseUnit(), Living {
    var number: Int = 0
    var hit: Long = 0
    var score: Long = 0
    override var hp: Int = 0
    override var defense: Int = 0
    override var weaponsId: List<Int> = emptyList()
    override var x: Double = 0.0
    override var y: Double = 0.0
    override var radius: Double = 0.0
    override var lastX: Double = 0.0
    override var lastY: Double = 0.0
    override var moveSpeed: Int = 0
    override var lastMoveTime: Long = 0
    override var force: Int = 0
    override var deathTime: Long = 0
    override var deathDuration: Long = 0
    override var graphicsId: Int = 0
}