package xyz.srclab.common.utils

import java.util.concurrent.atomic.AtomicLong

/**
 * Simple counter.
 */
interface Counter {

    /**
     * Gets current value.
     */
    fun getInt(): Int {
        return getLong().toInt()
    }

    /**
     * Sets current value.
     */
    fun setInt(value: Int) {
        setLong(value.toLong())
    }

    /**
     * Gets current value as long.
     */
    fun getLong(): Long

    /**
     * Sets current value.
     */
    fun setLong(value: Long)

    /**
     * Gets current value, then adds.
     * The returned value before the adding.
     */
    fun getAndAddInt(value: Int): Int {
        return getAndAddLong(value.toLong()).toInt()
    }

    /**
     * Gets current value as long, then adds.
     * The returned value before the adding.
     */
    fun getAndAddLong(value: Long): Long

    /**
     * Adds current value, then returns.
     * The returned value after the adding.
     */
    fun addAndGetInt(value: Int): Int {
        return addAndGetLong(value.toLong()).toInt()
    }

    /**
     * Adds current value, then returns.
     * The returned value after the adding.
     */
    fun addAndGetLong(value: Long): Long

    /**
     * Gets current value, then increment.
     * The returned value before the increment.
     */
    fun getAndIncrementInt(): Int {
        return getAndIncrementLong().toInt()
    }

    /**
     * Gets current value as long, then increment.
     * The returned value before the increment.
     */
    fun getAndIncrementLong(): Long

    /**
     * Increases current value, then returns.
     * The returned value after the increment.
     */
    fun incrementAndGetInt(): Int {
        return incrementAndGetLong().toInt()
    }

    /**
     * Increases current value, then returns.
     * The returned value after the increment.
     */
    fun incrementAndGetLong(): Long

    /**
     * Resets current value.
     */
    fun reset()

    companion object {

        /**
         * Returns the [Counter] of which initial value is [this].
         * @param atomic whether set the [Counter] thread-safe
         */
        @JvmName("startsAt")
        @JvmOverloads
        @JvmStatic
        fun Int.counterStarts(atomic: Boolean = false): Counter {
            return if (atomic) AtomicCounter(AtomicLong(this.toLong())) else SimpleCounter(this.toLong())
        }

        /**
         * Returns the [Counter] of which initial value is [this].
         * @param atomic whether set the [Counter] thread-safe
         */
        @JvmName("startsAt")
        @JvmOverloads
        @JvmStatic
        fun Long.counterStarts(atomic: Boolean = false): Counter {
            return if (atomic) AtomicCounter(AtomicLong(this)) else SimpleCounter(this)
        }

        private class SimpleCounter(private var value: Long, private val initValue: Long = value) : Counter {

            override fun getLong(): Long {
                return value
            }

            override fun setLong(value: Long) {
                this.value = value
            }

            override fun getAndAddLong(value: Long): Long {
                val result = this.value
                this.value += value
                return result
            }

            override fun addAndGetLong(value: Long): Long {
                this.value += value
                return this.value
            }

            override fun getAndIncrementLong(): Long {
                return this.value++
            }

            override fun incrementAndGetLong(): Long {
                return ++this.value
            }

            override fun reset() {
                value = initValue
            }
        }

        private class AtomicCounter(
            private val atomicLong: AtomicLong,
            private val initValue: Long = atomicLong.get()
        ) :
            Counter {

            override fun getLong(): Long {
                return atomicLong.get()
            }

            override fun setLong(value: Long) {
                atomicLong.set(value)
            }

            override fun getAndAddLong(value: Long): Long {
                return atomicLong.getAndAdd(value)
            }

            override fun addAndGetLong(value: Long): Long {
                return atomicLong.addAndGet(value)
            }

            override fun getAndIncrementLong(): Long {
                return atomicLong.getAndIncrement()
            }

            override fun incrementAndGetLong(): Long {
                return atomicLong.incrementAndGet()
            }

            override fun reset() {
                atomicLong.set(initValue)
            }
        }
    }
}