package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.base.Current
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JPanel

internal class OView : JFrame("O Space Battle") {

    init {
        this.isResizable = false
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.layout = BorderLayout()
        this.isVisible = true
        this.setSize(
            OConfig.width + insets.left + insets.right, OConfig.height + insets.top + insets.bottom
        )
        this.setLocationRelativeTo(null)
        val gamePanel = GamePanel( this)
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
        PaintThread().start()
        addKeyListener(KeyHandler())

        OController.start()
        //OController.go()
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        draw(g)
    }

    private fun draw(g: Graphics) {
        val tick = OTick
        val data = OController.data
        val scenario = data.scenario

        fun drawVersion() {
            g.withColor(boardColor) {
                it.drawString("v${OConfig.version} ", x, view.insets.top)
            }
        }

        fun List<OObjectUnit>.draw() {
            for (subjectUnit in this) {
                scenario.onDraw(subjectUnit, tick.time, g)
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

        fun List<OPlayer>.draw() {
            (this as List<OObjectUnit>).draw()
            for (player in this) {
                player.drawScoreboard()
            }
        }

        fun drawInfo() {
            val x = OConfig.width / 2 - OConfig.infoDisplayBoardWidth / 2
            val y = OConfig.height - OConfig.infoDisplayBoardHeight * 4
            g.withColor(boardColor) {
                var height = 0
                for (info in OController.logger.infos) {
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
            val interval = 1000L / OConfig.fps
            while (true) {
                this@GamePanel.repaint()
                Current.sleep(interval)
            }
        }
    }

    private inner class KeyHandler : KeyAdapter() {

        override fun keyPressed(e: KeyEvent) {
            if (OTick.isStop) {
                if (e.keyCode == KeyEvent.VK_G) {
                    controller = engine.loadNew()
                    OController.start()
                    return
                }
                return
            }
            if (e.keyCode == KeyEvent.VK_ESCAPE) {
                OController.toggle()
                return
            }
            if (!OTick.isGoing) {
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
            if (OTick.isStop) {
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
    }
}