package xyz.srclab.common.egg.boat

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable
import java.util.*

const val STATE_ALIVE = 1
const val STATE_DEAD = 0
const val CLEAR = -1

const val PLAYER_STATE_ALIVE = 1
const val PLAYER_STATE_READY = 0
const val PLAYER_STATE_DEFEAT = -1

open class OUnit : Serializable {

    var id: Long = 0
    var name: String = ""
    var x: Double = 0.0
    var y: Double = 0.0
    var radius: Double = 0.0
    var player: Int = 0
    var state: Int = STATE_DEAD
    var force: Int = 0

    var hp: Int = 100
    var deathTick: Long = -1
    var deathDuration: Long = 2000

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

open class OMovable : OUnit(), Serializable {

    var stepX: Double = 0.0
    var stepY: Double = 0.0

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

open class OWeapon : OUnit(), Serializable {

    var damage: Int = 1
    var coolDownTick: Long = 800
    var lastFileTick: Long = -1
    var subCoolDownTick: Long = 800
    var subLastFileTick: Long = -1
    var bullets: MutableList<OBullet> = LinkedList()

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
        val EMPTY = OWeapon()
    }
}

open class OTank : OMovable(), Serializable {

    var weapons: Array<OWeapon> = emptyArray()

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

open class OBullet : OMovable(), Serializable {

    var damage: Int = 1
    var weapon: OWeapon = OWeapon.EMPTY

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

open class OPlayer : Serializable {

    var number: Int = 0
    var force: Int = 0
    var score: Long = 0
    var state: Int = PLAYER_STATE_READY
    var startTime: Long = -1
    var endTime: Long = -1
    var endTick: Long = -1
    var directionX: Double = 0.0
    var directionY: Double = 0.0
    var tanks: MutableList<OTank> = LinkedList()

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}