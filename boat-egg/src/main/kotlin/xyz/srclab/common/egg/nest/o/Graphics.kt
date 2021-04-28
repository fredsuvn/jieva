package xyz.srclab.common.egg.nest.o

import java.awt.*

private val fonts = HashMap<Int, Font>()

internal inline fun Graphics.withColor(color: Color, action: (Graphics) -> Unit) {
    val oldColor = this.color
    this.color = color
    action(this)
    this.color = oldColor
}

internal inline fun Graphics.withFontSize(size: Int, action: (Graphics) -> Unit) {
    val oldFont = this.font
    if (oldFont.size == size) {
        action(this)
        return
    }
    val cachedFont = fonts.getOrPut(size) { Font(oldFont.name, oldFont.style, size) }
    this.font = cachedFont
    action(this)
    this.font = oldFont
}

internal fun Graphics.drawCenteredString(text: String, rect: Rectangle) {
    // Get the FontMetrics
    val metrics: FontMetrics = this.getFontMetrics(font)
    // Determine the X coordinate for the text
    val x: Int = rect.x + (rect.width - metrics.stringWidth(text)) / 2
    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
    val y: Int = rect.y + (rect.height - metrics.height) / 2 + metrics.ascent
    // Set the font
    //this.font = font
    // Draw the String
    this.drawString(text, x, y)
}