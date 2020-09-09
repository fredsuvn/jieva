package xyz.srclab.common.base

object Chars {

    @JvmStatic
    fun toString(chars: CharArray): String {
        return String(chars)
    }

    @JvmStatic
    fun toString(bytes: ByteArray): String {
        return String(bytes, Defaults.charset())
    }

    @JvmStatic
    fun toBytes(chars: CharArray): ByteArray {
        return String(chars).toByteArray(Defaults.charset())
    }

    @JvmStatic
    fun toBytes(chars: CharSequence): ByteArray {
        return chars.toString().toByteArray(Defaults.charset())
    }
}