package xyz.srclab.common.base

import java.util.concurrent.atomic.AtomicLong

/**
 * @author sunqian
 */
interface Counter {

    fun getInt(): Int {
        return getLong().toInt()
    }

    fun setInt(value: Int) {
        setLong(value.toLong())
    }

    fun getLong(): Long

    fun setLong(value: Long)

    fun getAndAddInt(value: Int): Int {
        return getAndAddLong(value.toLong()).toInt()
    }

    fun getAndAddLong(value: Long): Long

    fun addAndGetInt(value: Int): Int {
        return addAndGetLong(value.toLong()).toInt()
    }

    fun addAndGetLong(value: Long): Long

    fun getAndIncrementInt(): Int {
        return getAndIncrementLong().toInt()
    }

    fun getAndIncrementLong(): Long

    fun incrementAndGetInt(): Int {
        return incrementAndGetLong().toInt()
    }

    fun incrementAndGetLong(): Long

    companion object {

        @JvmStatic
        @JvmOverloads
        @JvmName("startsAt")
        fun Int.counterStarts(atomically: Boolean = false): Counter {
            return if (atomically) AtomicCounter(AtomicLong(this.toLong())) else SimpleCounter(this.toLong())
        }

        @JvmStatic
        @JvmOverloads
        @JvmName("startsAt")
        fun Long.counterStarts(atomically: Boolean = false): Counter {
            return if (atomically) AtomicCounter(AtomicLong(this)) else SimpleCounter(this)
        }
    }
}

private class SimpleCounter(private var value: Long) : Counter {

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
}

private class AtomicCounter(private val atomicLong: AtomicLong) : Counter {

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
}