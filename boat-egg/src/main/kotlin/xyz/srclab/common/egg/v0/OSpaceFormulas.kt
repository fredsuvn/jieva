package xyz.srclab.common.egg.v0

fun Int.moveSpeedToCoolDown(): Long {
    val interval = (100 - this) / 5L
    return if (interval <= 0) 1L else interval
}

fun Int.fireSpeedToCoolDown(): Long {
    val interval = (100 - this) * 25L
    return if (interval <= 0) 500 else interval
}