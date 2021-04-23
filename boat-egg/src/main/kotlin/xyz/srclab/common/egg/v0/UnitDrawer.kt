package xyz.srclab.common.egg.v0

import java.awt.Color
import java.awt.Graphics

internal interface UnitDrawer {

    fun draw(unit: SubjectUnit, tickTime: Long, graphics: Graphics)
}

internal object UnitDrawerManager {

    private val player1Drawer: UnitDrawer = CircularDrawer(Config.player1Color, Config.player1Text)
    private val player1AmmoDrawer: UnitDrawer = CircularDrawer(Config.player1AmmoColor, Config.player1AmmoText)
    private val player2Drawer: UnitDrawer = CircularDrawer(Config.player2Color, Config.player2Text)
    private val player2AmmoDrawer: UnitDrawer = CircularDrawer(Config.player2AmmoColor, Config.player2AmmoText)
    private val enemyDrawer: UnitDrawer = CircularDrawer(Config.enemyColor, Config.enemyText)
    private val enemyAmmoDrawer: UnitDrawer = CircularDrawer(Config.enemyAmmoColor, Config.enemyAmmoText)

    fun getDrawer(unit: SubjectUnit): UnitDrawer {
        return when (unit) {
            is Ammo -> {
                return when (val holder = unit.weapon.holder) {
                    is Enemy -> enemyAmmoDrawer
                    is Player -> if (holder.number == 1) player1AmmoDrawer else player2AmmoDrawer
                    else -> throw IllegalStateException("Unknown unit: $unit")
                }
            }
            is Player -> {
                return if (unit.number == 1) player1Drawer else player2Drawer
            }
            is Enemy -> enemyDrawer
            else -> throw IllegalStateException("Unknown unit: $unit")
        }
    }
}

private class CircularDrawer(
    private val color: Color,
    private val text: String?,
) : UnitDrawer {

    override fun draw(unit: SubjectUnit, tickTime: Long, graphics: Graphics) {
        val leftUpX = (unit.x - unit.radius).toInt()
        val leftUpY = (unit.y - unit.radius).toInt()
        val width = (unit.radius * 2).toInt()

        fun drawBody() {
            graphics.withColor(color) { g ->
                if (text === null) {
                    g.fillOval(leftUpX, leftUpY, width, width)
                } else {
                    g.withFontSize(width) {
                        it.drawString(text, leftUpX, leftUpY)
                    }
                }
            }
        }

        fun drawDead() {
            val elapsedTime = tickTime - unit.deathTime
            if (elapsedTime > unit.deathDuration) {
                if (!unit.keepBody) {
                    return
                }
                drawBody()
                val rightDownX = (unit.x + unit.radius).toInt()
                val rightDownY = (unit.y + unit.radius).toInt()
                val rightUpX = (unit.x + unit.radius).toInt()
                val rightUpY = (unit.y - unit.radius).toInt()
                val leftDownX = (unit.x - unit.radius).toInt()
                val leftDownY = (unit.y + unit.radius).toInt()
                graphics.withColor(Color.RED) {
                    it.drawLine(leftUpX, leftUpY, rightDownX, rightDownY)
                }
                graphics.withColor(Color.RED) {
                    it.drawLine(rightUpX, rightUpY, leftDownX, leftDownY)
                }
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
            return
        }

        drawBody()
    }
}