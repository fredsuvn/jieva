package xyz.srclab.common.egg.v0

import java.awt.Color
import java.awt.Font
import java.awt.Graphics

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