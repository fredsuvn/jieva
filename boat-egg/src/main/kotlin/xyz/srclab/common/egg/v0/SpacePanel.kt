package xyz.srclab.common.egg.v0

import java.awt.Color
import java.util.*
import javax.swing.JPanel

/**
 * @author sunqian
 */
class SpacePanel : JPanel() {

    private val livings: List<Living> = LinkedList()
    private val ammos: List<Ammo> = LinkedList()

    init {
        background = Color.BLACK
    }

    private fun refresh() {
        for (living in livings) {

        }
    }
}