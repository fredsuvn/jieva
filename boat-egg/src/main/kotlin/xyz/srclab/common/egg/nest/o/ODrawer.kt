package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.loadPropertiesResource
import xyz.srclab.common.collect.map
import xyz.srclab.common.reflect.getFieldValue
import java.awt.Color
import java.awt.Graphics
import java.awt.Rectangle

internal object ODrawer {

    private val draws: Map<String, DrawResource> by lazy {
        "o/draw.properties".loadPropertiesResource().map { k, v ->
            k.trim() to run {
                val args = v.split(",")
                val textSize = if (args[1].isNotBlank()) args[1].toInt() else 0
                val textColor =
                    if (args[2].isNotBlank()) Color::class.java.getFieldValue<Color>(args[2], null) else null
                DrawResource(
                    args[0].trim(),
                    textSize,
                    textColor,
                )
            }
        }
    }

    fun getDrawResource(id: String): DrawResource {
        return draws[id]!!
    }

    fun draw(unit: OObjectUnit, tickTime: Long, graphics: Graphics) {
        val drawResource = unit.drawResource
        val leftUpX = (unit.x - unit.radius).toInt()
        val leftUpY = (unit.y - unit.radius).toInt()
        val width = (unit.radius * 2).toInt()

        fun drawBody() {
            graphics.withColor(unit.player.color) { g ->
                g.fillOval(leftUpX, leftUpY, width, width)
                val text = drawResource.text
                val textSize = drawResource.textSize
                val textColor = drawResource.textColor
                if (text.isNotBlank() && textColor !== null) {
                    g.withFontSize(textSize) { g0 ->
                        g0.withColor(textColor) {
                            it.drawCenteredString(text, Rectangle(leftUpX, leftUpY, width, width))
                        }
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

internal data class DrawResource(
    var text: String,
    var textSize: Int,
    var textColor: Color?,
)