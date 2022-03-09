@file:JvmName("BRandom")

package xyz.srclab.common.base

import java.util.*
import java.util.function.Supplier

private var defaultRandom: Random = Random()

/**
 * Sets default [Random].
 */
fun setDefaultRandom(random: Random) {
    defaultRandom = random
}

/**
 * Gets default [Random].
 */
fun getDefaultRandom(): Random {
    return defaultRandom
}

/**
 * Returns random number in `[from, to)`.
 */
@JvmName("between")
fun randomBetween(from: Int, to: Int): Int {
    return defaultRandom.between(from, to)
}

/**
 * Returns random number in `[from, to)`.
 */
fun Random.between(from: Int, to: Int): Int {
    return this.nextInt(to - from) + from
}

/**
 * Returns a string consists of random chars in `[0, 9]`.
 */
@JvmOverloads
fun randomDigits(size: Int, random: Random = defaultRandom): String {
    return BRandomHolder.randomDigits.randomString(size, random)
}

/**
 * Returns a string consists of random chars in `[a, z]`.
 */
@JvmOverloads
fun randomLowerLetters(size: Int, random: Random = defaultRandom): String {
    return BRandomHolder.randomLowerLetters.randomString(size, random)
}

/**
 * Returns a string consists of random chars in `[A, Z]`.
 */
@JvmOverloads
fun randomUpperLetters(size: Int, random: Random = defaultRandom): String {
    return BRandomHolder.randomUpperLetters.randomString(size, random)
}

/**
 * Returns a string consists of random chars in `[a, z]` and `[A, Z]`.
 */
@JvmOverloads
fun randomLetters(size: Int, random: Random = defaultRandom): String {
    return BRandomHolder.randomLetters.randomString(size, random)
}

/**
 * Returns a string consists of random chars in `[0, 9]`, `[a, z]` and `[A, Z]`.
 */
@JvmOverloads
fun randomString(size: Int, random: Random = defaultRandom): String {
    return BRandomHolder.randomString.randomString(size, random)
}

/**
 * Returns a string consists of random chars in [this].
 */
@JvmOverloads
fun CharSequence.randomString(size: Int, random: Random = defaultRandom): String {
    val result = CharArray(size)
    var i = 0
    while (i < result.size) {
        result[i] = this[random.between(0, this.length)]
        i++
    }
    return String(result)
}

/**
 * Returns a new [RandomSupplierBuilder].
 */
@JvmName("newBuilder")
fun <T : Any> newRandomBuilder(): RandomSupplierBuilder<T> {
    return RandomSupplierBuilder()
}

/**
 * Builder to build a [Supplier] which provides random value.
 *
 * Using `score` methods to add a value or value generator associated with a score.
 * When us [Supplier.get] method of built supplier, it will return random results based on the proportion of scores.
 * For example:
 *
 * ```
 * Supplier<String> sp = new RandomSupplierBuilder<String>()
 *     .score(10, "a")
 *     .score(40, () -> "b")
 *     .build()
 * ```
 *
 * The result of `sp.get()` is 20% to "a" and 80% to "b".
 */
open class RandomSupplierBuilder<T : Any> {

    private var builderRandom: Random? = null
    private var totalScore = 0
    private val values: MutableList<Value<T>> = LinkedList()

    /**
     * Adds value associated with [score].
     */
    fun score(score: Int, value: T) = apply {
        if (score <= 0) {
            throw IllegalArgumentException("Score must > 0.")
        }
        return score(score) { value }
    }

    /**
     * Adds value generator associated with [score].
     */
    fun score(score: Int, value: Supplier<T>) = apply {
        if (score <= 0) {
            throw IllegalArgumentException("Score must > 0.")
        }
        values.add(Value(totalScore, totalScore + score, value))
        totalScore += score
        return this
    }

    /**
     * Sets the random.
     */
    fun random(random: Random) = apply {
        this.builderRandom = random
        return this
    }

    /**
     * Builds and returns final random supplier.
     */
    fun build(): Supplier<T> {
        if (totalScore <= 0 || values.isEmpty()) {
            throw IllegalStateException("There is no possible value to be added.")
        }
        return object : Supplier<T> {

            private val random = builderRandom ?: Random()
            private val totalScore = this@RandomSupplierBuilder.totalScore
            private val values = this@RandomSupplierBuilder.values

            override fun get(): T {
                val score = random.between(0, totalScore)
                val index = values.binarySearch {
                    if (it.scoreFrom <= score && it.scoreTo > score) {
                        return@binarySearch EQUAL_TO
                    }
                    if (it.scoreFrom > score) {
                        return@binarySearch GREATER_THAN
                    }
                    LESS_THAN
                }
                return values[index].supplier.get()
            }
        }
    }

    private data class Value<T>(
        val scoreFrom: Int,
        val scoreTo: Int,
        val supplier: Supplier<T>
    )
}

private object BRandomHolder {
    const val randomDigits = "0123456789"
    const val randomLowerLetters = "abcdefghijklnmopqrstuvwxyz"
    const val randomUpperLetters = "ABCDEFGHIJKLNMOPQRSTUVWXYZ"
    const val randomLetters = randomLowerLetters + randomUpperLetters
    const val randomString = randomDigits + randomLetters
}