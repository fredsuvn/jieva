package xyz.srclab.common.egg.o

import java.util.*

class OEngine(
    private val data: OData,
    private val config: OConfig
) {

    private val startListeners: MutableList<StartListener> = LinkedList<StartListener>()
    private val hitListeners: MutableList<HitListener> = LinkedList<HitListener>()
    private val fireListeners: MutableList<FireListener> = LinkedList<FireListener>()
    private val tickListeners: MutableList<TickListener> = LinkedList<TickListener>()

    fun addStartListener(listener: StartListener) {
        data.tick.lock {
            startListeners.add(listener)
        }
    }

    fun addHitListener(listener: HitListener) {
        data.tick.lock {
            hitListeners.add(listener)
        }
    }

    fun addFireListener(listener: FireListener) {
        data.tick.lock {
            fireListeners.add(listener)
        }
    }

    fun addTickListener(listener: TickListener) {
        data.tick.lock {
            tickListeners.add(listener)
        }
    }

    interface StartListener {
        fun onStart()
    }

    interface HitListener {
        fun onHit(
            attacker: Soldier,
            attacked: Soldier,
            weapon: Weapon,
            bullet: Bullet
        )
    }

    interface FireListener {
        fun onFire(
            holder: Soldier,
            weapon: Weapon
        )
    }

    interface TickListener {
        fun onTick(tick: Long)
    }

    // Tick

    fun nextTick() {
        data.tick.lock { nextTick0() }
    }

    private fun nextTick0() {

        // Check hit and status
        fun doHitAndStatus(soldiers1: MutableIterable<Soldier>, soldiers2: Iterable<Soldier>, gc: Boolean) {
            val it1 = soldiers1.iterator()
            while (it1.hasNext()) {
                val soldier1 = it1.next()
                var bulletCount = 0
                for (weapon in soldier1.weapons!!) {
                    val bullets = weapon.bullets
                    if (bullets.isNullOrEmpty()) {
                        continue
                    }
                    bulletCount += bullets.size
                    val bt = bullets.iterator()
                    while (bt.hasNext()) {
                        val bullet = bt.next()
                        if (bullet.death()) {
                            if (bullet.outOfDeath(data.tick.now) || bullet.outOfView(config)) {
                                bt.remove()
                                OLogger.debug("bullet removed: {}", bullet.id)
                                bulletCount--
                            }
                            continue
                        }
                        val it2 = soldiers2.iterator()
                        while (it2.hasNext()) {
                            val soldier2 = it2.next()
                            if (bullet.group == soldier2.group) {
                                continue
                            }
                            if (soldier2.death()) {
                                continue
                            }
                            if (bullet.hit(soldier2)) {
                                for (hitListener in hitListeners) {
                                    hitListener.onHit(soldier1, soldier2, weapon, bullet)
                                }
                            }
                        }
                    }
                }
                if (
                    gc
                    && bulletCount == 0
                    && (soldier1.death() && soldier1.outOfDeath(data.tick.now)
                        || soldier1.outOfView(config))
                ) {
                    it1.remove()
                    OLogger.debug("solider removed: {}", soldier1.id)
                }
            }
        }
        doHitAndStatus(data.players, data.computers, false)
        doHitAndStatus(data.computers, data.players, true)

        // Fire
        fun doFire(soldiers: Iterable<Soldier>) {
            for (soldier in soldiers) {
                for (weapon in soldier.weapons!!) {
                    if (weapon.readyFire(data.tick.now)) {
                        for (fireListener in fireListeners) {
                            fireListener.onFire(soldier, weapon)
                        }
                    }
                }
            }
        }
        doFire(data.players)
        doFire(data.computers)

        // Auto recover and move
        fun doAutoRecoverAndMove(soldiers: Iterable<Soldier>) {
            for (soldier in soldiers) {
                if (!soldier.death()) {
                    if (soldier.readyRecovery(data.tick.now)) {
                        soldier.hp += soldier.recovery
                        if (soldier.hp > soldier.totalHp) {
                            soldier.hp = soldier.totalHp
                        }
                    }
                    if (soldier.readyMove(data.tick.now)) {
                        soldier.x += soldier.stepX * config.unitX
                        soldier.y += soldier.stepY * config.unitX
                    }
                }
                for (weapon in soldier.weapons!!) {
                    for (bullet in weapon.bullets!!) {
                        if (!bullet.death()) {
                            if (bullet.readyMove(data.tick.now)) {
                                bullet.x += bullet.stepX * config.unitX
                                bullet.y += bullet.stepY * config.unitY
                            }
                        }
                    }
                }
            }
        }
        doAutoRecoverAndMove(data.players)
        doAutoRecoverAndMove(data.computers)

        // Tick
        for (tickListener in tickListeners) {
            tickListener.onTick(data.tick.now)
        }

        // Over
        data.tick.tick()
    }
}