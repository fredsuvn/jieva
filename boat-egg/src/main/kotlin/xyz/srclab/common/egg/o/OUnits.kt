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

    fun death(): Boolean {
        return this.deathTime >= 0
    }

    fun outOfDeath(now: Long): Boolean {
        return now - this.deathTime > this.deathKeepTime
    }

    fun outOfView(config: OConfig): Boolean {
        if (
            this.x < -config.viewWidthBuffer - this.radius
            || this.x > config.viewWidth + config.viewWidthBuffer + this.radius
            || this.y < -config.viewHeightBuffer - this.radius
            || this.y > config.viewHeight + config.viewHeightBuffer + this.radius
        ) {
            return true
        }
        return false
    }

    fun hit(o: Touchable): Boolean {
        val distance = distance(this, o)
        return distance < this.radius + o.radius
    }

    fun moveCoolDown(): Long {
        return (100 - speed) * 1L
    }

    fun readMove(now: Long): Boolean {
        if (now == lastMoveTime) {
            return false
        }
        return now - lastMoveTime > moveCoolDown()
    }
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
    var recovery: Int = 10
    var recoverySpeed: Int = 50
    var lastRecoveryTime: Long = -1

    fun recoveryCoolDown(): Long {
        return (100 - recoverySpeed) * 90L
    }

    fun readyRecovery(now: Long): Boolean {
        return now - lastRecoveryTime > recoveryCoolDown()
    }
}

open class Weapon {

    var id: Long = 0
    var type: Int = 0
    var fireSpeed: Long = 50
    var lastFireTime: Long = -1
    var fireKeepTime: Long = 0
    var lastKeepTime: Long = -1
    var damage: Int = 50
    var bullets: MutableList<Bullet>? = null

    fun fireCoolDown(): Long {
        return (100 - fireSpeed) * 20
    }

    fun readyOrKeepFire(now: Long): Boolean {
        if (now == lastFireTime) {
            return false
        }
        if (now > lastFireTime && now <= lastFireTime + fireKeepTime) {
            return true
        }
        return now - lastFireTime > fireCoolDown()
    }
}

const val COMPUTER_ENEMY_OWNER = 100
const val PLAYER_1_OWNER = 200
const val PLAYER_2_OWNER = 300
const val PLAYER_3_OWNER = 400
const val PLAYER_4_OWNER = 500

const val COMPUTER_ENEMY_GROUP = 100
const val PLAYER_GROUP = 200