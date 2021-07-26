//package xyz.srclab.common.egg.o
//
//import java.util.*
//
//class OEngine(
//    private val data: OData
//) {
//    private val hitListeners: MutableList<HitListener> = LinkedList<HitListener>()
//    private val defeatListeners: MutableList<DefeatListener> = LinkedList<DefeatListener>()
//    private val tickListeners: MutableList<TickListener> = LinkedList<TickListener>()
//
//    fun addHitListener(listener: HitListener) {
//        data.tick.lock {
//            hitListeners.add(listener)
//        }
//    }
//
//    fun addDefeatListener(listener: DefeatListener) {
//        data.tick.lock {
//            defeatListeners.add(listener)
//        }
//    }
//
//    fun addTickListener(listener: TickListener) {
//        data.tick.lock {
//            tickListeners.add(listener)
//        }
//    }
//
//    interface HitListener {
//        fun onHit(
//            attacker: Soldier,
//            attacked: Soldier,
//            weapon: Weapon,
//            bullet: Bullet
//        )
//    }
//
//    interface DefeatListener {
//        fun onDefeat()
//    }
//
//    interface TickListener {
//        fun onTick(tick: Long)
//    }
//
//    // Tick
//
//    fun nextTick() {
//        data.tick.lock { nextTick0() }
//    }
//
//    private fun nextTick0() {
//
//        // Check hit and status
//        fun doHitAndStatus(group1: OData.Group, group2: OData.Group) {
//
//            fun Touchable.isDeath(): Boolean {
//                return this.deathTime >= 0
//            }
//
//            fun Touchable.outOfDeath(): Boolean {
//                return data.tick.now - this.deathTime > this.deathKeepTime
//            }
//
//            fun Touchable.outOfView(): Boolean {
//                if (
//                    this.x < -data.viewWidthBuffer - this.radius
//                    || this.x > data.viewWidth + data.viewWidthBuffer + this.radius
//                    || this.y < -data.viewHeightBuffer - this.radius
//                    || this.y > data.viewHeight + data.viewHeightBuffer + this.radius
//                ) {
//                    return true
//                }
//                return false
//            }
//
//            fun isHit(o1: Touchable, o2: Touchable):Boolean {
//                val distance = distance(o1, o2)
//                return distance < o1.radius + o2.radius
//            }
//
//            fun doHit0(soldiers1: MutableIterable<Soldier>, soldiers2: MutableIterable<Soldier>) {
//                val it1 = soldiers1.iterator()
//                while (it1.hasNext()) {
//                    val soldier1 = it1.next()
//                    if (soldier1.isDeath()) {
//
//                    }
//                    var bulletCount = 0
//                    for (weapon in soldier1.weapons!!) {
//                        val bullets = weapon.bullets
//                        if (bullets.isNullOrEmpty()) {
//                            continue
//                        }
//                        bulletCount += bullets.size
//                        val bt = bullets.iterator()
//                        while (bt.hasNext()) {
//                            val bullet = bt.next()
//                            if (bullet.gc()) {
//                                bt.remove()
//                                bulletCount--
//                                continue
//                            }
//                            for (soldier2 in soldiers2) {
//                                if (bullet)
//                                if (isHit(bullet, soldier2)) {
//                                    for (hitListener in hitListeners) {
//                                        hitListener.onHit(soldier1, soldier2, weapon, bullet)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        doHitAndStatus(data.group1, data.group2)
//        doHitAndStatus(data.group2, data.group1)
//
//        // Fire
//        fun doFire(group: OData.Group) {
//
//        }
//        doFire(data.group1)
//        doFire(data.group2)
//
//        // Auto moving
//        fun doAutoMoving(group: OData.Group) {
//
//        }
//        doAutoMoving(data.group1)
//        doAutoMoving(data.group2)
//
//        // Tick
//        data.tick.tick()
//    }
//}