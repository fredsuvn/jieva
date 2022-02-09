package xyz.srclab.common.egg.boat

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

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

        const val STATE_ALIVE = 1
        const val STATE_DEAD = 0
        const val STATE_CLEAR = -1
    }
}

open class OMovable : OUnit(), Serializable {

    var moveCollDownTick: Long = 5
    var lastMoveTick: Long = -1

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

open class OWeapon : OUnit(), Serializable {

    var type: Int = 1
    var damage: Int = 1
    var coolDownTick: Long = 800
    var lastFileTick: Long = -1
    var subCoolDownTick: Long = 800
    var subLastFileTick: Long = -1
    var tank: OTank = OTank.EMPTY

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
        val EMPTY = OWeapon()
    }
}

open class OTank : OMovable(), Serializable {

    var weapons: Array<OWeapon> = emptyArray()

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
        val EMPTY = OTank()
    }
}

open class OBullet : OMovable(), Serializable {

    var damage: Int = 1
    var weapon: OWeapon = OWeapon.EMPTY
    var target: OUnit? = null
    var targetX: Double = 0.0
    var targetY: Double = 0.0

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

open class OPlayer : Serializable {

    var number: Int = 0
    var force: Int = 0
    var score: Long = 0
    var isBot: Boolean = false
    var state: Int = STATE_READY
    var startTime: Long = -1
    var endTime: Long = -1
    var endTick: Long = -1
    var direction: Int = DIRECTION_DOWN

    var operateCoolDownTick: Long = 10
    var lastOperateTick: Long = -1
    var operation: Int = 0

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION

        const val STATE_ALIVE = 1
        const val STATE_READY = 0
        const val STATE_DEFEAT = -1

        const val DIRECTION_UP = 1
        const val DIRECTION_DOWN = 2

        const val OPERATION_MOVE_UP = 1
        const val OPERATION_MOVE_DOWN = 2
        const val OPERATION_MOVE_LEFT = 3
        const val OPERATION_MOVE_RIGHT = 4
        const val OPERATION_MOVE_UP_LEFT = 5
        const val OPERATION_MOVE_UP_RIGHT = 6
        const val OPERATION_MOVE_DOWN_LEFT = 7
        const val OPERATION_MOVE_DOWN_RIGHT = 8
        const val OPERATION_FIRE = 9
    }
}