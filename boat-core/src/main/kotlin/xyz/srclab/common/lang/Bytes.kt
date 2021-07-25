@file:JvmName("Bytes")

package xyz.srclab.common.lang

import java.nio.ByteBuffer

@JvmOverloads
fun ByteBuffer.toByteArray(permitBacked: Boolean = true): ByteArray {
    if (this.hasArray()) {
        return if (permitBacked) {
            this.array()
        } else {
            this.array().clone()
        }
    }
    val array = ByteArray(this.remaining())
    this.get(array)
    return array
}