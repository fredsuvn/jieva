package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.egg.sample.View
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel

internal class OSpaceView(
    private val config: OSpaceConfig,
) : JFrame("O Space Battle"), View<OSpaceEngine, OSpaceController, OSpaceData, OSpaceScenario> {

    init {
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(config.width, config.height)
        setLocationRelativeTo(null)
        isVisible = true
        val gamePanel = GamePanel(config, OSpaceEngine(config))
        add(gamePanel)
        gamePanel.requestFocus()
    }
}

private class GamePanel(
    private val config: OSpaceConfig,
    engine: OSpaceEngine,
) : JPanel() {

    private val controller = engine.loadNew()

    init {
        background = Color.BLACK
        isVisible = true
        PaintThread().start()
        addKeyListener(KeyHandler())

        controller.start()
        //controller.go()
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        draw(g)
    }

    private fun draw(g: Graphics) {
        val tick = controller.tick
        val data = controller.data
        val scenario = data.scenario

        fun List<SubjectUnit>.draw() {
            for (subjectUnit in this) {
                scenario.onDraw(subjectUnit, tick.time, g)
            }
        }

        synchronized(data) {
            data.enemiesAmmos.draw()
            data.playersAmmos.draw()
            data.players.draw()
            data.enemies.draw()
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
            //OSpaceLogger.info("Game over...")
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