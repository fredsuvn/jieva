package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.Current
import java.awt.event.KeyEvent
import kotlin.random.Random

internal class OEngine(
    private val data: OData,
    private val tick: OTick,
    private val scenario: OScenario,
    private val keySet: Set<Int>
) {

    private val playingThread = PlayingThread()

    fun start() {
        if (!playingThread.isAlive) {
            playingThread.start()
        }
    }

    fun humanMove(unit: OSubjectUnit, currentTime: Long, stepX: Double, stepY: Double) {
        if (unit.isDead) {
            return
        }
        if (unit.x + stepX < 0 || unit.x + stepX > OConfig.width
            || unit.y + stepY < 0 || unit.y + stepY > OConfig.height
        ) {
            return
        }
        unit.move(currentTime, stepX, stepY)
    }

    fun attack(unit: OSubjectUnit, tickTime: Long, targetX: Double, targetY: Double) {
        for (weapon in unit.weapons) {
            weapon.attack(unit, tickTime, targetX, targetY)
        }
    }

    private fun OObjectUnit.move(tickTime: Long, stepX: Double, stepY: Double) {
        if (tickTime - this.lastMoveTime < this.moveSpeed.moveSpeedToCoolDown()) {
            return
        }
        this.lastX = this.x
        this.lastY = this.y
        this.x += stepX
        this.y += stepY
        this.lastMoveTime = tickTime
    }

    private fun OAmmo.move(tickTime: Long, stepX: Double, stepY: Double) {
        if (this.preparedTime > 0 && tickTime - this.createTime < preparedTime) {
            return
        }
        if (this.preparedTime > 0 && this.createTime > 0) {
            this.preparedTime = 0
            this.x = this.weapon.holder.x
            this.y = this.weapon.holder.y
        }
        (this as OObjectUnit).move(tickTime, stepX, stepY)
    }

    private fun OSubjectUnit.attack(target: OSubjectUnit, tickTime: Long) {
        for (weapon in this.weapons) {
            weapon.attack(this, target, tickTime)
        }
    }

    private fun OWeapon.attack(attacker: OSubjectUnit, target: OSubjectUnit, tickTime: Long) {
        attack(attacker, tickTime, target.x, target.y)
    }


    private fun OWeapon.attack(attacker: OSubjectUnit, tickTime: Long, targetX: Double, targetY: Double) {
        if (tickTime - this.lastFireTime < this.fireSpeed.fireSpeedToCoolDown()) {
            return
        }
        val ammos = this.actor.fire(this, tickTime, targetX, targetY)
        if (attacker.player.force == PLAYER_FORCE) {
            data.humanAmmos.addAll(ammos)
        }
        if (attacker.player.force == ENEMY_FORCE) {
            data.enemyAmmos.addAll(ammos)
        }
        this.lastFireTime = tick.time
    }

    private fun OAmmo.hit(subjectUnit: OSubjectUnit) {
        val damage = this.weapon.damage - subjectUnit.defense
        subjectUnit.hp -= damage
        if (subjectUnit.hp <= 0) {
            subjectUnit.deathTime = tick.time
        }
        this.deathTime = tick.time
    }

    private fun OObjectUnit.isOutOfBounds(): Boolean {
        val length = this.radius * 2
        return this.x < -length
                || this.x > OConfig.width + length
                || this.y < -OConfig.preparedHeight
                || this.y > OConfig.height + length
    }

    private fun OObjectUnit.distance(other: OObjectUnit): Double {
        return distance(this.x, this.y, other.x, other.y)
    }

    private fun pickHumanSubject(): OSubject {
        if (data.player1.isDead && !data.player2.isDead) {
            return data.humanSubjects[1]
        }
        if (data.player2.isDead && !data.player1.isDead) {
            return data.humanSubjects[0]
        }
        return if (Random.nextBoolean()) data.humanSubjects[0] else data.humanSubjects[1]
    }

    private inner class PlayingThread : Thread("PlayingThread-${threadSequence++}") {

        override fun run() {

            fun doWait() {
                if (!tick.isGoing) {
                    tick.awaitToGo()
                } else {
                    Current.sleep(OConfig.tickInterval)
                }
            }

            fun doClean() {

                fun MutableIterable<OAmmo>.cleanAmmos() {
                    val iterator = this.iterator()
                    while (iterator.hasNext()) {
                        val ammo = iterator.next()
                        if (ammo.stepX == 0.0 && ammo.stepY == 0.0) {
                            ammo.deathTime = tick.time
                        }
                        if (ammo.isOutOfBounds() || ammo.isDisappeared(tick.time)) {
                            OLogger.debug("Ammo cleaned: {}", ammo.id)
                            iterator.remove()
                        }
                    }
                }

                fun MutableIterable<OSubjectUnit>.cleanLivings() {
                    val iterator = this.iterator()
                    while (iterator.hasNext()) {
                        val living = iterator.next()
                        if (living.isOutOfBounds() || living.isDisappeared(tick.time)) {
                            OLogger.debug("Living cleaned: {}-{}", living.javaClass.typeName, living.id)
                            iterator.remove()
                        }
                    }
                }

                data.enemyAmmos.cleanAmmos()
                data.humanAmmos.cleanAmmos()
                data.enemySubjects.cleanLivings()
                //data.players.cleanLivings()
            }

            fun doControl() {
                if (keySet.contains(KeyEvent.VK_SPACE)) {
                    OController.fire(1)
                }
                if (keySet.contains(KeyEvent.VK_ENTER)) {
                    OController.fire(2)
                }
                when {
                    keySet.contains(KeyEvent.VK_A) && keySet.contains(KeyEvent.VK_W) -> OController.moveLeftUp(
                        1
                    )
                    keySet.contains(KeyEvent.VK_D) && keySet.contains(KeyEvent.VK_W) -> OController.moveRightUp(
                        1
                    )
                    keySet.contains(KeyEvent.VK_A) && keySet.contains(KeyEvent.VK_S) -> OController.moveLeftDown(
                        1
                    )
                    keySet.contains(KeyEvent.VK_D) && keySet.contains(KeyEvent.VK_S) -> OController.moveRightDown(
                        1
                    )
                    keySet.contains(KeyEvent.VK_W) -> OController.moveUp(1)
                    keySet.contains(KeyEvent.VK_S) -> OController.moveDown(1)
                    keySet.contains(KeyEvent.VK_A) -> OController.moveLeft(1)
                    keySet.contains(KeyEvent.VK_D) -> OController.moveRight(1)
                }
                when {
                    keySet.contains(KeyEvent.VK_LEFT) && keySet.contains(KeyEvent.VK_UP) -> OController.moveLeftUp(
                        2
                    )
                    keySet.contains(KeyEvent.VK_RIGHT) && keySet.contains(KeyEvent.VK_UP) -> OController.moveRightUp(
                        2
                    )
                    keySet.contains(KeyEvent.VK_LEFT) && keySet.contains(KeyEvent.VK_DOWN) -> OController.moveLeftDown(
                        2
                    )
                    keySet.contains(KeyEvent.VK_RIGHT) && keySet.contains(KeyEvent.VK_DOWN) -> OController.moveRightDown(
                        2
                    )
                    keySet.contains(KeyEvent.VK_UP) -> OController.moveUp(2)
                    keySet.contains(KeyEvent.VK_DOWN) -> OController.moveDown(2)
                    keySet.contains(KeyEvent.VK_LEFT) -> OController.moveLeft(2)
                    keySet.contains(KeyEvent.VK_RIGHT) -> OController.moveRight(2)
                }
            }

            fun doHit() {
                for (humansAmmo in data.humanAmmos) {
                    if (humansAmmo.isDead) {
                        continue
                    }
                    for (enemy in data.enemySubjects) {
                        if (enemy.isDead) {
                            continue
                        }
                        if (humansAmmo.distance(enemy) < humansAmmo.radius + enemy.radius) {
                            humansAmmo.hit(enemy)
                            scenario.onHitEnemy(
                                humansAmmo,
                                enemy,
                            )
                        }
                    }
                }
                for (enemiesAmmo in data.enemyAmmos) {
                    if (enemiesAmmo.isDead) {
                        continue
                    }
                    for (human in data.humanSubjects) {
                        if (human.isDead) {
                            continue
                        }
                        if (enemiesAmmo.distance(human) < enemiesAmmo.radius + human.radius) {
                            enemiesAmmo.hit(human)
                            scenario.onHitHuman(
                                enemiesAmmo,
                                human,
                            )
                        }
                    }
                }
            }

            fun doFire() {
                for (enemy in data.enemySubjects) {
                    if (enemy.isDead) {
                        continue
                    }
                    enemy.attack(pickHumanSubject(), tick.time)
                }
            }

            fun doAutoMove() {
                for (enemiesAmmo in data.enemyAmmos) {
                    if (enemiesAmmo.isDead) {
                        continue
                    }
                    enemiesAmmo.move(tick.time, enemiesAmmo.stepX, enemiesAmmo.stepY)
                }
                for (playerAmmo in data.humanAmmos) {
                    if (playerAmmo.isDead) {
                        continue
                    }
                    playerAmmo.move(tick.time, playerAmmo.stepX, playerAmmo.stepY)
                }
                for (enemy in data.enemySubjects) {
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
                    scenario.onTick()
                }
                tick.tick()
            }
            synchronized(data) {
                scenario.onEnd()
            }
            OLogger.debug("Play thread $name over")
        }
    }

    companion object {
        private var threadSequence: Long = 0L
    }
}