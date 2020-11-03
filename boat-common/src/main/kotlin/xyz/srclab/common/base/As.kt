@file:JvmName("As")
@file:JvmMultifileClass

package xyz.srclab.common.base

@JvmName("any")
fun <T> Any?.asAny(): T {
    return this as T
}

@JvmName("notNull")
fun <T : Any> T?.asNotNull(): T {
    return this as T
}