package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.Current
import xyz.srclab.common.egg.sample.View
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel

internal class OSpaceView(
    config: Config,
) : JFrame("O Space Battle"), View {

    init {
        this.isResizable = false
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.layout = BorderLayout()
        this.isVisible = true
        this.setSize(config.width + insets.left + insets.right, config.height + insets.top + insets.bottom)
        this.setLocationRelativeTo(null)
        val gamePanel = GamePanel(config, OSpaceEngine(config), this)
        this.add(gamePanel, BorderLayout.CENTER)
        gamePanel.requestFocus()
    }
}

private class GamePanel(
    private val config: Config,
    private val engine: OSpaceEngine,
    private val view: OSpaceView,
) : JPanel() {

    private var controller = engine.loadNew()
    private val boardColor = Color.WHITE

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

        fun drawVersion() {
            g.withColor(boardColor) {
                it.drawString("v${config.version} ", x, view.insets.top)
            }
        }

        fun List<SubjectUnit>.draw() {
            for (subjectUnit in this) {
                scenario.onDraw(subjectUnit, tick.time, g)
            }
        }

        fun Player.drawScoreboard() {
            val x = if (this.number == 1) 0 else config.width - config.scoreboardWidth
            g.withColor(boardColor) {
                it.drawString("Player ${this.number}", x, view.insets.top + config.scoreboardHeight)
                it.drawString("Hit: ${this.hit}", x, view.insets.top + config.scoreboardHeight * 2)
                it.drawString("Score: ${this.score}", x, view.insets.top + config.scoreboardHeight * 3)
            }
        }

        fun List<Player>.draw() {
            (this as List<SubjectUnit>).draw()
            for (player in this) {
                player.drawScoreboard()
            }
        }

        fun drawInfo() {
            val x = config.width / 2 - config.infoDisplayBoardWidth / 2
            val y = config.height - config.infoDisplayBoardHeight * 4
            g.withColor(boardColor) {
                var height = 0
                for (info in controller.logger.infos) {
                    it.drawString("${info.timestamp}: ${info.message} ", x, y + height)
                    height += config.infoDisplayBoardHeight
                }
            }
        }

        fun drawEndBoard() {
            val x = config.width / 2 - config.endBoardWidth / 2
            val y = config.height / 2 - config.endBoardHeight * 4 / 2
            g.withColor(boardColor) { graphics ->
                graphics.withFontSize(config.endBoardFontSize) {
                    it.drawString("Game Over! ", x, y)
                    it.drawString("Player 1: ${data.player1.score}", x, y + config.endBoardHeight)
                    it.drawString("Player 2: ${data.player2.score}", x, y + config.endBoardHeight * 2)
                    it.drawString("Press G to Again!", x, y + config.endBoardHeight * 3)
                }
            }
        }

        synchronized(data) {
            drawVersion()
            data.enemiesAmmos.draw()
            data.playersAmmos.draw()
            data.players.draw()
            data.enemies.draw()
            drawInfo()
            if (tick.isStop) {
                drawEndBoard()
            }
        }
    }

    private inner class PaintThread : Thread("PaintThread") {

        override fun run() {
            val interval = 1000L / config.fps
            while (true) {
                this@GamePanel.repaint()
                Current.sleep(interval)
            }
        }
    }

    private inner class KeyHandler : KeyAdapter() {

        override fun keyPressed(e: KeyEvent) {
            if (controller.tick.isStop) {
                if (e.keyCode == KeyEvent.VK_G) {
                    controller = engine.loadNew()
                    controller.start()
                    return
                }
                return
            }
            if (e.keyCode == KeyEvent.VK_ESCAPE) {
                controller.toggle()
                return
            }
            if (!controller.tick.isGoing) {
                return
            }
            when (e.keyCode) {
                KeyEvent.VK_W -> controller.pressKey(KeyEvent.VK_W)
                KeyEvent.VK_S -> controller.pressKey(KeyEvent.VK_S)
                KeyEvent.VK_A -> controller.pressKey(KeyEvent.VK_A)
                KeyEvent.VK_D -> controller.pressKey(KeyEvent.VK_D)
                KeyEvent.VK_SPACE -> controller.pressKey(KeyEvent.VK_SPACE)
                KeyEvent.VK_UP -> controller.pressKey(KeyEvent.VK_UP)
                KeyEvent.VK_DOWN -> controller.pressKey(KeyEvent.VK_DOWN)
                KeyEvent.VK_LEFT -> controller.pressKey(KeyEvent.VK_LEFT)
                KeyEvent.VK_RIGHT -> controller.pressKey(KeyEvent.VK_RIGHT)
                KeyEvent.VK_ENTER -> controller.pressKey(KeyEvent.VK_ENTER)
                else -> return
            }
        }

        override fun keyReleased(e: KeyEvent) {
            if (controller.tick.isStop) {
                return
            }
            when (e.keyCode) {
                KeyEvent.VK_W -> controller.releaseKey(KeyEvent.VK_W)
                KeyEvent.VK_S -> controller.releaseKey(KeyEvent.VK_S)
                KeyEvent.VK_A -> controller.releaseKey(KeyEvent.VK_A)
                KeyEvent.VK_D -> controller.releaseKey(KeyEvent.VK_D)
                KeyEvent.VK_SPACE -> controller.releaseKey(KeyEvent.VK_SPACE)
                KeyEvent.VK_UP -> controller.releaseKey(KeyEvent.VK_UP)
                KeyEvent.VK_DOWN -> controller.releaseKey(KeyEvent.VK_DOWN)
                KeyEvent.VK_LEFT -> controller.releaseKey(KeyEvent.VK_LEFT)
                KeyEvent.VK_RIGHT -> controller.releaseKey(KeyEvent.VK_RIGHT)
                KeyEvent.VK_ENTER -> controller.releaseKey(KeyEvent.VK_ENTER)
                else -> return
            }
        }
    }
}