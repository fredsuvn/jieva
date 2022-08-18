/**
 * Random utilities.
 */
@file:JvmName("BtRandom")

package xyz.srclab.common.base

import java.io.Serializable
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.function.Supplier
import java.util.stream.DoubleStream
import java.util.stream.IntStream
import java.util.stream.LongStream

private var defaultRandom: Random = ThreadLocalRandomHolder

/**
 * Gets default [Random].
 *
 * Note the default [Random] is based on [ThreadLocalRandom].
 */
fun defaultRandom(): Random {
    return defaultRandom
}

/**
 * Sets default [Random].
 */
fun setDefaultRandom(random: Random) {
    defaultRandom = random
}

/**
 * Returns random int with [defaultRandom].
 */
@JvmName("nextInt")
fun randomInt(): Int {
    return defaultRandom().nextInt()
}

/**
 * Returns random long with [defaultRandom].
 */
@JvmName("nextLong")
fun randomLong(): Long {
    return defaultRandom().nextLong()
}

/**
 * Returns random float with [defaultRandom].
 */
@JvmName("nextFloat")
fun randomFloat(): Float {
    return defaultRandom().nextFloat()
}

/**
 * Returns random double with [defaultRandom].
 */
@JvmName("nextDouble")
fun randomDouble(): Double {
    return defaultRandom().nextDouble()
}

/**
 * Returns random double with [defaultRandom].
 */
@JvmName("nextBoolean")
fun randomBoolean(): Boolean {
    return defaultRandom().nextBoolean()
}

/**
 * Returns random number in `[from, to)`.
 */
@JvmName("between")
fun randomBetween(from: Int, to: Int): Int {
    return defaultRandom().between(from, to)
}

/**
 * Returns random number in `[from, to)`.
 */
fun Random.between(from: Int, to: Int): Int {
    return this.nextInt(to - from) + from
}

/**
 * Returns random [size] long string of which consisted chars come from given [chars].
 */
fun randomString(size: Int, chars: CharSequence): String {
    return defaultRandom().getString(size, chars)
}

/**
 * Returns random [size] long string of which consisted chars come from given [chars].
 */
fun Random.getString(size: Int, chars: CharSequence): String {
    if (size == 0) {
        return ""
    }
    if (chars.isEmpty()) {
        throw IllegalArgumentException("GIven chars is null or empty.")
    }
    val builder = StringBuilder(size)
    for (i in 0 until size) {
        builder.append(chars[this.nextInt(chars.length)])
    }
    return builder.toString()
}

/**
 * Returns a new [RandomBuilder].
 */
@JvmName("newBuilder")
fun <T : Any> randomBuilder(): RandomBuilder<T> {
    return RandomBuilder()
}

private object ThreadLocalRandomHolder : Random() {

    override fun setSeed(seed: Long) {
        ThreadLocalRandom.current().setSeed(seed)
    }

    override fun nextBytes(bytes: ByteArray?) {
        ThreadLocalRandom.current().nextBytes(bytes)
    }

    override fun nextInt(): Int {
        return ThreadLocalRandom.current().nextInt()
    }

    override fun nextInt(bound: Int): Int {
        return ThreadLocalRandom.current().nextInt(bound)
    }

    override fun nextLong(): Long {
        return ThreadLocalRandom.current().nextLong()
    }

    override fun nextBoolean(): Boolean {
        return ThreadLocalRandom.current().nextBoolean()
    }

    override fun nextFloat(): Float {
        return ThreadLocalRandom.current().nextFloat()
    }

    override fun nextDouble(): Double {
        return ThreadLocalRandom.current().nextDouble()
    }

    override fun nextGaussian(): Double {
        return ThreadLocalRandom.current().nextGaussian()
    }

    override fun ints(streamSize: Long): IntStream {
        return ThreadLocalRandom.current().ints(streamSize)
    }

    override fun ints(): IntStream {
        return ThreadLocalRandom.current().ints()
    }

    override fun ints(streamSize: Long, randomNumberOrigin: Int, randomNumberBound: Int): IntStream {
        return ThreadLocalRandom.current().ints(streamSize, randomNumberOrigin, randomNumberBound)
    }

    override fun ints(randomNumberOrigin: Int, randomNumberBound: Int): IntStream {
        return ThreadLocalRandom.current().ints(randomNumberOrigin, randomNumberBound)
    }

    override fun longs(streamSize: Long): LongStream {
        return ThreadLocalRandom.current().longs(streamSize)
    }

    override fun longs(): LongStream {
        return ThreadLocalRandom.current().longs()
    }

    override fun longs(streamSize: Long, randomNumberOrigin: Long, randomNumberBound: Long): LongStream {
        return ThreadLocalRandom.current().longs(streamSize, randomNumberOrigin, randomNumberBound)
    }

    override fun longs(randomNumberOrigin: Long, randomNumberBound: Long): LongStream {
        return ThreadLocalRandom.current().longs(randomNumberOrigin, randomNumberBound)
    }

    override fun doubles(streamSize: Long): DoubleStream {
        return ThreadLocalRandom.current().doubles(streamSize)
    }

    override fun doubles(): DoubleStream {
        return ThreadLocalRandom.current().doubles()
    }

    override fun doubles(streamSize: Long, randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream {
        return ThreadLocalRandom.current().doubles(streamSize, randomNumberOrigin, randomNumberBound)
    }

    override fun doubles(randomNumberOrigin: Double, randomNumberBound: Double): DoubleStream {
        return ThreadLocalRandom.current().doubles(randomNumberOrigin, randomNumberBound)
    }
}

/**
 * Builder to build a [Supplier] which provides random value.
 *
 * Using `score` methods to add a value or value generator associated with a score.
 * When us [Supplier.get] method of built supplier, it will return random results based on the proportion of scores.
 * For example:
 *
 * ```
 * Supplier<String> sp = new RandomBuilder<String>()
 *     .score(10, "a")
 *     .score(40, () -> "b")
 *     .build()
 * ```
 *
 * The result of `sp.get()` is 20% to "a" and 80% to "b".
 */
open class RandomBuilder<T : Any> {

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
        return RandomSupplier(builderRandom ?: Random(), totalScore, ArrayList(values))
    }

    private class RandomSupplier<T>(
        private val random: Random,
        private val totalScore: Int,
        private val values: MutableList<Value<T>>,
    ) : Supplier<T>, Serializable {
        override fun get(): T {
            val score = random.between(0, totalScore)
            val index = values.binarySearch {
                if (it.scoreFrom <= score && it.scoreTo > score) {
                    return@binarySearch 0
                }
                if (it.scoreFrom > score) {
                    return@binarySearch 1
                }
                -1
            }
            return values[index].supplier.get()
        }
    }

    private data class Value<T>(
        val scoreFrom: Int,
        val scoreTo: Int,
        val supplier: Supplier<T>
    )
}