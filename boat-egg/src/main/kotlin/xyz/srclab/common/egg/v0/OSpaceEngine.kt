package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.egg.sample.Engine
import java.util.*
import kotlin.random.Random

internal class OSpaceEngine(private val config: OSpaceConfig) : Engine<OSpaceController, OSpaceData, OSpaceScenario> {

    override fun loadNew(): OSpaceController {
        return load(newOSpaceData())
    }

    override fun load(data: OSpaceData): OSpaceController {
        return OSpaceControllerImpl(data, config)
    }

    private fun newOSpaceData(): OSpaceData {
        return OSpaceData()
    }
}

private class OSpaceControllerImpl(
    override val data: OSpaceData,
    override val config: OSpaceConfig
) : OSpaceController {

    override val tick = OSpaceTick(config)

    private val scenario: OSpaceScenario = data.scenario
    private val playingThread = PlayingThread()

    override fun start() {
        playingThread.start()
        go()
        scenario.onStart(data, this)
    }

    override fun stop() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        tick.stop()
        scenario.onEnd(data, this)
    }

    override fun go() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        tick.go()
        if (!playingThread.isAlive) {
            playingThread.start()
        }
    }

    override fun pause() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        tick.pause()
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
            -config.xUnit * STEP_45_DEGREE_ANGLE,
            -config.yUnit * STEP_45_DEGREE_ANGLE
        )
    }

    override fun moveRightUp(player: Int) {
        getPlayer(player).move(
            tick.time,
            config.xUnit * STEP_45_DEGREE_ANGLE,
            -config.yUnit * STEP_45_DEGREE_ANGLE
        )
    }

    override fun moveLeftDown(player: Int) {
        getPlayer(player).move(
            tick.time,
            -config.xUnit * STEP_45_DEGREE_ANGLE,
            config.yUnit * STEP_45_DEGREE_ANGLE
        )
    }

    override fun moveRightDown(player: Int) {
        getPlayer(player).move(
            tick.time,
            config.xUnit * STEP_45_DEGREE_ANGLE,
            config.yUnit * STEP_45_DEGREE_ANGLE
        )
    }

    override fun fire(player: Int) {
        val p = getPlayer(player)
        p.attack(tick.time, p.x, 0.0)
    }

    private fun Player.move(currentTime: Long, stepX: Double, stepY: Double) {
        if (this.x < 0 || this.x > config.width || this.y < 0 || this.y > config.height) {
            return
        }
        (this as SubjectUnit).move(currentTime, stepX, stepY)
    }

    private fun SubjectUnit.move(tickTime: Long, stepX: Double, stepY: Double) {
        if (tickTime - this.lastMoveTime < this.moveSpeed.moveSpeedToCoolDown()) {
            return
        }
        this.lastX = this.x
        this.lastY = this.y
        this.x += stepX
        this.y += stepY
        this.lastMoveTime = tickTime
    }

    private fun Living.attack(target: Living, tickTime: Long) {
        for (weapon in this.weapons) {
            weapon.attack(this, target, tickTime)
        }
    }

    private fun Weapon.attack(attacker: Living, target: Living, tickTime: Long) {
        attack(attacker, tickTime, target.x, target.y)
    }

    private fun Living.attack(tickTime: Long, targetX: Double, targetY: Double) {
        for (weapon in this.weapons) {
            weapon.attack(this, tickTime, targetX, targetY)
        }
    }

    private fun Weapon.attack(attacker: Living, tickTime: Long, targetX: Double, targetY: Double) {
        if (tickTime - this.lastFireTime < this.fireSpeed.fireSpeedToCoolDown()) {
            return
        }
        val ammoMetas = scenario.onWeaponAct(this, targetX, targetY).fire(attacker, tickTime, targetX, targetY)
        for (ammoMeta in ammoMetas) {
            val ammo = Ammo(
                this,
                attacker.x,
                attacker.y,
                attacker.x,
                attacker.y,
                ammoMeta.xStepUnit * config.xUnit,
                ammoMeta.yStepUnit * config.yUnit,
                ammoMeta.radius,
                ammoMeta.moveSpeed,
                0,
                0,
                ammoMeta.deathDuration,
                this.holder.force,
                ammoMeta.drawerId
            )
            if (attacker.force == PLAYER_FORCE) {
                data.playersAmmos.add(ammo)
            }
            if (attacker.force == ENEMY_FORCE) {
                data.enemiesAmmos.add(ammo)
            }
        }
        this.lastFireTime = tick.time
    }

    private fun Ammo.hit(living: Living) {
        val damage = this.weapon.damage - living.defense
        living.hp -= damage
        if (living.hp <= 0) {
            living.deathTime = tick.time
        }
        this.deathTime = tick.time
    }

    private fun SubjectUnit.isOutOfBounds(): Boolean {
        val length = this.radius * 2
        return this.x < -length
                || this.x > config.width + length
                || this.y < -config.preparedHeight
                || this.y > config.height + length
    }

    private fun SubjectUnit.distance(other: SubjectUnit): Double {
        return distance(this.x, this.y, other.x, other.y)
    }

    private fun getPlayer(player: Int): Player {
        return if (player == 1) data.player1 else data.player2
    }

    private fun pickPlayer(): Player {
        if (data.player1.isDead && !data.player2.isDead) {
            return data.player2
        }
        if (data.player2.isDead && !data.player1.isDead) {
            return data.player1
        }
        return if (Random.nextBoolean()) data.player1 else data.player2
    }

    private inner class PlayingThread : Thread("PlayingThread") {

        override fun run() {

            fun doWait() {
                if (!tick.isGoing) {
                    tick.awaitToGo()
                } else {
                    Current.sleep(tick.tickDuration)
                }
            }

            fun doClean() {

                fun MutableIterable<Ammo>.cleanAmmos() {
                    val iterator = this.iterator()
                    while (iterator.hasNext()) {
                        val ammo = iterator.next()
                        if (ammo.isOutOfBounds() || ammo.isDisappeared(tick.time)) {
                            OSpaceLogger.debug("Ammo cleaned: {}", ammo.id)
                            iterator.remove()
                        }
                    }
                }

                fun MutableIterable<Living>.cleanLivings() {
                    val iterator = this.iterator()
                    while (iterator.hasNext()) {
                        val living = iterator.next()
                        if (living.isOutOfBounds() || living.isDisappeared(tick.time)) {
                            OSpaceLogger.debug("Living cleaned: {}-{}", living.javaClass.typeName, living.id)
                            iterator.remove()
                        }
                    }
                }

                data.enemiesAmmos.cleanAmmos()
                data.playersAmmos.cleanAmmos()
                data.enemies.cleanLivings()
                //data.players.cleanLivings()
            }

            fun doHit() {
                for (playersAmmo in data.playersAmmos) {
                    if (playersAmmo.isDead) {
                        continue
                    }
                    for (enemy in data.enemies) {
                        if (enemy.isDead) {
                            continue
                        }
                        if (playersAmmo.distance(enemy) < playersAmmo.radius + enemy.radius) {
                            playersAmmo.hit(enemy)
                            scenario.onHitEnemy(playersAmmo, enemy, data, this@OSpaceControllerImpl)
                        }
                    }
                }
                for (enemiesAmmo in data.enemiesAmmos) {
                    if (enemiesAmmo.isDead) {
                        continue
                    }
                    for (player in data.players) {
                        if (player.isDead) {
                            continue
                        }
                        if (enemiesAmmo.distance(player) < enemiesAmmo.radius + player.radius) {
                            enemiesAmmo.hit(player)
                            scenario.onHitPlayer(enemiesAmmo, player, data, this@OSpaceControllerImpl)
                        }
                    }
                }
            }

            fun doFire() {
                for (enemy in data.enemies) {
                    enemy.attack(pickPlayer(), tick.time)
                }
            }

            fun doAutoMove() {
                for (enemiesAmmo in data.enemiesAmmos) {
                    enemiesAmmo.move(tick.time, enemiesAmmo.stepX, enemiesAmmo.stepY)
                }
                for (playerAmmo in data.playersAmmos) {
                    playerAmmo.move(tick.time, playerAmmo.stepX, playerAmmo.stepY)
                }
                for (enemy in data.enemies) {
                    enemy.move(tick.time, enemy.stepX, enemy.stepY)
                }
            }

            while (!tick.isStop) {
                doWait()
                synchronized(data) {
                    doClean()
                    doHit()
                    doFire()
                    doAutoMove()
                }
                scenario.onTick(data, this@OSpaceControllerImpl)
                tick.tick()
            }
            scenario.onEnd(data, this@OSpaceControllerImpl)
        }
    }
}