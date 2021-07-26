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
//        // Check touching
//        fun doTouching(soldiers: Map<Long,Soldier>,soldiers: Map<Long,Soldier>){
//
//        }
//    }
//}