package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.inBounds

internal fun Int.moveSpeedToCoolDown(): Long {
    val interval = (100 - this)
    return interval.inBounds(1, 500).toLong()
}

internal fun Int.fireSpeedToCoolDown(): Long {
    val interval = (100 - this) * 40
    return interval.inBounds(5, 5000).toLong()
}