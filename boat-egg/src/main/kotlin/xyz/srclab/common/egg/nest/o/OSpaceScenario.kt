package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.randomBetween
import xyz.srclab.common.collect.sumLong
import java.awt.Graphics
import java.util.*

internal class OSpaceScenario {

    private val refresher = Refresher()

    fun onStart() {
        OData.playerObjects.add(createPlayer1(config))
        OData.playerObjects.add(createPlayer2(config))
        refresher.refresh(data, controller)
        controller.logger.info("Game start...")
    }

    fun onStop(data: OData, controller: OSpaceController) {
        controller.logger.info("Game stop...")
    }

    fun onTick(data: OData, controller: OSpaceController) {
        if (data.player1.isBody(controller.tick.time) && data.player2.isBody(controller.tick.time)) {
            controller.stop()
            return
        }
        refresher.refresh(data, controller)
    }

    fun onEnd(data: OData, controller: OSpaceController) {
        controller.logger.info("Game over!")
    }

    fun onHitEnemy(ammo: OAmmo, enemy: Enemy, data: OData, controller: OSpaceController) {
        val player = ammo.weapon.holder as OPlayer
        player.hit++
        player.score += enemy.score
        if (enemy.isDead) {
            val p = ammo.weapon.holder as OPlayer
            controller.logger.info("Player-{} killed enemy-{} at {}", p.number, enemy.id, controller.tick.time)
        }
    }

    fun onHitPlayer(ammo: OAmmo, player: OPlayer, data: OData, controller: OSpaceController) {
        if (player.isDead) {
            val e = ammo.weapon.holder as Enemy
            controller.logger.info("Enemy-{} killed player-{} at {}", e.id, player.number, controller.tick.time)
        }
    }

    fun onWeaponAct(weapon: OWeapon, targetX: Double, targetY: Double): WeaponActor {
        return when (weapon.actorId) {
            PLAYER_1_WEAPON_ACTOR_ID -> Player1WeaponActor
            PLAYER_2_WEAPON_ACTOR_ID -> Player2WeaponActor
            ENEMY_WEAPON_ACTOR_ID -> EnemyWeaponActor
            ENEMY_CRAZY_WEAPON_ACTOR_ID -> CrazyWeaponActor
            ENEMY_CONTINUOUS_WEAPON_ACTOR_ID -> ContinuousWeaponActor
            else -> throw IllegalStateException("Unknown weapon actor id: ${weapon.actorId}")
        }
    }

    fun onDraw(unit: OObjectUnit, tickTime: Long, graphics: Graphics) {
        UnitDrawerManager.getDrawer(unit).draw(unit, tickTime, graphics)
    }

    private class Refresher {

        private val refreshCoolDownTime = 5000L
        private var lastRefreshTime = 0L

        fun refresh(data: OData, controller: OSpaceController) {
            if (controller.tick.time - lastRefreshTime < refreshCoolDownTime) {
                return
            }
            val config = controller.config
            val level = data.playerSubjects.sumLong { it.score }
            when {
                level < 100 -> refreshEnemies(data, config, 1)
                level < 1000 -> refreshEnemies(data, config, 2)
                level < 2000 -> refreshEnemies(data, config, 3)
                level < 5000 -> refreshEnemies(data, config, 4)
                else -> refreshEnemies(data, config, 5)
            }
            lastRefreshTime = controller.tick.time
        }

        private fun refreshEnemies(data: OData, config: Config, times: Int) {
            for (i in 1..times) {
                for (it in 1..3) {
                    data.enemySubjects.add(createEnemy(config))
                }
                data.enemySubjects.add(createCrazyEnemy(config))
                data.enemySubjects.add(createContinuousEnemy(config))
            }
        }
    }
}

//Arguments:



//Weapons:

