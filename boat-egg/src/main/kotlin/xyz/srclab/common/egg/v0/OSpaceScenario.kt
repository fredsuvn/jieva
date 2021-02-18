package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.inBounds
import xyz.srclab.common.base.randomBetween
import xyz.srclab.common.egg.sample.Scenario
import xyz.srclab.common.reflect.shortName
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.util.*

class OSpaceScenario(
    private val config: OSpaceConfig,
    private val logger: OSpaceLogger,
) : Scenario {

    private var _data: OSpaceData? = null

    private var lastRefreshEnemiesTime: Long = 0
    private val refreshEnemiesCoolDownTime: Long = 5000

    val data: OSpaceData = _data!!

    fun loadNew() {
        val newData = OSpaceData()
        val p1 = CommonPlayer(1)
        p1.x = config.width * 0.25
        p1.y = config.height - p1.radius
        p1.lastX = p1.x
        p1.lastY = p1.y
        val p2 = CommonPlayer(2)
        p2.x = config.width * 0.75
        p2.y = config.height - p2.radius
        p2.lastX = p2.x
        p2.lastY = p2.y
        newData.player1 = p1
        newData.player2 = p2
        newData.enemies = LinkedList()
        _data = newData
    }

    fun load(data: OSpaceData) {
        _data = data
    }

    fun onHitEnemy(ammo: Ammo, ammoManager: AmmoManager, enemy: Enemy, tick: OSpaceTick) {
        val damage = ammoManager.weapon.damage - enemy.defense
        enemy.hp -= damage
        if (enemy.hp <= 0) {
            enemy.deathTime = tick.time
            val player = ammoManager.weapon.owner as Player
            player.hit++
            player.score += enemy.score
            logger.info("Enemy was killed by {}, damage: {}", player.name, damage)
        }
        ammo.deathTime = tick.time
    }

    fun onHitPlayer(ammo: Ammo, ammoManager: AmmoManager, player: Player, tick: OSpaceTick) {
        val damage = ammoManager.weapon.damage - player.defense
        player.hp -= damage
        if (player.hp <= 0) {
            player.deathTime = tick.time
            val enemy = ammoManager.weapon.owner as Enemy
            logger.info("{} was killed by {}, damage: {}", player.name, enemy.name, damage)
            if (data.player1!!.isDead && data.player2!!.isDead) {
                logger.info("Both player were dead, game over!")
                tick.stop()
            }
        }
        ammo.deathTime = tick.time
    }

    fun onTick(tick: OSpaceTick) {
        if (tick.time - lastRefreshEnemiesTime > refreshEnemiesCoolDownTime) {
            refreshEnemies()
        }
    }

    private fun refreshEnemies() {
        val score = data.player1!!.score + data.player2!!.score / 10
        val number = score.inBounds(3, 10)
        val random = Random()
        for (i in 1..number) {
            val x = randomBetween(config.preparedPadding, config.width - config.preparedPadding, random)
            val y = randomBetween(-config.preparedHeight, 0 - config.preparedPadding, random)
            val enemy = CommonEnemy()
            enemy.x = x.toDouble()
            enemy.y = y.toDouble()
            enemy.lastX = enemy.x
            enemy.lastX = enemy.y
            data.enemies!!.add(enemy)
        }
    }
}

class CommonPlayer(override val number: Int) : BaseOUnit(), Player {
    override val name: String = "Player-$number"
    override var hit: Long = 0
    override var score: Long = 0
    override var hp: Int = 100
    override var defense: Int = 0
    override val weapons: List<Weapon> = Collections.singletonList(PlayerWeapon(this))
    override val force: Int = FORCE_PLAYER
    override var deathTime: Long = 0
    override val deathDuration: Long = 1500
    override val radius: Double = 10.0
    override var x: Double = 0.0
    override var y: Double = 0.0
    override val speed: Int = 80
    override var lastMoveTime: Long = 0
    override var lastX: Double = 0.0
    override var lastY: Double = 0.0

    override fun display(component: Component, time: Long, faceX: Int, faceY: Int) {
        if (number == 1) {
            this.draw(component, time, Color.BLUE, faceX, faceY)
        } else {
            this.draw(component, time, Color.GREEN, faceX, faceY)
        }
    }
}

