@file:JvmName("Randoms")

package xyz.srclab.common.lang

import java.util.*
import java.util.function.Supplier

@JvmName("between")
fun Random.between(from: Int, to: Int): Int {
    return this.nextInt(to - from) + from
}

/**
 * Interface to get [T] in random.
 */
interface RandomSupplier<T : Any> : Supplier<T> {

    class Builder<T : Any> : CachingProductBuilder<RandomSupplier<T>>() {

        private var builderRandom: Random? = null
        private var builderTotalScore: Int = 1
        private val builderCases: MutableList<Case<T>> = LinkedList()

        fun mayBe(score: Int, `object`: T): Builder<T> {
            return mayBe(score) { `object` }
        }

        fun mayBe(score: Int, supplier: () -> T): Builder<T> {
            builderCases.add(Case(builderTotalScore, builderTotalScore + score, supplier))
            builderTotalScore += score
            this.commitModification()
            return this
        }

        fun mayBe(score: Int, supplier: Supplier<T>): Builder<T> {
            return mayBe(score) { supplier.get() }
        }

        fun random(random: Random): Builder<T> {
            this.builderRandom = random
            this.commitModification()
            return this
        }

        override fun buildNew(): RandomSupplier<T> {
            return object : RandomSupplier<T> {

                private val random = builderRandom ?: Random()
                private var totalScore = builderTotalScore
                private val cases = builderCases.sortedWith { a, b -> a.from - b.from }

                override fun get(): T {
                    val score = random.between(1, totalScore)
                    val index = cases.binarySearch {
                        if (it.from <= score && it.to > score) {
                            return@binarySearch 0
                        }
                        if (it.from > score) {
                            return@binarySearch 1
                        }
                        -1
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