package xyz.srclab.common.egg.nest.o

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

internal fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    if (x1 == x2) {
        return y2 - y1
    }
    if (y1 == y2) {
        return x2 - x1
    }
    return sqrt((x2 - x1).pow(2.0).absoluteValue + (y2 - y1).pow(2.0).absoluteValue)
}

internal fun step(x: Double, y: Double, targetX: Double, targetY: Double): Point {
    if (x == targetX) {
        val stepX = 0.0
        val stepY = if (targetY > y) 1.0 else if (targetY < y) -1.0 else 0.0
        return Point(stepX, stepY)
    }
    if (y == targetY) {
        val stepX = if (targetX > x) 1.0 else if (targetX < x) -1.0 else 0.0
        val stepY = 0.0
        return Point(stepX, stepY)
    }
    val distance = distance(x, y, targetX, targetY)
    val stepX = (targetX - x) / distance
    val stepY = (targetY - y) / distance
    return Point(stepX, stepY)
}

internal data class Point(val x: Double, val y: Double)