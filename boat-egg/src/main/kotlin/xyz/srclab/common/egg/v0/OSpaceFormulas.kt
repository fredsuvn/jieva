package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.inBounds

fun Int.moveSpeedToCoolDown(): Long {
    val interval = (100 - this) / 5
    return interval.inBounds(1, 500).toLong()
}

fun Int.fireSpeedToCoolDown(): Long {
    val interval = (100 - this) * 25
    return interval.inBounds(5, 500).toLong()
}