private const val PLAYER_1_WEAPON_ACTOR_ID = 200
private const val PLAYER_2_WEAPON_ACTOR_ID = 201
private const val ENEMY_WEAPON_ACTOR_ID = 202
private const val ENEMY_CRAZY_WEAPON_ACTOR_ID = 203
private const val ENEMY_CONTINUOUS_WEAPON_ACTOR_ID = 204

private fun createPlayer1Weapon(holder: OPlayer): OWeapon {
    return OWeapon(
        holder,
        PLAYER_WEAPON_DAMAGE,
        PLAYER_WEAPON_FIRE_SPEED,
        0,
        PLAYER_1_WEAPON_ACTOR_ID
    )
}

private fun createPlayer2Weapon(holder: OPlayer): OWeapon {
    return OWeapon(
        holder,
        PLAYER_WEAPON_DAMAGE,
        PLAYER_WEAPON_FIRE_SPEED,
        0,
        PLAYER_2_WEAPON_ACTOR_ID
    )
}

private fun createEnemyWeapon(holder: Enemy): OWeapon {
    return OWeapon(
        holder,
        ENEMY_WEAPON_DAMAGE,
        ENEMY_WEAPON_FIRE_SPEED,
        0,
        ENEMY_WEAPON_ACTOR_ID
    )
}

private fun createEnemyCrazyWeapon(holder: Enemy): OWeapon {
    return OWeapon(
        holder,
        ENEMY_WEAPON_DAMAGE,
        ENEMY_CRAZY_WEAPON_FIRE_SPEED,
        0,
        ENEMY_CRAZY_WEAPON_ACTOR_ID
    )
}

private fun createEnemyContinuousWeapon(holder: Enemy): OWeapon {
    return OWeapon(
        holder,
        ENEMY_WEAPON_DAMAGE,
        ENEMY_CRAZY_WEAPON_FIRE_SPEED,
        0,
        ENEMY_CONTINUOUS_WEAPON_ACTOR_ID
    )
}



//Player:
private fun createPlayer1(config: Config): OPlayer {
    val player = createBasePlayer(config)
    player.weapons = listOf(createPlayer1Weapon(player))
    //player.hp = 100000000
    return player
}

private fun createPlayer2(config: Config): OPlayer {
    val player = createBasePlayer(config)
    player.number = 2
    player.x = config.width * 0.75
    player.drawerId = PLAYER_2_DRAWER_ID
    player.lastX = player.x
    player.weapons = listOf(createPlayer2Weapon(player))
    //player.hp = 1
    return player
}

private fun createBasePlayer(config: Config): OPlayer {
    val player = OPlayer(
        1,
        0,
        0,
        config.width * 0.25,
        config.height - PLAYER_RADIUS,
        0.0,
        0.0,
        0.0,
        0.0,
        PLAYER_RADIUS,
        PLAYER_MOVE_SPEED,
        0,
        0,
        DEATH_DURATION,
        true,
        PLAYER_FORCE,
        PLAYER_1_DRAWER_ID,
        100,
        0,
        emptyList()
    )
    player.lastX = player.x
    player.lastY = player.y
    return player
}

//Enemy:
private fun createEnemy(config: Config): Enemy {
    val enemy = createBaseEnemy(config)
    enemy.weapons = listOf(createEnemyWeapon(enemy))
    return enemy
}

private fun createCrazyEnemy(config: Config): Enemy {
    val enemy = createBaseEnemy(config)
    enemy.weapons = listOf(createEnemyWeapon(enemy), createEnemyCrazyWeapon(enemy))
    return enemy
}

private fun createContinuousEnemy(config: Config): Enemy {
    val enemy = createBaseEnemy(config)
    enemy.weapons = listOf(createEnemyWeapon(enemy), createEnemyContinuousWeapon(enemy))
    return enemy
}

private fun createBaseEnemy(config: Config): Enemy {
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
        false,
        ENEMY_FORCE,
        ENEMY_DRAWER_ID,
        50,
        0,
        emptyList()
    )
}