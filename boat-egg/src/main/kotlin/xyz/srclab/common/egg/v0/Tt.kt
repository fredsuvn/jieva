package xyz.srclab.common.egg.v0

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import kotlin.system.exitProcess

object EggFrame : JFrame() {

}

fun main() {
    val egg = EggFrame
    egg.setSize(800, 500)
    egg.title = "egg"

    //egg.add(SpacePanel())

    egg.addWindowListener(object : WindowAdapter() {

        override fun windowClosing(e: WindowEvent?) {
            println("windowClosing")
            exitProcess(0)
        }
    })

    egg.addMouseListener(object : MouseAdapter() {

        override fun mouseClicked(e: MouseEvent) {
            egg.graphics.drawOval(e.x - 25, e.y - 25, 50, 50)
        }
    })

    egg.isVisible = true
}