package xyz.srclab.common.egg

/**
 * The staring [Engine].
 */
interface Playing {

    fun go()

    fun pause()

    fun stop()

    fun save(path: CharSequence)

    fun load(path: CharSequence)
}