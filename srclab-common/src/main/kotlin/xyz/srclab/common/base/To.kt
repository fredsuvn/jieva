package xyz.srclab.common.base

import java.math.BigDecimal
import java.math.BigInteger

/**
 * @author sunqian
 */
object To {

    @JvmStatic
    fun toString(any: Any?): String {
        return ToString.toString(any)
    }

    @JvmStatic
    fun toBoolean(any: Any?): Boolean {
        if (any == null) {
            return false
        }
        return any.toString().toBoolean()
    }

    @JvmStatic
    fun toByte(number: Number?): Byte {
        return number?.toByte() ?: 0
    }

    @JvmStatic
    fun toByte(number: String?): Byte {
        return number?.toByte() ?: 0
    }

    @JvmStatic
    fun toByte(any: Any?): Byte {
        if (any == null) {
            return 0
        }
        return if (any is Number) any.toByte() else any.toString().toByte()
    }

    @JvmStatic
    fun toShort(number: Number?): Short {
        return number?.toShort() ?: 0
    }

    @JvmStatic
    fun toShort(number: String?): Short {
        return number?.toShort() ?: 0
    }

    @JvmStatic
    fun toShort(any: Any?): Short {
        if (any == null) {
            return 0
        }
        return if (any is Number) any.toShort() else any.toString().toShort()
    }

    @JvmStatic
    fun toChar(number: Number?): Char {
        return As.notNull(toInt(number))
    }

    @JvmStatic
    fun toChar(number: String?): Char {
        return As.notNull(toInt(number))
    }

    @JvmStatic
    fun toChar(any: Any?): Char {
        return As.notNull(toInt(any))
    }

    @JvmStatic
    fun toInt(number: Number?): Int {
        return number?.toInt() ?: 0
    }

    @JvmStatic
    fun toInt(number: String?): Int {
        return number?.toInt() ?: 0
    }

    @JvmStatic
    fun toInt(any: Any?): Int {
        if (any == null) {
            return 0
        }
        return if (any is Number) any.toInt() else any.toString().toInt()
    }

    @JvmStatic
    fun toLong(number: Number?): Long {
        return number?.toLong() ?: 0
    }

    @JvmStatic
    fun toLong(number: String?): Long {
        return number?.toLong() ?: 0
    }

    @JvmStatic
    fun toLong(any: Any?): Long {
        if (any == null) {
            return 0
        }
        return if (any is Number) any.toLong() else any.toString().toLong()
    }

    @JvmStatic
    fun toFloat(number: Number?): Float {
        return number?.toFloat() ?: 0f
    }

    @JvmStatic
    fun toFloat(number: String?): Float {
        return number?.toFloat() ?: 0f
    }

    @JvmStatic
    fun toFloat(any: Any?): Float {
        if (any == null) {
            return 0f
        }
        return if (any is Number) any.toFloat() else any.toString().toFloat()
    }

    @JvmStatic
    fun toDouble(number: Number?): Double {
        return number?.toDouble() ?: 0.0
    }

    @JvmStatic
    fun toDouble(number: String?): Double {
        return number?.toDouble() ?: 0.0
    }

    @JvmStatic
    fun toDouble(any: Any?): Double {
        if (any == null) {
            return 0.0
        }
        return if (any is Number) any.toDouble() else any.toString().toDouble()
    }

    @JvmStatic
    fun toBigInteger(number: Number?): BigInteger {
        return BigInteger.valueOf(toLong(number))
    }

    @JvmStatic
    fun toBigInteger(number: String?): BigInteger {
        return BigInteger.valueOf(toLong(number))
    }

    @JvmStatic
    fun toBigInteger(any: Any?): BigInteger {
        return BigInteger.valueOf(toLong(any))
    }

    @JvmStatic
    fun toBigDecimal(any: Any?): BigDecimal {
        if (any == null) {
            return BigDecimal.ZERO
        }
        val str = any.toString()
        if ("1" == str) {
            return BigDecimal.ONE
        }
        if ("10" == str) {
            return BigDecimal.TEN
        }
        return BigDecimal(str)
    }
}