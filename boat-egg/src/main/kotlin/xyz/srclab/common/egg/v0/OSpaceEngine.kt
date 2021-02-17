package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.base.Ref
import xyz.srclab.common.egg.Engine
import java.util.concurrent.CountDownLatch
import kotlin.math.pow
import kotlin.math.sqrt

class OSpaceEngine(private val config: OSpaceConfig) : Engine<OSpaceScenario, OSpacePlaying> {

    override fun load(scenario: OSpaceScenario): OSpacePlaying {
        return OSpacePlayingImpl(scenario, config)
    }
}

private class OSpacePlayingImpl(
    val scenario: OSpaceScenario,
    val config: OSpaceConfig
) : OSpacePlaying {

    private val tick = OSpaceTick(config)
    private val countDownRef = CountDownRef(Ref.of<CountDownLatch>(null))
    private val playingThread = PlayingThread(config, scenario, countDownRef)

    init {
        countDownRef.reset()
        playingThread.start()
    }

    override fun moveLeft(player: Int) {
        getPlayer(player).move(tick.time, -config.xUnit, 0.0)
    }

    override fun moveRight(player: Int) {
        getPlayer(player).move(tick.time, config.xUnit, 0.0)
    }

    override fun moveUp(player: Int) {
        getPlayer(player).move(tick.time, 0.0, -config.yUnit)
    }

    override fun moveDown(player: Int) {
        getPlayer(player).move(tick.time, 0.0, config.yUnit)
    }

    override fun moveLeftUp(player: Int) {
        getPlayer(player).move(
            tick.time,
            -config.xUnit * RIGHT_ANGLE_COEFFICIENT,
            -config.yUnit * RIGHT_ANGLE_COEFFICIENT
        )
    }

    override fun moveRightUp(player: Int) {
        getPlayer(player).move(
            tick.time,
            config.xUnit * RIGHT_ANGLE_COEFFICIENT,
            -config.yUnit * RIGHT_ANGLE_COEFFICIENT
        )
    }

    override fun moveLeftDown(player: Int) {
        getPlayer(player).move(
            tick.time,
            -config.xUnit * RIGHT_ANGLE_COEFFICIENT,
            config.yUnit * RIGHT_ANGLE_COEFFICIENT
        )
    }

    override fun moveRightDown(player: Int) {
        getPlayer(player).move(
            tick.time,
            config.xUnit * RIGHT_ANGLE_COEFFICIENT,
            config.yUnit * RIGHT_ANGLE_COEFFICIENT
        )
    }

    override fun fire(player: Int) {
        val p = getPlayer(player)
        for (weapon in p.weapons) {
            weapon.attack(tick.time, p.x, 0.0)
        }
    }

    private fun getPlayer(player: Int): Player {
        return if (player == 1) scenario.player1 else scenario.player2
    }

    override fun go() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        tick.go()
        countDownRef.get().countDown()
    }

    override fun pause() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        countDownRef.reset()
        tick.pause()
    }

    override fun stop() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        tick.stop()
    }

    override fun save(path: CharSequence) {

    }

    override fun load(path: CharSequence) {
        TODO("Not yet implemented")
    }

    private fun MovableUnit.move(currentTime: Long, stepX: Double, stepY: Double) {
        if (currentTime - this.lastMoveTime < this.speed.moveSpeedToCoolDown()) {
            return
        }
        this.lastX = this.x
        this.lastY = this.y
        this.x += stepX
        this.y += stepY
        this.lastMoveTime = currentTime
    }

    private fun Weapon.attack(currentTime: Long, targetX: Double, targetY: Double): Ammo? {

        fun AutoMovableUnit.computeStep(targetX: Double, targetY: Double) {
            if (this.x == targetX) {
                this.stepX = 0.0
                this.stepY = if (targetY > this.y) config.yUnit else if (targetY < this.y) -config.yUnit else 0.0
                return
            }
            if (this.y == targetY) {
                this.stepX = if (targetX > this.x) config.xUnit else if (targetX < this.x) -config.xUnit else 0.0
                this.stepY = 0.0
                return
            }
            val distance = distance(this.x, this.y, targetX, targetY)
            this.stepX = (targetX - this.x) * config.xUnit / distance
            this.stepY = (targetY - this.y) * config.yUnit / distance
        }

        if (currentTime - this.lastFireTime < this.fireSpeed.fireSpeedToCoolDown()) {
            return null
        }
        val ammo = this.ammoManager.newAmmo()
        ammo.computeStep(targetX, targetY)
        this.ammoManager.ammos.add(ammo)
        return ammo
    }

    private fun distance(a: SizeUnit, b: SizeUnit): Double {
        return distance(a.x, a.y, b.x, b.y)
    }

    private fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        if (x1 == x2) {
            return y2 - y1
        }
        if (y1 == y2) {
            return x2 - x1
        }
        return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
    }

    private inner class PlayingThread(
        private val config: OSpaceConfig,
        private val scenario: OSpaceScenario,
        private val countDownRef: CountDownRef
    ) : Thread("PlayingThread") {

        private val tick = OSpaceTick(config)

        override fun run() {

            fun doPlayerAmmos(player: Player) {
                for (weapon in player.weapons) {
                    for (ammo in weapon.ammoManager.ammos) {
                        if (ammo.isDead) {
                            continue
                        }
                        for (enemy in scenario.enemies) {
                            if (!enemy.isDead && distance(ammo, enemy) < ammo.size + enemy.size) {
                                scenario.onHitEnemy(ammo, weapon.ammoManager, enemy)
                            }
                        }
                        if (!ammo.isDead) {
                            ammo.move(tick.time, ammo.stepX, ammo.stepY)
                        }
                    }
                }
            }

            fun doEnemyAmmos() {
                for (enemy in scenario.enemies) {
                    for (weapon in enemy.weapons) {
                        for (ammo in weapon.ammoManager.ammos) {
                            if (!ammo.isDead && distance(ammo, scenario.player1) < ammo.size + scenario.player1.size) {
                                scenario.onHitPlayer(ammo, weapon.ammoManager, scenario.player1)
                            }
                            if (!ammo.isDead && distance(ammo, scenario.player2) < ammo.size + scenario.player2.size) {
                                scenario.onHitPlayer(ammo, weapon.ammoManager, scenario.player2)
                            }
                            if (!ammo.isDead) {
                                ammo.move(tick.time, ammo.stepX, ammo.stepY)
                            }
                        }
                    }
                }
            }

            while (!tick.isStop) {
                //Wait if pause
                if (!tick.isGoing) {
                    val countDown = countDownRef.get()
                    countDown.await()
                } else {
                    Current.sleep(config.tickUnit)
                }

                //Do ammos
                doPlayerAmmos(scenario.player1)
                doPlayerAmmos(scenario.player2)
                doEnemyAmmos()

                //Do enemy-move
                for (enemy in scenario.enemies) {
                    enemy.move(tick.time, enemy.stepX, enemy.stepY)
                }

                tick.tick()
            }
        }
    }

    private class CountDownRef(private val ref: Ref<CountDownLatch>) : Ref<CountDownLatch> by ref {

        fun reset() {
            ref.set(CountDownLatch(1))
        }
    }

    companion object {

        private const val RIGHT_ANGLE_COEFFICIENT = 0.70711
    }
}