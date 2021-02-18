package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.base.Ref
import xyz.srclab.common.egg.Engine
import java.util.concurrent.CountDownLatch
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class OSpaceEngine(private val config: OSpaceConfig) : Engine<OSpaceScenario, OSpaceController, OSpaceData> {

    override fun load(scenario: OSpaceScenario): OSpaceController {
        return OSpaceControllerImpl(scenario, config)
    }
}

private class OSpaceControllerImpl(
    val scenario: OSpaceScenario,
    val config: OSpaceConfig
) : OSpaceController {

    private val tick = OSpaceTick(config)
    private val countDownRef = CountDownRef(Ref.of(null))
    private val playingThread = PlayingThread(config, scenario, countDownRef)

    init {
        countDownRef.reset()
        playingThread.start()
    }

    override fun startNew() {
        scenario.loadNew()
    }

    override fun go() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        tick.go()
        if (playingThread.isAlive) {
            countDownRef.get().countDown()
        } else {
            playingThread.start()
        }
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

    override fun save(): OSpaceData {
        return scenario.data
    }

    override fun load(data: OSpaceData) {
        scenario.load(data)
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
        return if (player == 1) scenario.data.player1!! else scenario.data.player2!!
    }

    private fun Player.move(currentTime: Long, stepX: Double, stepY: Double) {
        if (this.x < 0 || this.x > config.width || this.y < 0 || this.y > config.height) {
            return
        }
        (this as MovableUnit).move(currentTime, stepX, stepY)
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

    private fun Weapon.attack(currentTime: Long, targetX: Double, targetY: Double) {

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
            return
        }
        val ammos = this.ammoManager.newAmmos()
        for (ammo in ammos) {
            ammo.x = this.owner.x
            ammo.y = this.owner.y
            ammo.lastX = ammo.x
            ammo.lastY = ammo.y
            ammo.computeStep(targetX, targetY)
            this.ammoManager.ammos.add(ammo)
        }
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
        config: OSpaceConfig,
        private val scenario: OSpaceScenario,
        private val countDownRef: CountDownRef
    ) : Thread("PlayingThread") {

        private val tick = OSpaceTick(config)

        override fun run() {

            fun doWait() {
                if (!tick.isGoing) {
                    val countDown = countDownRef.get()
                    countDown.await()
                } else {
                    Current.sleep(tick.tickDuration)
                }
            }

            fun doClean(data: OSpaceData) {

                fun doClean0(iterable: MutableIterable<BodyUnit>) {
                    val iterator = iterable.iterator()
                    while (iterator.hasNext()) {
                        val it = iterator.next()
                        if (it.isDisappeared(tick.time, config)) {
                            iterator.remove()
                        }
                    }
                }

                for (weapon in data.player1!!.weapons) {
                    doClean0(weapon.ammoManager.ammos)
                }
                for (weapon in data.player2!!.weapons) {
                    doClean0(weapon.ammoManager.ammos)
                }
                for (enemy in data.enemies!!) {
                    for (weapon in enemy.weapons) {
                        doClean0(weapon.ammoManager.ammos)
                    }
                }
                doClean0(data.enemies!!)
            }

            fun doPlayerAmmos(data: OSpaceData) {

                fun doPlayerAmmos0(player: Player) {
                    for (weapon in player.weapons) {
                        for (ammo in weapon.ammoManager.ammos) {
                            if (ammo.isDead) {
                                continue
                            }
                            for (enemy in data.enemies!!) {
                                if (!enemy.isDead && distance(ammo, enemy) < ammo.radius + enemy.radius) {
                                    scenario.onHitEnemy(ammo, weapon.ammoManager, enemy, tick)
                                }
                            }
                            if (!ammo.isDead) {
                                ammo.move(tick.time, ammo.stepX, ammo.stepY)
                            }
                        }
                    }
                }

                doPlayerAmmos0(data.player1!!)
                doPlayerAmmos0(data.player2!!)
            }

            fun doEnemiesAmmos(data: OSpaceData) {
                for (enemy in data.enemies!!) {
                    for (weapon in enemy.weapons) {
                        for (ammo in weapon.ammoManager.ammos) {
                            if (!ammo.isDead && distance(
                                    ammo,
                                    data.player1!!
                                ) < ammo.radius + data.player1!!.radius
                            ) {
                                scenario.onHitPlayer(ammo, weapon.ammoManager, data.player1!!, tick)
                            }
                            if (!ammo.isDead && distance(
                                    ammo,
                                    data.player2!!
                                ) < ammo.radius + data.player2!!.radius
                            ) {
                                scenario.onHitPlayer(ammo, weapon.ammoManager, data.player2!!, tick)
                            }
                            if (!ammo.isDead) {
                                ammo.move(tick.time, ammo.stepX, ammo.stepY)
                            }
                        }
                    }
                }
            }

            fun doEnemiesFire(data: OSpaceData) {

                fun pickPlayer(): Player {
                    if (data.player1!!.isDead && !data.player2!!.isDead) {
                        return data.player2!!
                    }
                    if (data.player2!!.isDead && !data.player1!!.isDead) {
                        return data.player1!!
                    }
                    return if (Random.nextBoolean()) data.player1!! else data.player2!!
                }

                for (enemy in data.enemies!!) {
                    for (weapon in enemy.weapons) {
                        val player = pickPlayer()
                        weapon.attack(tick.time, player.x, player.y)
                    }
                }
            }

            fun doEnemiesMoving(data: OSpaceData) {
                for (enemy in data.enemies!!) {
                    enemy.move(tick.time, enemy.stepX, enemy.stepY)
                }
            }

            while (!tick.isStop) {
                //Wait if pause
                doWait()

                val data = scenario.data

                //Clean
                doClean(data)

                //Do ammos
                doPlayerAmmos(data)
                doEnemiesAmmos(data)

                //Do enemies fire
                doEnemiesFire(data)

                //Do enemies moving
                doEnemiesMoving(data)

                //Tick
                scenario.onTick(tick)
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