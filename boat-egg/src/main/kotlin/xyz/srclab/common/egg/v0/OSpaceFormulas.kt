package xyz.srclab.common.egg.v0

import xyz.srclab.common.base.inBounds

fun Int.moveSpeedToCoolDown(): Long {
    val interval = (100 - this) / 2
    return interval.inBounds(1, 500).toLong()
}

fun Int.fireSpeedToCoolDown(): Long {
    val interval = (100 - this) * 1000
    return interval.inBounds(5, 5000).toLong()
}