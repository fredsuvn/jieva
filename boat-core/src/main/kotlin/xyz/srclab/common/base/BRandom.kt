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
 * Returns a builder for [Randomer].
 */
fun <T : Any> randomerBuilder(): Randomer.Builder<T> {
    return Randomer.Builder()
}

/**
 * Random object generator.
 */
interface Randomer<T : Any> {

    fun next(): T

    class Builder<T : Any> {

        private var builderRandom: Random? = null
        private val builderCases: MutableList<Case<T>> = LinkedList()
        private var scoreCount = 1

        fun score(score: Int, obj: T): Builder<T> {
            return score(score) { obj }
        }

        @JvmSynthetic
        fun score(score: Int, supplier: () -> T): Builder<T> {
            return score(score, supplier.toSupplier())
        }

        fun score(score: Int, supplier: Supplier<T>): Builder<T> {
            builderCases.add(Case(scoreCount, scoreCount + score, supplier))
            scoreCount += score
            return this
        }

        fun random(random: Random): Builder<T> {
            this.builderRandom = random
            return this
        }

        fun build(): Randomer<T> {
            return object : Randomer<T> {

                private val random = builderRandom ?: Random()
                private val totalScore = scoreCount
                private val cases = builderCases.sortedWith { a, b -> a.from - b.from }

                override fun next(): T {
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

    companion object {
        @JvmStatic
        fun <T : Any> newBuilder(): Builder<T> {
            return Builder()
        }
    }
}