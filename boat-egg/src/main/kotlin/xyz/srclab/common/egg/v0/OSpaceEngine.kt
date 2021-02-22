package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.egg.sample.Engine
import java.awt.event.KeyEvent
import java.util.*
import kotlin.collections.HashSet
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

    private val playingThread = PlayingThread()
    private val scenario: OSpaceScenario = data.scenario
    private val keySet: MutableSet<Int> = HashSet()

    override fun start() {
        scenario.onStart(data, this)
        playingThread.start()
        go()
    }

    override fun stop() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        tick.stop()
        scenario.onStop(data, this)
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

    override fun toggle() {
        if (tick.isStop) {
            throw IllegalStateException("Game has been over!")
        }
        if (tick.isGoing) {
            tick.pause()
        } else {
            tick.go()
        }
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
        if (p.isDead) {
            return
        }
        p.attack(tick.time, p.x, 0.0)
    }

    override fun pressKey(vk: Int) {
        synchronized(keySet) {
            keySet.add(vk)
        }
    }

    override fun releaseKey(vk: Int) {
        synchronized(keySet) {
            keySet.remove(vk)
        }
    }

    private fun Player.move(currentTime: Long, stepX: Double, stepY: Double) {
        if (this.isDead) {
            return
        }
        if (this.x + stepX < 0 || this.x + stepX > config.width
            || this.y + stepY < 0 || this.y + stepY > config.height
        ) {
            return
        }
        (this as SubjectUnit).move(currentTime, stepX, stepY)
    }

    private fun Ammo.move(tickTime: Long, stepX: Double, stepY: Double) {
        if (this.preparedTime > 0 && tickTime - this.createTime < preparedTime) {
            return
        }
        if (this.preparedTime > 0 && this.createTime > 0) {
            this.preparedTime = 0
            this.x = this.weapon.holder.x
            this.y = this.weapon.holder.y
        }
        (this as SubjectUnit).move(tickTime, stepX, stepY)
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
                ammoMeta.createTime,
                ammoMeta.preparedTime,
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
                false,
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

    private inner class PlayingThread : Thread("PlayingThread-${threadSequence++}") {

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
                        if (ammo.stepX == 0.0 && ammo.stepY == 0.0) {
                            ammo.deathTime = tick.time
                        }
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

            fun doControl() {
                if (keySet.contains(KeyEvent.VK_SPACE)) {
                    fire(1)
                }
                if (keySet.contains(KeyEvent.VK_ENTER)) {
                    fire(2)
                }
                when {
                    keySet.contains(KeyEvent.VK_A) && keySet.contains(KeyEvent.VK_W) -> moveLeftUp(1)
                    keySet.contains(KeyEvent.VK_D) && keySet.contains(KeyEvent.VK_W) -> moveRightUp(1)
                    keySet.contains(KeyEvent.VK_A) && keySet.contains(KeyEvent.VK_S) -> moveLeftDown(1)
                    keySet.contains(KeyEvent.VK_D) && keySet.contains(KeyEvent.VK_S) -> moveRightDown(1)
                    keySet.contains(KeyEvent.VK_W) -> moveUp(1)
                    keySet.contains(KeyEvent.VK_S) -> moveDown(1)
                    keySet.contains(KeyEvent.VK_A) -> moveLeft(1)
                    keySet.contains(KeyEvent.VK_D) -> moveRight(1)
                }
                when {
                    keySet.contains(KeyEvent.VK_LEFT) && keySet.contains(KeyEvent.VK_UP) -> moveLeftUp(2)
                    keySet.contains(KeyEvent.VK_RIGHT) && keySet.contains(KeyEvent.VK_UP) -> moveRightUp(2)
                    keySet.contains(KeyEvent.VK_LEFT) && keySet.contains(KeyEvent.VK_DOWN) -> moveLeftDown(2)
                    keySet.contains(KeyEvent.VK_RIGHT) && keySet.contains(KeyEvent.VK_DOWN) -> moveRightDown(2)
                    keySet.contains(KeyEvent.VK_UP) -> moveUp(2)
                    keySet.contains(KeyEvent.VK_DOWN) -> moveDown(2)
                    keySet.contains(KeyEvent.VK_LEFT) -> moveLeft(2)
                    keySet.contains(KeyEvent.VK_RIGHT) -> moveRight(2)
                }
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
                    if (enemy.isDead) {
                        continue
                    }
                    enemy.attack(pickPlayer(), tick.time)
                }
            }

            fun doAutoMove() {
                for (enemiesAmmo in data.enemiesAmmos) {
                    if (enemiesAmmo.isDead) {
                        continue
                    }
                    enemiesAmmo.move(tick.time, enemiesAmmo.stepX, enemiesAmmo.stepY)
                }
                for (playerAmmo in data.playersAmmos) {
                    if (playerAmmo.isDead) {
                        continue
                    }
                    playerAmmo.move(tick.time, playerAmmo.stepX, playerAmmo.stepY)
                }
                for (enemy in data.enemies) {
                    if (enemy.isDead) {
                        continue
                    }
                    enemy.move(tick.time, enemy.stepX, enemy.stepY)
                }
            }

            while (!tick.isStop) {
                doWait()
                synchronized(data) {
                    doClean()
                    doControl()
                    doHit()
                    doFire()
                    doAutoMove()
                    scenario.onTick(data, this@OSpaceControllerImpl)
                }
                tick.tick()
            }
            synchronized(data) {
                scenario.onEnd(data, this@OSpaceControllerImpl)
            }
            OSpaceLogger.debug("Play thread $name over")
        }
    }

    companion object {
        private var threadSequence: Long = 0L
    }
}