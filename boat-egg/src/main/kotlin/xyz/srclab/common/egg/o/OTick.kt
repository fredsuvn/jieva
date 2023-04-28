package xyz.srclab.common.egg.o

class OTick(
    @Volatile
    private var count: Long = 0
) {

    val now: Long
        get() = count

    fun tick() {
        count++
    }
}