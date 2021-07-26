package xyz.srclab.common.egg.o

open class OUnits {
    var id: Long = 0
    var owner: Long = 0
    var group: Int = 0
}

open class Touchable : OUnits() {
    var x: Double = 0.0
    var y: Double = 0.0
    var radius: Double = 0.0
    var stepX: Double = 0.0
    var stepY: Double = 0.0
    var deathTime: Long = -1
    var deathKeepTime: Long = 2000
    var speed: Int = 50
    var lastMoveTime: Long = -1
}

open class Bullet : Touchable() {
    var damage: Int = 50
}

open class Soldier : Touchable() {
    var hp: Int = 100
    var defense: Int = 0
    var reward: Int = 0
    var weapons: MutableList<Weapon>? = null
}

open class Player : Soldier() {
    var username: String? = null
    var score: Long = 0
}

open class Weapon {
    var id: Long = 0
    var type: Int = 0
    var fireSpeed: Long = 50
    var lastFireTime: Long = -1
    var damage: Int = 50
    var bullets: MutableList<Bullet>? = null
}

const val COMPUTER_ENEMY_OWNER = 100
const val PLAYER_1_OWNER = 200
const val PLAYER_2_OWNER = 300
const val PLAYER_3_OWNER = 400
const val PLAYER_4_OWNER = 500

const val COMPUTER_ENEMY_GROUP = 100
const val PLAYER_GROUP = 200

const val ENEMY_COMMON_WEAPON = 100
const val ENEMY_FLOWER_WEAPON = 200
const val ENEMY_SHORT_WEAPON = 300
const val PLAYER_COMMON_WEAPON = 400