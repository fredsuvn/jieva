package xyz.srclab.common.egg.boat

import xyz.srclab.common.run.RunLatch
import kotlin.math.tan

open class OGame(
    private val data: OData,
    private val config: OConfig,
    private val scenario: OScenario
) {

    private var pauseFlag = false
    private val runLatch = RunLatch.newRunLatch()

    fun start() {

        var tick = 0L

    }

    fun pause() {
        runLatch.close()
        pauseFlag = true
    }

    fun go() {
        pauseFlag = false
        runLatch.open()
    }

    fun start0(tick: Long) {
        if (tick == 0L) {
            scenario.init(data)
        }

        while (true) {
            checkCollision(data.players, tick)
            autoMove(data.players, tick)
            clear(data.players, tick)
            if (!pauseFlag) {
                continue
            }
            runLatch.await()
        }
    }

    private fun operate(players: Array<OPlayer>, config: OConfig, tick: Long) {
        for (player in players) {
            if (
                player.number < OPlayer.HUMAN_NUMBER_START
                || player.operation == 0
                || tick - player.lastOperateTick > player.operateCoolDownTick
            ) {
                continue
            }
            val tank = player.tanks[0]
            when (player.operation) {
                OPlayer.OPERATION_MOVE_UP -> tank.y -= config.unitY
                OPlayer.OPERATION_MOVE_DOWN -> tank.y += config.unitY
                OPlayer.OPERATION_MOVE_LEFT -> tank.x -= config.unitY
                OPlayer.OPERATION_MOVE_RIGHT -> tank.x -= config.unitX
                OPlayer.OPERATION_MOVE_UP_LEFT -> {
                    tank.y -= config.unitY * HYPOTENUSE_COEFFICIENT
                    tank.x -= config.unitX * HYPOTENUSE_COEFFICIENT
                }
                OPlayer.OPERATION_MOVE_UP_RIGHT -> {
                    tank.y -= config.unitY * HYPOTENUSE_COEFFICIENT
                    tank.x += config.unitX * HYPOTENUSE_COEFFICIENT
                }
                OPlayer.OPERATION_MOVE_DOWN_LEFT -> {
                    tank.y += config.unitY * HYPOTENUSE_COEFFICIENT
                    tank.x -= config.unitX * HYPOTENUSE_COEFFICIENT
                }
                OPlayer.OPERATION_MOVE_DOWN_RIGHT -> {
                    tank.y += config.unitY * HYPOTENUSE_COEFFICIENT
                    tank.x += config.unitX * HYPOTENUSE_COEFFICIENT
                }
                OPlayer.OPERATION_FIRE -> {
                    if (player.direction == OPlayer.DIRECTION_UP) {
                        for (weapon in tank.weapons) {
                            scenario.onFire(weapon, tank.x, -config.preparedHeight.toDouble())
                        }
                    } else {
                        for (weapon in tank.weapons) {
                            scenario.onFire(weapon, tank.x, (config.preparedHeight + config.screenHeight).toDouble())
                        }
                    }
                }
            }
            player.lastOperateTick = tick
            player.operation = 0
        }
    }

    private fun checkCollision(players: Array<OPlayer>, tick: Long) {
        for (p1 in players) {
            for (p2 in players) {
                if (p1 === p2 || p1.force == p2.force) {
                    continue
                }
                for (tank1 in p1.tanks) {
                    for (weapon in tank1.weapons) {
                        for (bullet in weapon.bullets) {
                            for (tank2 in p2.tanks) {
                                if (bullet.state != OUnit.STATE_ALIVE || tank2.state != OUnit.STATE_ALIVE) {
                                    continue
                                }
                                if (isCollision(bullet, tank2)) {
                                    scenario.onHit(bullet, tank2, tick)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun autoMove(players: Array<OPlayer>, tick: Long) {
        for (player in players) {
            for (tank in player.tanks) {
                if (player.isBot) {
                    scenario.onMove(tank,tank.stepX,tank.stepY, tick)
                }
                for (weapon in tank.weapons) {
                    for (bullet in weapon.bullets) {
                        scenario.onMove(bullet,bullet.stepX,bullet.stepY, tick)
                    }
                }
            }
        }
    }

    private fun clear(players: Array<OPlayer>, tick: Long) {
        for (player in players) {
            val tankIt = player.tanks.iterator()
            while (tankIt.hasNext()) {
                val tank = tankIt.next()
                var bulletCount = 0
                for (weapon in tank.weapons) {
                    val bulletIt = weapon.bullets.iterator()
                    while (bulletIt.hasNext()) {
                        val bullet = bulletIt.next()
                        if (
                            bullet.state == OUnit.STATE_DEAD
                            && tick - bullet.deathTick > bullet.deathDuration
                            && !isInBounds(bullet, config)
                        ) {
                            bulletIt.remove()
                            scenario.onClearUnit(bullet, tick)
                        } else {
                            bulletCount++
                        }
                    }
                }
                if (!player.isBot) {
                    continue
                }
                if (
                    bulletCount == 0
                    && tank.state == OUnit.STATE_DEAD
                    && tick - tank.deathTick > tank.deathDuration
                    && !isInBounds(tank, config)
                ) {
                    tankIt.remove()
                    scenario.onClearUnit(tank, tick)
                }
            }
        }
    }

    private fun autoFire(players: Array<OPlayer>, tick: Long) {
        for (player in players) {
            if (!player.isBot) {
                continue
            }
            for (tank in player.tanks) {
                for (weapon in tank.weapons) {

                }
            }
        }
    }
}