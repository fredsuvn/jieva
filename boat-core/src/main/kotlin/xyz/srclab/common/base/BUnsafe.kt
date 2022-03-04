@file:JvmName("BUnsafe")

package xyz.srclab.common.base

/**
 * Returns given length of given array ([this]).
 */
fun Any.arrayLength(): Int {
    return java.lang.reflect.Array.getLength(this)
}