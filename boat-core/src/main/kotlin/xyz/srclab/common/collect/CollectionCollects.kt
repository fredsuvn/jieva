@file:JvmName("Collects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import kotlin.collections.addAll as addAllKt
import kotlin.collections.count as countKt
import kotlin.collections.plus as plusKt
import kotlin.collections.removeAll as removeAllKt
import kotlin.collections.retainAll as retainAllKt
import kotlin.collections.toList as toListKt
import kotlin.collections.toMutableList as toMutableListKt

fun <T> Collection<T>.containsAll(elements: Array<out T>): Boolean {
    return this.containsAll(elements.toListKt())
}

fun <T> Collection<T>.containsAll(elements: Iterable<T>): Boolean {
    return this.containsAll(elements.asToList())
}

fun <T> Collection<T>.count(): Int {
    return this.countKt()
}

fun <T> Collection<T>.toMutableList(): MutableList<T> {
    return this.toMutableListKt()
}

fun <T> Collection<T>.plus(element: T): List<T> {
    return this.plusKt(element)
}

fun <T> Collection<T>.plus(elements: Array<out T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Collection<T>.plus(elements: Iterable<T>): List<T> {
    return this.plusKt(elements)
}

fun <T> Collection<T>.plus(elements: Sequence<T>): List<T> {
    return this.plusKt(elements)
}

fun <T> MutableCollection<T>.addAll(elements: Array<out T>): Boolean {
    return this.addAllKt(elements)
}

fun <T> MutableCollection<T>.addAll(elements: Iterable<T>): Boolean {
    return this.addAllKt(elements)
}

fun <T> MutableCollection<T>.addAll(elements: Sequence<T>): Boolean {
    return this.addAllKt(elements)
}

fun <T> MutableCollection<T>.removeAll(elements: Array<out T>): Boolean {
    return this.removeAllKt(elements)
}

fun <T> MutableCollection<T>.removeAll(elements: Iterable<T>): Boolean {
    return this.removeAllKt(elements)
}

fun <T> MutableCollection<T>.removeAll(elements: Sequence<T>): Boolean {
    return this.removeAllKt(elements)
}

fun <T> MutableCollection<T>.retainAll(elements: Array<out T>): Boolean {
    return this.retainAllKt(elements)
}

fun <T> MutableCollection<T>.retainAll(elements: Iterable<T>): Boolean {
    return this.retainAllKt(elements)
}

fun <T> MutableCollection<T>.retainAll(elements: Sequence<T>): Boolean {
    return this.retainAllKt(elements)
}