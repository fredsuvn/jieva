package xyz.srclab.common.egg.boat

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

fun isCollision(u1: OUnit, u2: OUnit): Boolean {
    return distance(u1.x, u1.y, u2.x, u2.y) <= 0
}

private fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    if (x1 == x2) {
        return y2 - y1
    }
    if (y1 == y2) {
        return x2 - x1
    }
    return sqrt((x2 - x1).pow(2.0).absoluteValue + (y2 - y1).pow(2.0).absoluteValue)
}