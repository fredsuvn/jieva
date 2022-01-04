@file:JvmName("BRandom")

package xyz.srclab.common.base

import java.util.*
import java.util.function.Supplier

private val random: Random = Random()

/**
 * Returns random number in `[from, to)`.
 */
@JvmName("between")
fun randomBetween(from: Int, to: Int): Int {
    return random.between(from, to)
}

/**
 * Returns random number in `[from, to)`.
 */
fun Random.between(from: Int, to: Int): Int {
    return this.nextInt(to - from) + from
}

/**
 * Returns a new [RandomSupplierBuilder].
 */
fun <T : Any> randomSupplierBuilder(): RandomSupplierBuilder<T> {
    return RandomSupplierBuilder()
}


class RandomSupplierBuilder<T : Any> {

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