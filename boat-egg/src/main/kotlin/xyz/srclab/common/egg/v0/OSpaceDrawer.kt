package xyz.srclab.common.egg.v0

import java.awt.Graphics

internal interface OSpaceDrawer {

    val id: Int

    fun draw(unit: SubjectUnit, tickTime: Long, graphics: Graphics)
}