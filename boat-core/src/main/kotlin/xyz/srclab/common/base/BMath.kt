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
 * Returns end index exclusive computed from [offset] and [length]:
 *
 * ```
 * return offset + length;
 * ```
 */
fun endIndex(offset: Int, length: Int): Int = offset + length

/**
 * Returns end index exclusive computed from [offset] and [length]:
 *
 * ```
 * return offset + length;
 * ```
 */
fun endIndex(offset: Long, length: Long): Long = offset + length

/**
 * Returns the least number to meet: [blockSize] * `number` >= [size].
 */
fun blockNumber(size: Int, blockSize: Int): Int {
    val num = size / blockSize
    return if (size % blockSize == 0) num else num + 1
}

/**
 * Returns the least number to meet: [blockSize] * `number` >= [size].
 */
fun blockNumber(size: Long, blockSize: Long): Long {
    val num = size / blockSize
    return if (size % blockSize == 0L) num else num + 1
}

/**
 * Separates the [oldSize] with [oldBlockSize], then change the block size to [newBlockSize],
 * returns the least size for new block size. This function is equivalent to:
 *
 * ```
 * int newSize = oldSize / oldBlockSize * newBlockSize;
 * if (oldSize % oldBlockSize == 0) {
 *     return newSize;
 * }
 * return newSize + newBlockSize;
 * ```
 */
fun newSizeForBlock(oldSize: Int, oldBlockSize: Int, newBlockSize: Int): Int {
    val newSize = oldSize / oldBlockSize * newBlockSize
    return if (oldSize % oldBlockSize == 0) newSize else newSize + newBlockSize
}

/**
 * Separates the [oldSize] with [oldBlockSize], then change the block size to [newBlockSize],
 * returns the least size for new block size. This function is equivalent to:
 *
 * ```
 * int newSize = oldSize / oldBlockSize * newBlockSize;
 * if (oldSize % oldBlockSize == 0) {
 *     return newSize;
 * }
 * return newSize + newBlockSize;
 * ```
 */
fun newSizeForBlock(oldSize: Long, oldBlockSize: Long, newBlockSize: Long): Long {
    val newSize = oldSize / oldBlockSize * newBlockSize
    return if (oldSize % oldBlockSize == 0L) newSize else newSize + newBlockSize
}