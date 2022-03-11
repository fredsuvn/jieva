@file:JvmName("BSet")

package xyz.srclab.common.collect

import kotlin.collections.minus as minusKt
import kotlin.collections.plus as plusKt

/**
 * Returns new [LinkedHashSet] with [elements].
 */
fun <T> newSet(vararg elements: T): LinkedHashSet<T> {
    return LinkedHashSet<T>().collect(*elements)
}

fun <T> Set<T>.plus(element: T): Set<T> {
    return this.plusKt(element)
}

fun <T> Set<T>.plus(elements: Array<out T>): Set<T> {
    return this.plusKt(elements)
}

fun <T> Set<T>.plus(elements: Iterable<T>): Set<T> {
    return this.plusKt(elements)
}

fun <T> Set<T>.plus(elements: Sequence<T>): Set<T> {
    return this.plusKt(elements)
}

fun <T> Set<T>.minus(element: T): Set<T> {
    return this.minusKt(element)
}

fun <T> Set<T>.minus(elements: Array<out T>): Set<T> {
    return this.minusKt(elements)
}

fun <T> Set<T>.minus(elements: Iterable<T>): Set<T> {
    return this.minusKt(elements)
}

fun <T> Set<T>.minus(elements: Sequence<T>): Set<T> {
    return this.minusKt(elements)
}