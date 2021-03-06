package xyz.srclab.common.egg.v0

import java.awt.Color
import java.awt.Font
import java.awt.Graphics

private val fonts = HashMap<Int, Font>()

inline fun Graphics.withColor(color: Color, action: (Graphics) -> Unit) {
    val oldColor = this.color
    this.color = color
    action(this)
    this.color = oldColor
}

fun Graphics.withFontSize(size: Int, action: (Graphics) -> Unit) {
    val oldFont = this.font
    if (oldFont.size == size) {
        action(this)
        return
    }
    val cachedFont = fonts[size]
    if (cachedFont !== null) {
        this.font = cachedFont
        action(this)
        this.font = oldFont
        return
    }
    val newFont = Font(oldFont.name, oldFont.style, size)
    fonts[size] = newFont
    this.font = newFont
    action(this)
    this.font = oldFont
}