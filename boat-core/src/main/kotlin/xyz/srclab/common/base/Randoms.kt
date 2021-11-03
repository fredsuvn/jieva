@file:JvmName("Randoms")

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
@JvmName("between")
fun Random.between(from: Int, to: Int): Int {
    return this.nextInt(to - from) + from
}

/**
 * Interface to get [T] in random.
 */
interface RandomSupplier<T : Any> : Supplier<T> {

    class Builder<T : Any> : CacheableBuilder<RandomSupplier<T>>() {

        private var builderRandom: Random? = null
        private val builderCases: MutableList<Case<T>> = LinkedList()
        private var scoreCount = 1

        fun score(score: Int, obj: T): Builder<T> {
            return score(score) { obj }
        }

        fun score(score: Int, supplier: () -> T): Builder<T> {
            builderCases.add(Case(scoreCount, scoreCount + score, supplier))
            scoreCount += score
            this.commit()
            return this
        }

        fun score(score: Int, supplier: Supplier<T>): Builder<T> {
            return score(score) { supplier.get() }
        }

        fun random(random: Random): Builder<T> {
            this.builderRandom = random
            this.commit()
            return this
        }

        override fun buildNew(): RandomSupplier<T> {
            return object : RandomSupplier<T> {

                private val random = builderRandom ?: Random()
                private var totalScore = scoreCount
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
                    return cases[index].supplier()
                }
            }
        }

        private data class Case<T>(
            val from: Int,
            val to: Int,
            val supplier: () -> T
        )
    }

    companion object {
        @JvmStatic
        fun <T : Any> newBuilder(): Builder<T> {
            return Builder()
        }
    }
}