class CommonEnemy : BaseOUnit(), Enemy {
    override var score: Long = 10
    override var hp: Int = 100
    override var defense: Int = 0
    override val weapons: List<Weapon> = Collections.singletonList(EnemyWeapon(this))
    override val force: Int = FORCE_ENEMY
    override var deathTime: Long = 0
    override val deathDuration: Long = 1500
    override val radius: Double = 10.0
    override var x: Double = 0.0
    override var y: Double = 0.0
    override val speed: Int = 50
    override var lastMoveTime: Long = 0
    override var lastX: Double = 0.0
    override var lastY: Double = 0.0
    override var stepX: Double = 0.0
    override var stepY: Double = 0.0

    override fun display(component: Component, time: Long, faceX: Int, faceY: Int) {
        this.draw(component, time, Color.GRAY, faceX, faceY)
    }
}

class EnemyWeapon(override val owner: Living) : BaseOUnit(), Weapon {
    override val damage: Int = 50
    override val fireSpeed: Int = 50
    override var lastFireTime: Long = 0
    override val ammoManager: AmmoManager = EnemyAmmoManager(this)
}

class PlayerWeapon(override val owner: Living) : BaseOUnit(), Weapon {
    override val damage: Int = 100
    override val fireSpeed: Int = 50
    override var lastFireTime: Long = 0
    override val ammoManager: AmmoManager = PlayerAmmoManager(this)
}

abstract class BaseOUnit : OUnit {
    override val type: String = this.javaClass.shortName
    override val name: String = "${this.javaClass.shortName}-${counter++}"

    companion object {
        var counter = 0L
    }
}

class EnemyAmmoManager(override val weapon: Weapon) : AmmoManager {

    override val ammos: MutableList<Ammo> = LinkedList()
    override val force: Int = FORCE_ENEMY

    override fun newAmmos(): List<Ammo> {
        return Collections.singletonList(EnemyAmmo())
    }
}

class PlayerAmmoManager(override val weapon: Weapon) : AmmoManager {

    override val ammos: MutableList<Ammo> = LinkedList()
    override val force: Int = FORCE_PLAYER

    override fun newAmmos(): List<Ammo> {
        return Collections.singletonList(PlayerAmmo())
    }
}

class EnemyAmmo : AbstractAmmo() {
    override val color: Color = Color.YELLOW
    override val speed: Int = 90
    override val deathDuration: Long = 1500
}

class PlayerAmmo : AbstractAmmo() {
    override val color: Color = Color.ORANGE
    override val speed: Int = 90
    override val deathDuration: Long = 1500
}

abstract class AbstractAmmo : Ammo {

    abstract val color: Color

    override var deathTime: Long = 0
    override var x: Double = 0.0
    override var y: Double = 0.0
    override val radius: Double = 3.0
    override var stepX: Double = 0.0
    override var stepY: Double = 0.0
    override var lastMoveTime: Long = 0
    override var lastX: Double = 0.0
    override var lastY: Double = 0.0

    override fun display(component: Component, time: Long, faceX: Int, faceY: Int) {
        this.draw(component, time, color, faceX, faceY)
    }
}

private fun BodyUnit.draw(component: Component, time: Long, color: Color, faceX: Int, faceY: Int) {
    if (this.isDead) {
        this.playExplosion(component, time, faceX, faceY)
        return
    }
    component.graphics.withColor(color) {
        it.fillOval(
            this.x.toInt(),
            this.y.toInt(),
            this.radius.toInt(),
            this.radius.toInt()
        )
    }
}

private fun BodyUnit.playExplosion(component: Component, time: Long, faceX: Int, faceY: Int) {
    val duration = time - this.deathTime
    if (duration > this.deathDuration) {
        return
    }
    val maxExplosionRadius = this.radius * 1.5
    val per = duration.toDouble() / this.deathDuration.toDouble()
    val explosionRadius = maxExplosionRadius * per
    component.graphics.withColor(Color.RED) {
        it.fillOval(
            this.x.toInt(),
            this.y.toInt(),
            explosionRadius.toInt(),
            explosionRadius.toInt()
        )
    }
}

private fun Graphics.withColor(color: Color, action: (Graphics) -> Unit) {
    val old = this.color
    this.color = color
    action(this)
    this.color = old
}