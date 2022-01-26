@file:JvmName("BRange")

package xyz.srclab.common.base

fun remainingLength(totalLength: Int, offset: Int): Int = totalLength - offset

fun remainingLength(totalLength: Long, offset: Long): Long = totalLength - offset

fun needingBlock(totalSize: Int, blockSize: Int): Int {
    val div = totalSize / blockSize
    return if (totalSize % blockSize == 0) div else div + 1
}

fun needingBlock(totalSize: Long, blockSize: Long): Long {
    val div = totalSize / blockSize
    return if (totalSize % blockSize == 0L) div else div + 1
}