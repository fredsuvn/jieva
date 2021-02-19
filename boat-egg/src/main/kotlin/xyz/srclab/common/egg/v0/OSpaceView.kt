package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.egg.sample.View
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel

class OSpaceView(
    private val config: OSpaceConfig,
    private val logger: OSpaceLogger,
) : JFrame("O Space Battle"), View<OSpaceEngine, OSpaceScenario, OSpaceController, OSpaceData> {

    init {
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(config.width, config.height)
        setLocationRelativeTo(null)
        isVisible = true
        val gamePanel = GamePanel(config, OSpaceEngine(config), OSpaceScenario(config, logger), logger)
        add(gamePanel)
        gamePanel.requestFocus()
    }
}

private class GamePanel(
    private val config: OSpaceConfig,
    engine: OSpaceEngine,
    private val scenario: OSpaceScenario,
    private val logger: OSpaceLogger
) : JPanel() {

    private val controller = engine.load(scenario)

    init {
        background = Color.BLACK
        isVisible = true
        PaintThread().start()
        addKeyListener(KeyHandler())

        controller.startNew()
        //controller.go()
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        draw(g)
    }

    private fun draw(g: Graphics) {
        val tick = controller.tick

        fun List<Enemy>.display() {
            for (enemy in this) {
                for (weapon in enemy.weapons) {
                    for (ammo in weapon.ammoManager.ammos) {
                        ammo.display(g, tick.time, ammo.stepX.toInt(), ammo.stepY.toInt())
                    }
                }
                enemy.display(g, tick.time, enemy.stepX.toInt(), enemy.stepY.toInt())
            }
        }

        fun Player.display() {
            for (weapon in this.weapons) {
                for (ammo in weapon.ammoManager.ammos) {
                    ammo.display(g, tick.time, ammo.stepX.toInt(), ammo.stepY.toInt())
                }
            }
            this.display(g, tick.time, this.x.toInt(), 0)
        }

        val data = scenario.data

        synchronized(data) {
            data.enemies!!.display()
            data.player1!!.display()
            data.player2!!.display()
        }
    }

    private inner class PaintThread : Thread("PaintThread") {

        override fun run() {
            val tick = controller.tick

            val interval = 1000L / config.fps
            while (true) {
                if (tick.isStop) {
                    break
                }
                if (!tick.isGoing) {
                    tick.awaitToGo()
                }
//                val rectangles = controller.drawRectangles
//                for (rectangle in rectangles) {
//                    this@GamePanel.repaint(rectangle)
//                }
                this@GamePanel.repaint()
                Current.sleep(interval)
            }
            logger.info("Game over...")
        }
    }

    private inner class KeyHandler : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            when (e.keyCode) {
                KeyEvent.VK_A -> controller.moveLeft(1)
                KeyEvent.VK_D -> controller.moveRight(1)
                KeyEvent.VK_W -> controller.moveUp(1)
                KeyEvent.VK_S -> controller.moveDown(1)
                else -> return
            }
        }
    }
}