package xyz.srclab.common.egg.v0

import xyz.srclab.common.egg.Scenario
import java.awt.Color
import java.awt.Rectangle
import java.util.*
import javax.swing.JPanel

/**
 * @author sunqian
 */
class SpacePanel(
    val scenario: Scenario,
    val fps: Int,
) : JPanel() {

    private val rectangles: MutableCollection<Rectangle> = LinkedList()

    init {
        background = Color.BLACK
    }

    private fun startScenario() {

    }
}