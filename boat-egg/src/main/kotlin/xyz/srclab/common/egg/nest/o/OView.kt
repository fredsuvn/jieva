package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.Current
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel

internal class OView : JFrame(OConfig.name) {

    init {
        this.isResizable = false
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.layout = BorderLayout()
        this.isVisible = true
        this.setSize(
            OConfig.width + insets.left + insets.right, OConfig.height + insets.top + insets.bottom
        )
        this.setLocationRelativeTo(null)
        val gamePanel = GamePanel(this)
        this.add(gamePanel, BorderLayout.CENTER)
        gamePanel.requestFocus()
    }
}

private class GamePanel(
    private val view: OView,
) : JPanel() {

    private val boardColor = Color.WHITE

    init {
        background = Color.BLACK
        isVisible = true
        val keyHandler = KeyHandler()
        addKeyListener(keyHandler)

        OController.start()
        PaintThread().start()
        keyHandler.start()
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        draw(g)
    }

    private fun draw(g: Graphics) {

        val data = OController.data
        val tick = OController.tick

        fun drawVersion() {
            g.withColor(boardColor) {
                it.drawString("v${OConfig.version} ", x, view.insets.top)
            }
        }

        fun List<OObjectUnit>.draw() {
            for (unit in this) {
                ODrawer.draw(unit, tick.time, g)
            }
        }

        fun OPlayer.drawScoreboard() {
            val x = if (this.number == 1) 0 else OConfig.width - OConfig.scoreboardWidth
            g.withColor(boardColor) {
                it.drawString("Player ${this.number}", x, view.insets.top + OConfig.scoreboardHeight)
                it.drawString("Hit: ${this.hit}", x, view.insets.top + OConfig.scoreboardHeight * 2)
                it.drawString("Score: ${this.score}", x, view.insets.top + OConfig.scoreboardHeight * 3)
            }
        }

        fun drawInfo() {
            val x = OConfig.width / 2 - OConfig.infoDisplayBoardWidth / 2
            val y = OConfig.height - OConfig.infoDisplayBoardHeight * 4
            g.withColor(boardColor) {
                var height = 0
                for (info in OLogger.infos) {
                    it.drawString("${info.timestamp}: ${info.message} ", x, y + height)
                    height += OConfig.infoDisplayBoardHeight
                }
            }
        }

        fun drawEndBoard() {
            val x = OConfig.width / 2 - OConfig.endBoardWidth / 2
            val y = OConfig.height / 2 - OConfig.endBoardHeight * 4 / 2
            g.withColor(boardColor) { graphics ->
                graphics.withFontSize(OConfig.endBoardFontSize) {
                    it.drawString("Game Over! ", x, y)
                    it.drawString("Player 1: ${data.player1.score}", x, y + OConfig.endBoardHeight)
                    it.drawString("Player 2: ${data.player2.score}", x, y + OConfig.endBoardHeight * 2)
                    it.drawString("Press G to Again!", x, y + OConfig.endBoardHeight * 3)
                }
            }
        }

        synchronized(data) {
            drawVersion()
            data.player1.drawScoreboard()
            data.player2.drawScoreboard()
            data.humanAmmos.draw()
            data.enemyAmmos.draw()
            data.humanSubjects.draw()
            data.enemySubjects.draw()
            drawInfo()
            if (tick.isStop) {
                drawEndBoard()
            }
        }
    }

    private inner class PaintThread : Thread("PaintThread") {

        override fun run() {
            val interval = 1000L / OConfig.fps
            while (true) {
                this@GamePanel.repaint()
                Current.sleep(interval)
            }
        }
    }

    private inner class KeyHandler : KeyAdapter() {

        private var isStarted: Boolean = false

        override fun keyPressed(e: KeyEvent) {
            if (!isStarted) {
                return
            }
            val tick = OController.tick
            if (tick.isStop) {
                if (e.keyCode == KeyEvent.VK_G) {
                    OController.clearKeys()
                    OController.start()
                    return
                }
                return
            }
            if (e.keyCode == KeyEvent.VK_ESCAPE) {
                OController.toggle()
                return
            }
            if (!tick.isGoing) {
                return
            }
            when (e.keyCode) {
                KeyEvent.VK_W -> OController.pressKey(KeyEvent.VK_W)
                KeyEvent.VK_S -> OController.pressKey(KeyEvent.VK_S)
                KeyEvent.VK_A -> OController.pressKey(KeyEvent.VK_A)
                KeyEvent.VK_D -> OController.pressKey(KeyEvent.VK_D)
                KeyEvent.VK_SPACE -> OController.pressKey(KeyEvent.VK_SPACE)
                KeyEvent.VK_UP -> OController.pressKey(KeyEvent.VK_UP)
                KeyEvent.VK_DOWN -> OController.pressKey(KeyEvent.VK_DOWN)
                KeyEvent.VK_LEFT -> OController.pressKey(KeyEvent.VK_LEFT)
                KeyEvent.VK_RIGHT -> OController.pressKey(KeyEvent.VK_RIGHT)
                KeyEvent.VK_ENTER -> OController.pressKey(KeyEvent.VK_ENTER)
                else -> return
            }
        }

        override fun keyReleased(e: KeyEvent) {
            if (!isStarted) {
                return
            }
            val tick = OController.tick
            if (tick.isStop) {
                return
            }
            when (e.keyCode) {
                KeyEvent.VK_W -> OController.releaseKey(KeyEvent.VK_W)
                KeyEvent.VK_S -> OController.releaseKey(KeyEvent.VK_S)
                KeyEvent.VK_A -> OController.releaseKey(KeyEvent.VK_A)
                KeyEvent.VK_D -> OController.releaseKey(KeyEvent.VK_D)
                KeyEvent.VK_SPACE -> OController.releaseKey(KeyEvent.VK_SPACE)
                KeyEvent.VK_UP -> OController.releaseKey(KeyEvent.VK_UP)
                KeyEvent.VK_DOWN -> OController.releaseKey(KeyEvent.VK_DOWN)
                KeyEvent.VK_LEFT -> OController.releaseKey(KeyEvent.VK_LEFT)
                KeyEvent.VK_RIGHT -> OController.releaseKey(KeyEvent.VK_RIGHT)
                KeyEvent.VK_ENTER -> OController.releaseKey(KeyEvent.VK_ENTER)
                else -> return
            }
        }

        fun start() {
            isStarted = true
        }
    }
}