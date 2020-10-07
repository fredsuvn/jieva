@file:JvmName("As")

package xyz.srclab.common.base

fun <T> Any?.asAny(): T {
    return this as T
}

fun <T : Any> Any?.asNotNull(): T {
    return this as T
}