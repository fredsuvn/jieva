package xyz.srclab.common.egg.boat

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

const val HYPOTENUSE_COEFFICIENT = 0.70710678118655

fun isCollision(u1: OUnit, u2: OUnit): Boolean {
    return distance(u1.x, u1.y, u2.x, u2.y) < u1.radius + u2.radius
}

private fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    if (x1 == x2) {
        return (y2 - y1).absoluteValue
    }
    if (y1 == y2) {
        return (x2 - x1).absoluteValue
    }
    return sqrt((x2 - x1).pow(2.0).absoluteValue + (y2 - y1).pow(2.0).absoluteValue)
}

fun isInBounds(u: OUnit, config: OConfig): Boolean {
    return u.x >= -config.preparedWidth.toDouble()
        && u.x <= (config.preparedWidth + config.screenWidth).toDouble()
        && u.y >= -config.preparedHeight.toDouble()
        && u.y <= (config.preparedHeight + config.screenHeight).toDouble()
}

fun isInScreen(u: OUnit, config: OConfig): Boolean {
    return u.x >= 0
        && u.x <= config.screenWidth
        && u.y >= 0
        && u.y <= config.screenHeight
}