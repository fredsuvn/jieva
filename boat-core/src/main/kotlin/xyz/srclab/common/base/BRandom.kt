@file:JvmName("BRandom")

package xyz.srclab.common.base

import java.util.*
import java.util.function.Supplier

private val defaultRandom: Random = Random()

private const val randomDigits = "0123456789"
private const val randomLowerLetters = "abcdefghijklnmopqrstuvwxyz"
private const val randomUpperLetters = "ABCDEFGHIJKLNMOPQRSTUVWXYZ"
private const val randomString = randomDigits + randomLowerLetters + randomUpperLetters

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

@JvmOverloads
fun randomDigits(size: Int, random: Random = defaultRandom): String {
    return randomDigits.randomString(size, random)
}

@JvmOverloads
fun randomLowerLetters(size: Int, random: Random = defaultRandom): String {
    return randomLowerLetters.randomString(size, random)
}

@JvmOverloads
fun randomUpperLetters(size: Int, random: Random = defaultRandom): String {
    return randomUpperLetters.randomString(size, random)
}

@JvmOverloads
fun randomString(size: Int, random: Random = defaultRandom): String {
    return randomString.randomString(size, random)
}

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

open class RandomSupplierBuilder<T : Any> {

    private var builderRandom: Random? = null
    private val builderCases: MutableList<Case<T>> = LinkedList()
    private var scoreCount = 1

    fun score(score: Int, obj: T): RandomSupplierBuilder<T> {
        return score(score) { obj }
    }

    @JvmSynthetic
    fun score(score: Int, supplier: () -> T): RandomSupplierBuilder<T> {
        return score(score, supplier.toSupplier())
    }

    fun score(score: Int, supplier: Supplier<T>): RandomSupplierBuilder<T> {
        builderCases.add(Case(scoreCount, scoreCount + score, supplier))
        scoreCount += score
        return this
    }

    fun random(random: Random): RandomSupplierBuilder<T> {
        this.builderRandom = random
        return this
    }

    fun build(): Supplier<T> {
        return object : Supplier<T> {

            private val random = builderRandom ?: Random()
            private val totalScore = scoreCount
            private val cases = builderCases.sortedWith { a, b -> a.from - b.from }

            override fun get(): T {
                val score = random.between(1, totalScore)
                val index = cases.binarySearch {
                    if (it.from <= score && it.to > score) {
                        return@binarySearch EQUAL_TO
                    }
                    if (it.from > score) {
                        return@binarySearch GREATER_THAN
                    }
                    LESS_THAN
                }
                return cases[index].supplier.get()
            }
        }
    }

    private data class Case<T>(
        val from: Int,
        val to: Int,
        val supplier: Supplier<T>
    )
}