@file:JvmName("BMath")

package xyz.srclab.common.base

/**
 * Returns remaining length of [size] from [offset].
 */
fun remainingLength(size: Int, offset: Int): Int = size - offset

/**
 * Returns remaining length of [size] from [offset].
 */
fun remainingLength(size: Long, offset: Long): Long = size - offset

/**
 * Returns at least block number to meet: [blockSize] * number >= [size].
 */
fun blockAtLeast(size: Int, blockSize: Int): Int {
    val num = size / blockSize
    return if (size % blockSize == 0) num else num + 1
}

/**
 * Returns at least block number to meet: [blockSize] * number >= [size].
 */
fun blockAtLeast(size: Long, blockSize: Long): Long {
    val num = size / blockSize
    return if (size % blockSize == 0L) num else num + 1
}

/**
 * Returns at least block number to meet: [oldBlockSize] * number >= [size].
 */
fun blockAtLeast(size: Int, oldBlockSize: Int, newBlockSize:Int): Int {
    val num = size / oldBlockSize
    return if (size % oldBlockSize == 0) num else num + 1
}