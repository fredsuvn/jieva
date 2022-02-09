package xyz.srclab.common.egg.boat

import xyz.srclab.common.run.RunLatch

open class OGame(
    private val data: OData,
    private val config: OConfig,
    private val scenario:OScenario
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

    fun start0(tick:Long) {
        if (tick == 0L) {
            scenario.init(data)
        }

        while (true) {

            //Check collision
            for (p1 in data.players) {
                for (p2 in data.players) {
                    if (p1 === p2 || p1.force == p2.force) {
                        continue
                    }
                    //p1 -> p2
                    for (tank1 in p1.tanks) {
                        for (weapon in tank1.weapons) {
                            for (bullet in weapon.bullets) {
                                for (tank2 in p2.tanks) {
                                    if (isCollision(bullet, tank2)) {
                                        bullet.hp = 0
                                        bullet.deathTick = tick

                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (!pauseFlag) {
                continue
            }
            runLatch.await()
        }
    }
}