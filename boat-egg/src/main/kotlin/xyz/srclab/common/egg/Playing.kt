package xyz.srclab.common.egg

import java.awt.Rectangle

/**
 * The staring [Engine].
 */
interface Playing {

    val rectangles: List<Rectangle>

    fun go()

    fun pause()

    fun stop()
}