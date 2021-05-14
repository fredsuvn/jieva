package xyz.srclab.common.egg.nest.o

import xyz.srclab.common.lang.inBounds

internal fun Int.moveSpeedToCoolDown(): Long {
    val interval = (100 - this)
    return interval.inBounds(1, 500).toLong()
}

internal fun Int.fireSpeedToCoolDown(): Long {
    val interval = (100 - this) * 40
    return interval.inBounds(5, 5000).toLong()
}