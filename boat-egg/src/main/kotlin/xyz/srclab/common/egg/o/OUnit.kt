package xyz.srclab.common.egg.o

open class OUnit {

    var id: Long = 0
    var type: Int = 0
    var owner: Int = 0
    var force: Int = 0
    var state: Int = 0

    companion object {
        const val ALIVE_STATE = 1
        const val DEATH_STATE = 2
        const val OUT_STATE = 3
    }
}

open class Touchable : OUnit() {

    var x: Double = 0.0
    var y: Double = 0.0
    var radius: Double = 0.0
    var stepX: Double = 0.0
    var stepY: Double = 0.0
    var deathTime: Long = -1
    var deathKeepTime: Long = 2000
    var speed: Int = 50
    var lastMoveTime: Long = -1
    var clear: Boolean = false

    fun death(): Boolean {
        return this.deathTime >= 0
    }

    fun outOfDeath(now: Long): Boolean {
        return now - this.deathTime > this.deathKeepTime
    }

    fun outOfView(config: OConfig): Boolean {
        return (
            this.x < -config.viewWidthBuffer - this.radius
                || this.x > config.viewWidth + config.viewWidthBuffer + this.radius
                || this.y < -config.viewHeightBuffer - this.radius
                || this.y > config.viewHeight + config.viewHeightBuffer + this.radius
            )
    }

    fun hit(o: Touchable): Boolean {
        val distance = distance(this, o)
        return distance < this.radius + o.radius
    }

    private fun moveCoolDown(): Long {
        return (100 - speed) * 1L
    }

    fun readyMove(now: Long): Boolean {
        if (now == lastMoveTime) {
            return false
        }
        return now - lastMoveTime > moveCoolDown()
    }
}

open class Soldier : Touchable() {

    var totalHp: Int = 100
    var hp: Int = 100
    var defense: Int = 0
    var reward: Int = 0
    var weapons: MutableList<Weapon>? = null

    var recovery: Int = 10
    var recoverySpeed: Int = 50
    var lastRecoveryTime: Long = -1

    private fun recoveryCoolDown(): Long {
        return (100 - recoverySpeed) * 90L
    }

    fun readyRecovery(now: Long): Boolean {
        return now - lastRecoveryTime > recoveryCoolDown()
    }
}

open class Player : Soldier() {
    var username: String? = null
    var nickname: String? = null
    var score: Long = 0
}

open class Weapon : OUnit() {

    var fireSpeed: Long = 50
    var lastFireTime: Long = -1
    var damage: Int = 50
    var bullets: MutableList<Bullet>? = null

    private fun fireCoolDown(): Long {
        return (100 - fireSpeed) * 20
    }

    fun readyFire(now: Long): Boolean {
        if (now == lastFireTime) {
            return false
        }
        return now - lastFireTime > fireCoolDown()
    }
}

open class Bullet : Touchable() {
    var damage: Int = 50
}