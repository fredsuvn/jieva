package xyz.srclab.common.egg.nest.o

import java.awt.Color

internal object OController {

    private var _data: OData? = null
    private var _scenario: OScenario? = null
    private var _engine: OEngine? = null

    val data: OData
        get() {
            return _data!!
        }
    private val scenario: OScenario
        get() {
            return _scenario!!
        }
    private val engine: OEngine
        get() {
            return _engine!!
        }

    private val keySet: MutableSet<Int> = HashSet()

    fun start() {
        _data = OData(
            createPlayer1(),
            createPlayer2(),
            createEnemyPlayer(),
        )
        _scenario = OScenario(data)
        _engine = OEngine(scenario, data, keySet)

        scenario.onStart()
        go()

        engine.start()
    }

    fun stop() {
        if (OTick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        OTick.stop()
        scenario.onStop()
    }

    fun go() {
        if (OTick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        OTick.go()
    }

    fun pause() {
        if (OTick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        OTick.pause()
    }

    fun toggle() {
        if (OTick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        if (OTick.isGoing) {
            OTick.pause()
        } else {
            OTick.go()
        }
    }

    fun moveLeft(player: Int) {
        engine.humanMove(getPlayer(player), OTick.time, -OConfig.xUnit, 0.0)
    }

    fun moveRight(player: Int) {
        engine.humanMove(getPlayer(player), OTick.time, OConfig.xUnit, 0.0)
    }

    fun moveUp(player: Int) {
        engine.humanMove(getPlayer(player), OTick.time, 0.0, -OConfig.yUnit)
    }

    fun moveDown(player: Int) {
        engine.humanMove(getPlayer(player), OTick.time, 0.0, OConfig.yUnit)
    }

    fun moveLeftUp(player: Int) {
        engine.humanMove(
            getPlayer(player),
            OTick.time,
            -OConfig.xUnit * STEP_45_DEGREE_ANGLE,
            -OConfig.yUnit * STEP_45_DEGREE_ANGLE
        )
    }

    fun moveRightUp(player: Int) {
        engine.humanMove(
            getPlayer(player),
            OTick.time,
            OConfig.xUnit * STEP_45_DEGREE_ANGLE,
            -OConfig.yUnit * STEP_45_DEGREE_ANGLE
        )
    }

    fun moveLeftDown(player: Int) {
        engine.humanMove(
            getPlayer(player),
            OTick.time,
            -OConfig.xUnit * STEP_45_DEGREE_ANGLE,
            OConfig.yUnit * STEP_45_DEGREE_ANGLE
        )
    }

    fun moveRightDown(player: Int) {
        engine.humanMove(
            getPlayer(player),
            OTick.time,
            OConfig.xUnit * STEP_45_DEGREE_ANGLE,
            OConfig.yUnit * STEP_45_DEGREE_ANGLE
        )
    }

    fun fire(player: Int) {
        val p = getPlayer(player)
        if (p.isDead) {
            return
        }
        engine.attack(p, OTick.time, p.x, 0.0)
    }

    fun pressKey(vk: Int) {
        synchronized(keySet) {
            keySet.add(vk)
        }
    }

    fun releaseKey(vk: Int) {
        synchronized(keySet) {
            keySet.remove(vk)
        }
    }

    private fun getPlayer(player: Int): OSubject {
        return if (player == 1) data.humanSubjects[0] else data.humanSubjects[1]
    }

    private fun createPlayer1(): OPlayer {
        return OPlayer(
            1,
            0,
            0,
            PLAYER_FORCE,
            Color.ORANGE,
            false,
        )
    }

    private fun createPlayer2(): OPlayer {
        return OPlayer(
            2,
            0,
            0,
            PLAYER_FORCE,
            Color.BLUE,
            false,
        )
    }

    private fun createEnemyPlayer(): OPlayer {
        return OPlayer(
            99,
            0,
            0,
            ENEMY_FORCE,
            Color.GRAY,
            false,
        )
    }
}