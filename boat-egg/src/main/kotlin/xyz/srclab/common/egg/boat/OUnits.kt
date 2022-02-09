package xyz.srclab.common.egg.boat

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

open class OUnit : Serializable {

    var id: Long = 0
    var name: String = ""
    var x: Double = 0.0
    var y: Double = 0.0
    var radius: Double = 0.0
    var state: Int = STATE_DEAD

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
    var fireCoolDownTick: Long = 800
    var lastFireTick: Long = -1
    var subFireCoolDownTick: Long = 800
    var lastSubFireTick: Long = -1
    lateinit var tank: OTank

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

open class OTank : OMovable(), Serializable {

    var target: OTank? = null
    var weapons: Array<OWeapon> = emptyArray()
    lateinit var player: OPlayer

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

open class OBullet : OMovable(), Serializable {

    var damage: Int = 1
    var stepX: Double = 0.0
    var stepY: Double = 0.0
    lateinit var weapon: OWeapon

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

    var operateCoolDownTick: Long = 10
    var lastOperateTick: Long = -1

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION

        const val NUMBER_P1 = 1
        const val NUMBER_P2 = 2
        const val NUMBER_P3 = 3
        const val NUMBER_P4 = 4
        const val NUMBER_BOT_UP = 5
        const val NUMBER_BOT_DOWN = 6

        const val STATE_ALIVE = 1
        const val STATE_READY = 0
        const val STATE_DEFEAT = -1

        const val ORDER_MOVE_UP = 1
        const val ORDER_MOVE_DOWN = 2
        const val ORDER_MOVE_LEFT = 3
        const val ORDER_MOVE_RIGHT = 4
        const val ORDER_MOVE_UP_LEFT = 5
        const val ORDER_MOVE_UP_RIGHT = 6
        const val ORDER_MOVE_DOWN_LEFT = 7
        const val ORDER_MOVE_DOWN_RIGHT = 8
        const val ORDER_FIRE = 9
    }
}