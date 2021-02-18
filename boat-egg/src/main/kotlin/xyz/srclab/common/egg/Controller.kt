package xyz.srclab.common.egg

/**
 * The staring [Engine].
 */
interface Controller<D : Data> {

    fun startNew()

    fun go()

    fun pause()

    fun stop()

    fun save(): D

    fun load(data: D)
}