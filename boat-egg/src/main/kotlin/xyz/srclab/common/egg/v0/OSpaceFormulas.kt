package xyz.srclab.common.egg.v0

fun Int.moveSpeedToCoolDown(): Long {
    val interval = (100 - this) / 10L
    return if (interval <= 0) 1L else interval
}

fun Int.fireSpeedToCoolDown(): Long {
    val interval = (100 - this) / 100L
    return if (interval <= 0) 100L else interval
}