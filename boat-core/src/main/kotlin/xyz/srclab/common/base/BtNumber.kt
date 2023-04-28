/**
 * Number utilities.
 */
@file:JvmName("BtNumber")

package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.text.toBigDecimal as toBigDecimalKt
import kotlin.text.toBigInteger as toBigIntegerKt
import kotlin.text.toByte as toByteKt
import kotlin.text.toDouble as toDoubleKt
import kotlin.text.toFloat as toFloatKt
import kotlin.text.toInt as toIntKt
import kotlin.text.toLong as toLongKt
import kotlin.text.toShort as toShortKt

/**
 * A hundred.
 */
@JvmField
val HUNDRED: BigDecimal = BigDecimal("100")

/**
 * A thousand.
 */
@JvmField
val THOUSAND: BigDecimal = BigDecimal("1000")

/**
 * A hundred.
 */
@JvmField
val HUNDRED_INT: BigInteger = HUNDRED.toBigInteger()

/**
 * A thousand.
 */
@JvmField
val THOUSAND_INT: BigInteger = THOUSAND.toBigInteger()

/**
 * Converts chars to byte.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toByte(radix: Int = BtProps.radix()): Byte {
    return this.toString().toByteKt(radix)
}

/**
 * Converts chars to short.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toShort(radix: Int = BtProps.radix()): Short {
    return this.toString().toShortKt(radix)
}

/**
 * Converts chars to int.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toInt(radix: Int = BtProps.radix()): Int {
    return this.toString().toIntKt(radix)
}

/**
 * Converts chars to long.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toLong(radix: Int = BtProps.radix()): Long {
    return this.toString().toLongKt(radix)
}

/**
 * Converts chars to float.
 */
@Throws(NumberFormatException::class)
fun CharSequence.toFloat(): Float {
    return this.toString().toFloatKt()
}

/**
 * Converts chars to double.
 */
@Throws(NumberFormatException::class)
fun CharSequence.toDouble(): Double {
    return this.toString().toDoubleKt()
}

/**
 * Converts chars to [BigInteger].
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toBigInteger(radix: Int = BtProps.radix()): BigInteger {
    val str = this.toString()
    if (radix == 10) {
        return when (str) {
            "0" -> BigInteger.ZERO
            "1" -> BigInteger.ONE
            "10" -> BigInteger.TEN
            "100" -> HUNDRED_INT
            "1000" -> THOUSAND_INT
            else -> str.toBigIntegerKt()
        }
    }
    return str.toBigIntegerKt(radix)
}

/**
 * Converts chars to [BigDecimal].
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toBigDecimal(mathContext: MathContext = MathContext.UNLIMITED): BigDecimal {
    return when (val str = this.toString()) {
        "0" -> BigDecimal.ZERO
        "1" -> BigDecimal.ONE
        "10" -> BigDecimal.TEN
        "100" -> HUNDRED
        "1000" -> THOUSAND
        else -> str.toBigDecimalKt(mathContext)
    }
}

/**
 * Returns unsigned int.
 */
fun Byte.toUnsignedInt(): Int {
    return this.toInt() and 0x0000_00FF
}

/**
 * Returns unsigned long.
 */
fun Byte.toUnsignedLong(): Int {
    return this.toUnsignedInt() and 0x0000_00FF
}

/**
 * Returns unsigned int.
 */
fun Short.toUnsignedInt(): Int {
    return this.toInt() and 0x0000_FFFF
}

/**
 * Returns unsigned long.
 */
fun Short.toUnsignedLong(): Long {
    return this.toUnsignedInt().toLong() and 0x0000_FFFFL
}

/**
 * Returns unsigned long.
 */
fun Int.toUnsignedLong(): Long {
    return this.toLong() and 0x0000_0000_FFFF_FFFFL
}

/**
 * **Unsigned** int to binary string by [Integer.toBinaryString].
 * It will pad `0` at left if the [size] is shorter than length of the binary string.
 */
@JvmOverloads
fun Int.toBinaryString(size: Int = 32): String {
    return StringUtils.leftPad(Integer.toBinaryString(this), size, "0")
}

/**
 * **Unsigned** long to binary string by [java.lang.Long.toBinaryString].
 * It will pad `0` at left if the [size] is shorter than length of the binary string.
 */
@JvmOverloads
fun Long.toBinaryString(size: Int = 64): String {
    return StringUtils.leftPad(JavaLong.toBinaryString(this), size, "0")
}

/**
 * **Unsigned** int to hex string by [Integer.toHexString].
 * It will pad `0` at left if the [size] is shorter than length of the hex string.
 */
@JvmOverloads
fun Int.toHexString(size: Int = 8): String {
    return StringUtils.leftPad(Integer.toHexString(this), size, "0")
}

/**
 * **Unsigned** long to hex string by [java.lang.Long.toHexString].
 * It will pad `0` at left if the [size] is shorter than length of the hex string.
 */
@JvmOverloads
fun Long.toHexString(size: Int = 16): String {
    return StringUtils.leftPad(JavaLong.toHexString(this), size, "0")
}

/**
 * **Unsigned** int to octal string by [Integer.toOctalString].
 * It will pad `0` at left if the [size] is shorter than length of the octal string.
 */
@JvmOverloads
fun Int.toOctalString(size: Int = 11): String {
    return StringUtils.leftPad(Integer.toOctalString(this), size, "0")
}

/**
 * **Unsigned** long to octal string by [java.lang.Long.toOctalString].
 * It will pad `0` at left if the [size] is shorter than length of the octal string.
 */
@JvmOverloads
fun Long.toOctalString(size: Int = 22): String {
    return StringUtils.leftPad(JavaLong.toOctalString(this), size, "0")
}