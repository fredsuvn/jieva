package xyz.srclab.common.egg.o

import java.util.*

class OEngine(
    private val data: OData
) {

    private val config: OConfig = data.config
    private val tick: OTick = data.tick

    private val startListeners: MutableList<StartListener> = LinkedList<StartListener>()
    private val hitListeners: MutableList<HitListener> = LinkedList<HitListener>()
    private val fireListeners: MutableList<FireListener> = LinkedList<FireListener>()
    private val tickListeners: MutableList<TickListener> = LinkedList<TickListener>()

    fun addStartListener(listener: StartListener) {
        synchronized(data) {
            startListeners.add(listener)
        }
    }

    fun addHitListener(listener: HitListener) {
        synchronized(data) {
            hitListeners.add(listener)
        }
    }

    fun addFireListener(listener: FireListener) {
        synchronized(data) {
            fireListeners.add(listener)
        }
    }

    fun addTickListener(listener: TickListener) {
        synchronized(data) {
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
        synchronized(data) { nextTick0() }
    }

    private fun nextTick0() {

        // Do hit
        fun doHit(soldiers1: Iterable<Soldier>, soldiers2: Iterable<Soldier>) {
            for (soldier1 in soldiers1) {
                for (soldier2 in soldiers2) {
                    if (soldier1 == soldier2 || soldier2.death() || soldier2.outOfView(config) || soldier2.clear) {
                        continue
                    }
                    for (weapon in soldier1.weapons!!) {
                        for (bullet in weapon.bullets!!) {
                            if (bullet.death() || bullet.outOfView(config))
                        }
                    }
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