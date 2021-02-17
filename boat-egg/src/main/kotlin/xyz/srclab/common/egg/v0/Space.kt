package xyz.srclab.common.egg.v0

import xyz.srclab.annotations.OutParam
import xyz.srclab.common.base.Current
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @author sunqian
 */
class Space(
    val tickTime: Long,
    val weight: Double,
    val height: Double,
    val preparedHeight: Double,
    val p1: Player,
    val p2: Player,
    val listeners: List<SpaceListener>,
) {

    private var stop: Boolean = false

    private val _enemies: MutableList<Enemy> = LinkedList()
    private val _ammos: MutableList<Ammo> = LinkedList()

    val enemies: List<Enemy> get() = _enemies
    val ammos: List<Ammo> get() = _ammos

    fun start() {

        val now = Current.millis

        fun clean() {
            _ammos.removeIf {
                it.isDisappeared(now) || it.isOutOfSpace(weight, height)
            }
            _enemies.removeIf {
                it.isDisappeared(now) || it.isOutOfSpace(weight, height)
            }
        }

        fun doAmmoAndLive(ammo: Ammo, living: Living): Boolean {

            fun Int.atLeastZero(): Int {
                return if (this < 0) 0 else this
            }

            if (ammo.isDead) {
                return false
            }
            if (living.isDead) {
                return true
            }
            if (ammo.force != living.force && distance(ammo, living) < (ammo.size + living.size)) {
                val damage = (ammo.damage - living.defense).atLeastZero()
                living.hp -= damage
                if (living.hp <= 0) {
                    ammo.deadTime = now
                    living.deadTime = now
                    living.hp = 0
                }
                for (listener in listeners) {
                    if (listener is SpaceHitListener) {
                        listener.onHit(ammo, living, damage)
                    }
                }
                return false
            }
            return true
        }

        stop = true

        while (true) {

            clean()

            for (ammo in ammos) {
                for (enemy in enemies) {
                    if (!doAmmoAndLive(ammo, enemy)) {
                        continue
                    }
                }
                if (!doAmmoAndLive(ammo, p1)) {
                    continue
                }
                if (!doAmmoAndLive(ammo, p2)) {
                    continue
                }
                move(ammo, now, ammo.stepX, ammo.stepY)
            }

            for (enemy in enemies) {
                if (enemy.isDead) {
                    continue
                }
                for (weapon in enemy.weapons) {
                    if (now - weapon.lastFireTime > weapon.FireCoolDownTime) {
                        val ammo = weapon.attack()
                        _ammos.add(ammo)
                        weapon.lastFireTime = now
                        val p = pickOnePlayer()
                        computeStep(ammo, p.x, p.y)
                        move(ammo, now, ammo.stepX, ammo.stepY)
                    }
                }
                move(enemy, now, 0.0, 1.0)
            }

            if (stop) {
                break
            }

            Current.sleep(tickTime)
        }
    }

    fun stop() {
        stop = false
    }

    fun addEnemies(enemies: List<Enemy>) {
        _enemies.addAll(enemies)
    }

    fun move(@OutParam movableUnit: MovableUnit, now: Long, stepX: Double, stepY: Double) {
        if (now - movableUnit.lastMoveTime < movableUnit.moveCoolDownTime) {
            return
        }
        movableUnit.x += stepX
        movableUnit.y += stepY
        movableUnit.lastMoveTime = now
    }

    //fun attack(@OutParam )

    private fun computeStep(@OutParam movable: AutoMovableUnit, targetX: Double, targetY: Double) {
        val distance = distance(movable.x, movable.y, targetX, targetY)
        val per = 1 / distance
        movable.stepX += (targetX - movable.x) * per
        movable.stepY += (targetY - movable.y) * per
    }

    private fun distance(a: SizeUnit, b: SizeUnit): Double {
        return distance(a.x, a.y, b.x, b.y)
    }

    private fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
    }

    private fun pickOnePlayer(): Player {
        val r = Random().nextBoolean()
        return if (r) p1 else p2
    }

    private fun SizeUnit.isOutOfSpace(weight: Double, height: Double): Boolean {
        return x < 0 || x > weight || y < -preparedHeight || y > height
    }
}

interface SpaceListener

interface SpaceHitListener : SpaceListener {

    fun onHit(ammo: Ammo, living: Living, damage: Int)
}