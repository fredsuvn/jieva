package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.randomBetween
import xyz.srclab.common.collect.sumLong
import java.awt.Graphics

internal class OScenario(
    private val data: OData,
    private val tick: OTick,
) {

    private val refresher: ORefresher = ORefresher(data)

    fun onStart() {
        if (data.humanSubjects.isEmpty()) {
            val s1 = createHumanSubject(data.player1, 0.0, 0.0)
            val s2 = createHumanSubject(data.player2, 0.0, 0.0)
            s1.x = OConfig.width * 0.25
            s1.y = OConfig.height - s1.radius
            s2.x = OConfig.width * 0.75
            s2.y = OConfig.height - s1.radius
            data.humanSubjects.addAll(listOf(s1, s2))
        }
        OLogger.info("Game start...")
        refresher.refresh()
    }

    fun onStop() {
        OLogger.info("Game stop...")
    }

    fun onEnd() {
        OLogger.info("Game over!")
    }

    fun onTick() {
        if (data.player1Subject.isBody(tick.time) && data.player2Subject.isBody(tick.time)) {
            OController.stop()
            return
        }
        refresher.refresh()
    }

    fun onHitEnemy(ammo: OAmmo, enemy: OSubject) {
        val player = ammo.weapon.holder.player
        player.hit++
        player.score += enemy.score
        if (enemy.isDead) {
            OLogger.info("Player-{} killed enemy-{} at {}", player.number, enemy.id, tick.time)
        }
    }

    fun onHitHuman(ammo: OAmmo, human: OSubject) {
        if (human.isDead) {
            OLogger.info(
                "Enemy-{} killed player-{} at {}", ammo.weapon.holder.id, human.player.number, tick.time
            )
        }
    }

    fun onDraw(unit: OObjectUnit, graphics: Graphics) {
        ODrawer.draw(unit, tick.time, graphics)
    }

    private fun createHumanSubject(player: OPlayer, x: Double, y: Double): OSubject {
        val type = OTypeManager.getSubjectType("player-subject")
        val subject = OSubject(
            x,
            y,
            x,
            y,
            0.0,
            0.0,
            type.radius,
            type.moveSpeed,
            0,
            0,
            type.deathDuration,
            type.keepBody,
            player,
            ODrawer.getDrawResource(type.drawId),
            type,
            100,
            0,
            emptyList(),
            0,
        )
        val weaponType = OTypeManager.getWeaponType("player-weapon")
        val weapon = OWeapon(
            subject,
            weaponType.damage,
            weaponType.fireSpeed,
            0,
            OWeaponManager.getWeaponActor(weaponType.actorId),
            weaponType
        )
        subject.weapons = listOf(weapon)
        return subject
    }

    private inner class ORefresher(private val data: OData) {

        private val refreshCoolDownTime = 5000L
        private var lastRefreshTime = 0L

        fun refresh() {
            if (tick.time - lastRefreshTime < refreshCoolDownTime) {
                return
            }
            val level = data.humanSubjects.sumLong { it.score }
            when {
                level < 100 -> refreshEnemies(1)
                level < 1000 -> refreshEnemies(2)
                level < 2000 -> refreshEnemies(3)
                level < 5000 -> refreshEnemies(4)
                else -> {
                    refreshCrazyEnemies(5)
                }
            }
            lastRefreshTime = tick.time
        }

        private fun refreshEnemies(times: Int) {
            for (i in 1..times) {
                for (it in 1..3) {
                    data.enemySubjects.add(createCommonEnemy())
                }
                data.enemySubjects.add(createBloomEnemy())
                data.enemySubjects.add(createBulletEnemy())
            }
        }

        private fun refreshCrazyEnemies(times: Int) {
            refreshEnemies(times)
            data.enemySubjects.add(createCrazyEnemy())
        }

        private fun createCommonEnemy(): OSubject {
            return createEnemy(listOf(OTypeManager.getWeaponType("enemy-weapon")))
        }

        private fun createBloomEnemy(): OSubject {
            return createEnemy(listOf(OTypeManager.getWeaponType("enemy-weapon-bloom")))
        }

        private fun createBulletEnemy(): OSubject {
            return createEnemy(listOf(OTypeManager.getWeaponType("enemy-weapon-bullet")))
        }

        private fun createCrazyEnemy(): OSubject {
            return createEnemy(
                listOf(
                    OTypeManager.getWeaponType("enemy-weapon-bloom"),
                    OTypeManager.getWeaponType("enemy-weapon-bullet")
                )
            )
        }

        private fun createEnemy(weaponTypes: List<OWeaponType>): OSubject {
            val x = randomBetween(OConfig.preparedPadding, OConfig.width - OConfig.preparedPadding)
            val y = randomBetween(
                -OConfig.preparedHeight + OConfig.preparedPadding, 0 - OConfig.preparedPadding
            )
            val subject = createEnemySubject(data.playerEnemy, x.toDouble(), y.toDouble(), emptyList())
            val weapons = weaponTypes.map { t ->
                OWeapon(
                    subject,
                    t.damage,
                    t.fireSpeed,
                    0,
                    OWeaponManager.getWeaponActor(t.actorId),
                    t
                )
            }
            subject.weapons = weapons
            return subject
        }

        private fun createEnemySubject(player: OPlayer, x: Double, y: Double, weapons: List<OWeapon>): OSubject {
            val type = OTypeManager.getSubjectType("enemy-subject")
            return OSubject(
                x,
                y,
                x,
                y,
                0.0,
                OConfig.yUnit,
                type.radius,
                type.moveSpeed,
                0,
                0,
                type.deathDuration,
                type.keepBody,
                player,
                ODrawer.getDrawResource(type.drawId),
                type,
                100,
                0,
                weapons,
                50,
            )
        }
    }
}