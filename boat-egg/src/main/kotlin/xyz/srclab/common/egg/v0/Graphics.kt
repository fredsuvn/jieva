package xyz.srclab.common.egg.v0

import java.awt.Color
import java.awt.Graphics

fun Graphics.withColor(color: Color, action: (Graphics) -> Unit) {
    val oldColor = this.color
    this.color = color
    action(this)
    this.color = oldColor
}