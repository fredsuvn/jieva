package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.randomBetween
import xyz.srclab.common.collect.sumLong
import xyz.srclab.common.egg.sample.Scenario
import java.awt.Color
import java.awt.Graphics
import java.util.*

internal class OSpaceScenario : Scenario {

    fun onStart(data: OSpaceData, controller: OSpaceController) {
        val config = controller.config
        data.players.add(createPlayer1(config))
        data.players.add(createPlayer2(config))
        Refresher.refresh(data, controller)
    }

    fun onTick(data: OSpaceData, controller: OSpaceController) {
        Refresher.refresh(data, controller)
    }

    fun onEnd(data: OSpaceData, controller: OSpaceController) {
        OSpaceLogger.info("Game over!")
    }

    fun onHitEnemy(ammo: Ammo, enemy: Enemy, data: OSpaceData, controller: OSpaceController) {
        if (enemy.isDead) {
            val p = ammo.weapon.holder as Player
            OSpaceLogger.info("Player-{} killed enemy-{} at {}", p.number, enemy.id, controller.tick.time)
        }
    }

    fun onHitPlayer(ammo: Ammo, player: Player, data: OSpaceData, controller: OSpaceController) {
        if (player.isDead) {
            val e = ammo.weapon.holder as Enemy
            OSpaceLogger.info("Enemy-{} killed player-{} at {}", e.id, player.number, controller.tick.time)
        }
    }

    fun onWeaponAct(weapon: Weapon, targetX: Double, targetY: Double): OSpaceWeaponActor {
        return when (weapon.actorId) {
            PLAYER_WEAPON_ACTOR_ID -> PlayerWeaponActor
            ENEMY_WEAPON_ACTOR_ID -> EnemyWeaponActor
            ENEMY_CRAZY_WEAPON_ACTOR_ID -> CrazyWeaponActor
            else -> throw IllegalStateException("Unknown weapon actor id: ${weapon.actorId}")
        }
    }

    fun onDraw(unit: SubjectUnit, tickTime: Long, graphics: Graphics) {
        when (unit.drawerId) {
            PLAYER_1_DRAWER_ID -> Player1Drawer.draw(unit, tickTime, graphics)
            PLAYER_2_DRAWER_ID -> Player2Drawer.draw(unit, tickTime, graphics)
            PLAYER_AMMO_DRAWER_ID -> PlayerAmmoDrawer.draw(unit, tickTime, graphics)
            ENEMY_DRAWER_ID -> EnemyDrawer.draw(unit, tickTime, graphics)
            ENEMY_AMMO_DRAWER_ID -> EnemyAmmoDrawer.draw(unit, tickTime, graphics)
        }
    }

    private object Refresher {

        private const val refreshCoolDownTime = 5000L
        private var lastRefreshTime = 0L

        fun refresh(data: OSpaceData, controller: OSpaceController) {
            if (controller.tick.time - lastRefreshTime < refreshCoolDownTime) {
                return
            }
            val config = controller.config
            val level = data.players.sumLong { it.score }
            when {
                level < 100 -> refreshEnemies(data, config, 1)
                level < 1000 -> refreshEnemies(data, config, 2)
                level < 2000 -> refreshEnemies(data, config, 3)
                level < 5000 -> refreshEnemies(data, config, 4)
                else -> refreshEnemies(data, config, 5)
            }
            lastRefreshTime = controller.tick.time
        }

        private fun refreshEnemies(data: OSpaceData, config: OSpaceConfig, times: Int) {
            for (i in 1..times) {
                for (i in 1..4) {
                    data.enemies.add(createEnemy(config))
                }
                data.enemies.add(createCrazyEnemy(config))
            }
        }
    }
}

//Arguments:

private const val PLAYER_RADIUS = 16.0
private const val ENEMY_RADIUS = 16.0
private const val AMMO_RADIUS = 10.0

private const val PLAYER_MOVE_SPEED = 90
private const val ENEMY_MOVE_SPEED = 50
private const val AMMO_MOVE_SPEED = 80
private const val DEATH_DURATION = 4000L

private const val PLAYER_WEAPON_DAMAGE = 100
private const val ENEMY_WEAPON_DAMAGE = 50

private const val PLAYER_WEAPON_FIRE_SPEED = 90
private const val ENEMY_WEAPON_FIRE_SPEED = 50
private const val ENEMY_CRAZY_WEAPON_FIRE_SPEED = 60

//Drawer:

