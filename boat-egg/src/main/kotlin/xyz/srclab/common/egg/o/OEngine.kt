//package xyz.srclab.common.egg.o
//
//import xyz.srclab.common.collect.asMutable
//import xyz.srclab.common.collect.toSync
//import xyz.srclab.common.lang.Current
//import java.util.*
//import java.util.concurrent.CountDownLatch
//
//class OEngine(
//    data: OData
//) {
//
//    private val players: MutableList<Player> = LinkedList(data.players ?: emptyList()).toSync().asMutable()
//    private val enemies: MutableList<Enemy> = LinkedList(data.enemies ?: emptyList()).toSync().asMutable()
//    private val playerBullets: MutableList<Bullet> = LinkedList(data.playerBullets ?: emptyList()).toSync().asMutable()
//    private val enemyBullets: MutableList<Bullet> = LinkedList(data.enemyBullets ?: emptyList()).toSync().asMutable()
//    private val tick: OTick = data.tick ?: OTick()
//
//    private val killingListeners: MutableList<KillingListener> = LinkedList<KillingListener>().toSync().asMutable()
//    private val defeatListeners: MutableList<DefeatListener> = LinkedList<DefeatListener>().toSync().asMutable()
//    private val tickListeners: MutableList<TickListener> = LinkedList<TickListener>().toSync().asMutable()
//
//    private val latch: Latch = Latch()
//
//    fun addKillingListener(listener: KillingListener) {
//        killingListeners.add(listener)
//    }
//
//    fun addDefeatListener(listener: DefeatListener) {
//        defeatListeners.add(listener)
//    }
//
//    fun addTickListener(listener: TickListener) {
//        tickListeners.add(listener)
//    }
//
//    fun movePlayer(number: Int, stepX: Double, stepY: Double) {
//        val player = players[number - 1]
//        player.stepX = stepX
//        player.stepY = stepY
//    }
//
//    fun stopPlayer(number: Int) {
//        movePlayer(number, 0.0, 0.0)
//    }
//
//    interface KillingListener {
//        fun onKilling(
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
//        fun onTick(
//            tick: OTick,
//            players: MutableList<Player>,
//            enemies: MutableList<Enemy>,
//            bullets: MutableList<Bullet>
//        )
//    }
//
//    private class Latch {
//
//        @Volatile
//        private var _go: Boolean = false
//
//        @Volatile
//        private var countDownLatch: CountDownLatch = CountDownLatch(1)
//
//        fun go(go: Boolean) {
//            if (go) {
//                _go = true
//                countDownLatch.countDown()
//            } else {
//                countDownLatch = CountDownLatch(1)
//                _go = false
//            }
//        }
//
//        fun await() {
//            if (_go) {
//                return
//            }
//            countDownLatch.await()
//        }
//    }
//
//    private inner class GoThread : Thread("OEngine-GoThread") {
//
//        override fun run() {
//            while (true) {
//                latch.await()
//                computeCollision()
//                computeSteps()
//                gc()
//                Current.sleep(1)
//            }
//        }
//
//        private fun computeCollision() {
//
//            fun compute0(bullets: List<Bullet>, soldiers: List<Soldier>) {
//                for (bullet in bullets) {
//                    if (bullet.deathTime < 0) {
//                        continue
//                    }
//                    for (soldier in soldiers) {
//                        if (distance(bullet, soldier) < bullet.radius + soldier.radius) {
//                            val now = tick.now()
//                            bullet.deathTime = now
//                            soldier.deathTime = now
//                            for (killingListener in killingListeners) {
//                                //killingListener.onKilling(soldier)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        private fun computeSteps() {
//        }
//
//        private fun gc() {
//        }
//    }
//}