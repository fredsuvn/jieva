package xyz.srclab.common.egg.boat

import xyz.srclab.common.run.RunLatch
import java.util.*

open class OEngine(
    private val data: OData,
    private val config: OConfig,
    private val scenario: OScenario
) {

    private var pauseFlag = false
    private val runLatch = RunLatch.newRunLatch()
    private val orders: MutableList<Order> = Collections.synchronizedList(LinkedList())

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

    fun tickOne(tick: Long) {
        if (tick == 0L) {
            scenario.init(data)
        }

        while (true) {

            executeOrders(orders, config, tick)
            orders.clear()

            checkCollision(data.tanks, data.bullets, tick)

            autoMove(data.tanks, data.bullets, tick)

            autoClear(data.tanks, data.bullets, tick)

            autoFire(data.tanks, tick)

            if (!pauseFlag) {
                continue
            }
            runLatch.await()
        }
    }

    private fun executeOrders(orders: List<Order>, config: OConfig, tick: Long) {
        for (order in orders) {
            val tank = data.tanks[order.player]
            when (order.order) {
                OPlayer.ORDER_MOVE_UP -> scenario.onMove(tank, 0.0, -config.unitY, tick)
                OPlayer.ORDER_MOVE_DOWN -> scenario.onMove(tank, 0.0, config.unitY, tick)
                OPlayer.ORDER_MOVE_LEFT -> scenario.onMove(tank, -config.unitX, 0.0, tick)
                OPlayer.ORDER_MOVE_RIGHT -> scenario.onMove(tank, config.unitX, 0.0, tick)
                OPlayer.ORDER_MOVE_UP_LEFT -> scenario.onMove(
                    tank,
                    -config.unitX * HYPOTENUSE_COEFFICIENT,
                    -config.unitY * HYPOTENUSE_COEFFICIENT,
                    tick
                )
                OPlayer.ORDER_MOVE_UP_RIGHT -> scenario.onMove(
                    tank,
                    config.unitX * HYPOTENUSE_COEFFICIENT,
                    -config.unitY * HYPOTENUSE_COEFFICIENT,
                    tick
                )
                OPlayer.ORDER_MOVE_DOWN_LEFT -> scenario.onMove(
                    tank,
                    -config.unitX * HYPOTENUSE_COEFFICIENT,
                    config.unitY * HYPOTENUSE_COEFFICIENT,
                    tick
                )
                OPlayer.ORDER_MOVE_DOWN_RIGHT -> scenario.onMove(
                    tank,
                    config.unitX * HYPOTENUSE_COEFFICIENT,
                    config.unitY * HYPOTENUSE_COEFFICIENT,
                    tick
                )
                OPlayer.ORDER_FIRE -> {
                    if (order.player == OPlayer.NUMBER_P1 || order.player == OPlayer.NUMBER_P2) {
                        scenario.onFire(tank, tank.x, -config.preparedHeight.toDouble(), tick)
                    } else {
                        scenario.onFire(tank, tank.x, (config.screenHeight + config.preparedHeight).toDouble(), tick)
                    }
                }
            }
        }
    }

    private fun checkCollision(tanks: List<OTank>, bullets: List<OBullet>, tick: Long) {
        for (tank in tanks) {
            for (bullet in bullets) {
                if (
                    tank.state != OUnit.STATE_ALIVE
                    || bullet.state != OUnit.STATE_ALIVE
                    || tank.player.force == bullet.weapon.tank.player.force
                ) {
                    continue
                }
                if (isCollision(tank, bullet)) {
                    scenario.onHit(bullet, tank, tick)
                }
            }
        }
    }

    private fun autoMove(tanks: List<OTank>, bullets: List<OBullet>, tick: Long) {
        for (tank in tanks) {
            if (tank.state != OUnit.STATE_ALIVE) {
                continue
            }
            if (tank.player.number == OPlayer.NUMBER_BOT_UP) {
                scenario.onMove(tank, 0.0, config.unitY, tick)
            }
            if (tank.player.number == OPlayer.NUMBER_BOT_UP) {
                scenario.onMove(tank, 0.0, -config.unitY, tick)
            }
        }
        for (bullet in bullets) {
            if (bullet.state != OUnit.STATE_ALIVE) {
                continue
            }
            scenario.onMove(bullet, bullet.stepX, bullet.stepY, tick)
        }
    }

    private fun autoClear(tanks: MutableList<OTank>, bullets: MutableList<OBullet>, tick: Long) {
        val tankIt = tanks.iterator()
        while (tankIt.hasNext()) {
            val tank = tankIt.next()
            if (!tank.player.isBot) {
                continue
            }
            if (
                (tank.state == OUnit.STATE_DEAD && tick - tank.deathTick > tank.deathDuration)
                || !isInBounds(tank, config)
            ) {
                scenario.onClearUnit(tank, tick)
                tankIt.remove()
            }
        }
        val bulletIt = bullets.iterator()
        while (bulletIt.hasNext()) {
            val bullet = bulletIt.next()
            if (
                (bullet.state == OUnit.STATE_DEAD && tick - bullet.deathTick > bullet.deathDuration)
                || !isInBounds(bullet, config)
            ) {
                scenario.onClearUnit(bullet, tick)
                bulletIt.remove()
            }
        }
    }

    private fun autoFire(tanks: List<OTank>, tick: Long) {
        for (tank in tanks) {
            if (tank.state != OUnit.STATE_ALIVE) {
                continue
            }
            if (tank.player.number == OPlayer.NUMBER_BOT_UP) {
                scenario.onBotFire(tank, tick)
            }
            if (tank.player.number == OPlayer.NUMBER_BOT_UP) {
                scenario.onBotFire(tank, tick)
            }
        }
    }

    private data class Order(
        val player: Int,
        val order: Int,
    )
}