private const val PLAYER_1_DRAWER_ID = 100
private const val PLAYER_2_DRAWER_ID = 101
private const val PLAYER_AMMO_DRAWER_ID = 110
private const val ENEMY_DRAWER_ID = 120
private const val ENEMY_AMMO_DRAWER_ID = 130

private open class BaseDrawer(
    override val id: Int,
    private val color: Color,
) : OSpaceDrawer {

    override fun draw(unit: SubjectUnit, tickTime: Long, graphics: Graphics) {

        fun Graphics.withColor(color: Color, action: (Graphics) -> Unit) {
            val oldColor = this.color
            this.color = color
            action(this)
            this.color = oldColor
        }

        fun drawDead() {
            val elapsedTime = tickTime - unit.deathTime
            if (elapsedTime > unit.deathDuration) {
                return
            }
            val halfDeathDuration = unit.deathDuration / 2
            if (elapsedTime < halfDeathDuration) {
                val per = elapsedTime.toDouble() / halfDeathDuration
                val explosionRadius = unit.radius + unit.radius * per
                graphics.withColor(Color.RED) {
                    it.fillOval(
                        (unit.x - explosionRadius).toInt(),
                        (unit.y - explosionRadius).toInt(),
                        (explosionRadius * 2).toInt(),
                        (explosionRadius * 2).toInt(),
                    )
                }
                return
            }
            val explosionRadius = unit.radius * 2
            graphics.withColor(Color.RED) {
                it.fillOval(
                    (unit.x - explosionRadius).toInt(),
                    (unit.y - explosionRadius).toInt(),
                    (explosionRadius * 2).toInt(),
                    (explosionRadius * 2).toInt(),
                )
            }
            val per = (elapsedTime - halfDeathDuration).toDouble() / halfDeathDuration
            val disappearedRadius = explosionRadius * per
            graphics.fillOval(
                (unit.x - disappearedRadius).toInt(),
                (unit.y - disappearedRadius).toInt(),
                (disappearedRadius * 2).toInt(),
                (disappearedRadius * 2).toInt(),
            )
            return
        }

        if (unit.isDead) {
            drawDead()
        }

        graphics.withColor(color) {
            it.fillOval(
                (unit.x - unit.radius).toInt(),
                (unit.y - unit.radius).toInt(),
                (unit.radius * 2).toInt(),
                (unit.radius * 2).toInt(),
            )
        }
    }
}

private object Player1Drawer : BaseDrawer(PLAYER_1_DRAWER_ID, Color.BLUE)
private object Player2Drawer : BaseDrawer(PLAYER_2_DRAWER_ID, Color.GREEN)
private object PlayerAmmoDrawer : BaseDrawer(PLAYER_AMMO_DRAWER_ID, Color.PINK)
private object EnemyDrawer : BaseDrawer(ENEMY_DRAWER_ID, Color.YELLOW)
private object EnemyAmmoDrawer : BaseDrawer(ENEMY_AMMO_DRAWER_ID, Color.ORANGE)

//Weapons:

private const val PLAYER_WEAPON_ACTOR_ID = 200
private const val ENEMY_WEAPON_ACTOR_ID = 201
private const val ENEMY_CRAZY_WEAPON_ACTOR_ID = 202

private fun createPlayerWeapon(holder: Living): Weapon {
    return Weapon(
        holder,
        PLAYER_WEAPON_DAMAGE,
        PLAYER_WEAPON_FIRE_SPEED,
        0,
        PLAYER_WEAPON_ACTOR_ID
    )
}

private fun createEnemyWeapon(holder: Living): Weapon {
    return Weapon(
        holder,
        ENEMY_WEAPON_DAMAGE,
        ENEMY_WEAPON_FIRE_SPEED,
        0,
        ENEMY_WEAPON_ACTOR_ID
    )
}

private fun createEnemyCrazyWeapon(holder: Living): Weapon {
    return Weapon(
        holder,
        ENEMY_WEAPON_DAMAGE,
        ENEMY_CRAZY_WEAPON_FIRE_SPEED,
        0,
        ENEMY_CRAZY_WEAPON_ACTOR_ID
    )
}

private object PlayerWeaponActor : OSpaceWeaponActor {

    override val id: Int = PLAYER_WEAPON_ACTOR_ID

