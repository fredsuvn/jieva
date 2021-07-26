package xyz.srclab.common.utils

import java.util.concurrent.atomic.AtomicLong

/**
 * Simple counter.
 *
 * @author sunqian
 */
interface Counter {

    @JvmDefault
    fun getInt(): Int {
        return getLong().toInt()
    }

    @JvmDefault
    fun setInt(value: Int) {
        setLong(value.toLong())
    }

    fun getLong(): Long

    fun setLong(value: Long)

    @JvmDefault
    fun getAndAddInt(value: Int): Int {
        return getAndAddLong(value.toLong()).toInt()
    }

    fun getAndAddLong(value: Long): Long

    @JvmDefault
    fun addAndGetInt(value: Int): Int {
        return addAndGetLong(value.toLong()).toInt()
    }

    fun addAndGetLong(value: Long): Long

    @JvmDefault
    fun getAndIncrementInt(): Int {
        return getAndIncrementLong().toInt()
    }

    fun getAndIncrementLong(): Long

    @JvmDefault
    fun incrementAndGetInt(): Int {
        return incrementAndGetLong().toInt()
    }

    fun incrementAndGetLong(): Long

    fun reset()

    companion object {

        @JvmStatic
        @JvmName("startsAt")
        fun Int.counterStarts(): Counter {
            return SimpleCounter(this.toLong())
        }

        @JvmStatic
        @JvmName("startsAt")
        fun Int.counterStarts(atomic: Boolean = false): Counter {
            return if (atomic) AtomicCounter(AtomicLong(this.toLong())) else SimpleCounter(this.toLong())
        }

        @JvmStatic
        @JvmName("startsAt")
        fun Long.counterStarts(): Counter {
            return SimpleCounter(this)
        }

        @JvmStatic
        @JvmName("startsAt")
        fun Long.counterStarts(atomic: Boolean = false): Counter {
            return if (atomic) AtomicCounter(AtomicLong(this)) else SimpleCounter(this)
        }
    }
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

private class AtomicCounter(private val atomicLong: AtomicLong, private val initValue: Long = atomicLong.get()) :
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