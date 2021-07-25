package xyz.srclab.common.egg.o

class OTick(
    private var count: Long = 0
) {
    @Synchronized
    fun tick() {
        count++
    }

    fun now(): Long {
        return count
    }
}