    override fun fire(attacker: Living, tickTime: Long, targetX: Double, targetY: Double): List<AmmoMeta> {
        val p = step(attacker.x, attacker.y, targetX, targetY)
        return Collections.singletonList(
            AmmoMeta(
                p.x,
                p.y,
                DEATH_DURATION,
                AMMO_RADIUS,
                AMMO_MOVE_SPEED,
                PLAYER_AMMO_DRAWER_ID
            )
        )
    }
}

private object EnemyWeaponActor : OSpaceWeaponActor {

    override val id: Int = ENEMY_WEAPON_ACTOR_ID

    override fun fire(attacker: Living, tickTime: Long, targetX: Double, targetY: Double): List<AmmoMeta> {
        val p = step(attacker.x, attacker.y, targetX, targetY)
        return Collections.singletonList(
            AmmoMeta(
                p.x,
                p.y,
                DEATH_DURATION,
                AMMO_RADIUS,
                AMMO_MOVE_SPEED,
                ENEMY_DRAWER_ID
            )
        )
    }
}

private object CrazyWeaponActor : OSpaceWeaponActor {

    override val id: Int = ENEMY_CRAZY_WEAPON_ACTOR_ID

    override fun fire(attacker: Living, tickTime: Long, targetX: Double, targetY: Double): List<AmmoMeta> {
        val ammo1 = AmmoMeta(
            -1.0,
            0.0,
            DEATH_DURATION,
            AMMO_RADIUS,
            AMMO_MOVE_SPEED,
            ENEMY_DRAWER_ID
        )
        val ammo2 = ammo1.copy(-1.0 * STEP_45_DEGREE_ANGLE, -1.0 * STEP_45_DEGREE_ANGLE)
        val ammo3 = ammo1.copy(0.0, -1.0)
        val ammo4 = ammo1.copy(1.0 * STEP_45_DEGREE_ANGLE, -1.0 * STEP_45_DEGREE_ANGLE)
        val ammo5 = ammo1.copy(1.0, 0.0)
        val ammo6 = ammo1.copy(1.0 * STEP_45_DEGREE_ANGLE, 1.0 * STEP_45_DEGREE_ANGLE)
        val ammo7 = ammo1.copy(0.0, 1.0)
        val ammo8 = ammo1.copy(-1.0 * STEP_45_DEGREE_ANGLE, 1.0 * STEP_45_DEGREE_ANGLE)
        return listOf(ammo1, ammo2, ammo3, ammo4, ammo5, ammo6, ammo7, ammo8)
    }
}

//Player:
private fun createPlayer1(config: OSpaceConfig): Player {
    val player = Player(
        1,
        0,
        0,
        config.width * 0.25,
        config.height - 2 * PLAYER_RADIUS,
        0.0,
        0.0,
        0.0,
        0.0,
        PLAYER_RADIUS,
        PLAYER_MOVE_SPEED,
        0,
        0,
        DEATH_DURATION,
        PLAYER_FORCE,
        PLAYER_1_DRAWER_ID,
        100,
        0,
        emptyList()
    )
    player.lastX = player.x
    player.lastY = player.y
    player.weapons = listOf(createPlayerWeapon(player))
    return player
}

private fun createPlayer2(config: OSpaceConfig): Player {
    val player = createPlayer1(config)
    player.number = 2
    player.x = config.width * 0.75
    player.drawerId = PLAYER_2_DRAWER_ID
    player.lastX = player.x
    player.lastY = player.y
    return player
}

//Enemy:
private fun createEnemy(config: OSpaceConfig): Enemy {
    val enemy = createBaseEnemy(config)
    enemy.weapons = listOf(createEnemyWeapon(enemy))
    return enemy
}

private fun createCrazyEnemy(config: OSpaceConfig): Enemy {
    val enemy = createBaseEnemy(config)
    enemy.weapons = listOf(createEnemyWeapon(enemy), createEnemyCrazyWeapon(enemy))
    return enemy
}

private fun createBaseEnemy(config: OSpaceConfig): Enemy {
    val x = randomBetween(config.preparedPadding, config.width - config.preparedPadding)
    val y = randomBetween(-config.preparedHeight + config.preparedPadding, 0 - config.preparedPadding)
    return Enemy(
        10,
        x.toDouble(),
        y.toDouble(),
        x.toDouble(),
        y.toDouble(),
        0.0,
        config.yUnit,
        ENEMY_RADIUS,
        ENEMY_MOVE_SPEED,
        0,
        0,
        DEATH_DURATION,
        ENEMY_FORCE,
        ENEMY_DRAWER_ID,
        50,
        0,
        emptyList()
    )
}